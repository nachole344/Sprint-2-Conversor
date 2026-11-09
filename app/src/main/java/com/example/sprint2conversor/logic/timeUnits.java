package com.example.sprint2conversor.logic;

public enum timeUnits {
    MICROSEGUNDOS("µs", 3.6 * Math.pow(10, 9), false),
    MILISEGUNDOS("ms", 3.6 * Math.pow(10, 6), false),
    SEGUNDOS("s", 3600, false),
    MINUTOS("min", 60, false),
    HORAS("h", 1, true),
    DIAS("dias", 24, true),
    SEMANAS("semanas", 168, true),
    MESES("meses", 730, true),
    AÑOS("años", 8760, true);


    // Atributos
    private final String abreviacion;
    private final double factorConversion;
    private final boolean mayorAlBase;

    timeUnits(String abreviacion, double factorConversion, boolean mayorAlBase) {
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

    public static timeUnits getUnit(String abreviacion) throws IllegalArgumentException {
        for (timeUnits unit : timeUnits.values()) {
            if (unit.getAbreviacion().equalsIgnoreCase(abreviacion)) {
                return unit;
            }
        }

        throw new IllegalArgumentException("Unidad de tiempo no reconocida: " + abreviacion);
    }

}
