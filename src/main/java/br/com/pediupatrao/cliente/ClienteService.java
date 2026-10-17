package br.com.pediupatrao.cliente;

import br.com.pediupatrao.auditoria.AuditoriaService;
import br.com.pediupatrao.auditoria.OperacaoAuditoria;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final AuditoriaService auditoriaService;

    public ClienteService(ClienteRepository repository, AuditoriaService auditoriaService) {
        this.repository = repository;
        this.auditoriaService = auditoriaService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public List<Cliente> listar() {
        return repository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public Cliente buscarPorId(String id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado"));
    }

    @PreAuthorize("hasRole('ATENDENTE')")
    public Cliente criar(ClienteRequest request) {
        Cliente cliente = preencher(new Cliente(), request);
        Cliente salvo = repository.save(cliente);
        auditoriaService.registrar(OperacaoAuditoria.INCLUSAO_CLIENTE, "CLIENTE", salvo.getId(), Map.of(), valores(salvo), null);
        return salvo;
    }

    @PreAuthorize("hasRole('ATENDENTE')")
    public Cliente atualizar(String id, ClienteRequest request) {
        Cliente cliente = buscarPorId(id);
        Map<String, Object> antesCompleto = valores(cliente);
        Cliente salvo = repository.save(preencher(cliente, request));
        Map<String, Object> depoisCompleto = valores(salvo);
        Map<String, Object> antes = camposAlterados(antesCompleto, depoisCompleto);
        Map<String, Object> depois = camposAlterados(depoisCompleto, antesCompleto);
        if (!antes.isEmpty()) {
            auditoriaService.registrar(OperacaoAuditoria.ALTERACAO_CLIENTE, "CLIENTE", salvo.getId(), antes, depois, null);
        }
        return salvo;
    }

    private Cliente preencher(Cliente cliente, ClienteRequest request) {
        cliente.setNome(request.nome());
        cliente.setTelefone(formatarTelefone(request.telefone()));
        cliente.setEmail(request.email());
        cliente.setLogradouro(request.logradouro());
        cliente.setNumero(request.numero());
        cliente.setBairro(request.bairro());
        cliente.setCidade(request.cidade());
        return cliente;
    }

    private Map<String, Object> valores(Cliente cliente) {
        Map<String, Object> valores = new LinkedHashMap<>();
        valores.put("nome", cliente.getNome());
        valores.put("telefone", cliente.getTelefone());
        valores.put("email", cliente.getEmail());
        valores.put("logradouro", cliente.getLogradouro());
        valores.put("numero", cliente.getNumero());
        valores.put("bairro", cliente.getBairro());
        valores.put("cidade", cliente.getCidade());
        return valores;
    }

    private Map<String, Object> camposAlterados(Map<String, Object> origem, Map<String, Object> comparacao) {
        Map<String, Object> alterados = new LinkedHashMap<>();
        origem.forEach((campo, valor) -> {
            if (!java.util.Objects.equals(valor, comparacao.get(campo))) {
                alterados.put(campo, valor);
            }
        });
        return alterados;
    }

    private String formatarTelefone(String telefone) {
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.length() == 10) {
            return "(" + digitos.substring(0, 2) + ") " + digitos.substring(2, 6) + "-" + digitos.substring(6);
        }
        return "(" + digitos.substring(0, 2) + ") " + digitos.substring(2, 7) + "-" + digitos.substring(7);
    }
}
