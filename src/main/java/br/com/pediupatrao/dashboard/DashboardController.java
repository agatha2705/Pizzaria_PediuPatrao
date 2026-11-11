package br.com.pediupatrao.dashboard;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/resumo")
    public ResumoDashboard resumo(@RequestParam(required = false) LocalDate inicio,
            @RequestParam(required = false) LocalDate fim) {
        return service.resumo(inicio, fim);
    }

    @GetMapping("/vendas-por-dia")
    public List<VendaPorDia> vendasPorDia(@RequestParam(required = false) LocalDate inicio,
            @RequestParam(required = false) LocalDate fim) {
        return service.vendasPorDia(inicio, fim);
    }

    @GetMapping("/pedidos-por-status")
    public List<PedidosPorStatus> pedidosPorStatus(@RequestParam(required = false) LocalDate inicio,
            @RequestParam(required = false) LocalDate fim) {
        return service.pedidosPorStatus(inicio, fim);
    }

    @GetMapping("/produtos-mais-vendidos")
    public List<ProdutoMaisVendido> produtosMaisVendidos(@RequestParam(required = false) LocalDate inicio,
            @RequestParam(required = false) LocalDate fim,
            @RequestParam(defaultValue = "5") @Min(1) @Max(10) int limite) {
        return service.produtosMaisVendidos(inicio, fim, limite);
    }
}
