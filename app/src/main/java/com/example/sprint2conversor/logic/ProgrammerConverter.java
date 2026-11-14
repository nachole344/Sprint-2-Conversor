package com.example.sprint2conversor.logic;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

// conversiones de la pantalla de programacion: cambio de base, ascii y hexadecimal
// tambien es java normal, sin nada de android
public class ProgrammerConverter {

    // bases permitidas: de la 2 (binario) a la 36 (digitos 0-9 y letras a-z)
    private static final int MIN_BASE = 2;
    private static final int MAX_BASE = 36;
    // el ascii normal va del 0 al 127
    private static final int MAX_ASCII = 127;
    // limite de digitos pa que no se cuelgue la app con un numero gigante
    private static final int MAX_NUMBER_LENGTH = 512;

    // constructor privado: clase solo de metodos estaticos
    private ProgrammerConverter() {
        // clase de utilidades: no se instancia
    }

    // convierte un numero de una base a otra.
    // las bases permitidas van del 2 al 36.
    // usa biginteger, asi que no hay limite de tamano (antes fallaba con mas de 64 bits).
    // idea: leer el numero (que es un texto) en la base origen y escribirlo en la base destino
    public static String convertBase(String value, int baseFrom, int baseTo) {

        // comprobamos que las dos bases sean validas
        if (baseFrom < MIN_BASE || baseFrom > MAX_BASE ||
                baseTo < MIN_BASE || baseTo > MAX_BASE) {
            throw new IllegalArgumentException(
                    "Las bases deben estar entre 2 y 36"
            );
        }

        // no dejamos pasar un texto vacio
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Introduce un número");
        }

        // limpiamos espacios, guiones bajos y prefijos tipo 0x
        String cleanValue = cleanNumber(value, baseFrom);

        // si es demasiado largo, error
        if (cleanValue.length() > MAX_NUMBER_LENGTH) {
            throw new IllegalArgumentException("El número es demasiado largo");
        }

