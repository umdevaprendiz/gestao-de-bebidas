package servico;

import java.io.IOException;
import java.util.List;

import excecoes.BebidaNaoEncontradaException;
import excecoes.DadosInvalidosException;
import excecoes.EstoqueInsuficienteException;
import modelo.Bebida;
import modelo.Venda;

public interface OperacoesBebidas {

    int gerarProximoId();

    void cadastrar(Bebida nova) throws DadosInvalidosException;

    Bebida buscarPorId(int id) throws BebidaNaoEncontradaException;

    List<Bebida> buscarPorNome(String trecho);

    List<Bebida> listarOrdenadas();

    Bebida remover(int id) throws BebidaNaoEncontradaException;

    void reabastecer(int id, int unidades) throws BebidaNaoEncontradaException, DadosInvalidosException;

    Venda vender(int id, int unidades)
            throws BebidaNaoEncontradaException, EstoqueInsuficienteException, DadosInvalidosException;

    List<Venda> getVendas();

    int contarBebidas();

    int contarUnidadesEmEstoque();

    double calcularValorTotalEstoque();

    double calcularImpostoTotalEstoque();

    List<Bebida> listarEstoqueBaixo(int limite);

    double calcularTotalVendido();

    void carregar() throws IOException;

    void salvar() throws IOException;
}
