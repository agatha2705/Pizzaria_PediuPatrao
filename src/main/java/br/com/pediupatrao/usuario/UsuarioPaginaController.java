package br.com.pediupatrao.usuario;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioPaginaController {

    private final UsuarioService service;

    public UsuarioPaginaController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model, Authentication authentication,
            @RequestParam(required = false) String sucesso,
            @RequestParam(defaultValue = "false") boolean inativos) {
        model.addAttribute("usuarios", service.listar(inativos));
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("usuarioAtual", authentication.getName());
        model.addAttribute("mostrandoInativos", inativos);
        if ("perfil".equals(sucesso)) {
            model.addAttribute("mensagemSucesso", "Perfil atualizado com sucesso.");
        }
        if ("desativado".equals(sucesso)) {
            model.addAttribute("mensagemSucesso", "Usuário desativado. Esta conta não consegue mais entrar.");
        }
        if ("cadastrado".equals(sucesso)) {
            model.addAttribute("mensagemSucesso", "Usuário cadastrado com sucesso.");
        }
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new UsuarioCadastroForm());
        model.addAttribute("perfis", Perfil.values());
        return "usuarios/formulario";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("usuario") UsuarioCadastroForm form, BindingResult resultado, Model model) {
        if (resultado.hasErrors()) {
            model.addAttribute("perfis", Perfil.values());
            return "usuarios/formulario";
        }
        try {
            service.criar(form);
        } catch (IllegalArgumentException exception) {
            resultado.rejectValue("username", "duplicado", exception.getMessage());
            model.addAttribute("perfis", Perfil.values());
            return "usuarios/formulario";
        }
        return "redirect:/usuarios?sucesso=cadastrado";
    }

    @PostMapping("/{id}/perfil")
    public String alterarPerfil(@PathVariable String id, @RequestParam Perfil perfil) {
        service.alterarPerfil(id, perfil);
        return "redirect:/usuarios?sucesso=perfil";
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable String id) {
        service.desativar(id);
        return "redirect:/usuarios?sucesso=desativado";
    }
}
