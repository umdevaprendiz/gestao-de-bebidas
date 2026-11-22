package modelo;

import excecoes.DadosInvalidosException;

public class Refrigerante extends Bebida {

    private static final double ALIQUOTA_NORMAL = 0.20; // 20%
    private static final double ALIQUOTA_DIET = 0.15;   // 15% (versão diet/zero paga menos)

    private final String sabor;
    private final boolean diet;

    public Refrigerante(int id, String nome, double preco, int quantidade, int volumeMl,
                        String sabor, boolean diet) throws DadosInvalidosException {
        super(id, nome, preco, quantidade, volumeMl);
        this.sabor = validarTexto("sabor", sabor);
        this.diet = diet;
    }

    public String getSabor() {
        return sabor;
    }

    public boolean isDiet() {
        return diet;
    }

    @Override
    public double calcularImposto() {
        return getPreco() * (diet ? ALIQUOTA_DIET : ALIQUOTA_NORMAL);
    }

    @Override
    public String getCategoria() {
        return "Refrigerante";
    }

    @Override
    public String getDetalhes() {
        return "Sabor " + sabor + (diet ? " (diet/zero)" : "");
    }

    @Override
    protected String camposEspecificos() {
        return sabor + ";" + diet;
    }
}
