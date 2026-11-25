package app;

import java.util.Scanner;

public class Entrada {

    private static final Scanner SCANNER = new Scanner(System.in);

    private Entrada() {
    }

    public static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return SCANNER.nextLine().trim();
    }

    public static int lerInt(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido! Digite um número inteiro.");
            }
        }
    }

    public static double lerDouble(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            try {
                return Double.parseDouble(texto.replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido! Digite um número (ex.: 4,50).");
            }
        }
    }

    public static boolean lerSimNao(String mensagem) {
        while (true) {
            String resposta = lerTexto(mensagem + " (s/n): ").toLowerCase();
            if (resposta.equals("s") || resposta.equals("sim")) {
                return true;
            }
            if (resposta.equals("n") || resposta.equals("nao") || resposta.equals("não")) {
                return false;
            }
            System.out.println("Responda com 's' ou 'n'.");
        }
    }
}
