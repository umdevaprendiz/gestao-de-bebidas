package modelo;

import excecoes.DadosInvalidosException;

public class Cerveja extends BebidaAlcoolica {

    private static final double ALIQUOTA = 0.25; // 25% de imposto

    private final String estilo; // ex.: Pilsen, IPA, Weiss

    public Cerveja(int id, String nome, double preco, int quantidade, int volumeMl,
                   double teorAlcoolico, String estilo) throws DadosInvalidosException {
        super(id, nome, preco, quantidade, volumeMl, teorAlcoolico);
        this.estilo = validarTexto("estilo", estilo);
    }

    public String getEstilo() {
        return estilo;
    }

    @Override
    public double calcularImposto() {
        return getPreco() * ALIQUOTA;
    }

    @Override
    public String getCategoria() {
        return "Cerveja";
    }

    @Override
    public String getDetalhes() {
        return estilo + ", " + getTeorAlcoolico() + "% de álcool";
    }

    @Override
    protected String camposEspecificos() {
        return getTeorAlcoolico() + ";" + estilo;
    }
}
