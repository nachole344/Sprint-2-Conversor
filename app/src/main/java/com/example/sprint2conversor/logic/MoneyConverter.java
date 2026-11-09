package com.example.sprint2conversor.logic;

import java.util.LinkedHashMap;
import java.util.Map;

public class MoneyConverter {

    // Cuántos yuanes equivalen a 1 euro (se reutiliza para el Social Credit)
    private static final double CNY_PER_EUR = 7.47;

    // Monedas disponibles y sus equivalencias respecto al euro (1 EUR = X moneda)
    private static final Map<String, Double> RATES = new LinkedHashMap<>();

    static {
        RATES.put("Euro (EUR)", 1.0);
        RATES.put("Dólar (USD)", 1.12);
        RATES.put("Peso chileno (CLP)", 1087.36);
        RATES.put("Peso argentino (ARS)", 1729.71);
        RATES.put("Won surcoreano (KRW)", 1505.05);
        RATES.put("Yen japonés (JPY)", 177.02);
        RATES.put("Peso mexicano (MXN)", 20.82);
        RATES.put("Libra esterlina (GBP)", 0.84);
        RATES.put("Yuan chino (CNY)", CNY_PER_EUR);

        // Conversiones especiales
        RATES.put("Franco (FRANCO)", 1655.40);

        // 1 SOCIAL = 10 CNY
        // Por tanto, 1 EUR = CNY_PER_EUR / 10 SOCIAL
        RATES.put("Social Credit (SOCIAL)", CNY_PER_EUR / 10.0);
    }

    private MoneyConverter() {
        // Clase de utilidades: no se instancia
    }

    // Devuelve todas las monedas disponibles (en el orden en el que se muestran)
    public static String[] getCurrencies() {
        return RATES.keySet().toArray(new String[0]);
    }

    // Realiza la conversión entre dos monedas
    public static double convert(double amount,
                                 String fromCurrency,
                                 String toCurrency) {

        Double fromRate = RATES.get(fromCurrency);
        Double toRate = RATES.get(toCurrency);

        if (fromRate == null || toRate == null) {
            throw new IllegalArgumentException("Moneda no válida");
        }

        if (!Double.isFinite(amount)) {
            throw new IllegalArgumentException("El importe no es válido");
        }

        // Primero convierte a euros y después a la moneda destino
        return (amount / fromRate) * toRate;
    }
}