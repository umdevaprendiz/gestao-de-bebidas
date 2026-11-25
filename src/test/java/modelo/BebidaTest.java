package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import excecoes.DadosInvalidosException;
import excecoes.EstoqueInsuficienteException;

class BebidaTest {

    private static final double MARGEM = 0.0001;

    @Test
    void cadaTipoCalculaOImpostoDoSeuJeito() throws Exception {
        ArrayList<Bebida> bebidas = new ArrayList<>();
        bebidas.add(new Cerveja(1, "Cerveja", 10.0, 5, 350, 5.0, "Pilsen"));
        bebidas.add(new Vinho(2, "Vinho", 100.0, 5, 750, 13.0, "Tinto"));
        bebidas.add(new Refrigerante(3, "Refri", 10.0, 5, 2000, "Cola", false));
        bebidas.add(new Refrigerante(4, "Refri Diet", 10.0, 5, 2000, "Cola", true));
        bebidas.add(new Suco(5, "Suco", 10.0, 5, 1000, "Uva", false));
        bebidas.add(new Suco(6, "Suco Natural", 10.0, 5, 1000, "Uva", true));

        double[] esperado = {2.5, 30.0, 2.0, 1.5, 1.0, 0.5};
        int i = 0;
        for (Bebida bebida : bebidas) {
            assertEquals(esperado[i], bebida.calcularImposto(), MARGEM, "imposto de " + bebida.getNome());
            i++;
        }
    }

    @Test
    void precoFinalEPrecoMaisImposto() throws Exception {
        Bebida cerveja = new Cerveja(1, "Cerveja", 10.0, 5, 350, 5.0, "Pilsen");
        assertEquals(12.5, cerveja.getPrecoFinal(), MARGEM);
    }

    @Test
    void somenteBebidasComAlcoolSaoAlcoolicas() throws Exception {
        assertTrue(new Cerveja(1, "Cerveja", 10.0, 5, 350, 5.0, "Pilsen").isAlcoolica());
        assertTrue(new Vinho(2, "Vinho", 50.0, 5, 750, 13.0, "Tinto").isAlcoolica());
        assertFalse(new Refrigerante(3, "Refri", 8.0, 5, 2000, "Cola", false).isAlcoolica());
        assertFalse(new Suco(4, "Suco", 6.0, 5, 1000, "Uva", false).isAlcoolica());
    }

    @Test
    void precoNegativoEhRejeitado() {
        assertThrows(DadosInvalidosException.class,
                () -> new Cerveja(1, "Cerveja", -5.0, 5, 350, 5.0, "Pilsen"));
    }

    @Test
    void nomeVazioEhRejeitado() {
        assertThrows(DadosInvalidosException.class,
                () -> new Cerveja(1, "   ", 5.0, 5, 350, 5.0, "Pilsen"));
    }

    @Test
    void teorAlcoolicoAcimaDeCemEhRejeitado() {
        assertThrows(DadosInvalidosException.class,
                () -> new Vinho(1, "Vinho", 50.0, 5, 750, 150.0, "Tinto"));
    }

    @Test
    void nomeComPontoEVirgulaEhRejeitadoPoisQuebrariaOCsv() {
        assertThrows(DadosInvalidosException.class,
                () -> new Suco(1, "Suco;Doce", 5.0, 5, 1000, "Uva", false));
    }

    @Test
    void retirarMaisQueOEstoqueLancaExcecaoEMantemAQuantidade() throws Exception {
        Cerveja cerveja = new Cerveja(1, "Cerveja", 10.0, 5, 350, 5.0, "Pilsen");

        EstoqueInsuficienteException erro = assertThrows(EstoqueInsuficienteException.class,
                () -> cerveja.removerEstoque(10));

        assertEquals(5, erro.getDisponivel());
        assertEquals(10, erro.getSolicitado());
        assertEquals(5, cerveja.getQuantidade());
    }

    @Test
    void adicionarERetirarEstoqueAtualizaAQuantidade() throws Exception {
        Cerveja cerveja = new Cerveja(1, "Cerveja", 10.0, 5, 350, 5.0, "Pilsen");

        cerveja.removerEstoque(2);
        assertEquals(3, cerveja.getQuantidade());

        cerveja.adicionarEstoque(7);
        assertEquals(10, cerveja.getQuantidade());
    }

    @Test
    void quantidadeZeroOuNegativaEhRejeitada() throws Exception {
        Cerveja cerveja = new Cerveja(1, "Cerveja", 10.0, 5, 350, 5.0, "Pilsen");

        assertThrows(DadosInvalidosException.class, () -> cerveja.adicionarEstoque(0));
        assertThrows(DadosInvalidosException.class, () -> cerveja.removerEstoque(-1));
    }

    @Test
    void mesmoNomeEVolumeSaoIguaisMesmoComIdPrecoEMaiusculasDiferentes() throws Exception {
        Bebida a = new Cerveja(1, "Skol", 4.5, 10, 350, 4.7, "Pilsen");
        Bebida b = new Cerveja(2, "SKOL", 5.0, 3, 350, 4.7, "Pilsen");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void volumeOuNomeDiferenteSaoBebidasDiferentes() throws Exception {
        Bebida lata = new Cerveja(1, "Skol", 4.5, 10, 350, 4.7, "Pilsen");
        Bebida garrafa = new Cerveja(2, "Skol", 9.0, 10, 600, 4.7, "Pilsen");
        Bebida outra = new Cerveja(3, "Brahma", 4.5, 10, 350, 4.8, "Pilsen");

        assertNotEquals(lata, garrafa);
        assertNotEquals(lata, outra);
    }

    @Test
    void equalsRespeitaOContratoComElaMesmaNullEOutroTipo() throws Exception {
        Bebida a = new Cerveja(1, "Skol", 4.5, 10, 350, 4.7, "Pilsen");

        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "Skol");
    }

    @Test
    void hashSetNaoGuardaBebidasRepetidas() throws Exception {
        Set<Bebida> conjunto = new HashSet<>();
        conjunto.add(new Cerveja(1, "Skol", 4.5, 10, 350, 4.7, "Pilsen"));
        conjunto.add(new Cerveja(2, "skol", 5.0, 3, 350, 4.7, "Pilsen"));
        conjunto.add(new Vinho(3, "Casillero", 59.9, 12, 750, 13.5, "Tinto"));

        assertEquals(2, conjunto.size());
    }
}
