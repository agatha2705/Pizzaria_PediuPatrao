package br.com.pediupatrao.pedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ItemPedidoRequest(@NotBlank String produtoId, @Min(1) int quantidade) {
}
