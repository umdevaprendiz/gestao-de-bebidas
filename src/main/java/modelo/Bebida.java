package modelo;

import java.util.Locale;
import java.util.Objects;

import excecoes.DadosInvalidosException;
import excecoes.EstoqueInsuficienteException;

public abstract class Bebida implements Tributavel, Persistivel, Comparable<Bebida> {

    private final int id;
    private final String nome;
    private double preco;
    private int quantidade;
    private final int volumeMl;

    protected Bebida(int id, String nome, double preco, int quantidade, int volumeMl)
            throws DadosInvalidosException {
        if (id <= 0) {
            throw new DadosInvalidosException("O ID deve ser maior que zero.");
        }
        if (quantidade < 0) {
            throw new DadosInvalidosException("A quantidade em estoque não pode ser negativa.");
        }
        if (volumeMl <= 0) {
            throw new DadosInvalidosException("O volume deve ser maior que zero.");
        }
        this.id = id;
        this.nome = validarTexto("nome", nome);
        this.preco = validarPositivo("preço", preco);
        this.quantidade = quantidade;
        this.volumeMl = volumeMl;
    }

    protected static String validarTexto(String campo, String valor) throws DadosInvalidosException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DadosInvalidosException("O campo '" + campo + "' não pode ficar vazio.");
        }
        if (valor.contains(";")) {
            throw new DadosInvalidosException("O campo '" + campo + "' não pode conter ponto e vírgula (;).");
        }
        return valor.trim();
    }

    protected static double validarPositivo(String campo, double valor) throws DadosInvalidosException {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new DadosInvalidosException("O campo '" + campo + "' deve ser um número maior que zero.");
        }
        return valor;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) throws DadosInvalidosException {
        this.preco = validarPositivo("preço", preco);
    }

    public int getQuantidade() {
        return quantidade;
    }

    public int getVolumeMl() {
        return volumeMl;
    }

    public void adicionarEstoque(int unidades) throws DadosInvalidosException {
        if (unidades <= 0) {
            throw new DadosInvalidosException("A quantidade a adicionar deve ser maior que zero.");
        }
        quantidade += unidades;
    }

    public void removerEstoque(int unidades) throws EstoqueInsuficienteException, DadosInvalidosException {
        if (unidades <= 0) {
            throw new DadosInvalidosException("A quantidade a retirar deve ser maior que zero.");
        }
        if (unidades > quantidade) {
            throw new EstoqueInsuficienteException(nome, quantidade, unidades);
        }
        quantidade -= unidades;
    }

    public boolean isEstoqueBaixo(int limite) {
        return quantidade < limite;
    }

    public double getPrecoFinal() {
        return preco + calcularImposto();
    }

    public boolean isAlcoolica() {
        return false;
    }

    public abstract String getCategoria();

    public abstract String getDetalhes();

    protected abstract String camposEspecificos();

    @Override
    public String paraLinhaCsv() {
        return getCategoria().toUpperCase(Locale.ROOT) + ";" + id + ";" + nome + ";" + preco + ";"
                + quantidade + ";" + volumeMl + ";" + camposEspecificos();
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Bebida)) {
            return false;
        }
        Bebida outra = (Bebida) objeto;
        return volumeMl == outra.volumeMl
                && nome.toLowerCase(Locale.ROOT).equals(outra.nome.toLowerCase(Locale.ROOT));
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome.toLowerCase(Locale.ROOT), volumeMl);
    }

    @Override
    public int compareTo(Bebida outra) {
        return this.nome.compareToIgnoreCase(outra.nome);
    }

    @Override
    public String toString() {
        return String.format("[%02d] %-12s %-24s %5d ml | Qtd: %3d | R$ %6.2f (final R$ %6.2f) | %s",
                id, getCategoria(), nome, volumeMl, quantidade, preco, getPrecoFinal(), getDetalhes());
    }
}
