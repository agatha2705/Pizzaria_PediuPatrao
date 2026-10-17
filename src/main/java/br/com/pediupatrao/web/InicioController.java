package br.com.pediupatrao.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio(Authentication authentication, Model model) {
        model.addAttribute("usuario", authentication.getName());
        String perfil = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        model.addAttribute("perfil", perfil);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado";
    }
}
