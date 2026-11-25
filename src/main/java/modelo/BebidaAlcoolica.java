package modelo;

import excecoes.DadosInvalidosException;

public abstract class BebidaAlcoolica extends Bebida {

    private final double teorAlcoolico;

    protected BebidaAlcoolica(int id, String nome, double preco, int quantidade, int volumeMl,
                              double teorAlcoolico) throws DadosInvalidosException {
        super(id, nome, preco, quantidade, volumeMl);
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
        return true;
    }
}
