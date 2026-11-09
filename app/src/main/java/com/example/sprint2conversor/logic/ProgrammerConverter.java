
package com.example.sprint2conversor.logic;

public class ProgrammerConverter {

    // Convierte un numero de una base a otra.
    // Las bases permitidas van del 2 al 36.
    public static String convertBase(String value, int baseFrom, int baseTo) {

        if (baseFrom < 2 || baseFrom > 36 ||
                baseTo < 2 || baseTo > 36) {
            throw new IllegalArgumentException(
                    "Las bases deben estar entre 2 y 36"
            );
        }

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Introduce un numero");
        }

        try {
            String cleanValue = value.trim();

            // Convertimos primero a decimal (long).
            long decimal = Long.parseLong(cleanValue, baseFrom);

            // Convertimos de decimal a la base de destino.
            return Long.toString(decimal, baseTo).toUpperCase();

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El numero no es valido para la base seleccionada"
            );
        }
    }

    // Texto a ASCII.
    public static String textToAscii(String text) {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Introduce un texto");
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            if (i > 0) {
                result.append(" ");
            }

            int ascii = text.charAt(i);

            if (ascii > 127) {
                throw new IllegalArgumentException(
                        "Solo se admiten caracteres ASCII"
                );
            }

            result.append(ascii);
        }

        return result.toString();
    }

    // ASCII a texto.
    public static String asciiToText(String ascii) {

        if (ascii == null || ascii.trim().isEmpty()) {
            throw new IllegalArgumentException("Introduce los codigos ASCII");
        }

        String[] values = ascii.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        try {
            for (String value : values) {
                int code = Integer.parseInt(value);

                if (code < 0 || code > 127) {
                    throw new IllegalArgumentException(
                            "Codigo ASCII fuera de rango"
                    );
                }

                result.append((char) code);
            }

            return result.toString();

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Introduce codigos ASCII separados por espacios"
            );
        }
    }

    // Texto a hexadecimal (UTF-8).
    public static String textToHex(String text) {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Introduce un texto");
        }

        byte[] bytes = text.getBytes(
                java.nio.charset.StandardCharsets.UTF_8
        );

        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {
            result.append(String.format("%02X", b & 0xFF));
        }

        return result.toString();
    }

    // Hexadecimal a texto (UTF-8).
    public static String hexToText(String hex) {

        if (hex == null || hex.trim().isEmpty()) {
            throw new IllegalArgumentException("Introduce hexadecimal");
        }

        String cleanHex = hex.replaceAll("\\s+", "");

        if (cleanHex.length() % 2 != 0) {
            throw new IllegalArgumentException(
                    "El hexadecimal debe tener un numero par de caracteres"
            );
        }

        if (!cleanHex.matches("[0-9A-Fa-f]+")) {
            throw new IllegalArgumentException(
                    "El valor contiene caracteres hexadecimales no validos"
            );
        }

        byte[] bytes = new byte[cleanHex.length() / 2];

        for (int i = 0; i < cleanHex.length(); i += 2) {
            int value = Integer.parseInt(
                    cleanHex.substring(i, i + 2), 16
            );

            bytes[i / 2] = (byte) value;
        }

        return new String(
                bytes,
                java.nio.charset.StandardCharsets.UTF_8
        );
    }
}
