package com.example.sprint2conversor.logic;

import java.util.LinkedHashMap;
import java.util.Map;

// conversor generico para cualquier categoria de unidades.
//
// cada unidad se describe con dos numeros respecto a una unidad base comun:
//
// base = valor * escala + desplazamiento
//
// - unidades lineales (metros, kilos, litros...): solo escala (desplazamiento = 0).
// - temperatura: escala y desplazamiento, porque sus ceros no coinciden.
//
// para convertir entre dos unidades cualesquiera se pasa siempre por la base:
//
// resultado = (valor * escalaorigen + desplorigen - despldestino) / escaladestino
//
// asi no hace falta ningun if/switch por cada pareja de unidades:
// anadir una unidad nueva es anadir una linea con .add(...).
// esta clase es el cerebro de todas las conversiones de unidades
// la idea: todas las unidades de una categoria se comparan con una unidad base
// asi no hace falta un if por cada pareja (km->mi, m->ft, ...)
// con n unidades solo guardo n datos en vez de n*n formulas
public class UnitConverter {

    // nombre de la unidad -> {escala, desplazamiento}
    // linkedhashmap mantiene el orden en el que se muestran en el spinner
    // mapa: nombre de la unidad (ej 'km') -> array de 2 numeros {escala, desplazamiento}
    // uso linkedhashmap y no hashmap pa que se mantenga el orden en el que las anado
    // y asi salen en el spinner en ese mismo orden
    // final = la referencia no cambia, pero si puedo meter cosas dentro del mapa
    private final Map<String, double[]> units = new LinkedHashMap<>();

    // unidad lineal: base = valor * escala
    // version corta pa unidades normales (metros, kilos...): el desplazamiento es 0
    // simplemente llama a la version larga pasandole un 0
    public UnitConverter add(String name, double scale) {
        return add(name, scale, 0.0);
    }

    // unidad con desplazamiento: base = valor * escala + desplazamiento
    // version larga: la usa la temperatura porque sus ceros no coinciden
    // devuelve 'this' pa poder encadenar .add().add().add() como en categories
    public UnitConverter add(String name, double scale, double offset) {
        // validaciones: la escala no puede ser 0 porque luego dividimos entre ella
        // y ningun numero puede ser infinito ni nan
        if (scale == 0 || !Double.isFinite(scale) || !Double.isFinite(offset)) {
            throw new IllegalArgumentException("Factor no válido para " + name);
        }

        // guardamos la unidad: la clave es el nombre y el valor el array con los 2 numeros
        units.put(name, new double[]{scale, offset});
        return this; // permite encadenar: new unitconverter().add(...).add(...)
    }

    // unidades disponibles, en el orden en el que se anadieron
    // devuelve los nombres de las unidades pa rellenar los spinners
    // keyset() = solo las claves del mapa, y toarray lo pasa a string[]
    public String[] getUnits() {
        return units.keySet().toArray(new String[0]);
    }

    // metodo principal, sirve pa cualquier categoria
    // pasos: 1) pasar el valor a la unidad base  2) pasar de la base a la unidad destino
    public double convert(double value, String from, String to) {
        // buscamos los datos de la unidad de origen y los de la de destino
        // source[0] = escala, source[1] = desplazamiento (igual con target)
        // si el nombre no existe, get devuelve null
        double[] source = units.get(from);
        double[] target = units.get(to);

        // si alguna unidad no existe lanzo un error, la activity lo muestra con un snackbar
        if (source == null || target == null) {
            throw new IllegalArgumentException("Unidad no válida");
        }

        // isfinite da false si el valor es nan o infinito
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("El valor no es válido");
        }

        // 1) a la unidad base   2) de la unidad base a la unidad destino
        // formula: base = valor * escala + desplazamiento
        // ejemplo: 100 f a c -> 100 * 5/9 - 160/9 = 37.77 (ya en grados c, que es la base)
        double base = value * source[0] + source[1];
        // formula inversa: de la base a la unidad destino, se despeja: (base - desplazamiento) / escala
        // ejemplo: 37.77 c a k -> (37.77 - (-273.15)) / 1 = 310.93
        return (base - target[1]) / target[0];
    }
}