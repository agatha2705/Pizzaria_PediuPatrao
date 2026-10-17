package br.com.pediupatrao.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import java.util.List;
import java.util.ArrayList;

public class NovoPedidoForm {
    @NotBlank
    private String clienteId;
    @NotEmpty
    @Valid
    private List<@Valid ItemPedidoForm> itens = new ArrayList<>(List.of(new ItemPedidoForm()));

    public CriarPedidoRequest toRequest() {
        return new CriarPedidoRequest(clienteId, itens.stream().map(ItemPedidoForm::toRequest).toList());
    }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public List<ItemPedidoForm> getItens() { return itens; }
    public void setItens(List<ItemPedidoForm> itens) { this.itens = itens; }
}
