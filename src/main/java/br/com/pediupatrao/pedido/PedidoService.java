package br.com.pediupatrao.pedido;

import br.com.pediupatrao.auditoria.AuditoriaService;
import br.com.pediupatrao.auditoria.OperacaoAuditoria;
import br.com.pediupatrao.cliente.ClienteRepository;
import br.com.pediupatrao.produto.Produto;
import br.com.pediupatrao.produto.ProdutoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    private static final BigDecimal DESCONTO_MAXIMO = new BigDecimal("20.00");
    private final PedidoRepository pedidos;
    private final ClienteRepository clientes;
    private final ProdutoRepository produtos;
    private final AuditoriaService auditoria;

    public PedidoService(PedidoRepository pedidos, ClienteRepository clientes, ProdutoRepository produtos,
            AuditoriaService auditoria) {
        this.pedidos = pedidos;
        this.clientes = clientes;
        this.produtos = produtos;
        this.auditoria = auditoria;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public List<Pedido> listar() {
        return pedidos.findAllByOrderByDataHoraEntradaDesc();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public Pedido buscarPorId(String id) {
        return pedidos.findById(id).orElseThrow(() -> new IllegalArgumentException("Pedido nao encontrado"));
    }

    @PreAuthorize("hasRole('ATENDENTE')")
    public Pedido criar(CriarPedidoRequest request) {
        if (!clientes.existsById(request.clienteId())) {
            throw new IllegalArgumentException("Cliente nao encontrado");
        }
        Pedido pedido = new Pedido();
        pedido.setClienteId(request.clienteId());
        pedido.setItens(criarItens(request.itens()));
        pedido.setSubtotal(calcularSubtotal(pedido.getItens()));
        pedido.setPercentualDesconto(BigDecimal.ZERO);
        pedido.setValorDesconto(BigDecimal.ZERO.setScale(2));
        pedido.setValorTotal(pedido.getSubtotal());
        pedido.setStatus(StatusPedido.RECEBIDO);
        pedido.setResponsavelEntrada(usuarioAtual());
        pedido.setDataHoraEntrada(LocalDateTime.now());
        Pedido salvo = pedidos.save(pedido);
        auditoria.registrar(OperacaoAuditoria.INCLUSAO_PEDIDO, "PEDIDO", salvo.getId(), Map.of(), resumo(salvo), null);
        return salvo;
    }

    @PreAuthorize("hasAnyRole('GERENTE', 'ATENDENTE')")
    public Pedido alterarStatus(String id, StatusPedido proximoStatus) {
        Pedido pedido = buscarPorId(id);
        StatusPedido anterior = pedido.getStatus();
        validarTransicao(anterior, proximoStatus);
        pedido.setStatus(proximoStatus);
        if (proximoStatus == StatusPedido.SAIU_PARA_ENTREGA || proximoStatus == StatusPedido.RETIRADO) {
            pedido.setResponsavelSaida(usuarioAtual());
            pedido.setDataHoraSaida(LocalDateTime.now());
        }
        Pedido salvo = pedidos.save(pedido);
        Map<String, Object> antes = Map.of("status", anterior.name());
        Map<String, Object> depois = new LinkedHashMap<>();
        depois.put("status", salvo.getStatus().name());
        if (salvo.getDataHoraSaida() != null) {
            depois.put("responsavelSaida", salvo.getResponsavelSaida());
            depois.put("dataHoraSaida", salvo.getDataHoraSaida().toString());
        }
        auditoria.registrar(OperacaoAuditoria.ALTERACAO_STATUS_PEDIDO, "PEDIDO", salvo.getId(), antes, depois, null);
        return salvo;
    }

    @PreAuthorize("hasRole('GERENTE')")
    public Pedido aplicarDesconto(String id, BigDecimal percentual) {
        Pedido pedido = buscarPorId(id);
        bloquearSeSaiuOuCancelado(pedido, "Desconto");
        if (percentual.compareTo(BigDecimal.ZERO) <= 0 || percentual.compareTo(DESCONTO_MAXIMO) > 0) {
            throw new IllegalArgumentException("O desconto deve ser maior que zero e no maximo 20%");
        }
        Map<String, Object> antes = Map.of(
                "percentualDesconto", pedido.getPercentualDesconto(),
                "valorDesconto", pedido.getValorDesconto(),
                "valorTotal", pedido.getValorTotal());
        pedido.setPercentualDesconto(percentual.setScale(2, RoundingMode.HALF_UP));
        recalcularValores(pedido);
        Pedido salvo = pedidos.save(pedido);
        auditoria.registrar(OperacaoAuditoria.APLICACAO_DESCONTO, "PEDIDO", salvo.getId(), antes,
                Map.of("percentualDesconto", salvo.getPercentualDesconto(), "valorDesconto", salvo.getValorDesconto(),
                        "valorTotal", salvo.getValorTotal()), null);
        return salvo;
    }

    @PreAuthorize("hasRole('GERENTE')")
    public Pedido cancelar(String id, String justificativa) {
        Pedido pedido = buscarPorId(id);
        bloquearSeSaiuOuCancelado(pedido, "Cancelamento");
        if (justificativa == null || justificativa.isBlank()) {
            throw new IllegalArgumentException("O cancelamento exige justificativa");
        }
        StatusPedido statusAnterior = pedido.getStatus();
        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setJustificativaCancelamento(justificativa.trim());
        pedido.setResponsavelCancelamento(usuarioAtual());
        pedido.setDataHoraCancelamento(LocalDateTime.now());
        Pedido salvo = pedidos.save(pedido);
        auditoria.registrar(OperacaoAuditoria.CANCELAMENTO_PEDIDO, "PEDIDO", salvo.getId(),
                Map.of("status", statusAnterior.name()),
                Map.of("status", salvo.getStatus().name(), "responsavelCancelamento", salvo.getResponsavelCancelamento()),
                salvo.getJustificativaCancelamento());
        return salvo;
    }

    private List<ItemPedido> criarItens(List<ItemPedidoRequest> solicitados) {
        List<ItemPedido> itens = new ArrayList<>();
        for (ItemPedidoRequest solicitado : solicitados) {
            Produto produto = produtos.findById(solicitado.produtoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado"));
            if (!produto.isAtivo()) {
                throw new IllegalArgumentException("Produto indisponivel");
            }
            ItemPedido item = new ItemPedido();
            item.setProdutoId(produto.getId());
            item.setNomeProduto(produto.getNome());
            item.setQuantidade(solicitado.quantidade());
            item.setPrecoUnitario(moeda(produto.getPreco()));
            item.setSubtotal(moeda(produto.getPreco().multiply(BigDecimal.valueOf(solicitado.quantidade()))));
            itens.add(item);
        }
        return itens;
    }

    private void validarTransicao(StatusPedido atual, StatusPedido proximo) {
        if (atual == StatusPedido.CANCELADO) {
            throw new IllegalArgumentException("Pedido cancelado nao pode ser alterado");
        }
        boolean permitida = switch (atual) {
            case RECEBIDO -> proximo == StatusPedido.EM_PREPARACAO;
            case EM_PREPARACAO -> proximo == StatusPedido.PRONTO;
            case PRONTO -> proximo == StatusPedido.SAIU_PARA_ENTREGA || proximo == StatusPedido.RETIRADO;
            case SAIU_PARA_ENTREGA, RETIRADO -> proximo == StatusPedido.FINALIZADO;
            case FINALIZADO, CANCELADO -> false;
        };
        if (!permitida) {
            throw new IllegalArgumentException("Transicao de status invalida: " + atual + " para " + proximo);
        }
    }

    private void bloquearSeSaiuOuCancelado(Pedido pedido, String operacao) {
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new IllegalArgumentException(operacao + " nao e permitido em pedido cancelado");
        }
        if (pedido.getStatus() == StatusPedido.SAIU_PARA_ENTREGA || pedido.getStatus() == StatusPedido.RETIRADO
                || pedido.getStatus() == StatusPedido.FINALIZADO) {
            throw new IllegalArgumentException(operacao + " so e permitido antes da saida ou retirada");
        }
    }

    private BigDecimal calcularSubtotal(List<ItemPedido> itens) {
        return moeda(itens.stream().map(ItemPedido::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private void recalcularValores(Pedido pedido) {
        BigDecimal desconto = pedido.getSubtotal().multiply(pedido.getPercentualDesconto())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        pedido.setValorDesconto(moeda(desconto));
        pedido.setValorTotal(moeda(pedido.getSubtotal().subtract(desconto)));
    }

    private BigDecimal moeda(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private String usuarioAtual() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Map<String, Object> resumo(Pedido pedido) {
        Map<String, Object> valores = new LinkedHashMap<>();
        valores.put("clienteId", pedido.getClienteId());
        valores.put("status", pedido.getStatus().name());
        valores.put("subtotal", pedido.getSubtotal());
        valores.put("valorTotal", pedido.getValorTotal());
        valores.put("responsavelEntrada", pedido.getResponsavelEntrada());
        valores.put("dataHoraEntrada", pedido.getDataHoraEntrada().toString());
        return valores;
    }
}
