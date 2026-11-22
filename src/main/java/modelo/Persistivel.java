package modelo;

/**
 * INTERFACE: contrato para objetos que sabem se transformar em uma linha de arquivo CSV.
 * Tanto Bebida quanto Venda implementam, e o ArquivoRepositorio salva qualquer
 * Persistivel sem precisar saber qual é a classe concreta.
 */
public interface Persistivel {

    /** Linha CSV (campos separados por ponto e vírgula) que representa o objeto. */
    String paraLinhaCsv();
}
