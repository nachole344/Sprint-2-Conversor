package com.example.sprint2conversor.logic;

public class Conversor {

    // ==============================
    //      CONVERSIONES SIMPLES
    // ==============================

    public double convertirLongitud(double longitudOriginal, lengthUnits origen, lengthUnits destino) throws IllegalArgumentException {
        double longitudFinal = longitudOriginal;

        // Si el destino y la origen son iguales, no se hace nada
        if (origen == destino){
            return longitudFinal;
        }

        // Convertir el valor original a metros
        switch (origen) {
            case MILIMETROS:
                longitudFinal /= 1000;
                break;
            case CENTIMETROS:
                longitudFinal /= 100;
                break;
            case DECIMETROS:
                longitudFinal /= 10;
                break;
            case METROS:
                longitudFinal /= 1;
                break;
            case KILOMETROS:
                longitudFinal *= 1000;
                break;
            case PULGADAS:
                longitudFinal /= 39.37;
                break;
            case PIES:
                longitudFinal /= 3.281;
                break;
            case YARDAS:
                longitudFinal /= 1.094;
                break;
            case MILLAS:
                longitudFinal *= 1609.34;
                break;
            default: // No deberia suceder
                throw new IllegalArgumentException("Unidad de longitud origen no reconocida: " + origen);

        }

        // De metros se convierte a la unidad deseada
        switch (destino) {
            case MILIMETROS:
                longitudFinal *= 1000;
                break;
            case CENTIMETROS:
                longitudFinal *= 100;
                break;
            case DECIMETROS:
                longitudFinal *= 10;
                break;
            case METROS:
                longitudFinal *= 1;
                break;
            case KILOMETROS:
                longitudFinal /= 1000;
                break;
            case PULGADAS:
                longitudFinal *= 39.37;
                break;
            case PIES:
                longitudFinal *= 3.281;
                break;
            case YARDAS:
                longitudFinal *= 1.094;
                break;
            case MILLAS:
                longitudFinal /= 1609.34;
                break;
            default: // No deberia suceder
                throw new IllegalArgumentException("Unidad de longitud destino no reconocida: " + origen);

        }

        return longitudFinal;

    }

    public double convertirTiempo(double tiempoOriginal, timeUnits origen, timeUnits destino) throws IllegalArgumentException {
        double tiempoFinal = tiempoOriginal;

        // Si el destino y la origen son iguales, no se hace nada
        if (origen == destino){
            return tiempoFinal;
        }

        // Se cambian las unidades de origen a horas
        switch (origen) {
            case MICROSEGUNDOS:
                tiempoFinal /= 3.6 * Math.pow(10, 9);
                break;
            case MILISEGUNDOS:
                tiempoFinal /= 3.6 * Math.pow(10, 6);
                break;
            case SEGUNDOS:
                tiempoFinal /= 3600;
                break;
            case MINUTOS:
                tiempoFinal /= 60;
                break;
            case HORAS:
                tiempoFinal /= 1;
                break;
            case DIAS:
                tiempoFinal *= 24;
                break;
            case SEMANAS:
                tiempoFinal *= 168;
                break;
            case MESES:
                tiempoFinal *= 730;
                break;
            case AÑOS:
                tiempoFinal *= 8760;
                break;
            default: // No deberia suceder
                throw new IllegalArgumentException("Unidad de tiempo origen no reconocida: " + origen);

        }
        // Se cambian horas a la unidad deseada
        switch (destino) {
            case MICROSEGUNDOS:
                tiempoFinal *= 3.6 * Math.pow(10, 9);
                break;
            case MILISEGUNDOS:
                tiempoFinal *= 3.6 * Math.pow(10, 6);
                break;
            case SEGUNDOS:
                tiempoFinal *= 3600;
                break;
            case MINUTOS:
                tiempoFinal *= 60;
                break;
            case HORAS:
                tiempoFinal *= 1;
                break;
            case DIAS:
                tiempoFinal /= 24;
                break;
            case SEMANAS:
                tiempoFinal /= 168;
                break;
            case MESES:
                tiempoFinal /= 730;
                break;
            case AÑOS:
                tiempoFinal /= 8760;
                break;
            default: // No deberia suceder
                throw new IllegalArgumentException("Unidad de tiempo destino no reconocida: " + destino);

        }

        return tiempoFinal;

    }

