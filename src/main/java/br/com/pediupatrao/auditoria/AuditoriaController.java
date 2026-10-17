package br.com.pediupatrao.auditoria;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final RegistroAuditoriaRepository repository;

    public AuditoriaController(RegistroAuditoriaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public List<RegistroAuditoria> listar() {
        return repository.findAllByOrderByDataHoraDesc();
    }
}
