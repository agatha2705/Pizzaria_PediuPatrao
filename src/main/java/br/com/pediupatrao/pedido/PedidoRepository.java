package br.com.pediupatrao.pedido;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PedidoRepository extends MongoRepository<Pedido, String> {
    List<Pedido> findAllByOrderByDataHoraEntradaDesc();
}
