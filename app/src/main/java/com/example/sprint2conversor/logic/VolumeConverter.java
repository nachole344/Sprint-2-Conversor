package com.example.sprint2conversor.logic;

import java.util.LinkedHashMap;
import java.util.Map;

public class VolumeConverter {

    // LinkedHashMap mantiene el orden en el que se muestran las unidades en pantalla
    private static final Map<String, Double> FACTORS = new LinkedHashMap<>();

    static {
        // Factores de conversión respecto al litro
        FACTORS.put("L", 1.0);
        FACTORS.put("mL", 0.001);
        FACTORS.put("m³", 1000.0);
        FACTORS.put("gal US", 3.785411784);
    }

    private VolumeConverter() {
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

        // Convertir siempre primero a litros
        double valueInLitres = value * fromFactor;

        // Convertir de litros a la unidad seleccionada
        return valueInLitres / toFactor;
    }
}