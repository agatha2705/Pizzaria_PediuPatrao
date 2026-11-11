package br.com.pediupatrao.pedido;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PedidoRepository extends MongoRepository<Pedido, String> {
    List<Pedido> findAllByOrderByDataHoraEntradaDesc();

    @Query(value = "{ 'dataHoraEntrada': { $gte: ?0, $lt: ?1 } }", sort = "{ 'dataHoraEntrada': 1 }")
    List<Pedido> findAllByDataHoraEntradaGreaterThanEqualAndDataHoraEntradaLessThanOrderByDataHoraEntradaAsc(
            LocalDateTime inicio, LocalDateTime fimExclusivo);
}
