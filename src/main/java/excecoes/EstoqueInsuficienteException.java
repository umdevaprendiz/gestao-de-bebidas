package excecoes;

public class EstoqueInsuficienteException extends GestaoBebidasException {

    private final int disponivel;
    private final int solicitado;

    public EstoqueInsuficienteException(String nomeBebida, int disponivel, int solicitado) {
        super("Estoque insuficiente de '" + nomeBebida + "': disponível " + disponivel
                + ", solicitado " + solicitado + ".");
        this.disponivel = disponivel;
        this.solicitado = solicitado;
    }

    public int getDisponivel() {
        return disponivel;
    }

    public int getSolicitado() {
        return solicitado;
    }
}
