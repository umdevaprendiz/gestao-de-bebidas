package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import excecoes.DadosInvalidosException;

public class Venda implements Persistivel {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final int idBebida;
    private final String nomeBebida;
    private final int quantidade;
    private final double valorTotal;
    private final LocalDateTime dataHora;

    public Venda(int idBebida, String nomeBebida, int quantidade, double valorTotal, LocalDateTime dataHora) {
        this.idBebida = idBebida;
        this.nomeBebida = nomeBebida;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.dataHora = dataHora;
    }

    public int getIdBebida() {
        return idBebida;
    }

    public String getNomeBebida() {
        return nomeBebida;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String paraLinhaCsv() {
        return idBebida + ";" + nomeBebida + ";" + quantidade + ";" + valorTotal + ";" + dataHora;
    }

    public static Venda deLinhaCsv(String linha) throws DadosInvalidosException {
        String[] campos = linha.split(";", -1);
        if (campos.length != 5) {
            throw new DadosInvalidosException("Linha de venda com número de campos incorreto: " + linha);
        }
        try {
            return new Venda(
                    Integer.parseInt(campos[0]),
                    campos[1],
                    Integer.parseInt(campos[2]),
                    Double.parseDouble(campos[3]),
                    LocalDateTime.parse(campos[4]));
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new DadosInvalidosException("Linha de venda inválida: " + linha);
        }
    }

    @Override
    public String toString() {
        return String.format("%s | %-24s | %3d un. | Total: R$ %8.2f",
                dataHora.format(FORMATO), nomeBebida, quantidade, valorTotal);
    }
}
