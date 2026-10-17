package br.com.pediupatrao.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClienteRequest(
        @NotBlank String nome,
        @NotBlank @Pattern(regexp = "^(?:\\(?\\d{2}\\)?\\s?)?\\d{4,5}-?\\d{4}$", message = "Informe um telefone com DDD e 10 ou 11 dígitos") String telefone,
        String email,
        @NotBlank String logradouro,
        @NotBlank String numero,
        @NotBlank String bairro,
        @NotBlank String cidade) {
}
