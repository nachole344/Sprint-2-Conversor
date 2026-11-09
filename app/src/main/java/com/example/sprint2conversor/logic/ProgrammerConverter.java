
package com.example.sprint2conversor.logic;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ProgrammerConverter {

    // Constructor privado: esta clase solo contiene metodos estaticos.
    private ProgrammerConverter() {
    }

    // =====================================================
    // CONVERSION ENTRE BASES NUMERICAS
    // =====================================================

    // Convierte un numero de una base a otra.
    // Las bases permitidas van del 2 al 36.
    public static String convertBase(String value, int baseFrom, int baseTo) {

        // Comprobamos que las bases sean validas.
        if (baseFrom < 2 || baseFrom > 36 ||
                baseTo < 2 || baseTo > 36) {
            throw new IllegalArgumentException(
                    "Las bases deben estar entre 2 y 36"
            );
        }

        // Comprobamos que se haya introducido un numero.
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Introduce un numero"
            );
        }

        String cleanValue = value.trim();

        try {
            // Convertimos el numero a decimal.
            long decimal = Long.parseLong(cleanValue, baseFrom);

            // Convertimos el decimal a la base de destino.
            return Long.toString(decimal, baseTo).toUpperCase(Locale.ROOT);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El numero no es valido para la base seleccionada"
            );
        }
    }

    // =====================================================
    // CONVERSION TEXTO - ASCII
    // =====================================================

    // Convierte un texto a sus codigos ASCII.
    public static String textToAscii(String text) {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(
                    "Introduce un texto"
            );
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {

            char character = text.charAt(i);

            // ASCII utiliza caracteres del 0 al 127.
            if (character > 127) {
                throw new IllegalArgumentException(
                        "Solo se admiten caracteres ASCII"
                );
            }

            if (i > 0) {
                result.append(" ");
            }

            result.append((int) character);
        }

        return result.toString();
    }

    // Convierte codigos ASCII a texto.
    // Los codigos deben estar separados por espacios.
    public static String asciiToText(String ascii) {

        if (ascii == null || ascii.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Introduce los codigos ASCII"
            );
        }

        String[] values = ascii.trim().split("\\s+");

        StringBuilder result = new StringBuilder();

        for (String value : values) {

            try {
                int code = Integer.parseInt(value);

                // Comprobamos el rango ASCII.
                if (code < 0 || code > 127) {
                    throw new IllegalArgumentException(
                            "Codigo ASCII fuera de rango (0-127)"
                    );
                }

                result.append((char) code);

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Introduce codigos ASCII separados por espacios"
                );
            }
        }

        return result.toString();
    }

    // =====================================================
    // CONVERSION TEXTO - HEXADECIMAL
    // =====================================================

    // Convierte texto a hexadecimal utilizando UTF-8.
    public static String textToHex(String text) {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(
                    "Introduce un texto"
            );
        }

        // Convertimos el texto en bytes UTF-8.
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);

        StringBuilder result = new StringBuilder();

        // Cada byte se representa mediante dos caracteres hexadecimales.
        for (byte b : bytes) {

            String hex = String.format(
                    Locale.ROOT,
                    "%02X",
                    b & 0xFF
            );

            result.append(hex);
        }

        return result.toString();
    }

    // Convierte hexadecimal a texto utilizando UTF-8.
    public static String hexToText(String hex) {

        if (hex == null || hex.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Introduce un valor hexadecimal"
            );
        }

        // Eliminamos espacios para permitir separar los bytes.
        String cleanHex = hex.replaceAll("\\s+", "");

        // Cada byte necesita dos caracteres.
        if (cleanHex.length() % 2 != 0) {
            throw new IllegalArgumentException(
                    "El hexadecimal debe tener un numero par de caracteres"
            );
        }

        // Comprobamos que solo contenga caracteres hexadecimales.
        if (!cleanHex.matches("[0-9A-Fa-f]+")) {
            throw new IllegalArgumentException(
                    "El valor contiene caracteres hexadecimales no validos"
            );
        }

        byte[] bytes = new byte[cleanHex.length() / 2];

        // Convertimos cada pareja hexadecimal en un byte.
        for (int i = 0; i < cleanHex.length(); i += 2) {

            int value = Integer.parseInt(
                    cleanHex.substring(i, i + 2),
                    16
            );

            bytes[i / 2] = (byte) value;
        }

        // Reconstruimos el texto original.
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
