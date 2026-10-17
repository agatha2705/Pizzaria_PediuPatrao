package br.com.pediupatrao.cliente;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/clientes")
public class ClientePaginaController {

    private final ClienteService service;

    public ClientePaginaController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", service.listar());
        return "clientes/lista";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasRole('ATENDENTE')")
    public String novo(Model model) {
        model.addAttribute("cliente", new ClienteForm());
        model.addAttribute("titulo", "Cadastrar cliente");
        model.addAttribute("acao", "/clientes");
        return "clientes/formulario";
    }

    @PostMapping
    @PreAuthorize("hasRole('ATENDENTE')")
    public String criar(@Valid @ModelAttribute("cliente") ClienteForm form, BindingResult resultado, Model model) {
        if (resultado.hasErrors()) {
            prepararFormulario(model, "Cadastrar cliente", "/clientes");
            return "clientes/formulario";
        }
        service.criar(form.toRequest());
        return "redirect:/clientes";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ATENDENTE')")
    public String editar(@PathVariable String id, Model model) {
        Cliente cliente = service.buscarPorId(id);
        model.addAttribute("cliente", ClienteForm.from(cliente));
        model.addAttribute("titulo", "Alterar cliente");
        model.addAttribute("acao", "/clientes/" + id);
        return "clientes/formulario";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ATENDENTE')")
    public String atualizar(@PathVariable String id, @Valid @ModelAttribute("cliente") ClienteForm form,
            BindingResult resultado, Model model) {
        if (resultado.hasErrors()) {
            prepararFormulario(model, "Alterar cliente", "/clientes/" + id);
            return "clientes/formulario";
        }
        service.atualizar(id, form.toRequest());
        return "redirect:/clientes";
    }

    private void prepararFormulario(Model model, String titulo, String acao) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("acao", acao);
    }
}
