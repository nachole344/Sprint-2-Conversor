package com.example.sprint2conversor.logic;

public enum lengthUnits {
    // Sistema internacional
    MILIMETROS("mm", 1000, false),
    CENTIMETROS("cm", 100, false),
    DECIMETROS("dm", 10, false),
    METROS("m", 1, true),
    KILOMETROS("km", 1000, true),

    // Sistema imperial
    PULGADAS("in", 39.37, false),
    PIES("ft", 3.281, false),
    YARDAS("yd", 1.094, false),
    MILLAS("mi", 1609.34, true);


    // Atributos
    private final String abreviacion;
    private final double factorConversion;
    private final boolean mayorAlBase;

    lengthUnits(String abreviacion, double factorConversion, boolean mayorAlBase) {
        this.abreviacion = abreviacion;
        this.factorConversion = factorConversion;
        this.mayorAlBase = mayorAlBase;
    }

    public String getAbreviacion() {
        return abreviacion;
    }

    public double getFactorConversion() {
        return factorConversion;
    }

    public boolean getMayorAlBase() {
        return mayorAlBase;
    }

    public static lengthUnits getUnit(String abreviacion) throws IllegalArgumentException {
        for (lengthUnits unit : lengthUnits.values()) {
            if (unit.getAbreviacion().equalsIgnoreCase(abreviacion)) {
                return unit;
            }
        }

        throw new IllegalArgumentException("Unidad de longitud no reconocida: " + abreviacion);
    }
}
