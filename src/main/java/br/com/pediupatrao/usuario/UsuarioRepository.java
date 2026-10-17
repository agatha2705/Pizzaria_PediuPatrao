package br.com.pediupatrao.usuario;

import java.util.Optional;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    List<Usuario> findAllByAtivoTrue();
}
