package com.example.sprint2conversor.logic;


public class MathConverter {
    // operaciones de fracciones
    public static String calculateFraction(int num1, int den1, int num2, int den2, String operacion) {
        if (den1 == 0 || den2 == 0) {
            throw new IllegalArgumentException("el denominador no puede ser 0");
        }
        int numerador;
        int denominador;

        switch (operacion) {
            case "Sumar":
                numerador = num1 * den2 + num2 * den1;
                denominador = den1 * den2;
                break;

            case "Restar":
                numerador = num1 * den2 - num2 * den1;
                denominador = den1 * den2;
                break;

            case "Multiplicar":
                numerador = num1 * num2;
                denominador = den1 * den2;
                break;

            case "Dividir":
                if (num2 == 0) {
                    throw new IllegalArgumentException("no se puede dividir entre una fraccion igual a 0");
                }
                numerador = num1 * den2;
                denominador = den1 * num2;
                break;
            default:
                throw new IllegalArgumentException("operacion no valida");
        }

        if (denominador < 0) {
            numerador = -numerador;
            denominador = -denominador;
        }
        int divisor = mcd(Math.abs(numerador), Math.abs(denominador));

        numerador /= divisor;
        denominador /= divisor;

        return numerador + " / " + denominador;
    }

    // maximo comun divisor
    private static int mcd(int a, int b) {
        while (b != 0) {
            int resto = a % b;
            a = b;
            b = resto;
        }
        return a == 0 ? 1 : a;
    }

    //porcentaje
    public static double calcularPorcentaje(double porcentaje, double value) {
        return (porcentaje / 100) * value;
    }

    //raiz cuadradaa
    public static double calcularRaizCuadrada(double num) {
        if (num < 0) {
            throw new IllegalArgumentException("no existe raiz real de un numero negativo");
        }
        return Math.sqrt(num);
    }

    // potencia
    public static double calcularPotencia(double base, double exponente) {
        double resultado = Math.pow(base, exponente);

        if (Double.isNaN(resultado) || Double.isInfinite(resultado)) {
            throw new IllegalArgumentException("el resultado no es valido");
        }
        return resultado;
    }

    // grados a radianes
    public static double gradosARadianes(double grados) {
        return Math.toRadians(grados);
    }

    // radianes a grados
    public static double radianesAGrados(double radianes) {
        return Math.toDegrees(radianes);
    }
}