package persistencia;

import excecoes.DadosInvalidosException;
import modelo.Bebida;
import modelo.Cerveja;
import modelo.Refrigerante;
import modelo.Suco;
import modelo.Vinho;

public class BebidaFactory {

    private static final int CAMPOS_ESPERADOS = 8;

    private BebidaFactory() {
    }

    public static Bebida criarDeLinha(String linha) throws DadosInvalidosException {
        String[] campos = linha.split(";", -1);
        if (campos.length != CAMPOS_ESPERADOS) {
            throw new DadosInvalidosException("número de campos incorreto");
        }

        try {
            String tipo = campos[0];
            int id = Integer.parseInt(campos[1]);
            String nome = campos[2];
            double preco = Double.parseDouble(campos[3]);
            int quantidade = Integer.parseInt(campos[4]);
            int volumeMl = Integer.parseInt(campos[5]);

            switch (tipo) {
                case "CERVEJA":
                    return new Cerveja(id, nome, preco, quantidade, volumeMl,
                            Double.parseDouble(campos[6]), campos[7]);
                case "VINHO":
                    return new Vinho(id, nome, preco, quantidade, volumeMl,
                            Double.parseDouble(campos[6]), campos[7]);
                case "REFRIGERANTE":
                    return new Refrigerante(id, nome, preco, quantidade, volumeMl,
                            campos[6], Boolean.parseBoolean(campos[7]));
                case "SUCO":
                    return new Suco(id, nome, preco, quantidade, volumeMl,
                            campos[6], Boolean.parseBoolean(campos[7]));
                default:
                    throw new DadosInvalidosException("tipo de bebida desconhecido: " + tipo);
            }
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("número em formato inválido");
        }
    }
}
