package br.com.pediupatrao.pedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public class ItemPedidoForm {
    @NotBlank
    private String produtoId;
    @Min(1)
    @Max(99)
    private int quantidade = 1;

    public ItemPedidoRequest toRequest() {
        return new ItemPedidoRequest(produtoId, quantidade);
    }

    public String getProdutoId() { return produtoId; }
    public void setProdutoId(String produtoId) { this.produtoId = produtoId; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
