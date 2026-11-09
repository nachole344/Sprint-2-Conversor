package com.example.sprint2conversor;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sprint2conversor.logic.MassConverter;
import com.example.sprint2conversor.logic.VolumeConverter;
import com.example.sprint2conversor.logic.MathConverter;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ConverterActivity extends AppCompatActivity {


    // variables de conversor


    private EditText etValorOrigen, etValorResultado;
    private Spinner spUnidadOrigen, spUnidadDestino;
    private TextView tvResultado, tvTitulo, tvSubtitulo;

    private String categoriaActual;

    private String[] unidadesMasa = {
            "kg", "g", "mg", "lb", "oz", "t"
    };

    private String[] unidadesVolumen = {
            "L", "mL", "m³", "gal US"
    };

    // variables mates


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // obten la categoria del mainactivity
        categoriaActual = getIntent().getStringExtra("category");

        if (categoriaActual == null) {
            categoriaActual = "Peso";
        }

        // si la categoria es mates
        if (categoriaActual.equalsIgnoreCase("Matematicas")
                || categoriaActual.equalsIgnoreCase("Matemáticas")) {

            setContentView(R.layout.activity_math);
            configureMathScreen();
            return;
        }

        // si la categoria NO es mates
        setContentView(R.layout.activity_converter);

        initializeConverterViews();
        configureCategory();
    }

    // conversor peso y volumen

    private void initializeConverterViews() {

        etValorOrigen = findViewById(R.id.etValueFrom);
        etValorResultado = findViewById(R.id.etValueTo);

        spUnidadOrigen = findViewById(R.id.spFrom);
        spUnidadDestino = findViewById(R.id.spTo);

        tvResultado = findViewById(R.id.tvResult);
        tvTitulo = findViewById(R.id.tvTitle);
        tvSubtitulo = findViewById(R.id.tvSubtitle);

        Button btnConvert = findViewById(R.id.btnConvert);
        Button btnBack = findViewById(R.id.btnBack);

        btnConvert.setOnClickListener(v -> calculate());

        btnBack.setOnClickListener(v -> finish());
    }

    private void configureCategory() {

        String[] options;

        if (categoriaActual.equalsIgnoreCase("Peso")
                || categoriaActual.equalsIgnoreCase("Peso y masa")
                || categoriaActual.equalsIgnoreCase("Masa")) {

            categoriaActual = "Peso y masa";
            options = unidadesMasa;

            tvTitulo.setText("Conversor de masa");
            tvSubtitulo.setText("Convierte entre diferentes unidades de masa");

        } else if (categoriaActual.equalsIgnoreCase("Volumen")) {

            categoriaActual = "Volumen";
            options = unidadesVolumen;

            tvTitulo.setText("Conversor de volumen");
            tvSubtitulo.setText("Convierte entre diferentes unidades de volumen");

        } else {

            tvTitulo.setText("Categoría no disponible");
            tvSubtitulo.setText("Esta categoría todavía no está implementada");

            showMessage("Categoría no disponible");
            return;
        }

        // crear adaptador personalizado
        ArrayAdapter<String> adapter = createUnitAdapter(options);

        // aplica las unidades del adaptador
        spUnidadOrigen.setAdapter(adapter);
        spUnidadDestino.setAdapter(adapter);

        spUnidadOrigen.setSelection(0);

        if (options.length > 1) {
            spUnidadDestino.setSelection(1);
        }

        etValorResultado.setText("");
        tvResultado.setText("Resultado de la conversión");
    }

    private void calculate() {

        String input = etValorOrigen.getText().toString().trim();

        if (input.isEmpty()) {
            showMessage("Introduce un valor primero");
            return;
        }

        try {

            double value = Double.parseDouble(
                    input.replace(',', '.')
            );

            if (!Double.isFinite(value)) {
                showMessage("Introduce un número válido");
                return;
            }

            String from = spUnidadOrigen.getSelectedItem().toString();
            String to = spUnidadDestino.getSelectedItem().toString();

            double result;

            if (categoriaActual.equals("Peso y masa")) {

                result = MassConverter.convert(value, from, to);

            } else if (categoriaActual.equals("Volumen")) {

                result = VolumeConverter.convert(value, from, to);

            } else {

                showMessage("Categoría no disponible");
                return;
            }

            String formattedResult = formatNumber(result);

            etValorResultado.setText(formattedResult);
            tvResultado.setText(formattedResult + " " + to);

        } catch (NumberFormatException e) {

            showMessage("Introduce un número válido");

        } catch (IllegalArgumentException e) {

            showMessage("No se ha podido realizar la conversión");
        }
    }

    // pantalla mates

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

        // campos raizes
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

        btnFraction.setOnClickListener(v -> calculateFraction());
        btnPercentage.setOnClickListener(v -> calculatePercentage());
        btnRoot.setOnClickListener(v -> calculateRoot());
        btnPower.setOnClickListener(v -> calculatePower());
        btnAngle.setOnClickListener(v -> calculateAngle());
    }

    // crear dapatador personalizado para las unidades
    private ArrayAdapter<String> createUnitAdapter(String[] options) {

        return new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                options
        ) {

            // texto cuando spinner esta cerrado
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                TextView textView = new TextView(ConverterActivity.this);

                textView.setText(getItem(position));
                textView.setTextColor(Color.parseColor("#F1F5F9"));
                textView.setTextSize(16);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setPadding(14, 0, 14, 0);
                textView.setTypeface(null, android.graphics.Typeface.BOLD);

                return textView;
            }

            // aparecen al desplegar
            @Override
            public View getDropDownView(
                    int position,
                    View convertView,
                    ViewGroup parent
            ) {

                TextView textView = new TextView(ConverterActivity.this);

                textView.setText(getItem(position));
                textView.setTextColor(Color.parseColor("#F1F5F9"));
                textView.setTextSize(15);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setPadding(18, 0, 18, 0);
                textView.setMinHeight(52);
                textView.setBackgroundColor(Color.parseColor("#111B2D"));

                return textView;
            }
        };
    }

    // configurar opciones spinner
    private void configureMathSpinners() {

        String[] fractionOperations = {
                "Sumar",
                "Restar",
                "Multiplicar",
                "Dividir"
        };

        String[] angleDirections = {
                "Grados a radianes",
                "Radianes a grados"
        };

        ArrayAdapter<String> fractionAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                fractionOperations
        );

        fractionAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spOperacionFraccion.setAdapter(fractionAdapter);

        ArrayAdapter<String> angleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                angleDirections
        );

        angleAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spDireccionAngulo.setAdapter(angleAdapter);
    }

    // calcular fracciones

    private void calculateFraction() {

        try {

            int num1 = Integer.parseInt(
                    getInput(etNumeradorFraccion1)
            );

            int den1 = Integer.parseInt(
                    getInput(etDenominadorFraccion1)
            );

            int num2 = Integer.parseInt(
                    getInput(etNumeradorFraccion2)
            );

            int den2 = Integer.parseInt(
                    getInput(etDenominadorFraccion2)
            );

            String operation =
                    spOperacionFraccion.getSelectedItem().toString();

            String result = MathConverter.calculateFraction(
                    num1, den1,
                    num2, den2,
                    operation
            );

            tvResultadoFraccion.setText("Resultado: " + result);

        } catch (NumberFormatException e) {

            showMessage("Introduce todos los valores de las fracciones");

        } catch (IllegalArgumentException e) {

            showMessage(e.getMessage());
        }
    }

    // calcular porcentajes

    private void calculatePercentage() {

        try {

            double percentage = parseNumber(etPorcentaje);
            double value = parseNumber(etValorPorcentaje);

            double result = MathConverter.calcularPorcentaje(
                    percentage,
                    value
            );

            tvResultadoPorcentaje.setText(
                    "Resultado: " + formatNumber(result)
            );

        } catch (NumberFormatException e) {

            showMessage("Introduce el porcentaje y la cantidad");
        }
    }

    // calcular raiz cuadrada

    private void calculateRoot() {

        try {

            double number = parseNumber(etNumeroRaiz);

            double result = MathConverter.calcularRaizCuadrada(number);

            tvResultadoRaiz.setText(
                    "Resultado: " + formatNumber(result)
            );

        } catch (NumberFormatException e) {

            showMessage("Introduce un número");

        } catch (IllegalArgumentException e) {

            showMessage(e.getMessage());
        }
    }

    // calcular potencias

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

        } catch (NumberFormatException e) {

            showMessage("Introduce la base y el exponente");

        } catch (IllegalArgumentException e) {

            showMessage(e.getMessage());
        }
    }

    // calcular angulos

    private void calculateAngle() {

        try {

            double angle = parseNumber(etAngulo);

            String direction =
                    spDireccionAngulo.getSelectedItem().toString();

            double result;

            if (direction.equals("Grados a radianes")) {

                result = MathConverter.gradosARadianes(angle);

                tvResultadoAngulo.setText(
                        "Resultado: " + formatNumber(result) + " rad"
                );

            } else {

                result = MathConverter.radianesAGrados(angle);

                tvResultadoAngulo.setText(
                        "Resultado: " + formatNumber(result) + "°"
                );
            }

        } catch (NumberFormatException e) {

            showMessage("Introduce un ángulo válido");
        }
    }

    // metdodos auxiliares

    // obtener el texto de un EditText
    private String getInput(EditText editText) {

        String value = editText.getText().toString().trim();

        if (value.isEmpty()) {
            throw new NumberFormatException();
        }

        return value;
    }

    // convertir texto a numero decimal
    private double parseNumber(EditText editText) {

        String value = getInput(editText).replace(',', '.');

        double number = Double.parseDouble(value);

        if (!Double.isFinite(number)) {
            throw new NumberFormatException();
        }

        return number;
    }

    // resultados con decimales
    private String formatNumber(double number) {

        DecimalFormatSymbols symbols =
                DecimalFormatSymbols.getInstance(
                        new Locale("es", "ES")
                );

        DecimalFormat format =
                new DecimalFormat("#,##0.##########", symbols);

        return format.format(number);
    }

    // enseñaar mensajes Snackbar
    private void showMessage(String message) {

        int rootId;

        if (categoriaActual != null
                && (categoriaActual.equalsIgnoreCase("Matematicas")
                || categoriaActual.equalsIgnoreCase("Matemáticas"))) {

            rootId = R.id.rootMath;

        } else {

            rootId = R.id.rootConverter;
        }

        Snackbar.make(
                findViewById(rootId),
                message,
                Snackbar.LENGTH_SHORT
        ).show();
    }
}
