package com.example.sprint2conversor.logic;

public enum speedUnits {

    // Sistema Internacional
    METROS_POR_SEGUNDO("m/s", 3.6, true),
    KILOMETROS_POR_HORA("Km/h", 1, true),

    // Sistema Imperial
    PIE_POR_SEGUNDO("ft/s", 1.09728, true),
    MILLAS_POR_HORA("mph", 1.60934, true),

    // Otros
    NUDO("kn", 1.852, true),                 //     --> Milla nautica por hora
    MATCH("Match", 1234.8, true),            //     --> Velocidad del Sonido
    C("Luz", 1.079 * Math.pow(10, 9), true); //     --> Velocidad de la luz

    // Atributos
    private final String abreviacion;
    private final double factorConversion;
    private final boolean mayorAlBase;

    speedUnits(String abreviacion, double factorConversion, boolean mayorAlBase) {
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

    public static speedUnits getUnit(String abreviacion) throws IllegalArgumentException {
        for (speedUnits unit : speedUnits.values()) {
            if (unit.getAbreviacion().equalsIgnoreCase(abreviacion)) {
                return unit;
            }
        }

        throw new IllegalArgumentException("Unidad de velocidad no reconocida: " + abreviacion);
    }

}