    public double convertirAlmacenamiento(double almacenamientoOriginal, storageUnits origen, storageUnits destino) throws IllegalArgumentException {
        double almacenamientoFinal = almacenamientoOriginal;

        // Si el destino y la origen son iguales, no se hace nada
        if (origen == destino){
            return almacenamientoFinal;
        }

        // Se convierten las unidades originales a bytes
        switch (origen) {
            case BIT:
                almacenamientoFinal /= 8;
                break;
            case BYTE:
                almacenamientoFinal /= 1;
                break;
            case KILOBYTE:
                almacenamientoFinal *= 1024;
                break;
            case MEGABYTE:
                almacenamientoFinal *= Math.pow(1024, 2);;
                break;
            case GIGABYTE:
                almacenamientoFinal *= Math.pow(1024, 3);
                break;
            case TERABYTE:
                almacenamientoFinal *= Math.pow(1024, 4);
                break;
            default: // No deberia suceder
                throw new IllegalArgumentException("Unidad de almacenamiento origen no reconocida: " + origen);
        }

        // Se convierten bytes a la unidad deseada
        switch (destino){
            case BIT:
                almacenamientoFinal *= 8;
                break;
            case BYTE:
                almacenamientoFinal *= 1;
                break;
            case KILOBYTE:
                almacenamientoFinal /= 1024;
                break;
            case MEGABYTE:
                almacenamientoFinal /= Math.pow(1024, 2);;
                break;
            case GIGABYTE:
                almacenamientoFinal /= Math.pow(1024, 3);
                break;
            case TERABYTE:
                almacenamientoFinal /= Math.pow(1024, 4);
                break;
            default: // No deberia suceder
                throw new IllegalArgumentException("Unidad de almacenamiento destino no reconocida: " + destino);

        }

        return almacenamientoFinal;
    }

    // ================================
    //      CONVERSIONES COMPLEJAS
    // ================================

    public double convertirVelocidad(double velocidadOriginal, speedUnits origen, speedUnits destino) throws IllegalArgumentException {
        double velocidadFinal = velocidadOriginal;

        // Si el destino y la origen son iguales, no se hace nada
        if (origen == destino){
            return velocidadFinal;
        }

        // Se convierten las unidades originales a Km/h
        switch (origen) {
            case METROS_POR_SEGUNDO:
                velocidadFinal /= 3.6;
                break;
            case KILOMETROS_POR_HORA:
                velocidadFinal /= 1;
                break;
            case PIE_POR_SEGUNDO:
                velocidadFinal *= 1.09728;
                break;
            case MILLAS_POR_HORA:
                velocidadFinal *= 1.60934;
                break;
            case MATCH:
                velocidadFinal *= 1.225;
                break;
            case NUDO:
                velocidadFinal *= 1.852;
                break;
            case C:
                velocidadFinal *= 1.079 * Math.pow(10, 9);
                break;
            default: // No deberia suceder
                throw new IllegalArgumentException("Unidad de velocidad origen no reconocida: " + origen);

        }

        // Se convierten Km/h a la unidad deseada
        switch (destino){
            case METROS_POR_SEGUNDO:
                velocidadFinal *= 3.6;
                break;
            case KILOMETROS_POR_HORA:
                velocidadFinal *= 1;
                break;
            case PIE_POR_SEGUNDO:
                velocidadFinal /= 1.09728;
                break;
            case MILLAS_POR_HORA:
                velocidadFinal /= 1.60934;
                break;
            case MATCH:
                velocidadFinal /= 1.225;
                break;
            case NUDO:
                velocidadFinal /= 1.852;
                break;
            case C:
                velocidadFinal /= 1.079 * Math.pow(10, 9);
                break;
            default: // No deberia suceder
                throw new IllegalArgumentException("Unidad de velocidad origen no reconocida: " + origen);

        }

        return velocidadFinal;

    }
}
