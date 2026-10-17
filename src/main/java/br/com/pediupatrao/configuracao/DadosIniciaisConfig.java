package br.com.pediupatrao.configuracao;

import br.com.pediupatrao.usuario.Perfil;
import br.com.pediupatrao.usuario.Usuario;
import br.com.pediupatrao.usuario.UsuarioRepository;
import br.com.pediupatrao.produto.Produto;
import br.com.pediupatrao.produto.ProdutoRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DadosIniciaisConfig {

    @Bean
    CommandLineRunner criarUsuariosIniciais(UsuarioRepository usuarios, ProdutoRepository produtos,
            PasswordEncoder passwordEncoder) {
        return args -> {
            criarSeAusente(usuarios, passwordEncoder, "admin", "admin123", Perfil.ADMIN);
            criarSeAusente(usuarios, passwordEncoder, "gerente", "gerente123", Perfil.GERENTE);
            criarSeAusente(usuarios, passwordEncoder, "atendente", "atendente123", Perfil.ATENDENTE);
            criarProdutoSeNecessario(produtos, "Pizza Calabresa", "45.00");
            criarProdutoSeNecessario(produtos, "Pizza Mussarela", "42.00");
            criarProdutoSeNecessario(produtos, "Refrigerante 2L", "12.00");
        };
    }

    private void criarSeAusente(UsuarioRepository usuarios, PasswordEncoder passwordEncoder,
            String username, String senha, Perfil perfil) {
        if (usuarios.existsByUsername(username)) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setPerfil(perfil);
        usuarios.save(usuario);
    }

    private void criarProdutoSeNecessario(ProdutoRepository produtos, String nome, String preco) {
        if (produtos.findAll().stream().anyMatch(produto -> produto.getNome().equalsIgnoreCase(nome))) {
            return;
        }
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setPreco(new BigDecimal(preco));
        produtos.save(produto);
    }
}
