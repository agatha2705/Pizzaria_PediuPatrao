package br.com.pediupatrao.dashboard;

import java.time.LocalDateTime;

public record ErroDashboardApi(LocalDateTime dataHora, int status, String mensagem) {
}
