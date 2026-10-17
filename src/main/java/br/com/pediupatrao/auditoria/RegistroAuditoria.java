package br.com.pediupatrao.auditoria;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import br.com.pediupatrao.pedido.StatusPedido;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("auditorias")
public class RegistroAuditoria {

    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    @Id
    private String id;
    private String usuario;
    private String perfil;
    private LocalDateTime dataHora;
    private OperacaoAuditoria operacao;
    private String entidade;
    private String entidadeId;
    private Map<String, Object> valoresAnteriores;
    private Map<String, Object> valoresNovos;
    private String justificativa;

    public String getId() { return id; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public OperacaoAuditoria getOperacao() { return operacao; }
    public void setOperacao(OperacaoAuditoria operacao) { this.operacao = operacao; }
    public String getEntidade() { return entidade; }
    public void setEntidade(String entidade) { this.entidade = entidade; }
    public String getEntidadeId() { return entidadeId; }
    public void setEntidadeId(String entidadeId) { this.entidadeId = entidadeId; }
    public Map<String, Object> getValoresAnteriores() { return valoresAnteriores; }
    public void setValoresAnteriores(Map<String, Object> valoresAnteriores) { this.valoresAnteriores = valoresAnteriores; }
    public Map<String, Object> getValoresNovos() { return valoresNovos; }
    public void setValoresNovos(Map<String, Object> valoresNovos) { this.valoresNovos = valoresNovos; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }

    public String getResumo() {
        if (operacao == null) return "Evento registrado";
        return switch (operacao) {
            case ALTERACAO_STATUS_PEDIDO -> "Status: " + valorAnterior("status") + " > " + valorNovo("status");
            case APLICACAO_DESCONTO -> "Desconto aplicado: " + valorNovo("percentualDesconto") + "%";
            case CANCELAMENTO_PEDIDO -> "Pedido cancelado";
            case INCLUSAO_CLIENTE -> "Cliente cadastrado";
            case ALTERACAO_CLIENTE -> "Dados do cliente atualizados";
            case INCLUSAO_PEDIDO -> "Pedido registrado";
        };
    }

    public boolean isExibirDetalhes() {
        return operacao == OperacaoAuditoria.CANCELAMENTO_PEDIDO
                || (operacao == OperacaoAuditoria.ALTERACAO_STATUS_PEDIDO
                && valoresNovos != null
                && "FINALIZADO".equals(String.valueOf(valoresNovos.get("status"))));
    }

    private String valorAnterior(String campo) {
        return valoresAnteriores == null ? "—" : formatarValor(valoresAnteriores.getOrDefault(campo, "—"));
    }

    private String valorNovo(String campo) {
        return valoresNovos == null ? "—" : formatarValor(valoresNovos.getOrDefault(campo, "—"));
    }

    public String formatarValor(Object valor) {
        if (valor == null) return "—";
        String texto = String.valueOf(valor);
        try {
            return StatusPedido.valueOf(texto).getDescricao();
        } catch (IllegalArgumentException ignored) {
            try {
                return LocalDateTime.parse(texto).format(DATA_HORA);
            } catch (RuntimeException ignoredAgain) {
                return texto;
            }
        }
    }

    public String rotuloCampo(String campo) {
        return switch (campo) {
            case "status" -> "Status";
            case "responsavelSaida" -> "Responsável pela saída";
            case "dataHoraSaida" -> "Data e hora da saída";
            case "responsavelEntrada" -> "Responsável pela entrada";
            case "dataHoraEntrada" -> "Data e hora da entrada";
            default -> campo;
        };
    }
}
