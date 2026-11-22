package persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import excecoes.DadosInvalidosException;
import modelo.Bebida;
import modelo.Suco;
import modelo.Vinho;

class BebidaFactoryTest {

    @Test
    void linhaGeradaPorUmaBebidaVoltaIgual() throws Exception {
        Bebida original = new Vinho(7, "Vinho do Porto", 89.9, 3, 750, 19.5, "Tinto");

        Bebida lida = BebidaFactory.criarDeLinha(original.paraLinhaCsv());

        assertInstanceOf(Vinho.class, lida);
        assertEquals(original.paraLinhaCsv(), lida.paraLinhaCsv());
    }

    @Test
    void linhaDeSucoNaturalEhLidaCorretamente() throws Exception {
        Bebida bebida = BebidaFactory.criarDeLinha("SUCO;3;Suco de Uva;5.5;10;1000;Uva;true");

        assertInstanceOf(Suco.class, bebida);
        assertTrue(((Suco) bebida).isNatural());
    }

    @Test
    void tipoDesconhecidoEhRejeitado() {
        assertThrows(DadosInvalidosException.class,
                () -> BebidaFactory.criarDeLinha("XPTO;1;Algo;1.0;1;100;a;b"));
    }

    @Test
    void numeroInvalidoEhRejeitado() {
        assertThrows(DadosInvalidosException.class,
                () -> BebidaFactory.criarDeLinha("CERVEJA;1;Nome;abc;1;100;5.0;IPA"));
    }

    @Test
    void linhaComCamposAMenosEhRejeitada() {
        assertThrows(DadosInvalidosException.class,
                () -> BebidaFactory.criarDeLinha("CERVEJA;1;Nome"));
    }
}
