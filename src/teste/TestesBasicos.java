package teste;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import excecoes.BebidaNaoEncontradaException;
import excecoes.DadosInvalidosException;
import excecoes.EstoqueInsuficienteException;
import modelo.Bebida;
import modelo.Cerveja;
import modelo.Refrigerante;
import modelo.Suco;
import modelo.Venda;
import modelo.Vinho;
import persistencia.ArquivoRepositorio;
import persistencia.BebidaFactory;
import servico.GerenciadorBebidas;

/**
 * Testes automáticos em Java puro (sem JUnit, para não precisar de nenhuma biblioteca).
 * Cada teste chama verificar(...); no final mostramos quantos passaram.
 * Para rodar: testar.bat
 */
public class TestesBasicos {

    private static int total = 0;
    private static int falhas = 0;

    public static void main(String[] args) throws Exception {
        testarImpostoPolimorfico();
        testarValidacaoDeDados();
        testarEstoqueInsuficiente();
        testarConversaoCsv();
        testarVenda();
        testarBuscaInexistente();
        testarPersistenciaEmArquivo();

        System.out.println();
        System.out.println((total - falhas) + " de " + total + " verificações passaram.");
        if (falhas > 0) {
            System.out.println("HÁ FALHAS!");
            System.exit(1);
        }
        System.out.println("Todos os testes passaram.");
    }

    // ---------------------------- auxiliares ----------------------------

    private static void verificar(boolean condicao, String descricao) {
        total++;
        if (condicao) {
            System.out.println("  [OK]   " + descricao);
        } else {
            falhas++;
            System.out.println("  [FALHA] " + descricao);
        }
    }

    private static boolean iguais(double a, double b) {
        return Math.abs(a - b) < 0.0001;
    }

    // ---------------------------- testes ----------------------------

    private static void testarImpostoPolimorfico() throws Exception {
        System.out.println("Imposto (polimorfismo):");
        ArrayList<Bebida> lista = new ArrayList<>();
        lista.add(new Cerveja(1, "Cerveja", 10.0, 5, 350, 5.0, "Pilsen"));          // 25%
        lista.add(new Vinho(2, "Vinho", 100.0, 5, 750, 13.0, "Tinto"));             // 30%
        lista.add(new Refrigerante(3, "Refri", 10.0, 5, 2000, "Cola", false));      // 20%
        lista.add(new Refrigerante(4, "Refri Diet", 10.0, 5, 2000, "Cola", true));  // 15%
        lista.add(new Suco(5, "Suco", 10.0, 5, 1000, "Uva", false));                // 10%
        lista.add(new Suco(6, "Suco Natural", 10.0, 5, 1000, "Uva", true));         // 5%

        double[] esperado = {2.5, 30.0, 2.0, 1.5, 1.0, 0.5};
        int i = 0;
        for (Bebida bebida : lista) {
            verificar(iguais(esperado[i], bebida.calcularImposto()), "imposto de " + bebida.getNome());
            i++;
        }
        verificar(iguais(12.5, lista.get(0).getPrecoFinal()), "preço final da cerveja = 12.50");
        verificar(lista.get(0).isAlcoolica() && !lista.get(2).isAlcoolica(), "isAlcoolica() por tipo");
    }

    private static void testarValidacaoDeDados() {
        System.out.println("Validação de dados:");

        try {
            new Cerveja(1, "Teste", -5.0, 5, 350, 5.0, "Pilsen");
            verificar(false, "preço negativo deveria lançar exceção");
        } catch (DadosInvalidosException e) {
            verificar(true, "preço negativo é rejeitado");
        }

        try {
            new Cerveja(1, "   ", 5.0, 5, 350, 5.0, "Pilsen");
            verificar(false, "nome vazio deveria lançar exceção");
        } catch (DadosInvalidosException e) {
            verificar(true, "nome vazio é rejeitado");
        }

        try {
            new Vinho(1, "Teste", 5.0, 5, 750, 150.0, "Tinto");
            verificar(false, "teor acima de 100% deveria lançar exceção");
        } catch (DadosInvalidosException e) {
            verificar(true, "teor alcoólico acima de 100% é rejeitado");
        }

        try {
            new Suco(1, "Suco;Doce", 5.0, 5, 1000, "Uva", false);
            verificar(false, "';' no nome deveria lançar exceção");
        } catch (DadosInvalidosException e) {
            verificar(true, "nome com ';' é rejeitado (quebraria o CSV)");
        }
    }

    private static void testarEstoqueInsuficiente() throws Exception {
        System.out.println("Estoque insuficiente:");
        Cerveja cerveja = new Cerveja(1, "Cerveja", 10.0, 5, 350, 5.0, "Pilsen");

        try {
            cerveja.removerEstoque(10);
            verificar(false, "retirar 10 de 5 deveria lançar exceção");
        } catch (EstoqueInsuficienteException e) {
            verificar(true, "EstoqueInsuficienteException lançada");
            verificar(e.getDisponivel() == 5 && e.getSolicitado() == 10, "exceção informa disponível e solicitado");
        }
        verificar(cerveja.getQuantidade() == 5, "estoque continua 5 após a falha");

        cerveja.removerEstoque(2);
        verificar(cerveja.getQuantidade() == 3, "retirar 2 de 5 deixa 3");
        cerveja.adicionarEstoque(7);
        verificar(cerveja.getQuantidade() == 10, "adicionar 7 a 3 deixa 10");
    }

