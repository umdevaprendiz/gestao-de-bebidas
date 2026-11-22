package modelo;

import excecoes.DadosInvalidosException;

/**
 * Segunda CLASSE ABSTRATA da hierarquia: bebidas com álcool (Cerveja, Vinho).
 * Herda tudo de Bebida e acrescenta o teor alcoólico, comum às duas filhas.
 *
 * Hierarquia:  Bebida -> BebidaAlcoolica -> Cerveja / Vinho
 *              Bebida -> Refrigerante / Suco
 */
public abstract class BebidaAlcoolica extends Bebida {

    private final double teorAlcoolico; // em porcentagem (ex.: 4.7)

    protected BebidaAlcoolica(int id, String nome, double preco, int quantidade, int volumeMl,
                              double teorAlcoolico) throws DadosInvalidosException {
        super(id, nome, preco, quantidade, volumeMl); // reaproveita o construtor da classe pai
        if (!Double.isFinite(teorAlcoolico) || teorAlcoolico <= 0 || teorAlcoolico > 100) {
            throw new DadosInvalidosException("O teor alcoólico deve estar entre 0 e 100%.");
        }
        this.teorAlcoolico = teorAlcoolico;
    }

    public double getTeorAlcoolico() {
        return teorAlcoolico;
    }

    @Override
    public boolean isAlcoolica() {
        return true; // sobrescreve o método de Bebida
    }
}
