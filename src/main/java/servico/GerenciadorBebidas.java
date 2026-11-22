package servico;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import excecoes.BebidaNaoEncontradaException;
import excecoes.DadosInvalidosException;
import excecoes.EstoqueInsuficienteException;
import modelo.Bebida;
import modelo.Venda;
import persistencia.ArquivoRepositorio;

/**
 * Classe de serviço: concentra as regras do sistema (cadastrar, vender, reabastecer...).
 * O Main só conversa com o usuário; quem decide e calcula é esta classe.
 *
 * Usa uma COMPOSIÇÃO: o gerenciador "tem" uma lista de Bebidas, uma lista de Vendas
 * e um repositório de arquivos.
 */
public class GerenciadorBebidas {

    private final ArrayList<Bebida> bebidas = new ArrayList<>();
    private final ArrayList<Venda> vendas = new ArrayList<>();
    private final ArquivoRepositorio repositorio;

    public GerenciadorBebidas(ArquivoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    // ---------------------------- Persistência ----------------------------

    public void carregar() throws IOException {
        bebidas.clear();
        vendas.clear();
        bebidas.addAll(repositorio.carregarBebidas());
        vendas.addAll(repositorio.carregarVendas());
    }

    public void salvar() throws IOException {
        repositorio.salvarBebidas(bebidas);
        repositorio.salvarVendas(vendas);
    }

    // ---------------------------- Cadastro e consulta ----------------------------

    /** O próximo ID é o maior ID existente + 1. */
    public int gerarProximoId() {
        int maior = 0;
        for (Bebida bebida : bebidas) {
            if (bebida.getId() > maior) {
                maior = bebida.getId();
            }
        }
        return maior + 1;
    }

    public void cadastrar(Bebida nova) throws DadosInvalidosException {
        for (Bebida existente : bebidas) {
            boolean mesmoNome = existente.getNome().equalsIgnoreCase(nova.getNome());
            boolean mesmoVolume = existente.getVolumeMl() == nova.getVolumeMl();
            if (mesmoNome && mesmoVolume) {
                throw new DadosInvalidosException("Já existe '" + existente.getNome() + "' de "
                        + existente.getVolumeMl() + " ml cadastrada (ID " + existente.getId() + ").");
            }
        }
        bebidas.add(nova);
    }

    public Bebida buscarPorId(int id) throws BebidaNaoEncontradaException {
        for (Bebida bebida : bebidas) {
            if (bebida.getId() == id) {
                return bebida;
            }
        }
        throw new BebidaNaoEncontradaException(id);
    }

    public ArrayList<Bebida> buscarPorNome(String trecho) {
        ArrayList<Bebida> encontradas = new ArrayList<>();
        String busca = trecho.toLowerCase();
        for (Bebida bebida : bebidas) {
            if (bebida.getNome().toLowerCase().contains(busca)) {
                encontradas.add(bebida);
            }
        }
        return encontradas;
    }

    /** Devolve uma CÓPIA ordenada por nome (usa o compareTo da classe Bebida). */
    public ArrayList<Bebida> listarOrdenadas() {
        ArrayList<Bebida> copia = new ArrayList<>(bebidas);
        Collections.sort(copia);
        return copia;
    }

    public Bebida remover(int id) throws BebidaNaoEncontradaException {
        Bebida bebida = buscarPorId(id);
        bebidas.remove(bebida);
        return bebida;
    }

    // ---------------------------- Estoque e vendas ----------------------------

    public void reabastecer(int id, int unidades) throws BebidaNaoEncontradaException, DadosInvalidosException {
        Bebida bebida = buscarPorId(id);
        bebida.adicionarEstoque(unidades);
    }

    public Venda vender(int id, int unidades)
            throws BebidaNaoEncontradaException, EstoqueInsuficienteException, DadosInvalidosException {
        Bebida bebida = buscarPorId(id);
        bebida.removerEstoque(unidades); // pode lançar EstoqueInsuficienteException

        double total = bebida.getPrecoFinal() * unidades;
        Venda venda = new Venda(bebida.getId(), bebida.getNome(), unidades, total,
                LocalDateTime.now().withNano(0));
        vendas.add(venda);
        return venda;
    }

    public List<Venda> getVendas() {
        return Collections.unmodifiableList(vendas); // quem chama só pode ler
    }

    // ---------------------------- Relatórios ----------------------------

    public int contarBebidas() {
        return bebidas.size();
    }

    public int contarUnidadesEmEstoque() {
        int total = 0;
        for (Bebida bebida : bebidas) {
            total += bebida.getQuantidade();
        }
        return total;
    }

    /** Valor do estoque a preço de venda (com imposto). */
    public double calcularValorTotalEstoque() {
        double total = 0;
        for (Bebida bebida : bebidas) {
            total += bebida.getPrecoFinal() * bebida.getQuantidade();
        }
        return total;
    }

    /**
     * Imposto embutido no estoque. POLIMORFISMO: calcularImposto() é chamado em cada
     * Bebida e cada tipo (Cerveja, Vinho, Refrigerante, Suco) calcula do seu jeito.
     */
    public double calcularImpostoTotalEstoque() {
        double total = 0;
        for (Bebida bebida : bebidas) {
            total += bebida.calcularImposto() * bebida.getQuantidade();
        }
        return total;
    }

    public ArrayList<Bebida> listarEstoqueBaixo(int limite) {
        ArrayList<Bebida> baixas = new ArrayList<>();
        for (Bebida bebida : bebidas) {
            if (bebida.isEstoqueBaixo(limite)) {
                baixas.add(bebida);
            }
        }
        return baixas;
    }

    public double calcularTotalVendido() {
        double total = 0;
        for (Venda venda : vendas) {
            total += venda.getValorTotal();
        }
        return total;
    }
}
