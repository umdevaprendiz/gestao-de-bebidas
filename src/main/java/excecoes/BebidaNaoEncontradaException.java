package excecoes;

public class BebidaNaoEncontradaException extends GestaoBebidasException {

    public BebidaNaoEncontradaException(int id) {
        super("Nenhuma bebida encontrada com o ID " + id + ".");
    }
}
