package modelo;

import excecoes.DadosInvalidosException;

public class Suco extends Bebida {

    private static final double ALIQUOTA_NORMAL = 0.10;  // 10%
    private static final double ALIQUOTA_NATURAL = 0.05; // 5% (suco 100% natural/integral)

    private final String fruta;
    private final boolean natural;

    public Suco(int id, String nome, double preco, int quantidade, int volumeMl,
                String fruta, boolean natural) throws DadosInvalidosException {
        super(id, nome, preco, quantidade, volumeMl);
        this.fruta = validarTexto("fruta", fruta);
        this.natural = natural;
    }

    public String getFruta() {
        return fruta;
    }

    public boolean isNatural() {
        return natural;
    }

    @Override
    public double calcularImposto() {
        return getPreco() * (natural ? ALIQUOTA_NATURAL : ALIQUOTA_NORMAL);
    }

    @Override
    public String getCategoria() {
        return "Suco";
    }

    @Override
    public String getDetalhes() {
        return "Fruta " + fruta + (natural ? " (100% natural)" : "");
    }

    @Override
    protected String camposEspecificos() {
        return fruta + ";" + natural;
    }
}
