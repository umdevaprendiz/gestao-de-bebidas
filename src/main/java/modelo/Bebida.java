package modelo;

import java.util.Locale;

import excecoes.DadosInvalidosException;
import excecoes.EstoqueInsuficienteException;

/**
 * CLASSE ABSTRATA: representa qualquer bebida do sistema.
 * Não dá para fazer "new Bebida(...)": só as subclasses (Cerveja, Vinho, ...) podem ser criadas.
 *
 * Aqui ficam os atributos e comportamentos COMUNS (reuso com herança).
 * O que muda de bebida para bebida fica nos métodos abstratos, que cada filha implementa.
 *
 * ENCAPSULAMENTO: todos os atributos são privados; o acesso é feito por getters/métodos
 * que validam os dados antes de alterar o objeto.
 */
public abstract class Bebida implements Tributavel, Persistivel, Comparable<Bebida> {

    private final int id;
    private String nome;
    private double preco;        // preço de venda SEM imposto
    private int quantidade;      // unidades em estoque
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

    // ---------- Validações reaproveitadas pelas subclasses ----------

    /** Texto não pode ser vazio nem conter ';' (que é o separador do arquivo CSV). */
    protected static String validarTexto(String campo, String valor) throws DadosInvalidosException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DadosInvalidosException("O campo '" + campo + "' não pode ficar vazio.");
        }
        if (valor.contains(";")) {
            throw new DadosInvalidosException("O campo '" + campo + "' não pode conter ponto e vírgula (;).");
        }
        return valor.trim();
    }

    /** Número tem que ser finito e maior que zero. */
    protected static double validarPositivo(String campo, double valor) throws DadosInvalidosException {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new DadosInvalidosException("O campo '" + campo + "' deve ser um número maior que zero.");
        }
        return valor;
    }

    // ---------- Getters e setters ----------

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws DadosInvalidosException {
        this.nome = validarTexto("nome", nome);
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

    // ---------- Regras de negócio ----------

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

    /** Preço que o cliente paga: preço + imposto (o imposto varia conforme a subclasse). */
    public double getPrecoFinal() {
        return preco + calcularImposto();
    }

    /** Subclasses alcoólicas sobrescrevem para retornar true. */
    public boolean isAlcoolica() {
        return false;
    }

    // ---------- Métodos ABSTRATOS: cada tipo de bebida define o seu ----------

    /** Nome da categoria, ex.: "Cerveja". */
    public abstract String getCategoria();

    /** Descrição dos atributos específicos, ex.: "Pilsen, 4.7% de álcool". */
    public abstract String getDetalhes();

    /** Campos específicos da subclasse, já separados por ';', para gravar no CSV. */
    protected abstract String camposEspecificos();

    // ---------- Persistência (interface Persistivel) ----------

    @Override
    public String paraLinhaCsv() {
        return getCategoria().toUpperCase(Locale.ROOT) + ";" + id + ";" + nome + ";" + preco + ";"
                + quantidade + ";" + volumeMl + ";" + camposEspecificos();
    }

    // ---------- Ordenação (interface Comparable): por nome ----------

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
