package br.com.pediupatrao.pedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CriarPedidoRequest(@NotBlank String clienteId,
        @NotEmpty @Size(max = 20) List<@Valid ItemPedidoRequest> itens) {
}
