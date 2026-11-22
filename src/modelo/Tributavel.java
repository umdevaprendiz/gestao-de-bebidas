package modelo;

/**
 * INTERFACE: contrato para qualquer coisa que paga imposto.
 * Cada tipo de bebida implementa do seu jeito (alíquotas diferentes) -> POLIMORFISMO.
 */
public interface Tributavel {

    /** Valor do imposto (em R$) cobrado sobre UMA unidade. */
    double calcularImposto();
}
