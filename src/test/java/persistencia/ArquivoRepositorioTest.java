package persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import servico.GerenciadorBebidas;
import modelo.Cerveja;
import modelo.Refrigerante;

class ArquivoRepositorioTest {

    @TempDir
    Path pasta; // pasta temporária criada e apagada pelo próprio JUnit

    @Test
    void salvarERecarregarMantemEstoqueEVendas() throws Exception {
        ArquivoRepositorio repositorio = new ArquivoRepositorio(pasta);
        GerenciadorBebidas original = new GerenciadorBebidas(repositorio);
        original.cadastrar(new Cerveja(original.gerarProximoId(), "Cerveja", 10.0, 5, 350, 5.0, "Pilsen"));
        original.cadastrar(new Refrigerante(original.gerarProximoId(), "Guaraná", 8.0, 20, 2000, "Guaraná", true));
        original.vender(1, 2);
        original.salvar();

        GerenciadorBebidas recarregado = new GerenciadorBebidas(repositorio);
        recarregado.carregar();

        assertEquals(2, recarregado.contarBebidas());
        assertEquals(3, recarregado.buscarPorId(1).getQuantidade());
        assertEquals(1, recarregado.getVendas().size());
        assertEquals(original.calcularTotalVendido(), recarregado.calcularTotalVendido(), 0.0001);
    }

    @Test
    void semArquivosCarregaListasVazias() throws Exception {
        ArquivoRepositorio repositorio = new ArquivoRepositorio(pasta);

        assertFalse(repositorio.existemDados());
        assertTrue(repositorio.carregarBebidas().isEmpty());
        assertTrue(repositorio.carregarVendas().isEmpty());
    }

    @Test
    void linhaInvalidaEhIgnoradaSemDerrubarOCarregamento() throws Exception {
        Files.writeString(pasta.resolve("bebidas.csv"),
                "CERVEJA;1;Skol;4.5;45;350;4.7;Pilsen\nlinha-estragada\n");

        ArquivoRepositorio repositorio = new ArquivoRepositorio(pasta);

        assertEquals(1, repositorio.carregarBebidas().size());
    }

    @Test
    void criaAPastaDeDadosSeElaNaoExistir() throws Exception {
        ArquivoRepositorio repositorio = new ArquivoRepositorio(pasta.resolve("dados"));
        GerenciadorBebidas gerenciador = new GerenciadorBebidas(repositorio);
        gerenciador.cadastrar(new Cerveja(1, "Cerveja", 10.0, 5, 350, 5.0, "Pilsen"));

        gerenciador.salvar();

        assertTrue(repositorio.existemDados());
    }
}
