package br.com.pediupatrao.dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.com.pediupatrao.pedido.Pedido;
import br.com.pediupatrao.pedido.PedidoRepository;
import br.com.pediupatrao.pedido.StatusPedido;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private PedidoRepository repository;

    @InjectMocks
    private DashboardService service;

    @Test
    void calculaFaturamentoSomenteComPedidosFinalizados() {
        when(repository.findAllByDataHoraEntradaGreaterThanEqualAndDataHoraEntradaLessThanOrderByDataHoraEntradaAsc(
                any(), any())).thenReturn(List.of(
                pedido(StatusPedido.FINALIZADO, "100.00", "10.00"),
                pedido(StatusPedido.PRONTO, "45.00", "0.00"),
                pedido(StatusPedido.CANCELADO, "30.00", "0.00")));

        ResumoDashboard resumo = service.resumo(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 7));

        assertEquals(new BigDecimal("100.00"), resumo.faturamento());
        assertEquals(1, resumo.pedidosFinalizados());
        assertEquals(3, resumo.pedidosRecebidos());
        assertEquals(new BigDecimal("100.00"), resumo.ticketMedio());
        assertEquals(new BigDecimal("10.00"), resumo.descontosConcedidos());
        assertEquals(1, resumo.cancelamentos());
    }

    @Test
    void rejeitaPeriodoComDatasInvertidas() {
        assertThrows(IllegalArgumentException.class,
                () -> service.resumo(LocalDate.of(2026, 11, 8), LocalDate.of(2026, 11, 1)));
    }

    private Pedido pedido(StatusPedido status, String total, String desconto) {
        Pedido pedido = new Pedido();
        pedido.setStatus(status);
        pedido.setValorTotal(new BigDecimal(total));
        pedido.setValorDesconto(new BigDecimal(desconto));
        pedido.setDataHoraEntrada(LocalDateTime.of(2026, 11, 2, 10, 0));
        return pedido;
    }
}
