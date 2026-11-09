package com.example.sprint2conversor.logic;

import java.util.HashMap;
import java.util.Map;

public class VolumeConverter {

    private static final Map<String, Double> FACTORS = new HashMap<>();

    static {
        //factores de conversiones de litros
        FACTORS.put("L", 1.0);
        FACTORS.put("mL", 0.001);
        FACTORS.put("m³", 1000.0);
        FACTORS.put("gal US", 3.785411784);
    }

    public static double convert(double value, String from, String to){
        if (!FACTORS.containsKey(from) || !FACTORS.containsKey(to)){
            throw new IllegalArgumentException("Unidad no valida");
        }

        // convertir siempre primero a litros
        double valueInLitres = value * FACTORS.get(from);
        // convertir de litros a la unidad seleccionada
        return valueInLitres / FACTORS.get(to);
    }
}

