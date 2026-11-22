package app;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import excecoes.DadosInvalidosException;
import excecoes.EstoqueInsuficienteException;
import excecoes.GestaoBebidasException;
import modelo.Bebida;
import modelo.Cerveja;
import modelo.Refrigerante;
import modelo.Suco;
import modelo.Venda;
import modelo.Vinho;
import persistencia.ArquivoRepositorio;
import servico.DadosExemplo;
import servico.GerenciadorBebidas;

/**
 * Menu de console do Sistema de Gerenciamento de Bebidas.
 * Esta classe só conversa com o usuário; as regras ficam em GerenciadorBebidas.
 */
public class Main {

    private static final int LIMITE_ESTOQUE_BAIXO = 10;

    private static GerenciadorBebidas gerenciador;

    public static void main(String[] args) {
        ArquivoRepositorio repositorio = new ArquivoRepositorio(Paths.get("dados"));
        gerenciador = new GerenciadorBebidas(repositorio);

        carregarDados(repositorio);

        try {
            executarMenu();
        } catch (NoSuchElementException e) {
            // acontece quando a entrada do teclado é encerrada (Ctrl+Z / Ctrl+D)
            System.out.println("\nEntrada encerrada. Até logo!");
        }
    }

    // ------------------------------------------------------------------
    // Inicialização
    // ------------------------------------------------------------------

    private static void carregarDados(ArquivoRepositorio repositorio) {
        try {
            if (repositorio.existemDados()) {
                gerenciador.carregar();
                System.out.println("Dados carregados: " + gerenciador.contarBebidas() + " bebida(s).");
            } else {
                DadosExemplo.popular(gerenciador);
                gerenciador.salvar();
                System.out.println("Primeira execução: carreguei " + gerenciador.contarBebidas()
                        + " bebidas de exemplo.");
            }
        } catch (IOException e) {
            System.out.println("Não foi possível ler/gravar os arquivos de dados: " + e.getMessage());
        } catch (GestaoBebidasException e) {
            System.out.println("Erro ao preparar os dados: " + e.getMessage());
        }
    }

