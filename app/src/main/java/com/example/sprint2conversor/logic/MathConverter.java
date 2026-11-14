package com.example.sprint2conversor.logic;

import java.math.BigInteger;

// aqui estan las operaciones de la pantalla de matematicas
// no tiene nada de android, es java normal (asi se puede probar solo)
public class MathConverter {

    // operaciones con fracciones
    // se usa biginteger para evitar desbordamientos con numeros grandes
    // recibe 2 fracciones (numerador y denominador) y una operacion en texto
    // devuelve el resultado ya simplificado
    public static String calculateFraction(int num1, int den1, int num2, int den2, String operacion) {

        // no existe una fraccion con denominador 0
        if (den1 == 0 || den2 == 0) {
            throw new IllegalArgumentException("El denominador no puede ser 0");
        }

        // por si llega null, asi no explota el switch
        if (operacion == null) {
            throw new IllegalArgumentException("Operación no válida");
        }

        // paso los int a biginteger pa que al multiplicar no se desborde
        // (un int multiplicado por otro int puede pasarse de 2 mil millones y daria un resultado raro)
        BigInteger n1 = BigInteger.valueOf(num1);
        BigInteger d1 = BigInteger.valueOf(den1);
        BigInteger n2 = BigInteger.valueOf(num2);
        BigInteger d2 = BigInteger.valueOf(den2);

        // estas dos variables guardan el resultado antes de simplificar
        BigInteger numerador;
        BigInteger denominador;

        // segun el texto del spinner hacemos una cuenta u otra
        switch (operacion) {
            // a/b + c/d = (a*d + c*b) / (b*d)
            case "Sumar":
                numerador = n1.multiply(d2).add(n2.multiply(d1));
                denominador = d1.multiply(d2);
                break;

            // a/b - c/d = (a*d - c*b) / (b*d)
            case "Restar":
                numerador = n1.multiply(d2).subtract(n2.multiply(d1));
                denominador = d1.multiply(d2);
                break;

            // a/b * c/d = (a*c) / (b*d)
            case "Multiplicar":
                numerador = n1.multiply(n2);
                denominador = d1.multiply(d2);
                break;

            // a/b : c/d = (a*d) / (b*c)  (se multiplica por la inversa)
            case "Dividir":
                // signum() da 0 si el numero es 0. dividir entre una fraccion 0/algo no se puede
                if (n2.signum() == 0) {
                    throw new IllegalArgumentException(
                            "No se puede dividir entre una fracción igual a 0"
                    );
                }
                numerador = n1.multiply(d2);
                denominador = d1.multiply(n2);
                break;

            // si llega una operacion rara lanzamos error
            default:
                throw new IllegalArgumentException("Operación no válida");
        }

        // el signo siempre va en el numerador
        // ej: 1 / -2 pasa a -1 / 2
        if (denominador.signum() < 0) {
            numerador = numerador.negate();
            denominador = denominador.negate();
        }

        // simplificar la fraccion (el denominador nunca es 0, asi que el mcd es >= 1)
        // gcd = maximo comun divisor. dividiendo arriba y abajo por el, la fraccion queda simplificada
        // ej: 6/8 -> gcd = 2 -> 3/4
        BigInteger divisor = numerador.gcd(denominador);

        numerador = numerador.divide(divisor);
        denominador = denominador.divide(divisor);

        // si el denominador es 1, el resultado es un numero entero
        if (denominador.equals(BigInteger.ONE)) {
            return numerador.toString();
        }

        // si no, lo devolvemos como texto 'a / b'
        return numerador + " / " + denominador;
    }

    // porcentaje
    // x% de y = (x / 100) * y ; ej: 20% de 150 = 30
    public static double calcularPorcentaje(double porcentaje, double value) {
        double resultado = (porcentaje / 100) * value;

        // por si el resultado se sale de rango (infinito)
        if (!Double.isFinite(resultado)) {
            throw new IllegalArgumentException("El resultado no es válido");
        }
        return resultado;
    }

    // raiz cuadrada
    // raiz cuadrada con math.sqrt
    public static double calcularRaizCuadrada(double num) {
        // de un numero negativo no hay raiz real, avisamos con un error
        if (num < 0) {
            throw new IllegalArgumentException(
                    "No existe la raíz real de un número negativo"
            );
        }
        return Math.sqrt(num);
    }

    // potencia
    // base elevada a exponente con math.pow
    public static double calcularPotencia(double base, double exponente) {
        double resultado = Math.pow(base, exponente);

        // nan = 'not a number'. pasa por ejemplo con (-8) elevado a 0.5
        if (Double.isNaN(resultado)) {
            throw new IllegalArgumentException(
                    "El resultado no es un número real"
            );
        }

        // infinito pasa por ejemplo con 0 elevado a -1 o con numeros enormes
        if (Double.isInfinite(resultado)) {
            throw new IllegalArgumentException(
                    "El resultado es demasiado grande o no está definido"
            );
        }
        return resultado;
    }

    // grados a radianes
    // 180 grados = pi radianes. math.toradians hace la cuenta (grados * pi / 180)
    public static double gradosARadianes(double grados) {
        return Math.toRadians(grados);
    }

    // radianes a grados
    // al reves: radianes * 180 / pi
    public static double radianesAGrados(double radianes) {
        return Math.toDegrees(radianes);
    }
}