package com.example.sprint2conversor.logic;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

// registro central de categorias de conversion.
//
// para anadir una categoria nueva basta con llamar a register(...) con su
// unitconverter: las pantallas la muestran y la convierten sin tocar nada mas.
public final class Categories {

    // claves que viajan en el intent entre las dos pantallas
    // claves de cada categoria: son los textos que viajan en el intent de una pantalla a otra
    // las pongo como constantes pa no equivocarme escribiendolas a mano
    public static final String LENGTH = "Longitud";
    public static final String WEIGHT = "Peso";
    public static final String TEMPERATURE = "Temperatura";
    public static final String SPEED = "Velocidad";
    public static final String VOLUME = "Volumen";
    public static final String CURRENCY = "Monedas";
    public static final String TIME = "Tiempo";
    public static final String DIGITAL = "Datos";
    public static final String AREA = "Area";
    public static final String PRESSURE = "Presion";

    // pantallas especiales (no usan unitconverter)
    // matematicas y programacion no usan unitconverter, tienen su propia pantalla
    public static final String MATH = "Matematicas";
    public static final String PROGRAMMER = "Programacion";

    // nota que se muestra bajo el conversor de monedas
    // aviso que sale en monedas: las tasas son de ejemplo y no se actualizan solas
    // el enunciado pide explicar de donde salen las tasas, por eso lo pongo visible
    private static final String CURRENCY_NOTE =
            "Tasas de ejemplo con fines demostrativos: son valores fijos del código "
                    + "y no se actualizan en vivo. En una versión real se obtendrían de "
                    + "una API de tipos de cambio y se guardarían en caché.";

    // datos de una categoria
    // una categoria es solo un paquete de datos: titulo, subtitulo, unidades y reglas
    // es 'static' porque no necesita un objeto categories pa existir
    public static final class Category {
        public final String key;
        public final String title;
        public final String subtitle;
        // nota opcional, si es null no se muestra nada
        public final String note; // puede ser null
        // solo la temperatura deja poner negativos (-10 grados tiene sentido, -10 kg no)
        public final boolean allowNegative; // solo la temperatura admite negativos
        // el conversor con las unidades de esta categoria
        public final UnitConverter converter;

        // constructor: solo guarda los datos que le paso
        Category(String key, String title, String subtitle, String note,
                 boolean allowNegative, UnitConverter converter) {
            this.key = key;
            this.title = title;
            this.subtitle = subtitle;
            this.note = note;
            this.allowNegative = allowNegative;
            this.converter = converter;
        }
    }

    // clave normalizada (sin acentos, minusculas) -> categoria
    // diccionario pa buscar una categoria por su clave
    // la clave se guarda normalizada (sin tildes ni mayusculas) pa que 'presion' y 'presion' con tilde valgan igual
    private static final Map<String, Category> BY_KEY = new LinkedHashMap<>();

