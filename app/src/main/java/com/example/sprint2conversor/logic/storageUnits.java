package com.example.sprint2conversor.logic;

public enum storageUnits {
    BIT("Bit", 8, false),
    BYTE("Byte", 1, true),
    KILOBYTE("KB", 1024, true),
    MEGABYTE("MB", Math.pow(1024, 2), true),
    GIGABYTE("GB", Math.pow(1024, 3), true),
    TERABYTE("TB", Math.pow(1024, 4), true);

    // Atributos
    private final String abreviacion;
    private final double factorConversion;
    private final boolean mayorAlBase;

    storageUnits(String abreviacion, double factorConversion, boolean mayorAlBase) {
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

    public static storageUnits getUnit(String abreviacion) throws IllegalArgumentException {
        for (storageUnits unit : storageUnits.values()) {
            if (unit.getAbreviacion().equalsIgnoreCase(abreviacion)) {
                return unit;
            }
        }

        throw new IllegalArgumentException("Unidad de almacenamiento no reconocida: " + abreviacion);
    }
}
