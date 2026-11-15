package com.example.sprint2conversor.logic;

public enum energyUnits {
    JULIO("J", 1, true),
    KILOVATIO_HORA("kWh", 3600000, true),
    CALORIA("cal", 4.184, true),
    KILOCALORIA("kcal", 4184, true),
    ELECTRONVOLTIO("eV", 1.602 * Math.pow(10, -19), true),
    BTU("BTU", 1055.05, true);


    // Atributos
    private final String abreviacion;
    private final double factorConversion;
    private final boolean mayorAlBase;

    energyUnits(String abreviacion, double factorConversion, boolean mayorAlBase) {
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

    public static energyUnits getUnit(String abreviacion) throws IllegalArgumentException {
        for (energyUnits unit : energyUnits.values()) {
            if (unit.getAbreviacion().equalsIgnoreCase(abreviacion)) {
                return unit;
            }
        }

        throw new IllegalArgumentException("Unidad de energia no reconocida: " + abreviacion);
    }

}
