package com.example.sprint2conversor.logic;

import java.math.BigInteger;

public class MathConverter {

    // Operaciones con fracciones
    // Se usa BigInteger para evitar desbordamientos con números grandes
    public static String calculateFraction(int num1, int den1, int num2, int den2, String operacion) {

        if (den1 == 0 || den2 == 0) {
            throw new IllegalArgumentException("El denominador no puede ser 0");
        }

        if (operacion == null) {
            throw new IllegalArgumentException("Operación no válida");
        }

        BigInteger n1 = BigInteger.valueOf(num1);
        BigInteger d1 = BigInteger.valueOf(den1);
        BigInteger n2 = BigInteger.valueOf(num2);
        BigInteger d2 = BigInteger.valueOf(den2);

        BigInteger numerador;
        BigInteger denominador;

        switch (operacion) {
            case "Sumar":
                numerador = n1.multiply(d2).add(n2.multiply(d1));
                denominador = d1.multiply(d2);
                break;

            case "Restar":
                numerador = n1.multiply(d2).subtract(n2.multiply(d1));
                denominador = d1.multiply(d2);
                break;

            case "Multiplicar":
                numerador = n1.multiply(n2);
                denominador = d1.multiply(d2);
                break;

            case "Dividir":
                if (n2.signum() == 0) {
                    throw new IllegalArgumentException(
                            "No se puede dividir entre una fracción igual a 0"
                    );
                }
                numerador = n1.multiply(d2);
                denominador = d1.multiply(n2);
                break;

            default:
                throw new IllegalArgumentException("Operación no válida");
        }

        // El signo siempre va en el numerador
        if (denominador.signum() < 0) {
            numerador = numerador.negate();
            denominador = denominador.negate();
        }

        // Simplificar la fracción (el denominador nunca es 0, así que el mcd es >= 1)
        BigInteger divisor = numerador.gcd(denominador);

        numerador = numerador.divide(divisor);
        denominador = denominador.divide(divisor);

        // Si el denominador es 1, el resultado es un número entero
        if (denominador.equals(BigInteger.ONE)) {
            return numerador.toString();
        }

        return numerador + " / " + denominador;
    }

    // Porcentaje
    public static double calcularPorcentaje(double porcentaje, double value) {
        double resultado = (porcentaje / 100) * value;

        if (!Double.isFinite(resultado)) {
            throw new IllegalArgumentException("El resultado no es válido");
        }
        return resultado;
    }

    // Raíz cuadrada
    public static double calcularRaizCuadrada(double num) {
        if (num < 0) {
            throw new IllegalArgumentException(
                    "No existe la raíz real de un número negativo"
            );
        }
        return Math.sqrt(num);
    }

    // Potencia
    public static double calcularPotencia(double base, double exponente) {
        double resultado = Math.pow(base, exponente);

        if (Double.isNaN(resultado)) {
            throw new IllegalArgumentException(
                    "El resultado no es un número real"
            );
        }

        if (Double.isInfinite(resultado)) {
            throw new IllegalArgumentException(
                    "El resultado es demasiado grande o no está definido"
            );
        }
        return resultado;
    }

    // Grados a radianes
    public static double gradosARadianes(double grados) {
        return Math.toRadians(grados);
    }

    // Radianes a grados
    public static double radianesAGrados(double radianes) {
        return Math.toDegrees(radianes);
    }
}