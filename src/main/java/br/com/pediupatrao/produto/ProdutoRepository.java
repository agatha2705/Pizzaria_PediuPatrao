package br.com.pediupatrao.produto;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProdutoRepository extends MongoRepository<Produto, String> {
    List<Produto> findAllByAtivoTrue();
}
