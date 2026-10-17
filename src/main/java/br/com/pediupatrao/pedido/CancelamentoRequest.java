package br.com.pediupatrao.pedido;

import jakarta.validation.constraints.NotBlank;

public record CancelamentoRequest(@NotBlank String justificativa) {
}
