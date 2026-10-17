package br.com.pediupatrao.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UsuarioCadastroForm {
    @NotBlank
    @Size(min = 3, max = 40)
    private String username;
    @NotBlank
    @Size(min = 8, max = 100)
    private String senha;
    @NotNull
    private Perfil perfil;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
}
