package br.com.pediupatrao.auditoria;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RegistroAuditoriaRepository extends MongoRepository<RegistroAuditoria, String> {
    List<RegistroAuditoria> findAllByOrderByDataHoraDesc();
}