    private static void salvarDados() {
        try {
            gerenciador.salvar();
        } catch (IOException e) {
            System.out.println("ATENÇÃO: não foi possível salvar os dados: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Menu
    // ------------------------------------------------------------------

    private static void exibirMenu() {
        System.out.println();
        System.out.println("=========================================");
        System.out.println("   SISTEMA DE GERENCIAMENTO DE BEBIDAS");
        System.out.println("=========================================");
        System.out.println(" 1 - Cadastrar bebida");
        System.out.println(" 2 - Listar bebidas");
        System.out.println(" 3 - Buscar bebida por nome");
        System.out.println(" 4 - Reabastecer estoque");
        System.out.println(" 5 - Registrar venda");
        System.out.println(" 6 - Remover bebida");
        System.out.println(" 7 - Relatório do estoque");
        System.out.println(" 8 - Histórico de vendas");
        System.out.println(" 0 - Sair");
        System.out.println("-----------------------------------------");
    }

    private static void executarMenu() {
        int opcao;
        do {
            exibirMenu();
            opcao = Entrada.lerInt("Escolha uma opção: ");

            try {
                switch (opcao) {
                    case 1:
                        cadastrarBebida();
                        break;
                    case 2:
                        listarBebidas();
                        break;
                    case 3:
                        buscarBebidas();
                        break;
                    case 4:
                        reabastecerEstoque();
                        break;
                    case 5:
                        registrarVenda();
                        break;
                    case 6:
                        removerBebida();
                        break;
                    case 7:
                        exibirRelatorio();
                        break;
                    case 8:
                        exibirHistoricoVendas();
                        break;
                    case 0:
                        System.out.println("Dados salvos na pasta 'dados'. Até logo!");
                        break;
                    default:
                        System.out.println("Opção inexistente. Tente novamente.");
                }
            } catch (EstoqueInsuficienteException e) {
                // captura mais específica primeiro...
                System.out.println("Venda não realizada! " + e.getMessage());
            } catch (GestaoBebidasException e) {
                // ...e a exceção "mãe" captura todas as outras do sistema
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    // ------------------------------------------------------------------
    // Opções do menu
    // ------------------------------------------------------------------

    private static void cadastrarBebida() throws GestaoBebidasException {
        System.out.println("\n--- Cadastrar bebida ---");
        System.out.println("1) Cerveja   2) Vinho   3) Refrigerante   4) Suco");
        int tipo = Entrada.lerInt("Tipo: ");
        if (tipo < 1 || tipo > 4) {
            throw new DadosInvalidosException("Tipo de bebida inválido.");
        }

        // dados comuns a todas as bebidas
        String nome = Entrada.lerTexto("Nome: ");
        double preco = Entrada.lerDouble("Preço sem imposto (R$): ");
        int quantidade = Entrada.lerInt("Quantidade em estoque: ");
        int volumeMl = Entrada.lerInt("Volume (ml): ");
        int id = gerenciador.gerarProximoId();

        // dados específicos de cada tipo
        Bebida nova;
        switch (tipo) {
            case 1: {
                double teor = Entrada.lerDouble("Teor alcoólico (%): ");
                String estilo = Entrada.lerTexto("Estilo (ex.: Pilsen, IPA): ");
                nova = new Cerveja(id, nome, preco, quantidade, volumeMl, teor, estilo);
                break;
            }
            case 2: {
                double teor = Entrada.lerDouble("Teor alcoólico (%): ");
                String tipoVinho = Entrada.lerTexto("Tipo (Tinto, Branco, Rosé): ");
                nova = new Vinho(id, nome, preco, quantidade, volumeMl, teor, tipoVinho);
                break;
            }
            case 3: {
                String sabor = Entrada.lerTexto("Sabor: ");
                boolean diet = Entrada.lerSimNao("É diet/zero?");
                nova = new Refrigerante(id, nome, preco, quantidade, volumeMl, sabor, diet);
                break;
            }
            default: {
                String fruta = Entrada.lerTexto("Fruta: ");
                boolean natural = Entrada.lerSimNao("É 100% natural?");
                nova = new Suco(id, nome, preco, quantidade, volumeMl, fruta, natural);
                break;
            }
        }

        gerenciador.cadastrar(nova);
        salvarDados();
        System.out.println("Bebida cadastrada com sucesso! (ID " + id + ")");
    }

    private static void listarBebidas() {
        System.out.println("\n--- Bebidas cadastradas ---");
        ArrayList<Bebida> lista = gerenciador.listarOrdenadas();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma bebida cadastrada.");
            return;
        }
        // for-each + POLIMORFISMO: println chama o toString() de cada tipo de bebida
        for (Bebida bebida : lista) {
            System.out.println(bebida);
        }
    }

    private static void buscarBebidas() {
        System.out.println("\n--- Buscar bebida ---");
        String trecho = Entrada.lerTexto("Digite parte do nome: ");
        ArrayList<Bebida> encontradas = gerenciador.buscarPorNome(trecho);
        if (encontradas.isEmpty()) {
            System.out.println("Nenhuma bebida encontrada para '" + trecho + "'.");
            return;
        }
        for (Bebida bebida : encontradas) {
            System.out.println(bebida);
        }
    }

    private static void reabastecerEstoque() throws GestaoBebidasException {
        System.out.println("\n--- Reabastecer estoque ---");
        listarBebidas();
        int id = Entrada.lerInt("ID da bebida: ");
        int unidades = Entrada.lerInt("Unidades a adicionar: ");

        gerenciador.reabastecer(id, unidades);
        salvarDados();

        Bebida bebida = gerenciador.buscarPorId(id);
        System.out.println("Estoque atualizado: " + bebida.getNome() + " agora tem "
                + bebida.getQuantidade() + " unidade(s).");
    }

    private static void registrarVenda() throws GestaoBebidasException {
        System.out.println("\n--- Registrar venda ---");
        listarBebidas();
        int id = Entrada.lerInt("ID da bebida: ");
        Bebida bebida = gerenciador.buscarPorId(id);

        // isAlcoolica() é polimórfico: cada tipo responde de acordo com a sua classe
        if (bebida.isAlcoolica() && !Entrada.lerSimNao("Bebida alcoólica. O cliente tem 18 anos ou mais?")) {
            System.out.println("Venda cancelada: proibida para menores de 18 anos.");
            return;
        }

        int unidades = Entrada.lerInt("Quantidade: ");
        Venda venda = gerenciador.vender(id, unidades);
        salvarDados();

        System.out.println("Venda registrada!");
        System.out.println(venda);
        if (bebida.isEstoqueBaixo(LIMITE_ESTOQUE_BAIXO)) {
            System.out.println("Atenção: estoque baixo de '" + bebida.getNome() + "' ("
                    + bebida.getQuantidade() + " un.).");
        }
    }

    private static void removerBebida() throws GestaoBebidasException {
        System.out.println("\n--- Remover bebida ---");
        listarBebidas();
        int id = Entrada.lerInt("ID da bebida: ");
        Bebida bebida = gerenciador.buscarPorId(id);

        if (!Entrada.lerSimNao("Remover '" + bebida.getNome() + "' do sistema?")) {
            System.out.println("Operação cancelada.");
            return;
        }
        gerenciador.remover(id);
        salvarDados();
        System.out.println("Bebida removida.");
    }

    private static void exibirRelatorio() {
        System.out.println("\n--- Relatório do estoque ---");
        System.out.println("Tipos de bebida cadastrados : " + gerenciador.contarBebidas());
        System.out.println("Unidades em estoque         : " + gerenciador.contarUnidadesEmEstoque());
        System.out.printf("Valor do estoque (c/ imposto): R$ %.2f%n", gerenciador.calcularValorTotalEstoque());
        System.out.printf("Imposto embutido no estoque  : R$ %.2f%n", gerenciador.calcularImpostoTotalEstoque());
        System.out.printf("Total já vendido             : R$ %.2f%n", gerenciador.calcularTotalVendido());

        System.out.println("\nBebidas com estoque baixo (menos de " + LIMITE_ESTOQUE_BAIXO + " unidades):");
        ArrayList<Bebida> baixas = gerenciador.listarEstoqueBaixo(LIMITE_ESTOQUE_BAIXO);
        if (baixas.isEmpty()) {
            System.out.println("  Nenhuma. Tudo certo!");
        }
        for (Bebida bebida : baixas) {
            System.out.println("  - " + bebida.getNome() + " (" + bebida.getQuantidade() + " un.)");
        }
    }

    private static void exibirHistoricoVendas() {
        System.out.println("\n--- Histórico de vendas ---");
        if (gerenciador.getVendas().isEmpty()) {
            System.out.println("Nenhuma venda registrada ainda.");
            return;
        }
        for (Venda venda : gerenciador.getVendas()) {
            System.out.println(venda);
        }
        System.out.printf("Total vendido: R$ %.2f%n", gerenciador.calcularTotalVendido());
    }
}
