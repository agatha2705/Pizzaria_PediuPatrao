package br.com.pediupatrao.auditoria;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class RegistroAuditoriaRespostaTest {

    @Test
    void mascaraDadosPessoaisNaRespostaDaApi() {
        RegistroAuditoria registro = new RegistroAuditoria();
        registro.setValoresNovos(Map.of(
                "nome", "Amanda Souza",
                "telefone", "(11) 98765-4321",
                "email", "amanda@email.com",
                "logradouro", "Rua das Flores",
                "status", "PRONTO"));

        RegistroAuditoriaResposta resposta = RegistroAuditoriaResposta.de(registro);

        assertEquals("[dado protegido]", resposta.valoresNovos().get("nome"));
        assertEquals("***-4321", resposta.valoresNovos().get("telefone"));
        assertEquals("a***@email.com", resposta.valoresNovos().get("email"));
        assertEquals("[dado protegido]", resposta.valoresNovos().get("logradouro"));
        assertEquals("PRONTO", resposta.valoresNovos().get("status"));
    }
}
