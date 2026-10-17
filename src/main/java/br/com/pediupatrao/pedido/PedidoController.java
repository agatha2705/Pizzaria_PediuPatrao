package br.com.pediupatrao.pedido;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pedido> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public Pedido buscar(@PathVariable String id) { return service.buscarPorId(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido criar(@Valid @RequestBody CriarPedidoRequest request) { return service.criar(request); }

    @PatchMapping("/{id}/status")
    public Pedido alterarStatus(@PathVariable String id, @Valid @RequestBody StatusPedidoRequest request) {
        return service.alterarStatus(id, request.status());
    }

    @PatchMapping("/{id}/desconto")
    public Pedido aplicarDesconto(@PathVariable String id, @Valid @RequestBody DescontoRequest request) {
        return service.aplicarDesconto(id, request.percentual());
    }

    @PatchMapping("/{id}/cancelamento")
    public Pedido cancelar(@PathVariable String id, @Valid @RequestBody CancelamentoRequest request) {
        return service.cancelar(id, request.justificativa());
    }
}
