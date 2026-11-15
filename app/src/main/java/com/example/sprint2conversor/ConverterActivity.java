package com.example.sprint2conversor;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sprint2conversor.logic.Categories;
import com.example.sprint2conversor.logic.MathConverter;
import com.example.sprint2conversor.logic.ProgrammerConverter;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

// esta es la activity 2: hace de 3 pantallas distintas segun la categoria que le llegue
// 1) conversor normal (activity_converter.xml) pa las categorias de unidades
// 2) matematicas (activity_math.xml)
// 3) programacion (activity_programmer.xml)
public class ConverterActivity extends AppCompatActivity {

    // claves que mainactivity envia en el intent
    // clave del dato que manda mainactivity en el intent, tiene que ser la misma en las dos pantallas
    public static final String EXTRA_CATEGORY = "category";

    // colores de los desplegables (spinners)
    // colores en formato argb (0xffrrggbb) pa los spinners
    private static final int COLOR_TEXT = 0xFFF1F5F9;
    private static final int COLOR_DROPDOWN_BG = 0xFF111B2D;

    // formato espanol: coma pa los decimales y punto pa los miles
    private static final Locale SPANISH = Locale.forLanguageTag("es-ES");

    // variables de conversor

    // vistas del conversor normal: lo que escribes, lo que sale, los spinners y los textos
    private EditText etValorOrigen, etValorResultado;
    private Spinner spUnidadOrigen, spUnidadDestino;
    private TextView tvResultado, tvTitulo, tvSubtitulo, tvNota;

    // categoria actual del conversor (null si la clave no existe)
    // categoria que se esta usando (tiene el titulo, las unidades y el conversor)
    private Categories.Category currentCategory;

    // id de la vista raiz de la pantalla actual (para snackbar e insets)
    // id de la vista raiz de cada pantalla, lo uso pa el snackbar y pa los insets
    private int rootViewId = android.R.id.content;

    // variables mates

    // vistas de la pantalla de matematicas, un grupo por cada operacion
    private EditText etNumeradorFraccion1, etDenominadorFraccion1;
    private EditText etNumeradorFraccion2, etDenominadorFraccion2;

    private EditText etPorcentaje, etValorPorcentaje;
    private EditText etNumeroRaiz;
    private EditText etBasePotencia, etExponentePotencia;
    private EditText etAngulo;

    private Spinner spOperacionFraccion;
    private Spinner spDireccionAngulo;

    private TextView tvResultadoFraccion;
    private TextView tvResultadoPorcentaje;
    private TextView tvResultadoRaiz;
    private TextView tvResultadoPotencia;
    private TextView tvResultadoAngulo;

    // variables programacion

    // orden de los modos 0..11: {baseorigen, basedestino}
    // tabla de pares: asi no hace falta un case por cada conversion de bases
    // el modo 0 (decimal -> binario) usa la fila 0 = {10, 2}, el modo 1 la fila 1, etc
    private static final int[][] BASE_PAIRS = {
            {10, 2}, {2, 10},
            {10, 8}, {8, 10},
            {10, 16}, {16, 10},
            {2, 8}, {8, 2},
            {2, 16}, {16, 2},
            {8, 16}, {16, 8}
    };

    // numeros de los modos que no son pares de bases, coinciden con la posicion en el spinner
    private static final int MODE_CUSTOM_BASE = 12;
    private static final int MODE_TEXT_TO_ASCII = 13;
    private static final int MODE_ASCII_TO_TEXT = 14;
    private static final int MODE_TEXT_TO_HEX = 15;
    private static final int MODE_HEX_TO_TEXT = 16;
    private static final int MODE_ASCII_TO_HEX = 17;
    private static final int MODE_HEX_TO_ASCII = 18;

    // se ejecuta al abrir la pantalla, decide que diseno cargar segun la categoria
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getintent() = el intent con el que nos abrieron, de ahi sacamos la categoria que mando la activity 1
        String requested = getIntent().getStringExtra(EXTRA_CATEGORY);
        String key = Categories.normalize(requested == null ? Categories.WEIGHT : requested);

