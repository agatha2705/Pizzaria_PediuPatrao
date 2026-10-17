package br.com.pediupatrao.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ClienteForm {

    @NotBlank
    private String nome;
    @NotBlank
    @Pattern(regexp = "^(?:\\(?\\d{2}\\)?\\s?)?\\d{4,5}-?\\d{4}$", message = "Informe um telefone com DDD e 10 ou 11 dígitos")
    private String telefone;
    private String email;
    @NotBlank
    private String logradouro;
    @NotBlank
    private String numero;
    @NotBlank
    private String bairro;
    @NotBlank
    private String cidade;

    public static ClienteForm from(Cliente cliente) {
        ClienteForm form = new ClienteForm();
        form.nome = cliente.getNome();
        form.telefone = cliente.getTelefone();
        form.email = cliente.getEmail();
        form.logradouro = cliente.getLogradouro();
        form.numero = cliente.getNumero();
        form.bairro = cliente.getBairro();
        form.cidade = cliente.getCidade();
        return form;
    }

    public ClienteRequest toRequest() {
        return new ClienteRequest(nome, telefone, email, logradouro, numero, bairro, cidade);
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
}
