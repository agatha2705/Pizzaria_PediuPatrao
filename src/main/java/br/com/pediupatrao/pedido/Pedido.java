package br.com.pediupatrao.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("pedidos")
public class Pedido {

    @Id
    private String id;
    private String clienteId;
    private List<ItemPedido> itens = new ArrayList<>();
    private BigDecimal subtotal;
    private BigDecimal percentualDesconto = BigDecimal.ZERO;
    private BigDecimal valorDesconto = BigDecimal.ZERO;
    private BigDecimal valorTotal;
    private StatusPedido status;
    private String responsavelEntrada;
    private LocalDateTime dataHoraEntrada;
    private String responsavelSaida;
    private LocalDateTime dataHoraSaida;
    private String justificativaCancelamento;
    private String responsavelCancelamento;
    private LocalDateTime dataHoraCancelamento;

    public String getId() { return id; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getPercentualDesconto() { return percentualDesconto; }
    public void setPercentualDesconto(BigDecimal percentualDesconto) { this.percentualDesconto = percentualDesconto; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public String getResponsavelEntrada() { return responsavelEntrada; }
    public void setResponsavelEntrada(String responsavelEntrada) { this.responsavelEntrada = responsavelEntrada; }
    public LocalDateTime getDataHoraEntrada() { return dataHoraEntrada; }
    public void setDataHoraEntrada(LocalDateTime dataHoraEntrada) { this.dataHoraEntrada = dataHoraEntrada; }
    public String getResponsavelSaida() { return responsavelSaida; }
    public void setResponsavelSaida(String responsavelSaida) { this.responsavelSaida = responsavelSaida; }
    public LocalDateTime getDataHoraSaida() { return dataHoraSaida; }
    public void setDataHoraSaida(LocalDateTime dataHoraSaida) { this.dataHoraSaida = dataHoraSaida; }
    public String getJustificativaCancelamento() { return justificativaCancelamento; }
    public void setJustificativaCancelamento(String justificativaCancelamento) { this.justificativaCancelamento = justificativaCancelamento; }
    public String getResponsavelCancelamento() { return responsavelCancelamento; }
    public void setResponsavelCancelamento(String responsavelCancelamento) { this.responsavelCancelamento = responsavelCancelamento; }
    public LocalDateTime getDataHoraCancelamento() { return dataHoraCancelamento; }
    public void setDataHoraCancelamento(LocalDateTime dataHoraCancelamento) { this.dataHoraCancelamento = dataHoraCancelamento; }
}
