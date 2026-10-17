package br.com.pediupatrao.pedido;

import jakarta.validation.constraints.NotNull;

public record StatusPedidoRequest(@NotNull StatusPedido status) {
}
