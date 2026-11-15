package com.example.sprint2conversor.logic;

public class Conversor {

    /** Devuelve si esta categoria se convierte con los enums de este conversor. */
    public boolean soportaCategoria(String categoria) {
        if (categoria == null) return false;
        switch (Categories.normalize(categoria)) {
            case "longitud":
            case "velocidad":
            case "tiempo":
            case "datos":
            case "energia":
                return true;
            default:
                return false;
        }
    }

    /** Unidades visibles para las categorias implementadas mediante enums. */
    public String[] obtenerUnidades(String categoria) {
        switch (Categories.normalize(categoria)) {
            case "longitud": return abreviaciones(lengthUnits.values());
            case "velocidad": return abreviaciones(speedUnits.values());
            case "tiempo": return abreviaciones(timeUnits.values());
            case "datos": return abreviaciones(storageUnits.values());
            case "energia": return abreviaciones(energyUnits.values());
            default: throw new IllegalArgumentException("Categoría no reconocida: " + categoria);
        }
    }

    /** Convierte desde los nombres mostrados en los desplegables. */
    public double convertir(String categoria, double valor, String origen, String destino) {
        switch (Categories.normalize(categoria)) {
            case "longitud":
                return convertirLongitud(valor, lengthUnits.getUnit(origen), lengthUnits.getUnit(destino));
            case "velocidad":
                return convertirVelocidad(valor, speedUnits.getUnit(origen), speedUnits.getUnit(destino));
            case "tiempo":
                return convertirTiempo(valor, timeUnits.getUnit(origen), timeUnits.getUnit(destino));
            case "datos":
                return convertirAlmacenamiento(valor, storageUnits.getUnit(origen), storageUnits.getUnit(destino));
            case "energia":
                return convertirEnergia(valor, energyUnits.getUnit(origen), energyUnits.getUnit(destino));
            default: throw new IllegalArgumentException("Categoría no reconocida: " + categoria);
        }
    }

    private static String[] abreviaciones(Enum<?>[] unidades) {
        String[] resultado = new String[unidades.length];
        for (int i = 0; i < unidades.length; i++) {
            Enum<?> unidad = unidades[i];
            if (unidad instanceof lengthUnits) resultado[i] = ((lengthUnits) unidad).getAbreviacion();
            else if (unidad instanceof speedUnits) resultado[i] = ((speedUnits) unidad).getAbreviacion();
            else if (unidad instanceof timeUnits) resultado[i] = ((timeUnits) unidad).getAbreviacion();
            else if (unidad instanceof storageUnits) resultado[i] = ((storageUnits) unidad).getAbreviacion();
            else if (unidad instanceof energyUnits) resultado[i] = ((energyUnits) unidad).getAbreviacion();
        }
        return resultado;
    }

    // ==============================
    //      CONVERSIONES SIMPLES
    // ==============================

    // Longitud
    public double convertirLongitud(double longitudOriginal, lengthUnits origen, lengthUnits destino) {
        double longitudFinal = longitudOriginal;

        // Si el destino y la origen son iguales, no se hace nada
        if (origen == destino){
            return longitudFinal;
        }

        // Convertir el valor original a metros
        if (origen.getMayorAlBase()){
            longitudFinal *= origen.getFactorConversion();
        } else {
            longitudFinal /= origen.getFactorConversion();
        }

        // De metros se convierte a la unidad deseada
        if (destino.getMayorAlBase()){
            longitudFinal /= destino.getFactorConversion();
        } else {
            longitudFinal *= destino.getFactorConversion();
        }

        return longitudFinal;

    }

    // Tiempo
    public double convertirTiempo(double tiempoOriginal, timeUnits origen, timeUnits destino) {
        double tiempoFinal = tiempoOriginal;

        // Si el destino y la origen son iguales, no se hace nada
        if (origen == destino){
            return tiempoFinal;
        }

        // Convertir el valor original a horas
        if (origen.getMayorAlBase()){
            tiempoFinal *= origen.getFactorConversion();
        } else {
            tiempoFinal /= origen.getFactorConversion();
        }

        // De horas se convierte a la unidad deseada
        if (destino.getMayorAlBase()){
            tiempoFinal /= destino.getFactorConversion();
        } else {
            tiempoFinal *= destino.getFactorConversion();
        }

        return tiempoFinal;

    }

    // Almacenamiento
    public double convertirAlmacenamiento(double almacenamientoOriginal, storageUnits origen, storageUnits destino) throws IllegalArgumentException {
        double almacenamientoFinal = almacenamientoOriginal;

        // Si el destino y la origen son iguales, no se hace nada
        if (origen == destino){
            return almacenamientoFinal;
        }

        // Convertir el valor original a bytes
        if (origen.getMayorAlBase()){
            almacenamientoFinal *= origen.getFactorConversion();
        } else {
            almacenamientoFinal /= origen.getFactorConversion();
        }

        // De bytes se convierte a la unidad deseada
        if (destino.getMayorAlBase()){
            almacenamientoFinal /= destino.getFactorConversion();
        } else {
            almacenamientoFinal *= destino.getFactorConversion();
        }

        return almacenamientoFinal;

    }

    // ================================
    //      CONVERSIONES COMPLEJAS
    // ================================

    // Velocidad
    public double convertirVelocidad(double velocidadOriginal, speedUnits origen, speedUnits destino) throws IllegalArgumentException {
        double velocidadFinal = velocidadOriginal;

        // Si el destino y la origen son iguales, no se hace nada
        if (origen == destino){
            return velocidadFinal;
        }

        // Convertir el valor original a km/h
        if (origen.getMayorAlBase()){
            velocidadFinal *= origen.getFactorConversion();
        } else {
            velocidadFinal /= origen.getFactorConversion();
        }

        // De km/h se convierte a la unidad deseada
        if (destino.getMayorAlBase()){
            velocidadFinal /= destino.getFactorConversion();
        } else {
            velocidadFinal *= destino.getFactorConversion();
        }

        return velocidadFinal;

    }

    // Energia
    public double convertirEnergia(double energiaOriginal, energyUnits origen, energyUnits destino) throws IllegalArgumentException {
        double energiaFinal = energiaOriginal;

        // Si el destino y la origen son iguales, no se hace nada
        if (origen == destino){
            return energiaFinal;
        }

        // Convertir el valor original a Julios
        if (origen.getMayorAlBase()){
            energiaFinal *= origen.getFactorConversion();
        } else {
            energiaFinal /= origen.getFactorConversion();
        }

        // De Julios se convierte a la unidad deseada
        if (destino.getMayorAlBase()){
            energiaFinal /= destino.getFactorConversion();
        } else {
            energiaFinal *= destino.getFactorConversion();
        }

        return energiaFinal;
    }
}