        try {
            // convertimos primero a entero y despues a la base de destino
            // biginteger(texto, base) lee el texto en esa base y lo guarda como numero
            // uso biginteger y no long pa que valgan numeros muy grandes
            // si hay un digito que no existe en esa base (un 2 en binario) lanza numberformatexception
            BigInteger number = new BigInteger(cleanValue, baseFrom);

            // tostring(base) escribe el numero en la base destino
            // y lo paso a mayusculas pa que las letras del hexadecimal salgan grandes
            return number.toString(baseTo).toUpperCase(Locale.ROOT);

            // si el texto no es valido en esa base, avisamos con un mensaje claro
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El número no es válido para la base " + baseFrom
            );
        }
    }

    // quita espacios, guiones bajos (1111_0000) y el prefijo de la base (0x, 0b, 0o)
    // deja el numero limpio: quita espacios y '_', guarda el signo y quita el prefijo si coincide con la base
    private static String cleanNumber(String value, int baseFrom) {

        // replaceall usa regex: \s son los espacios y _ el guion bajo
        String text = value.replaceAll("[\\s_]", "");
        String sign = "";

        // guardo el signo aparte pa quitarlo del texto y luego volver a ponerlo
        if (text.startsWith("-") || text.startsWith("+")) {
            if (text.startsWith("-")) {
                sign = "-";
            }
            text = text.substring(1);
        }

        // paso a minusculas solo pa comparar el prefijo (da igual si es 0x o con la x en mayuscula)
        String lower = text.toLowerCase(Locale.ROOT);

        // solo se quita el prefijo cuando coincide con la base de origen
        // el prefijo solo se quita si coincide con la base: 0x en hex, 0b en binario y 0o en octal
        // ojo: en hexadecimal la 'b' es un digito valido, por eso 0b solo se quita en base 2
        if ((baseFrom == 16 && lower.startsWith("0x"))
                || (baseFrom == 2 && lower.startsWith("0b"))
                || (baseFrom == 8 && lower.startsWith("0o"))) {
            text = text.substring(2);
        }

        return sign + text;
    }

    // texto a ascii.
    // cada caracter del texto -> su codigo ascii, separados por espacios
    // ej: 'hola' -> 104 111 108 97
    public static String textToAscii(String text) {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Introduce un texto");
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            // un char en java es en realidad un numero, asi que al guardarlo en un int sale su codigo
            int ascii = text.charAt(i);

            // las tildes, la enye o los emojis estan fuera del ascii basico
            if (ascii > MAX_ASCII) {
                throw new IllegalArgumentException(
                        "Solo se admiten caracteres ASCII"
                );
            }

            if (i > 0) {
                result.append(" ");
            }

            // anadimos el numero (no la letra)
            result.append(ascii);
        }

        return result.toString();
    }

    // ascii a texto.
    // al reves: codigos ascii separados por espacios -> texto
    public static String asciiToText(String ascii) {

        if (ascii == null || ascii.trim().isEmpty()) {
            throw new IllegalArgumentException("Introduce los códigos ASCII");
        }

        // se admiten espacios, comas y puntos y coma como separadores
        // split corta el texto por espacios, comas o punto y coma (es una regex)
        String[] values = ascii.trim().split("[\\s,;]+");
        StringBuilder result = new StringBuilder();

        for (String value : values) {
            int code;

            try {
                // pasamos cada trozo a numero; si no es un numero lanzamos error
                code = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Introduce códigos ASCII separados por espacios"
                );
            }

            if (code < 0 || code > MAX_ASCII) {
                throw new IllegalArgumentException(
                        "Código ASCII fuera de rango (0-127)"
                );
            }

            // (char) convierte el numero en su letra
            result.append((char) code);
        }

        return result.toString();
    }

    // texto a hexadecimal (utf-8).
    // texto -> hexadecimal usando utf-8 (cada letra son 1 o mas bytes)
    // ej: 'a' -> 61 ; una e con tilde ocupa 2 bytes: c3a9
    public static String textToHex(String text) {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Introduce un texto");
        }

        // getbytes saca los bytes del texto en utf-8
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);

        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {
            // %02x = 2 cifras hexadecimales, rellenando con un 0 delante (5 -> 05)
            // b & 0xff pasa el byte (que en java puede ser negativo) a un numero de 0 a 255
            result.append(String.format(Locale.ROOT, "%02X", b & 0xFF));
        }

        return result.toString();
    }

    // hexadecimal a texto (utf-8).
    // hexadecimal -> texto: hace el camino contrario a texttohex
    public static String hexToText(String hex) {

        if (hex == null || hex.trim().isEmpty()) {
            throw new IllegalArgumentException("Introduce hexadecimal");
        }

        // quitamos los espacios (despues quitamos el 0x si lo hay)
        String cleanHex = hex.replaceAll("\\s+", "");

        // prefijo opcional 0x
        if (cleanHex.startsWith("0x") || cleanHex.startsWith("0X")) {
            cleanHex = cleanHex.substring(2);
        }

        if (cleanHex.isEmpty()) {
            throw new IllegalArgumentException("Introduce hexadecimal");
        }

        // cada byte son 2 cifras hex, asi que la cantidad de cifras tiene que ser par
        if (cleanHex.length() % 2 != 0) {
            throw new IllegalArgumentException(
                    "El hexadecimal debe tener un número par de caracteres"
            );
        }

        // matches con regex: solo valen digitos 0-9 y letras a-f
        if (!cleanHex.matches("[0-9A-Fa-f]+")) {
            throw new IllegalArgumentException(
                    "El valor contiene caracteres hexadecimales no válidos"
            );
        }

        // un byte por cada 2 cifras
        byte[] bytes = new byte[cleanHex.length() / 2];

        for (int i = 0; i < cleanHex.length(); i += 2) {
            // cogemos 2 cifras, las leemos en base 16 y las guardamos como byte
            bytes[i / 2] = (byte) Integer.parseInt(
                    cleanHex.substring(i, i + 2), 16
            );
        }

        // decodificacion estricta: avisa si los bytes no forman utf-8 valido
        // (antes devolvia caracteres "" sin avisar)
        // decodificador estricto: si los bytes no son utf-8 valido da error
        // (con new string(...) normal saldrian rombos raros sin avisar)
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);

        try {
            // convierte los bytes en texto
            return decoder.decode(ByteBuffer.wrap(bytes)).toString();
        } catch (CharacterCodingException e) {
            throw new IllegalArgumentException(
                    "Los bytes no forman un texto UTF-8 válido"
            );
        }
    }
}