package br.com.pediupatrao.usuario;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    @AfterEach
    void limparAutenticacao() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void impedeRebaixamentoDoUltimoAdministradorAtivo() {
        Usuario admin = adminAtivo();
        autenticarComo("outro-admin");
        when(repository.findById("admin-1")).thenReturn(Optional.of(admin));
        when(repository.countByPerfilAndAtivoTrue(Perfil.ADMIN)).thenReturn(1L);

        assertThrows(IllegalArgumentException.class, () -> service.alterarPerfil("admin-1", Perfil.GERENTE));

        verify(repository, never()).save(admin);
    }

    @Test
    void impedeDesativacaoDoUltimoAdministradorAtivo() {
        Usuario admin = adminAtivo();
        autenticarComo("outro-admin");
        when(repository.findById("admin-1")).thenReturn(Optional.of(admin));
        when(repository.countByPerfilAndAtivoTrue(Perfil.ADMIN)).thenReturn(1L);

        assertThrows(IllegalArgumentException.class, () -> service.desativar("admin-1"));

        verify(repository, never()).save(admin);
    }

    private Usuario adminAtivo() {
        Usuario usuario = new Usuario();
        usuario.setId("admin-1");
        usuario.setUsername("admin-principal");
        usuario.setPerfil(Perfil.ADMIN);
        usuario.setAtivo(true);
        return usuario;
    }

    private void autenticarComo(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, "nao-importa"));
    }
}
