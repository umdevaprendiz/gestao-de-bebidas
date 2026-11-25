package servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import excecoes.BebidaNaoEncontradaException;
import excecoes.DadosInvalidosException;
import excecoes.EstoqueInsuficienteException;
import modelo.Cerveja;
import modelo.Suco;
import modelo.Venda;
import persistencia.ArquivoRepositorio;

class GerenciadorBebidasTest {

    private static final double MARGEM = 0.0001;

    @TempDir
    Path pasta;

    private OperacoesBebidas gerenciador;

    @BeforeEach
    void preparar() throws Exception {
        gerenciador = new GerenciadorBebidas(new ArquivoRepositorio(pasta));
        gerenciador.cadastrar(new Cerveja(gerenciador.gerarProximoId(), "Cerveja", 10.0, 5, 350, 5.0, "Pilsen"));
    }

    @Test
    void vendaCobraPrecoComImpostoEBaixaOEstoque() throws Exception {
        Venda venda = gerenciador.vender(1, 2);

        assertEquals(25.0, venda.getValorTotal(), MARGEM);
        assertEquals(3, gerenciador.buscarPorId(1).getQuantidade());
        assertEquals(1, gerenciador.getVendas().size());
        assertEquals(25.0, gerenciador.calcularTotalVendido(), MARGEM);
    }

    @Test
    void vendaAcimaDoEstoqueEhBloqueadaESemRegistroNoHistorico() {
        assertThrows(EstoqueInsuficienteException.class, () -> gerenciador.vender(1, 99));

        assertTrue(gerenciador.getVendas().isEmpty());
    }

    @Test
    void naoPermiteCadastrarMesmoNomeEVolumeDuasVezes() {
        assertThrows(DadosInvalidosException.class, () -> gerenciador.cadastrar(
                new Cerveja(gerenciador.gerarProximoId(), "cerveja", 9.0, 1, 350, 5.0, "IPA")));
    }

    @Test
    void mesmoNomeComVolumeDiferenteEhPermitido() throws Exception {
        gerenciador.cadastrar(new Cerveja(gerenciador.gerarProximoId(), "Cerveja", 18.0, 1, 600, 5.0, "Pilsen"));

        assertEquals(2, gerenciador.contarBebidas());
    }

    @Test
    void buscaPorIdInexistenteLancaExcecao() {
        assertThrows(BebidaNaoEncontradaException.class, () -> gerenciador.buscarPorId(99));
    }

    @Test
    void buscaPorNomeIgnoraMaiusculasEMinusculas() {
        assertEquals(1, gerenciador.buscarPorNome("CERVE").size());
        assertTrue(gerenciador.buscarPorNome("xyz").isEmpty());
    }

    @Test
    void proximoIdEhOMaiorIdMaisUm() throws Exception {
        gerenciador.cadastrar(new Suco(gerenciador.gerarProximoId(), "Suco", 5.0, 5, 1000, "Uva", false));

        assertEquals(3, gerenciador.gerarProximoId());
    }

    @Test
    void reabastecerSomaNoEstoque() throws Exception {
        gerenciador.reabastecer(1, 10);

        assertEquals(15, gerenciador.buscarPorId(1).getQuantidade());
    }

    @Test
    void relatoriosSomamValorEImpostoDoEstoque() {
        assertEquals(62.5, gerenciador.calcularValorTotalEstoque(), MARGEM);
        assertEquals(12.5, gerenciador.calcularImpostoTotalEstoque(), MARGEM);
        assertEquals(5, gerenciador.contarUnidadesEmEstoque());
    }

    @Test
    void listaDeEstoqueBaixoTrazSomenteQuemEstaAbaixoDoLimite() {
        assertEquals(1, gerenciador.listarEstoqueBaixo(10).size());
        assertTrue(gerenciador.listarEstoqueBaixo(5).isEmpty());
    }

    @Test
    void removerTiraABebidaDaLista() throws Exception {
        gerenciador.remover(1);

        assertEquals(0, gerenciador.contarBebidas());
        assertThrows(BebidaNaoEncontradaException.class, () -> gerenciador.remover(1));
    }
}
