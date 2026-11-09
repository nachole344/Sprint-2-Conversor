package com.example.sprint2conversor.logic;

import java.util.HashMap;
import java.util.Map;

public class MassConverter {

    private static final Map<String, Double> FACTORS = new HashMap<>();
    static {
        //factores de conversiones de la massa
        FACTORS.put("kg", 1.0);
        FACTORS.put("g", 0.001);
        FACTORS.put("mg", 0.000001);
        FACTORS.put("lb", 0.45359237);
        FACTORS.put("oz", 0.028349523125);
        FACTORS.put("t", 1000.0);
    }

    public static double convert(double value, String from, String to){
        if (!FACTORS.containsKey(from) || !FACTORS.containsKey(to)){
            throw new IllegalArgumentException("Unidad no valida");
        }

        // convertir siempre primero a kg
        double valueInkg = value * FACTORS.get(from);
        // convertir de kg a la unidad seleccionada
        return valueInkg / FACTORS.get(to);
    }
}
