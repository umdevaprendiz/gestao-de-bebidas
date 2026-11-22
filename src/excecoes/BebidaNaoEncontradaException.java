package excecoes;

/**
 * Lançada quando se procura uma bebida por ID e ela não existe.
 */
public class BebidaNaoEncontradaException extends GestaoBebidasException {

    public BebidaNaoEncontradaException(int id) {
        super("Nenhuma bebida encontrada com o ID " + id + ".");
    }
}
