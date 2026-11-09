package com.example.sprint2conversor.logic;

import java.util.LinkedHashMap;
import java.util.Map;

public class MassConverter {

    // LinkedHashMap mantiene el orden en el que se muestran las unidades en pantalla
    private static final Map<String, Double> FACTORS = new LinkedHashMap<>();

    static {
        // Factores de conversión respecto al kilogramo
        FACTORS.put("kg", 1.0);
        FACTORS.put("g", 0.001);
        FACTORS.put("mg", 0.000001);
        FACTORS.put("lb", 0.45359237);
        FACTORS.put("oz", 0.028349523125);
        FACTORS.put("t", 1000.0);
    }

    private MassConverter() {
        // Clase de utilidades: no se instancia
    }

    // Devuelve las unidades disponibles (en el orden en el que se muestran)
    public static String[] getUnits() {
        return FACTORS.keySet().toArray(new String[0]);
    }

    public static double convert(double value, String from, String to) {
        Double fromFactor = FACTORS.get(from);
        Double toFactor = FACTORS.get(to);

        if (fromFactor == null || toFactor == null) {
            throw new IllegalArgumentException("Unidad no válida");
        }

        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("El valor no es válido");
        }

        // Convertir siempre primero a kg
        double valueInKg = value * fromFactor;

        // Convertir de kg a la unidad seleccionada
        return valueInKg / toFactor;
    }
}