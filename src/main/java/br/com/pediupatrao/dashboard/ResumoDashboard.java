package br.com.pediupatrao.dashboard;

import java.math.BigDecimal;

public record ResumoDashboard(BigDecimal faturamento, long pedidosFinalizados, long pedidosRecebidos,
        BigDecimal ticketMedio, BigDecimal descontosConcedidos, long cancelamentos) {
}
