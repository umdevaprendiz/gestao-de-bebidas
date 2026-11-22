package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import excecoes.DadosInvalidosException;
import modelo.Bebida;
import modelo.Persistivel;
import modelo.Venda;

/**
 * Lê e grava os dados em arquivos de texto (CSV) dentro de uma pasta.
 *   <pasta>/bebidas.csv  -> estoque
 *   <pasta>/vendas.csv   -> histórico de vendas
 */
public class ArquivoRepositorio {

    private final Path arquivoBebidas;
    private final Path arquivoVendas;

    public ArquivoRepositorio(Path pasta) {
        this.arquivoBebidas = pasta.resolve("bebidas.csv");
        this.arquivoVendas = pasta.resolve("vendas.csv");
    }

    public boolean existemDados() {
        return Files.exists(arquivoBebidas);
    }

    // ---------------------------- GRAVAÇÃO ----------------------------

    public void salvarBebidas(List<Bebida> bebidas) throws IOException {
        salvar(arquivoBebidas, bebidas);
    }

    public void salvarVendas(List<Venda> vendas) throws IOException {
        salvar(arquivoVendas, vendas);
    }

    /**
     * Grava qualquer lista de Persistivel (Bebida, Venda...) usando INTERFACE + POLIMORFISMO:
     * o método só precisa saber chamar paraLinhaCsv().
     */
    private void salvar(Path arquivo, List<? extends Persistivel> itens) throws IOException {
        Files.createDirectories(arquivo.toAbsolutePath().getParent());

        // try-with-resources: o arquivo é fechado automaticamente, mesmo se der erro
        try (BufferedWriter escritor = Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
            for (Persistivel item : itens) {
                escritor.write(item.paraLinhaCsv());
                escritor.newLine();
            }
        }
    }

    // ---------------------------- LEITURA ----------------------------

    public ArrayList<Bebida> carregarBebidas() throws IOException {
        ArrayList<Bebida> bebidas = new ArrayList<>();
        if (!Files.exists(arquivoBebidas)) {
            return bebidas;
        }

        try (BufferedReader leitor = Files.newBufferedReader(arquivoBebidas, StandardCharsets.UTF_8)) {
            String linha;
            int numeroLinha = 0;
            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                if (linha.trim().isEmpty()) {
                    continue;
                }
                try {
                    bebidas.add(BebidaFactory.criarDeLinha(linha));
                } catch (DadosInvalidosException e) {
                    // Uma linha ruim não derruba o programa: avisa e segue para a próxima.
                    System.out.println("Aviso: linha " + numeroLinha + " de bebidas.csv ignorada ("
                            + e.getMessage() + ").");
                }
            }
        }
        return bebidas;
    }

    public ArrayList<Venda> carregarVendas() throws IOException {
        ArrayList<Venda> vendas = new ArrayList<>();
        if (!Files.exists(arquivoVendas)) {
            return vendas;
        }

        try (BufferedReader leitor = Files.newBufferedReader(arquivoVendas, StandardCharsets.UTF_8)) {
            String linha;
            int numeroLinha = 0;
            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                if (linha.trim().isEmpty()) {
                    continue;
                }
                try {
                    vendas.add(Venda.deLinhaCsv(linha));
                } catch (DadosInvalidosException e) {
                    System.out.println("Aviso: linha " + numeroLinha + " de vendas.csv ignorada ("
                            + e.getMessage() + ").");
                }
            }
        }
        return vendas;
    }
}
