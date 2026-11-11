package br.com.pediupatrao.dashboard;

import java.math.BigDecimal;

public record ProdutoMaisVendido(String produto, long quantidadeVendida, BigDecimal faturamento) {
}
