package excecoes;

/**
 * Lançada quando um dado informado pelo usuário (ou lido do arquivo) é inválido.
 * Exemplos: preço negativo, nome vazio, quantidade zero.
 */
public class DadosInvalidosException extends GestaoBebidasException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
