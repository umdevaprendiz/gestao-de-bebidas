package modelo;

import excecoes.DadosInvalidosException;

public class Vinho extends BebidaAlcoolica {

    private static final double ALIQUOTA = 0.30; // 30% de imposto

    private final String tipo; // Tinto, Branco, Rosé...

    public Vinho(int id, String nome, double preco, int quantidade, int volumeMl,
                 double teorAlcoolico, String tipo) throws DadosInvalidosException {
        super(id, nome, preco, quantidade, volumeMl, teorAlcoolico);
        this.tipo = validarTexto("tipo", tipo);
    }

    public String getTipo() {
        return tipo;
    }

    @Override
    public double calcularImposto() {
        return getPreco() * ALIQUOTA;
    }

    @Override
    public String getCategoria() {
        return "Vinho";
    }

    @Override
    public String getDetalhes() {
        return tipo + ", " + getTeorAlcoolico() + "% de álcool";
    }

    @Override
    protected String camposEspecificos() {
        return getTeorAlcoolico() + ";" + tipo;
    }
}
