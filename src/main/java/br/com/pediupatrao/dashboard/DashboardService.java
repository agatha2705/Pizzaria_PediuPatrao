package br.com.pediupatrao.dashboard;

import br.com.pediupatrao.pedido.ItemPedido;
import br.com.pediupatrao.pedido.Pedido;
import br.com.pediupatrao.pedido.PedidoRepository;
import br.com.pediupatrao.pedido.StatusPedido;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private static final int DIAS_MAXIMOS_CONSULTA = 366;
    private final PedidoRepository pedidos;

    public DashboardService(PedidoRepository pedidos) {
        this.pedidos = pedidos;
    }

    public ResumoDashboard resumo(LocalDate inicio, LocalDate fim) {
        List<Pedido> doPeriodo = buscarNoPeriodo(inicio, fim);
        List<Pedido> finalizados = doPeriodo.stream().filter(pedido -> pedido.getStatus() == StatusPedido.FINALIZADO).toList();
        BigDecimal faturamento = somar(finalizados, Pedido::getValorTotal);
        return new ResumoDashboard(faturamento, finalizados.size(), doPeriodo.size(),
                finalizados.isEmpty() ? BigDecimal.ZERO.setScale(2)
                        : moeda(faturamento.divide(BigDecimal.valueOf(finalizados.size()), 2, RoundingMode.HALF_UP)),
                somar(finalizados, Pedido::getValorDesconto),
                doPeriodo.stream().filter(pedido -> pedido.getStatus() == StatusPedido.CANCELADO).count());
    }

    public List<VendaPorDia> vendasPorDia(LocalDate inicio, LocalDate fim) {
        Periodo periodo = resolverPeriodo(inicio, fim);
        Map<LocalDate, BigDecimal> faturamento = new LinkedHashMap<>();
        Map<LocalDate, Long> quantidade = new LinkedHashMap<>();
        for (LocalDate data = periodo.inicio(); !data.isAfter(periodo.fim()); data = data.plusDays(1)) {
            faturamento.put(data, BigDecimal.ZERO.setScale(2));
            quantidade.put(data, 0L);
        }
        buscarNoPeriodo(periodo.inicio(), periodo.fim()).stream().filter(pedido -> pedido.getStatus() == StatusPedido.FINALIZADO)
                .forEach(pedido -> {
                    LocalDate data = pedido.getDataHoraEntrada().toLocalDate();
                    faturamento.merge(data, valorSeguro(pedido.getValorTotal()), BigDecimal::add);
                    quantidade.merge(data, 1L, Long::sum);
                });
        return faturamento.entrySet().stream()
                .map(entry -> new VendaPorDia(entry.getKey(), moeda(entry.getValue()), quantidade.get(entry.getKey())))
                .toList();
    }

    public List<PedidosPorStatus> pedidosPorStatus(LocalDate inicio, LocalDate fim) {
        Map<StatusPedido, Long> totais = new EnumMap<>(StatusPedido.class);
        for (StatusPedido status : StatusPedido.values()) totais.put(status, 0L);
        buscarNoPeriodo(inicio, fim).forEach(pedido -> totais.merge(pedido.getStatus(), 1L, Long::sum));
        List<PedidosPorStatus> resposta = new ArrayList<>();
        for (StatusPedido status : StatusPedido.values()) {
            resposta.add(new PedidosPorStatus(status.name(), status.getDescricao(), totais.get(status)));
        }
        return resposta;
    }

    public List<ProdutoMaisVendido> produtosMaisVendidos(LocalDate inicio, LocalDate fim, int limite) {
        if (limite < 1 || limite > 10) throw new IllegalArgumentException("O limite deve estar entre 1 e 10");
        Map<String, ProdutoAcumulado> produtos = new LinkedHashMap<>();
        buscarNoPeriodo(inicio, fim).stream().filter(pedido -> pedido.getStatus() != StatusPedido.CANCELADO)
                .flatMap(pedido -> pedido.getItens().stream()).forEach(item ->
                        produtos.computeIfAbsent(item.getNomeProduto(), chave -> new ProdutoAcumulado()).adicionar(item));
        return produtos.entrySet().stream()
                .map(entry -> new ProdutoMaisVendido(entry.getKey(), entry.getValue().quantidade, moeda(entry.getValue().faturamento)))
                .sorted(Comparator.comparing(ProdutoMaisVendido::quantidadeVendida).reversed().thenComparing(ProdutoMaisVendido::produto))
                .limit(limite).toList();
    }

    private List<Pedido> buscarNoPeriodo(LocalDate inicio, LocalDate fim) {
        Periodo periodo = resolverPeriodo(inicio, fim);
        LocalDateTime inicioDataHora = periodo.inicio().atStartOfDay();
        LocalDateTime fimExclusivo = periodo.fim().plusDays(1).atStartOfDay();
        return pedidos.findAllByDataHoraEntradaGreaterThanEqualAndDataHoraEntradaLessThanOrderByDataHoraEntradaAsc(inicioDataHora, fimExclusivo);
    }

    private Periodo resolverPeriodo(LocalDate inicio, LocalDate fim) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioResolvido = inicio == null ? hoje.minusDays(6) : inicio;
        LocalDate fimResolvido = fim == null ? hoje : fim;
        if (inicioResolvido.isAfter(fimResolvido)) throw new IllegalArgumentException("A data inicial deve ser anterior ou igual à data final");
        if (ChronoUnit.DAYS.between(inicioResolvido, fimResolvido) > DIAS_MAXIMOS_CONSULTA) throw new IllegalArgumentException("O período máximo de consulta é de 366 dias");
        return new Periodo(inicioResolvido, fimResolvido);
    }

    private BigDecimal somar(List<Pedido> pedidos, java.util.function.Function<Pedido, BigDecimal> seletor) {
        return moeda(pedidos.stream().map(seletor).map(this::valorSeguro).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private BigDecimal valorSeguro(BigDecimal valor) { return valor == null ? BigDecimal.ZERO : valor; }
    private BigDecimal moeda(BigDecimal valor) { return valor.setScale(2, RoundingMode.HALF_UP); }

    private record Periodo(LocalDate inicio, LocalDate fim) { }

    private static class ProdutoAcumulado {
        private long quantidade;
        private BigDecimal faturamento = BigDecimal.ZERO;
        void adicionar(ItemPedido item) {
            quantidade += item.getQuantidade();
            faturamento = faturamento.add(item.getSubtotal() == null ? BigDecimal.ZERO : item.getSubtotal());
        }
    }
}
