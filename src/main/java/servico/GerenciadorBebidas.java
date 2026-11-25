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

public class GerenciadorBebidas implements OperacoesBebidas {

    private final ArrayList<Bebida> bebidas = new ArrayList<>();
    private final ArrayList<Venda> vendas = new ArrayList<>();
    private final ArquivoRepositorio repositorio;

    public GerenciadorBebidas(ArquivoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void carregar() throws IOException {
        bebidas.clear();
        vendas.clear();
        bebidas.addAll(repositorio.carregarBebidas());
        vendas.addAll(repositorio.carregarVendas());
    }

    @Override
    public void salvar() throws IOException {
        repositorio.salvarBebidas(bebidas);
        repositorio.salvarVendas(vendas);
    }

    @Override
    public int gerarProximoId() {
        int maior = 0;
        for (Bebida bebida : bebidas) {
            if (bebida.getId() > maior) {
                maior = bebida.getId();
            }
        }
        return maior + 1;
    }

    @Override
    public void cadastrar(Bebida nova) throws DadosInvalidosException {
        int posicao = bebidas.indexOf(nova);
        if (posicao >= 0) {
            Bebida existente = bebidas.get(posicao);
            throw new DadosInvalidosException("Já existe '" + existente.getNome() + "' de "
                    + existente.getVolumeMl() + " ml cadastrada (ID " + existente.getId() + ").");
        }
        bebidas.add(nova);
    }

    @Override
    public Bebida buscarPorId(int id) throws BebidaNaoEncontradaException {
        for (Bebida bebida : bebidas) {
            if (bebida.getId() == id) {
                return bebida;
            }
        }
        throw new BebidaNaoEncontradaException(id);
    }

    @Override
    public List<Bebida> buscarPorNome(String trecho) {
        ArrayList<Bebida> encontradas = new ArrayList<>();
        String busca = trecho.toLowerCase();
        for (Bebida bebida : bebidas) {
            if (bebida.getNome().toLowerCase().contains(busca)) {
                encontradas.add(bebida);
            }
        }
        return encontradas;
    }

    @Override
    public List<Bebida> listarOrdenadas() {
        ArrayList<Bebida> copia = new ArrayList<>(bebidas);
        Collections.sort(copia);
        return copia;
    }

    @Override
    public Bebida remover(int id) throws BebidaNaoEncontradaException {
        Bebida bebida = buscarPorId(id);
        bebidas.remove(bebida);
        return bebida;
    }

    @Override
    public void reabastecer(int id, int unidades) throws BebidaNaoEncontradaException, DadosInvalidosException {
        Bebida bebida = buscarPorId(id);
        bebida.adicionarEstoque(unidades);
    }

    @Override
    public Venda vender(int id, int unidades)
            throws BebidaNaoEncontradaException, EstoqueInsuficienteException, DadosInvalidosException {
        Bebida bebida = buscarPorId(id);
        bebida.removerEstoque(unidades);

        double total = bebida.getPrecoFinal() * unidades;
        Venda venda = new Venda(bebida.getId(), bebida.getNome(), unidades, total,
                LocalDateTime.now().withNano(0));
        vendas.add(venda);
        return venda;
    }

    @Override
    public List<Venda> getVendas() {
        return Collections.unmodifiableList(vendas);
    }

    @Override
    public int contarBebidas() {
        return bebidas.size();
    }

    @Override
    public int contarUnidadesEmEstoque() {
        int total = 0;
        for (Bebida bebida : bebidas) {
            total += bebida.getQuantidade();
        }
        return total;
    }

    @Override
    public double calcularValorTotalEstoque() {
        double total = 0;
        for (Bebida bebida : bebidas) {
            total += bebida.getPrecoFinal() * bebida.getQuantidade();
        }
        return total;
    }

    @Override
    public double calcularImpostoTotalEstoque() {
        double total = 0;
        for (Bebida bebida : bebidas) {
            total += bebida.calcularImposto() * bebida.getQuantidade();
        }
        return total;
    }

    @Override
    public List<Bebida> listarEstoqueBaixo(int limite) {
        ArrayList<Bebida> baixas = new ArrayList<>();
        for (Bebida bebida : bebidas) {
            if (bebida.isEstoqueBaixo(limite)) {
                baixas.add(bebida);
            }
        }
        return baixas;
    }

    @Override
    public double calcularTotalVendido() {
        double total = 0;
        for (Venda venda : vendas) {
            total += venda.getValorTotal();
        }
        return total;
    }
}
