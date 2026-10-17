package br.com.pediupatrao.pedido;

import br.com.pediupatrao.cliente.ClienteRepository;
import br.com.pediupatrao.produto.ProdutoRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pedidos")
public class PedidoPaginaController {

    private final PedidoService service;
    private final ClienteRepository clientes;
    private final ProdutoRepository produtos;

    public PedidoPaginaController(PedidoService service, ClienteRepository clientes, ProdutoRepository produtos) {
        this.service = service;
        this.clientes = clientes;
        this.produtos = produtos;
    }

    @GetMapping
    public String listar(Model model, Authentication authentication) {
        model.addAttribute("pedidos", service.listar());
        model.addAttribute("ehGerente", possuiPerfil(authentication, "ROLE_GERENTE"));
        model.addAttribute("podeAtualizarStatus", possuiPerfil(authentication, "ROLE_GERENTE")
                || possuiPerfil(authentication, "ROLE_ATENDENTE"));
        return "pedidos/lista";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasRole('ATENDENTE')")
    public String novo(Model model) {
        prepararFormulario(model);
        model.addAttribute("pedido", new NovoPedidoForm());
        return "pedidos/formulario";
    }

    @PostMapping
    @PreAuthorize("hasRole('ATENDENTE')")
    public String criar(@Valid @ModelAttribute("pedido") NovoPedidoForm form, BindingResult resultado, Model model) {
        if (resultado.hasErrors()) {
            prepararFormulario(model);
            return "pedidos/formulario";
        }
        service.criar(form.toRequest());
        return "redirect:/pedidos";
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('GERENTE', 'ATENDENTE')")
    public String alterarStatus(@PathVariable String id, @RequestParam StatusPedido status) {
        service.alterarStatus(id, status);
        return "redirect:/pedidos";
    }

    @PostMapping("/{id}/desconto")
    @PreAuthorize("hasRole('GERENTE')")
    public String aplicarDesconto(@PathVariable String id, @RequestParam BigDecimal percentual) {
        service.aplicarDesconto(id, percentual);
        return "redirect:/pedidos";
    }

    @PostMapping("/{id}/cancelamento")
    @PreAuthorize("hasRole('GERENTE')")
    public String cancelar(@PathVariable String id, @RequestParam String justificativa) {
        service.cancelar(id, justificativa);
        return "redirect:/pedidos";
    }

    private void prepararFormulario(Model model) {
        model.addAttribute("clientes", clientes.findAll());
        model.addAttribute("produtos", produtos.findAllByAtivoTrue());
    }

    private boolean possuiPerfil(Authentication authentication, String perfil) {
        return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(perfil));
    }

    public List<StatusPedido> proximosStatus(StatusPedido atual) {
        return switch (atual) {
            case RECEBIDO -> List.of(StatusPedido.EM_PREPARACAO);
            case EM_PREPARACAO -> List.of(StatusPedido.PRONTO);
            case PRONTO -> List.of(StatusPedido.SAIU_PARA_ENTREGA, StatusPedido.RETIRADO);
            case SAIU_PARA_ENTREGA, RETIRADO -> List.of(StatusPedido.FINALIZADO);
            case FINALIZADO, CANCELADO -> List.of();
        };
    }
}
