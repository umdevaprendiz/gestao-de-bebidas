package excecoes;

/**
 * Exceção base do sistema (HERANÇA de exceções).
 * Todas as exceções do nosso domínio estendem esta classe, então no Main
 * dá para capturar todas de uma vez com um único catch (GestaoBebidasException).
 *
 * É "checked": o compilador obriga a tratar com try/catch ou declarar com throws.
 */
public class GestaoBebidasException extends Exception {

    public GestaoBebidasException(String mensagem) {
        super(mensagem);
    }
}
