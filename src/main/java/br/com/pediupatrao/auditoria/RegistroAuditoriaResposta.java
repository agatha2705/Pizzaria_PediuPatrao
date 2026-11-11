package br.com.pediupatrao.auditoria;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Representação segura dos registros para a API. Os valores completos
 * permanecem no registro de auditoria para fins de rastreabilidade, mas dados
 * pessoais não são expostos integralmente em respostas HTTP.
 */
public record RegistroAuditoriaResposta(
        String id,
        String usuario,
        String perfil,
        LocalDateTime dataHora,
        OperacaoAuditoria operacao,
        String entidade,
        String entidadeId,
        Map<String, Object> valoresAnteriores,
        Map<String, Object> valoresNovos,
        String justificativa) {

    private static final Set<String> DADOS_PESSOAIS = Set.of(
            "nome", "telefone", "email", "logradouro", "numero", "bairro", "cidade", "clienteId");

    public static RegistroAuditoriaResposta de(RegistroAuditoria registro) {
        return new RegistroAuditoriaResposta(
                registro.getId(), registro.getUsuario(), registro.getPerfil(), registro.getDataHora(),
                registro.getOperacao(), registro.getEntidade(), registro.getEntidadeId(),
                mascarar(registro.getValoresAnteriores()), mascarar(registro.getValoresNovos()),
                registro.getJustificativa());
    }

    private static Map<String, Object> mascarar(Map<String, Object> valores) {
        if (valores == null || valores.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> protegidos = new LinkedHashMap<>();
        valores.forEach((campo, valor) -> protegidos.put(campo,
                DADOS_PESSOAIS.contains(campo) ? mascararValor(campo, valor) : valor));
        return protegidos;
    }

    private static String mascararValor(String campo, Object valor) {
        if (valor == null) return "[dado protegido]";
        String texto = String.valueOf(valor);
        if ("telefone".equals(campo)) {
            String digitos = texto.replaceAll("\\D", "");
            return digitos.length() < 4 ? "[dado protegido]" : "***-" + digitos.substring(digitos.length() - 4);
        }
        if ("email".equals(campo)) {
            int arroba = texto.indexOf('@');
            return arroba > 0 ? texto.substring(0, 1) + "***" + texto.substring(arroba) : "[dado protegido]";
        }
        return "[dado protegido]";
    }
}
