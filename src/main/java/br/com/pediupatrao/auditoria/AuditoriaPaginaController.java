package br.com.pediupatrao.auditoria;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auditoria")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class AuditoriaPaginaController {
    private final RegistroAuditoriaRepository repository;

    public AuditoriaPaginaController(RegistroAuditoriaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("registros", repository.findAllByOrderByDataHoraDesc());
        return "auditoria/lista";
    }
}