        // pantalla matematicas
        // el return de dentro corta oncreate, asi no sigue con el conversor normal
        if (key.equals("matematicas")) {

            setContentView(R.layout.activity_math);
            rootViewId = R.id.rootMath;
            applySystemBarInsets();
            configureMathScreen();
            return;
        }

        // pantalla programacion
        if (key.equals("programacion")) {

            setContentView(R.layout.activity_programmer);
            rootViewId = R.id.rootProgrammer;
            applySystemBarInsets();
            configureProgrammerScreen();
            return;
        }

        // conversor normal
        // si no es ninguna de las anteriores es un conversor de unidades normal
        setContentView(R.layout.activity_converter);
        rootViewId = R.id.rootConverter;
        applySystemBarInsets();

        // primero enlazamos las vistas y luego configuramos la categoria
        initializeConverterViews();
        configureCategory(key);
    }

    // pantalla programacion

    // prepara la pantalla de programacion: spinners, boton convertir y boton volver
    private void configureProgrammerScreen() {

        // finish() cierra esta activity y volvemos a la anterior (la de categorias)
        View btnBack = findViewById(R.id.btnBackProgrammer);
        btnBack.setOnClickListener(v -> finish());

        Spinner spProgrammerMode = findViewById(R.id.spProgrammerMode);
        Spinner spBaseFrom = findViewById(R.id.spBaseFrom);
        Spinner spBaseTo = findViewById(R.id.spBaseTo);

        EditText etProgrammerValue = findViewById(R.id.etProgrammerValue);
        Button btnProgrammerConvert = findViewById(R.id.btnProgrammerConvert);
        TextView tvProgrammerResult = findViewById(R.id.tvProgrammerResult);
        LinearLayout layoutCustomBases = findViewById(R.id.layoutCustomBases);

        // modos de conversion (el orden debe coincidir con base_pairs y mode_*)
        // el orden de esta lista importa: la posicion en el spinner es el numero de modo
        String[] modes = {
                "Decimal → Binario",
                "Binario → Decimal",
                "Decimal → Octal",
                "Octal → Decimal",
                "Decimal → Hexadecimal",
                "Hexadecimal → Decimal",
                "Binario → Octal",
                "Octal → Binario",
                "Binario → Hexadecimal",
                "Hexadecimal → Binario",
                "Octal → Hexadecimal",
                "Hexadecimal → Octal",
                "Base personalizada",
                "Texto → ASCII",
                "ASCII → Texto",
                "Texto → HEX",
                "HEX → Texto",
                "ASCII → HEX",
                "HEX → ASCII"
        };

        // el adapter es lo que rellena el spinner con la lista de textos
        spProgrammerMode.setAdapter(createSpinnerAdapter(modes, 14, 14));

        // bases disponibles: 2 hasta 36
        // 35 bases: de la 2 a la 36
        String[] bases = new String[35];

        for (int i = 0; i < bases.length; i++) {
            bases[i] = String.valueOf(i + 2);
        }

        spBaseFrom.setAdapter(createSpinnerAdapter(bases, 14, 14));
        spBaseTo.setAdapter(createSpinnerAdapter(bases, 14, 14));

        spBaseFrom.setSelection(8); // base 10
        spBaseTo.setSelection(0); // base 2

        // mostrar las bases personalizadas solamente cuando corresponda
        // y adaptar la ayuda del campo de entrada al tipo de conversion
        // este listener salta cada vez que el usuario cambia de modo en el spinner
        spProgrammerMode.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        // solo se ven los spinners de base origen y destino en el modo 'base personalizada'
                        layoutCustomBases.setVisibility(
                                position == MODE_CUSTOM_BASE
                                        ? View.VISIBLE
                                        : View.GONE
                        );

                        // cambiamos el texto de ayuda del campo segun el modo
                        etProgrammerValue.setHint(getProgrammerHint(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        // boton de conversion
        // todo va dentro de un try/catch pa que si algo falla salga un snackbar y no se cierre la app
        btnProgrammerConvert.setOnClickListener(v -> {

            // sin trim: en los modos de texto los espacios tambien cuentan
            String value = etProgrammerValue.getText().toString();

            try {
                // getselecteditemposition() = numero del modo elegido
                String result = calculateProgrammer(
                        spProgrammerMode.getSelectedItemPosition(),
                        value,
                        spBaseFrom.getSelectedItem().toString(),
                        spBaseTo.getSelectedItem().toString()
                );

                // mostramos el resultado y escondemos el teclado
                tvProgrammerResult.setText(result);
                hideKeyboard();

            } catch (IllegalArgumentException e) {
                showMessage(e.getMessage());
            } catch (Exception e) {
                showMessage("No se ha podido realizar la conversión.");
            }
        });
    }

    // devuelve el texto de ayuda segun el modo (numero, texto, ascii o hex)
    private String getProgrammerHint(int mode) {

        switch (mode) {
            case MODE_TEXT_TO_ASCII:
            case MODE_TEXT_TO_HEX:
                return "Introduce un texto...";

            case MODE_ASCII_TO_TEXT:
            case MODE_ASCII_TO_HEX:
                return "Códigos ASCII separados por espacios...";

            case MODE_HEX_TO_TEXT:
            case MODE_HEX_TO_ASCII:
                return "Introduce un valor hexadecimal...";

            default:
                return "Introduce un número...";
        }
    }

    // aqui se decide que conversion hacer segun el modo del spinner
    private String calculateProgrammer(
            int mode,
            String value,
            String baseFrom,
            String baseTo) {

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Introduce un valor para convertir."
            );
        }

        // conversiones entre bases predefinidas
        // los modos 0 a 11 son cambios de base fijos, salen de la tabla base_pairs
        if (mode >= 0 && mode < BASE_PAIRS.length) {
            return ProgrammerConverter.convertBase(
                    value,
                    BASE_PAIRS[mode][0],
                    BASE_PAIRS[mode][1]
            );
        }

        switch (mode) {

            // base personalizada: las bases vienen de los 2 spinners (son textos, hay que pasarlos a int)
            case MODE_CUSTOM_BASE:
                return ProgrammerConverter.convertBase(
                        value,
                        Integer.parseInt(baseFrom),
                        Integer.parseInt(baseTo)
                );

            case MODE_TEXT_TO_ASCII:
                return ProgrammerConverter.textToAscii(value);

            case MODE_ASCII_TO_TEXT:
                return ProgrammerConverter.asciiToText(value);

            case MODE_TEXT_TO_HEX:
                return ProgrammerConverter.textToHex(value);

            case MODE_HEX_TO_TEXT:
                return ProgrammerConverter.hexToText(value);

            // ascii -> hex: primero pasamos a texto y despues el texto a hex (encadenamos 2 conversiones)
            case MODE_ASCII_TO_HEX:
                return ProgrammerConverter.textToHex(
                        ProgrammerConverter.asciiToText(value)
                );

            case MODE_HEX_TO_ASCII:
                return ProgrammerConverter.textToAscii(
                        ProgrammerConverter.hexToText(value)
                );

            default:
                throw new IllegalArgumentException(
                        "Selecciona un tipo de conversión."
                );
        }
    }

    // conversor peso, volumen y monedas

    // enlaza las vistas del conversor normal con las variables de java
    private void initializeConverterViews() {

        // r.id.xxx es el id que puse en el xml con android:id
        etValorOrigen = findViewById(R.id.etValueFrom);
        etValorResultado = findViewById(R.id.etValueTo);

        spUnidadOrigen = findViewById(R.id.spFrom);
        spUnidadDestino = findViewById(R.id.spTo);

        tvResultado = findViewById(R.id.tvResult);
        tvTitulo = findViewById(R.id.tvTitle);
        tvSubtitulo = findViewById(R.id.tvSubtitle);
        tvNota = findViewById(R.id.tvNote);

        Button btnConvert = findViewById(R.id.btnConvert);
        Button btnBack = findViewById(R.id.btnBack);

        // true = lo ha pulsado el usuario, asi que si hay un error se muestra un mensaje
        btnConvert.setOnClickListener(v -> calculate(true));

        btnBack.setOnClickListener(v -> finish());

        // al cambiar de unidad se recalcula en silencio (sin mensajes de error)
        // cuando cambias una unidad en un spinner se vuelve a calcular solo
        // uso calculate(false) pa que no salgan mensajes de error (si aun no has escrito nada no molesta)
        AdapterView.OnItemSelectedListener recalculate =
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        calculate(false);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                };

        spUnidadOrigen.setOnItemSelectedListener(recalculate);
        spUnidadDestino.setOnItemSelectedListener(recalculate);

        // la tecla "hecho" del teclado tambien convierte
        // ime_action_done = el boton de 'ok' del teclado
        etValorOrigen.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                calculate(true);
                return true;
            }
            return false;
        });
    }

    // todo lo especifico de cada categoria (titulo, unidades, nota...) viene de categories
    // busca la categoria en categories y rellena titulo, subtitulo y spinners
    private void configureCategory(String key) {

        // find devuelve null si la clave no existe
        currentCategory = Categories.find(key);

        // categoria desconocida: avisamos y salimos
        if (currentCategory == null) {
            tvTitulo.setText("Categoría no disponible");
            tvSubtitulo.setText("Esta categoría todavía no está implementada");

            showMessage("Categoría no disponible");
            return;
        }

        // ponemos el titulo y el subtitulo de la categoria
        tvTitulo.setText(currentCategory.title);
        tvSubtitulo.setText(currentCategory.subtitle);

        // nota opcional (por ejemplo, aviso de tasas de ejemplo en monedas)
        // setvisibility: visible = se ve, gone = no se ve ni ocupa espacio
        if (currentCategory.note != null) {
            tvNota.setText(currentCategory.note);
            tvNota.setVisibility(View.VISIBLE);
        } else {
            tvNota.setVisibility(View.GONE);
        }

        // las unidades salen del unitconverter de la categoria, asi no las escribo aqui a mano
        String[] options = currentCategory.converter.getUnits();

        // crear adaptador personalizado
        // los dos spinners comparten el mismo adaptador
        ArrayAdapter<String> adapter = createSpinnerAdapter(options, 16, 15);

        spUnidadOrigen.setAdapter(adapter);
        spUnidadDestino.setAdapter(adapter);

        // por defecto: origen = primera unidad, destino = segunda
        spUnidadOrigen.setSelection(0);

        if (options.length > 1) {
            spUnidadDestino.setSelection(1);
        }

        etValorResultado.setText("");
        tvResultado.setText("Esperando conversión...");
    }

    // useraction = true  -> el usuario pulso el boton: se muestran mensajes
    // useraction = false -> recalculo automatico al cambiar de unidad: sin mensajes
    // este es el metodo mas importante del conversor normal: valida, convierte y muestra
    private void calculate(boolean userAction) {

        // si no hay categoria no se puede convertir
        if (currentCategory == null) {
            if (userAction) {
                showMessage("Categoría no disponible");
            }
            return;
        }

        // leemos lo que ha escrito el usuario, trim() quita los espacios de los lados
        String input = etValorOrigen.getText().toString().trim();

        if (input.isEmpty()) {
            if (userAction) {
                showMessage("Introduce un valor primero");
            }
            return;
        }

        try {

            // pasamos el texto a numero, el replace cambia la coma por punto porque java solo entiende el punto
            double value = Double.parseDouble(
                    input.replace(',', '.')
            );

            if (!Double.isFinite(value)) {
                if (userAction) {
                    showMessage("Introduce un número válido");
                }
                return;
            }

            // solo la temperatura admite valores negativos
            // validacion: los negativos solo valen en temperatura
            if (value < 0 && !currentCategory.allowNegative) {
                if (userAction) {
                    showMessage("Este conversor no admite valores negativos");
                }
                return;
            }

            // leemos las unidades elegidas en los spinners
            String from = spUnidadOrigen.getSelectedItem().toString();
            String to = spUnidadDestino.getSelectedItem().toString();

            // la misma llamada sirve para cualquier categoria
            // esta linea hace toda la conversion y vale pa cualquier categoria (la formula esta en unitconverter)
            double result = currentCategory.converter.convert(value, from, to);

            if (!Double.isFinite(result)) {
                if (userAction) {
                    showMessage("El resultado es demasiado grande");
                }
                return;
            }

            // formateamos el resultado (redondeo y coma decimal)
            String formattedResult = formatNumber(result);

            etValorResultado.setText(formattedResult);
            tvResultado.setText(
                    formatNumber(value) + " " + from + " = " + formattedResult + " " + to
            );

            if (userAction) {
                hideKeyboard();
                // mensaje de exito con snackbar
                showMessage("Conversión completada");
            }

        } catch (NumberFormatException e) {

            if (userAction) {
                showMessage("Introduce un número válido");
            }

        } catch (IllegalArgumentException e) {

            if (userAction) {
                showMessage(
                        e.getMessage() != null
                                ? e.getMessage()
                                : "No se ha podido realizar la conversión"
                );
            }
        }
    }

    // pantalla mates

    // prepara la pantalla de matematicas: enlaza vistas, rellena spinners y conecta los botones
    private void configureMathScreen() {

        // btn volver
        Button btnBack = findViewById(R.id.btnBackMath);
        btnBack.setOnClickListener(v -> finish());

        // campos fracciones
        etNumeradorFraccion1 = findViewById(R.id.etFractionNum1);
        etDenominadorFraccion1 = findViewById(R.id.etFractionDen1);
        etNumeradorFraccion2 = findViewById(R.id.etFractionNum2);
        etDenominadorFraccion2 = findViewById(R.id.etFractionDen2);

        spOperacionFraccion = findViewById(R.id.spFractionOperation);
        tvResultadoFraccion = findViewById(R.id.tvFractionResult);

        // campos porcentajes
        etPorcentaje = findViewById(R.id.etPercentage);
        etValorPorcentaje = findViewById(R.id.etPercentageValue);
        tvResultadoPorcentaje = findViewById(R.id.tvPercentageResult);

        // campos raices
        etNumeroRaiz = findViewById(R.id.etRootNumber);
        tvResultadoRaiz = findViewById(R.id.tvRootResult);

        // campos potencias
        etBasePotencia = findViewById(R.id.etPowerBase);
        etExponentePotencia = findViewById(R.id.etPowerExponent);
        tvResultadoPotencia = findViewById(R.id.tvPowerResult);

        // campos de angulos
        etAngulo = findViewById(R.id.etAngle);
        spDireccionAngulo = findViewById(R.id.spAngleDirection);
        tvResultadoAngulo = findViewById(R.id.tvAngleResult);

        // configuracion opciones del spinner
        configureMathSpinners();

        // conectar btns
        Button btnFraction = findViewById(R.id.btnFraction);
        Button btnPercentage = findViewById(R.id.btnPercentage);
        Button btnRoot = findViewById(R.id.btnRoot);
        Button btnPower = findViewById(R.id.btnPower);
        Button btnAngle = findViewById(R.id.btnAngle);

        // cada boton llama a su metodo de calculo
        btnFraction.setOnClickListener(v -> calculateFraction());
        btnPercentage.setOnClickListener(v -> calculatePercentage());
        btnRoot.setOnClickListener(v -> calculateRoot());
        btnPower.setOnClickListener(v -> calculatePower());
        btnAngle.setOnClickListener(v -> calculateAngle());
    }

    // configurar opciones spinner
    // (ahora usan el mismo adaptador oscuro que el resto: antes el texto
    // podia verse negro sobre fondo oscuro segun el tema)
    private void configureMathSpinners() {

        // estos textos los reconoce mathconverter.calculatefraction()
        String[] fractionOperations = {
                "Sumar",
                "Restar",
                "Multiplicar",
                "Dividir"
        };

        // la posicion 0 es grados -> radianes, la 1 radianes -> grados
        String[] angleDirections = {
                "Grados a radianes",
                "Radianes a grados"
        };

        spOperacionFraccion.setAdapter(
                createSpinnerAdapter(fractionOperations, 14, 14)
        );

        spDireccionAngulo.setAdapter(
                createSpinnerAdapter(angleDirections, 14, 14)
        );
    }

    // calcular fracciones

    // lee las 4 cajas, llama a mathconverter y muestra el resultado
    private void calculateFraction() {

        try {

            // getinput devuelve el texto (o lanza error si esta vacio) y parseint lo pasa a entero
            int num1 = Integer.parseInt(getInput(etNumeradorFraccion1));
            int den1 = Integer.parseInt(getInput(etDenominadorFraccion1));
            int num2 = Integer.parseInt(getInput(etNumeradorFraccion2));
            int den2 = Integer.parseInt(getInput(etDenominadorFraccion2));

            String operation =
                    spOperacionFraccion.getSelectedItem().toString();

            // la cuenta la hace mathconverter, aqui solo mostramos
            String result = MathConverter.calculateFraction(
                    num1, den1,
                    num2, den2,
                    operation
            );

            tvResultadoFraccion.setText("Resultado: " + result);
            hideKeyboard();

        } catch (NumberFormatException e) {

            showMessage("Introduce todos los valores de las fracciones");

        } catch (IllegalArgumentException e) {

            showMessage(e.getMessage());
        }
    }

    // calcular porcentajes

    // porcentaje: lee el % y la cantidad
    private void calculatePercentage() {

        try {

            // parsenumber = leer la caja y pasarla a double (mas abajo)
            double percentage = parseNumber(etPorcentaje);
            double value = parseNumber(etValorPorcentaje);

            double result = MathConverter.calcularPorcentaje(
                    percentage,
                    value
            );

            tvResultadoPorcentaje.setText(
                    "Resultado: " + formatNumber(result)
            );
            hideKeyboard();

        } catch (NumberFormatException e) {

            showMessage("Introduce el porcentaje y la cantidad");

        } catch (IllegalArgumentException e) {

            showMessage(e.getMessage());
        }
    }

    // calcular raiz cuadrada

    // raiz cuadrada: si el numero es negativo mathconverter lanza un error y sale un snackbar
    private void calculateRoot() {

        try {

            double number = parseNumber(etNumeroRaiz);

            double result = MathConverter.calcularRaizCuadrada(number);

            tvResultadoRaiz.setText(
                    "Resultado: " + formatNumber(result)
            );
            hideKeyboard();

        } catch (NumberFormatException e) {

            showMessage("Introduce un número");

        } catch (IllegalArgumentException e) {

            showMessage(e.getMessage());
        }
    }

    // calcular potencias

    // potencia: base elevada al exponente
    private void calculatePower() {

        try {

            double base = parseNumber(etBasePotencia);
            double exponent = parseNumber(etExponentePotencia);

            double result = MathConverter.calcularPotencia(
                    base,
                    exponent
            );

            tvResultadoPotencia.setText(
                    "Resultado: " + formatNumber(result)
            );
            hideKeyboard();

        } catch (NumberFormatException e) {

            showMessage("Introduce la base y el exponente");

        } catch (IllegalArgumentException e) {

            showMessage(e.getMessage());
        }
    }

    // calcular angulos

    // angulos: pasa de grados a radianes o al reves segun el spinner
    private void calculateAngle() {

        try {

            double angle = parseNumber(etAngulo);

            // posicion 0 = 'grados a radianes', posicion 1 = 'radianes a grados'
            if (spDireccionAngulo.getSelectedItemPosition() == 0) {

                // grados a radianes
                double result = MathConverter.gradosARadianes(angle);

                tvResultadoAngulo.setText(
                        "Resultado: " + formatNumber(result) + " rad"
                );

            } else {

                // radianes a grados
                double result = MathConverter.radianesAGrados(angle);

                tvResultadoAngulo.setText(
                        "Resultado: " + formatNumber(result) + "°"
                );
            }

            hideKeyboard();

        } catch (NumberFormatException e) {

            showMessage("Introduce un ángulo válido");
        }
    }

    // adaptador de spinners

    // crea un adaptador con el estilo oscuro de la app.
    // closedsp = tamano del texto con el spinner cerrado, opensp = al desplegarlo
    // los spinners por defecto salen con texto oscuro y no se ve en nuestro fondo, por eso creo el adaptador a mano
    private ArrayAdapter<String> createSpinnerAdapter(
            String[] options,
            int closedSp,
            int openSp) {

        // clase anonima: creamos un arrayadapter y sobreescribimos como se dibuja cada fila
        return new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                options
        ) {

            // texto cuando el spinner esta cerrado
            @NonNull
            @Override
            public View getView(
                    int position,
                    @Nullable View convertView,
                    @NonNull ViewGroup parent) {

                // creamos el textview por codigo en vez de por xml
                TextView textView = new TextView(ConverterActivity.this);

                textView.setText(getItem(position));
                textView.setTextColor(COLOR_TEXT);
                textView.setTextSize(closedSp);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setPadding(dp(8), 0, dp(8), 0);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setSingleLine(true);

                return textView;
            }

            // aparecen al desplegar
            @Override
            public View getDropDownView(
                    int position,
                    @Nullable View convertView,
                    @NonNull ViewGroup parent) {

                TextView textView = new TextView(ConverterActivity.this);

                textView.setText(getItem(position));
                textView.setTextColor(COLOR_TEXT);
                textView.setTextSize(openSp);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setPadding(dp(16), 0, dp(16), 0);
                textView.setMinHeight(dp(48));
                textView.setBackgroundColor(COLOR_DROPDOWN_BG);

                return textView;
            }
        };
    }

    // metodos auxiliares

    // obtener el texto de un edittext
    // si la caja esta vacia lanzo numberformatexception a proposito, asi los catch de los metodos muestran el mensaje
    private String getInput(EditText editText) {

        String value = editText.getText().toString().trim();

        if (value.isEmpty()) {
            throw new NumberFormatException();
        }

        return value;
    }

    // convertir texto a numero decimal
    // reutilizo getinput, cambio la coma por punto y compruebo que sea un numero normal (no infinito)
    private double parseNumber(EditText editText) {

        String value = getInput(editText).replace(',', '.');

        double number = Double.parseDouble(value);

        if (!Double.isFinite(number)) {
            throw new NumberFormatException();
        }

        return number;
    }

    // resultados con decimales
    // - se redondea a 12 cifras significativas para eliminar el ruido de los
    // decimales binarios (por ejemplo 1000000.0000000001).
    // - los valores muy grandes o muy pequenos se muestran en notacion cientifica.
    // se usa pa que los resultados salgan bonitos: sin cosas tipo 112.00000000000001 y con coma
    private String formatNumber(double number) {

        if (!Double.isFinite(number)) {
            return String.valueOf(number);
        }

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(SPANISH);
        double abs = Math.abs(number);

        // numeros enormes o diminutos en notacion cientifica (1,5e-10)
        if (abs != 0 && (abs < 1e-9 || abs >= 1e15)) {
            return new DecimalFormat("0.###########E0", symbols).format(number);
        }

        // bigdecimal.round(12 cifras) quita los errores de decimales de java (los double no son exactos)
        // decimalformat con el patron '#,##0.####' pone separador de miles y hasta 12 decimales sin ceros de relleno
        BigDecimal rounded = new BigDecimal(Double.toString(number))
                .round(new MathContext(12));

        return new DecimalFormat("#,##0.############", symbols).format(rounded);
    }

    // dp -> px
    // android trabaja en pixeles pero yo pienso en dp, la densidad de la pantalla hace la conversion
    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    // oculta el teclado tras calcular
    // getcurrentfocus = la caja que tiene el cursor ahora mismo
    private void hideKeyboard() {

        View focus = getCurrentFocus();

        if (focus == null) {
            return;
        }

        // inputmethodmanager es el servicio de android que controla el teclado
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }
    }

    // evita que el contenido quede bajo la barra de estado / navegacion
    // (android 15 fuerza el modo edge-to-edge)
    // igual que en mainactivity: respetar las barras del sistema
    private void applySystemBarInsets() {

        View root = findViewById(rootViewId);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {

            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
            );

            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);

            return windowInsets;
        });
    }

    // ensenar mensajes snackbar
    // todos los mensajes de esta activity pasan por aqui, asi siempre son snackbar
    private void showMessage(String message) {

        if (message == null || message.isEmpty()) {
            message = "No se ha podido realizar la operación";
        }

        // make(vista donde se muestra, texto, duracion)
        Snackbar.make(
                findViewById(rootViewId),
                message,
                Snackbar.LENGTH_SHORT
        ).show();
    }
}