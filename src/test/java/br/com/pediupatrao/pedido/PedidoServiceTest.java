package br.com.pediupatrao.pedido;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.pediupatrao.auditoria.AuditoriaService;
import br.com.pediupatrao.cliente.ClienteRepository;
import br.com.pediupatrao.produto.ProdutoRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidos;

    @Mock
    private ClienteRepository clientes;

    @Mock
    private ProdutoRepository produtos;

    @Mock
    private AuditoriaService auditoria;

    @InjectMocks
    private PedidoService service;

    @Test
    void rejeitaQuantidadeMenorQueUmMesmoSemValidacaoDoControlador() {
        when(clientes.existsById("cliente-1")).thenReturn(true);
        CriarPedidoRequest request = new CriarPedidoRequest("cliente-1", List.of(new ItemPedidoRequest("pizza-1", 0)));

        assertThrows(IllegalArgumentException.class, () -> service.criar(request));

        verify(produtos, never()).findById("pizza-1");
    }

    @Test
    void rejeitaQuantidadeAcimaDoLimiteMesmoSemValidacaoDoControlador() {
        when(clientes.existsById("cliente-1")).thenReturn(true);
        CriarPedidoRequest request = new CriarPedidoRequest("cliente-1", List.of(new ItemPedidoRequest("pizza-1", 100)));

        assertThrows(IllegalArgumentException.class, () -> service.criar(request));

        verify(produtos, never()).findById("pizza-1");
    }
}
