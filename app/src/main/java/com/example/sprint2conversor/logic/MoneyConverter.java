package com.example.sprint2conversor.logic;

import java.util.LinkedHashMap;
import java.util.Map;
public class MoneyConverter {// Monedas disponibles y sus equivalencias respecto al euro
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
        RATES.put("Yuan chino (CNY)", 7.47);

        // Conversiones especiales
        RATES.put("Franco (FRANCO)", 1655.40);

        // 1 SOCIAL = 10 CNY
        // Por tanto, 1 EUR = 7.8 / 10 SOCIAL
        RATES.put("Social Credit (SOCIAL)", 7.47 / 10.0);
    }

    // Devuelve todas las monedas disponibles
    public static String[] getCurrencies() {
        return RATES.keySet().toArray(new String[0]);
    }

    // Realiza la conversión entre dos monedas
    public static double convert(double amount,
                                 String fromCurrency,
                                 String toCurrency) {

        if (!RATES.containsKey(fromCurrency) ||
                !RATES.containsKey(toCurrency)) {
            throw new IllegalArgumentException("Moneda no válida");
        }

        double fromRate = RATES.get(fromCurrency);
        double toRate = RATES.get(toCurrency);

        // Primero convierte a euros y después a la moneda destino
        return (amount / fromRate) * toRate;
    }
}
