package servico;

import excecoes.GestaoBebidasException;
import modelo.Cerveja;
import modelo.Refrigerante;
import modelo.Suco;
import modelo.Vinho;

/**
 * Bebidas de exemplo, carregadas na primeira execução para o sistema não começar vazio.
 */
public class DadosExemplo {

    private DadosExemplo() {
    }

    public static void popular(GerenciadorBebidas g) throws GestaoBebidasException {
        g.cadastrar(new Cerveja(g.gerarProximoId(), "Skol Pilsen", 4.50, 48, 350, 4.7, "Pilsen"));
        g.cadastrar(new Cerveja(g.gerarProximoId(), "Heineken", 6.90, 36, 330, 5.0, "Lager"));
        g.cadastrar(new Cerveja(g.gerarProximoId(), "Colorado Appia", 14.90, 8, 600, 5.5, "Weiss"));
        g.cadastrar(new Vinho(g.gerarProximoId(), "Casillero del Diablo", 59.90, 12, 750, 13.5, "Tinto"));
        g.cadastrar(new Vinho(g.gerarProximoId(), "Miolo Seleção", 39.90, 6, 750, 12.0, "Branco"));
        g.cadastrar(new Refrigerante(g.gerarProximoId(), "Coca-Cola", 8.50, 60, 2000, "Cola", false));
        g.cadastrar(new Refrigerante(g.gerarProximoId(), "Guaraná Zero", 7.20, 30, 2000, "Guaraná", true));
        g.cadastrar(new Suco(g.gerarProximoId(), "Del Valle Laranja", 6.80, 24, 1000, "Laranja", false));
        g.cadastrar(new Suco(g.gerarProximoId(), "Suco Integral de Uva", 12.90, 5, 1000, "Uva", true));
    }
}
