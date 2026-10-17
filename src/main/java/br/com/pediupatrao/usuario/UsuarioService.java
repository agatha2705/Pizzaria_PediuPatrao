package br.com.pediupatrao.usuario;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> listar(boolean incluirInativos) {
        return incluirInativos ? repository.findAll() : repository.findAllByAtivoTrue();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void criar(UsuarioCadastroForm form) {
        if (repository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("Nome de usuario ja esta em uso");
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(form.getUsername().trim());
        usuario.setSenha(passwordEncoder.encode(form.getSenha()));
        usuario.setPerfil(form.getPerfil());
        usuario.setAtivo(true);
        repository.save(usuario);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void alterarPerfil(String id, Perfil novoPerfil) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));
        String usuarioAtual = SecurityContextHolder.getContext().getAuthentication().getName();
        if (usuario.getUsername().equals(usuarioAtual)) {
            throw new IllegalArgumentException("Nao e permitido alterar o proprio perfil");
        }
        usuario.setPerfil(novoPerfil);
        repository.save(usuario);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void desativar(String id) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));
        String usuarioAtual = SecurityContextHolder.getContext().getAuthentication().getName();
        if (usuario.getUsername().equals(usuarioAtual)) {
            throw new IllegalArgumentException("Nao e permitido desativar a propria conta");
        }
        usuario.setAtivo(false);
        repository.save(usuario);
    }
}
