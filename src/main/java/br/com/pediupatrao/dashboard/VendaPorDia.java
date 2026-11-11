package br.com.pediupatrao.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VendaPorDia(LocalDate data, BigDecimal faturamento, long pedidosFinalizados) {
}