    private static void testarConversaoCsv() throws Exception {
        System.out.println("Conversão para/de CSV:");
        Bebida original = new Vinho(7, "Vinho do Porto", 89.9, 3, 750, 19.5, "Tinto");
        Bebida lida = BebidaFactory.criarDeLinha(original.paraLinhaCsv());

        verificar(lida instanceof Vinho, "a linha gera um objeto Vinho");
        verificar(lida.paraLinhaCsv().equals(original.paraLinhaCsv()), "ida e volta mantém todos os dados");

        Bebida suco = BebidaFactory.criarDeLinha("SUCO;3;Suco de Uva;5.5;10;1000;Uva;true");
        verificar(suco instanceof Suco && ((Suco) suco).isNatural(), "linha de suco natural é lida corretamente");

        try {
            BebidaFactory.criarDeLinha("XPTO;1;Algo;1.0;1;100;a;b");
            verificar(false, "tipo desconhecido deveria lançar exceção");
        } catch (DadosInvalidosException e) {
            verificar(true, "tipo desconhecido é rejeitado");
        }

        try {
            BebidaFactory.criarDeLinha("CERVEJA;1;Nome;abc;1;100;5.0;IPA");
            verificar(false, "preço não numérico deveria lançar exceção");
        } catch (DadosInvalidosException e) {
            verificar(true, "número inválido é rejeitado");
        }
    }

    private static void testarVenda() throws Exception {
        System.out.println("Venda:");
        GerenciadorBebidas g = novoGerenciador();
        g.cadastrar(new Cerveja(g.gerarProximoId(), "Cerveja", 10.0, 5, 350, 5.0, "Pilsen"));

        Venda venda = g.vender(1, 2);
        verificar(iguais(25.0, venda.getValorTotal()), "2 x R$ 12,50 (com imposto) = R$ 25,00");
        verificar(g.buscarPorId(1).getQuantidade() == 3, "estoque cai de 5 para 3");
        verificar(g.getVendas().size() == 1, "a venda entra no histórico");
        verificar(iguais(25.0, g.calcularTotalVendido()), "total vendido = 25.00");

        try {
            g.vender(1, 99);
            verificar(false, "vender acima do estoque deveria lançar exceção");
        } catch (EstoqueInsuficienteException e) {
            verificar(true, "vender acima do estoque é bloqueado");
        }
        verificar(g.getVendas().size() == 1, "venda recusada não entra no histórico");

        try {
            g.cadastrar(new Cerveja(g.gerarProximoId(), "cerveja", 9.0, 1, 350, 5.0, "IPA"));
            verificar(false, "bebida duplicada deveria lançar exceção");
        } catch (DadosInvalidosException e) {
            verificar(true, "mesmo nome + volume não pode ser cadastrado duas vezes");
        }
    }

    private static void testarBuscaInexistente() throws Exception {
        System.out.println("Busca:");
        GerenciadorBebidas g = novoGerenciador();
        g.cadastrar(new Suco(g.gerarProximoId(), "Suco de Laranja", 5.0, 5, 1000, "Laranja", false));

        try {
            g.buscarPorId(99);
            verificar(false, "ID inexistente deveria lançar exceção");
        } catch (BebidaNaoEncontradaException e) {
            verificar(true, "ID inexistente lança BebidaNaoEncontradaException");
        }
        verificar(g.buscarPorNome("LARANJA").size() == 1, "busca por nome ignora maiúsculas/minúsculas");
        verificar(g.buscarPorNome("xyz").isEmpty(), "busca sem resultado devolve lista vazia");
    }

    private static void testarPersistenciaEmArquivo() throws Exception {
        System.out.println("Persistência em arquivo:");
        Path pasta = Files.createTempDirectory("bebidas-teste");
        try {
            ArquivoRepositorio repositorio = new ArquivoRepositorio(pasta);
            GerenciadorBebidas g1 = new GerenciadorBebidas(repositorio);
            g1.cadastrar(new Cerveja(g1.gerarProximoId(), "Cerveja", 10.0, 5, 350, 5.0, "Pilsen"));
            g1.cadastrar(new Refrigerante(g1.gerarProximoId(), "Guaraná", 8.0, 20, 2000, "Guaraná", true));
            g1.vender(1, 2);
            g1.salvar();

            GerenciadorBebidas g2 = new GerenciadorBebidas(repositorio);
            g2.carregar();
            verificar(g2.contarBebidas() == 2, "2 bebidas recarregadas do arquivo");
            verificar(g2.buscarPorId(1).getQuantidade() == 3, "estoque recarregado corretamente");
            verificar(g2.getVendas().size() == 1, "histórico de vendas recarregado");
            verificar(iguais(g1.calcularTotalVendido(), g2.calcularTotalVendido()), "total vendido igual após recarregar");
        } finally {
            // limpa os arquivos temporários, aconteça o que acontecer
            limparPasta(pasta);
        }
    }

    private static GerenciadorBebidas novoGerenciador() {
        // pasta fictícia: estes testes não gravam nada em disco
        return new GerenciadorBebidas(new ArquivoRepositorio(Path.of("pasta-que-nao-e-usada")));
    }

    private static void limparPasta(Path pasta) throws IOException {
        Files.deleteIfExists(pasta.resolve("bebidas.csv"));
        Files.deleteIfExists(pasta.resolve("vendas.csv"));
        Files.deleteIfExists(pasta);
    }
}
