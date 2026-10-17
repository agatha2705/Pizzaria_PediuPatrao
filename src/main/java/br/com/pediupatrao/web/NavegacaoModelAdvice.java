package br.com.pediupatrao.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavegacaoModelAdvice {

    @ModelAttribute("rotaAtual")
    public String rotaAtual(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
