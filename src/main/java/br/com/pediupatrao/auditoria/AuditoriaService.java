package br.com.pediupatrao.auditoria;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    private final RegistroAuditoriaRepository repository;

    public AuditoriaService(RegistroAuditoriaRepository repository) {
        this.repository = repository;
    }

    public void registrar(OperacaoAuditoria operacao, String entidade, String entidadeId,
            Map<String, Object> valoresAnteriores, Map<String, Object> valoresNovos, String justificativa) {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        RegistroAuditoria registro = new RegistroAuditoria();
        registro.setUsuario(autenticacao.getName());
        registro.setPerfil(autenticacao.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .orElse("SEM_PERFIL"));
        registro.setDataHora(LocalDateTime.now());
        registro.setOperacao(operacao);
        registro.setEntidade(entidade);
        registro.setEntidadeId(entidadeId);
        registro.setValoresAnteriores(valoresAnteriores);
        registro.setValoresNovos(valoresNovos);
        registro.setJustificativa(justificativa);
        repository.save(registro);
    }
}