    // bloque estatico: se ejecuta una sola vez al cargar la clase y rellena el diccionario
    // pa anadir una categoria nueva solo hay que copiar un bloque register(...)
    static {
        // distancia (base: metro)
        // las unidades se definen con su factor respecto a la base (aqui el metro)
        // ej: 'km' vale 1000 metros, 'cm' vale 0.01 metros
        register(new Category(LENGTH,
                        "Conversor de longitud",
                        "Convierte entre diferentes unidades de distancia",
                        null, false,
                        new UnitConverter()
                                .add("m", 1)
                                .add("km", 1000)
                                .add("cm", 0.01)
                                .add("mm", 0.001)
                                .add("mi", 1609.344)
                                .add("yd", 0.9144)
                                .add("ft", 0.3048)
                                .add("in", 0.0254)),
                "distancia");

        // masa (base: kilogramo)
        // los alias ('peso y masa', 'masa') son otros nombres que tambien llevan a esta categoria
        register(new Category(WEIGHT,
                        "Conversor de masa",
                        "Convierte entre diferentes unidades de masa",
                        null, false,
                        new UnitConverter()
                                .add("kg", 1)
                                .add("g", 0.001)
                                .add("mg", 0.000001)
                                .add("lb", 0.45359237)
                                .add("oz", 0.028349523125)
                                .add("t", 1000)),
                "peso y masa", "masa");

        // temperatura (base: c) -> base = valor * escala + desplazamiento
        // f: c = f * 5/9 - 160/9        k: c = k - 273.15
        // la temperatura es la unica que usa desplazamiento (el tercer numero del add)
        // por eso no hace falta una estrategia aparte: la misma formula de unitconverter vale
        // true = esta categoria si admite valores negativos
        register(new Category(TEMPERATURE,
                "Conversor de temperatura",
                "Convierte entre grados Celsius, Fahrenheit y Kelvin",
                null, true,
                new UnitConverter()
                        .add("°C", 1, 0)
                        .add("°F", 5.0 / 9.0, -160.0 / 9.0)
                        .add("K", 1, -273.15)));

        // velocidad (base: m/s)
        // km/h = 1000 m / 3600 s ; kn = nudos (1 milla nautica = 1852 m)
        register(new Category(SPEED,
                "Conversor de velocidad",
                "Convierte entre diferentes unidades de velocidad",
                null, false,
                new UnitConverter()
                        .add("m/s", 1)
                        .add("km/h", 1000.0 / 3600.0)
                        .add("mph", 0.44704)
                        .add("kn", 1852.0 / 3600.0)
                        .add("ft/s", 0.3048)));

        // volumen (base: litro)
        // base: litro
        register(new Category(VOLUME,
                "Conversor de volumen",
                "Convierte entre diferentes unidades de volumen",
                null, false,
                new UnitConverter()
                        .add("L", 1)
                        .add("mL", 0.001)
                        .add("m³", 1000)
                        .add("gal US", 3.785411784)
                        .add("fl oz US", 0.0295735295625)));

        // tiempo (base: segundo)
        // 'sem' = semana (7 dias = 604800 segundos)
        register(new Category(TIME,
                "Conversor de tiempo",
                "Convierte entre diferentes unidades de tiempo",
                null, false,
                new UnitConverter()
                        .add("s", 1)
                        .add("ms", 0.001)
                        .add("min", 60)
                        .add("h", 3600)
                        .add("d", 86400)
                        .add("sem", 604800)));

        // informacion digital (base: byte; 1 kb = 1024 b)
        // uso 1024 (no 1000) pa kb, mb, gb... porque asi se cuenta en informatica
        // 1 bit = 1/8 de byte = 0.125
        register(new Category(DIGITAL,
                        "Información digital",
                        "Convierte entre bits, bytes y sus múltiplos (1 KB = 1024 B)",
                        null, false,
                        new UnitConverter()
                                .add("bit", 0.125)
                                .add("B", 1)
                                .add("KB", 1024.0)
                                .add("MB", 1024.0 * 1024)
                                .add("GB", 1024.0 * 1024 * 1024)
                                .add("TB", 1024.0 * 1024 * 1024 * 1024)),
                "datos digitales", "informacion digital");

        // area (base: m2)
        // 1 hectarea = 10000 m2
        register(new Category(AREA,
                "Conversor de área",
                "Convierte entre diferentes unidades de superficie",
                null, false,
                new UnitConverter()
                        .add("m²", 1)
                        .add("cm²", 0.0001)
                        .add("km²", 1_000_000)
                        .add("ha", 10_000)
                        .add("acre", 4046.8564224)
                        .add("ft²", 0.09290304)));

        // presion (base: pascal)
        // 1 atm = 101325 pa ; 1 bar = 100000 pa
        register(new Category(PRESSURE,
                "Conversor de presión",
                "Convierte entre diferentes unidades de presión",
                null, false,
                new UnitConverter()
                        .add("Pa", 1)
                        .add("kPa", 1000)
                        .add("bar", 100_000)
                        .add("atm", 101_325)
                        .add("psi", 6894.757293168)
                        .add("mmHg", 133.322387415)));

        // monedas (base: euro). cada tasa es "unidades de moneda por 1 eur",
        // asi que la escala de la unidad es 1 / tasa.
        // en monedas cada tasa es 'cuantas unidades de esa moneda hay en 1 euro'
        // la escala de cada moneda es 1 / tasa, asi la misma formula de siempre funciona
        // ej: 1 usd = 1 / 1.12 euros
        // las tasas son de ejemplo (mira currency_note)
        register(new Category(CURRENCY,
                        "Conversor de monedas",
                        "Convierte entre diferentes divisas",
                        CURRENCY_NOTE, false,
                        new UnitConverter()
                                .add("Euro (EUR)", 1 / 1.0)
                                .add("Dólar (USD)", 1 / 1.12)
                                .add("Peso chileno (CLP)", 1 / 1087.36)
                                .add("Peso argentino (ARS)", 1 / 1729.71)
                                .add("Won surcoreano (KRW)", 1 / 1505.05)
                                .add("Yen japonés (JPY)", 1 / 177.02)
                                .add("Peso mexicano (MXN)", 1 / 20.82)
                                .add("Libra esterlina (GBP)", 1 / 0.84)
                                .add("Yuan chino (CNY)", 1 / 7.47)
                                .add("Franco (FRANCO)", 1 / 1655.40)
                                // 1 social = 10 cny  ->  1 eur = 0,747 social
                                .add("Social Credit (SOCIAL)", 1 / (7.47 / 10.0))),
                "moneda");
    }

    // constructor privado: nadie puede hacer new categories(), solo se usan sus metodos estaticos
    private Categories() {
        // clase de utilidades: no se instancia
    }

    // mete la categoria en el diccionario con su clave y con todos sus alias
    // los '...' (varargs) permiten pasar 0, 1 o varios alias
    private static void register(Category category, String... aliases) {
        BY_KEY.put(normalize(category.key), category);

        for (String alias : aliases) {
            BY_KEY.put(normalize(alias), category);
        }
    }

    // busca una categoria por su clave (ignora acentos y mayusculas). null si no existe
    // busca una categoria por su clave. si no existe devuelve null (la activity lo comprueba)
    public static Category find(String key) {
        return key == null ? null : BY_KEY.get(normalize(key));
    }

    // quita acentos y mayusculas: "matematicas" -> "matematicas"
    // quita tildes y mayusculas pa poder comparar textos sin problemas
    public static String normalize(String value) {
        if (value == null) {
            return "";
        }

        // normalizer separa cada letra de su tilde y el replaceall borra las tildes (regex \p{m})
        String text = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return text.trim().toLowerCase(Locale.ROOT);
    }
}