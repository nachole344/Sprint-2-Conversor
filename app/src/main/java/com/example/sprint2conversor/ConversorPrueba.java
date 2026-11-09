package com.example.sprint2conversor;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sprint2conversor.logic.*;

import java.util.Arrays;

public class ConversorPrueba extends AppCompatActivity {

    // Elementos de la vista
    private Spinner listaConversiones;
    private Spinner listaUnidadesOrigen;
    private Spinner listaUnidadesDestino;
    private EditText introValor;
    private TextView resultadoConversion;

    // Conversor
    private Conversor conversor = new Conversor();

    // Arrays y adaptadores
    private String[] longitudes = Arrays.stream(lengthUnits.values()).map(lengthUnits::getAbreviacion).toArray(String[]::new);
    private String[] tiempos = Arrays.stream(timeUnits.values()).map(timeUnits::getAbreviacion).toArray(String[]::new);
    private String[] almacenamiento = Arrays.stream(storageUnits.values()).map(storageUnits::getAbreviacion).toArray(String[]::new);
    private String[] velocidad = Arrays.stream(speedUnits.values()).map(speedUnits::getAbreviacion).toArray(String[]::new);
    private String[] energia = Arrays.stream(energyUnits.values()).map(energyUnits::getAbreviacion).toArray(String[]::new);
    private ArrayAdapter<String> adapterLongitud;
    private ArrayAdapter<String> adapterTiempo;
    private ArrayAdapter<String> adapterAlmacenamiento;
    private ArrayAdapter<String> adapterVelocidad;
    private ArrayAdapter<String> adapterEnergia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conversor_prueba);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaConversiones = findViewById(R.id.SeleccionTipoDeConversion);
        listaUnidadesOrigen = findViewById(R.id.medidaOrigen);
        listaUnidadesDestino = findViewById(R.id.medidaDestino);

        adapterLongitud = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, longitudes);
        adapterTiempo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiempos);
        adapterAlmacenamiento = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, almacenamiento);
        adapterVelocidad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, velocidad);
        adapterEnergia = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, energia);

        String[] conversiones = {"Longitud", "Tiempo", "Almacenamiento", "Velocidad", "Energia"};


        ArrayAdapter<String> adapterConversion = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, conversiones);
        listaConversiones.setAdapter(adapterConversion);


        // Por defecto se dejará longitud
        listaUnidadesOrigen.setAdapter(adapterLongitud);
        listaUnidadesDestino.setAdapter(adapterLongitud);

        introValor = findViewById(R.id.introValor);
        resultadoConversion = findViewById(R.id.resultadoConversion);

        listaConversiones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cambiarMedidas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button botonConvertir = findViewById(R.id.button2);
        botonConvertir.setOnClickListener(v -> hacerConversion());

    }

    public void cambiarMedidas() {
        String tipoConversion = listaConversiones.getSelectedItem().toString();
        switch (tipoConversion){
            case "Longitud":
                listaUnidadesOrigen.setAdapter(adapterLongitud);
                listaUnidadesDestino.setAdapter(adapterLongitud);
                break;
            case "Tiempo":
                listaUnidadesOrigen.setAdapter(adapterTiempo);
                listaUnidadesDestino.setAdapter(adapterTiempo);
                break;
            case "Almacenamiento":
                listaUnidadesOrigen.setAdapter(adapterAlmacenamiento);
                listaUnidadesDestino.setAdapter(adapterAlmacenamiento);
                break;
            case "Velocidad":
                listaUnidadesOrigen.setAdapter(adapterVelocidad);
                listaUnidadesDestino.setAdapter(adapterVelocidad);
                break;
            case "Energia":
                listaUnidadesOrigen.setAdapter(adapterEnergia);
                listaUnidadesDestino.setAdapter(adapterEnergia);
                break;
            default:
                break;
        }
    }

    public void hacerConversion() {
        String tipoConversion = listaConversiones.getSelectedItem().toString();

        switch (tipoConversion){
            case "Longitud":
                convertirLongitud();
                break;
            case "Tiempo":
                convertirTiempo();
                break;
            case "Almacenamiento":
                convertirAlmacenamiento();
                break;
            case "Velocidad":
                convertirVelocidad();
                break;
            case "Energia":
                convertirEnergia();
                break;
            default:
                break;
        }

    }

    public void convertirLongitud() {
        String origenStr = listaUnidadesOrigen.getSelectedItem().toString();
        String destinoStr = listaUnidadesDestino.getSelectedItem().toString();

        try {
            // Convertir a enum
            lengthUnits origen = lengthUnits.getUnit(origenStr);
            lengthUnits destino = lengthUnits.getUnit(destinoStr);
            double valor = Double.parseDouble(introValor.getText().toString());

            // Realizar la conversion
            double resultado = conversor.convertirLongitud(valor, origen, destino);
            resultadoConversion.setText(String.valueOf(resultado));

        // Manejar excepciones
        } catch (IllegalArgumentException e) {
            resultadoConversion.setText("Error: " + e.getMessage());
        }

    }

    public void convertirTiempo() {
        String origenStr = listaUnidadesOrigen.getSelectedItem().toString();
        String destinoStr = listaUnidadesDestino.getSelectedItem().toString();

        try {
            // Convertir a enum
            timeUnits origen = timeUnits.getUnit(origenStr);
            timeUnits destino = timeUnits.getUnit(destinoStr);
            double valor = Double.parseDouble(introValor.getText().toString());

            // Realizar la conversion
            double resultado = conversor.convertirTiempo(valor, origen, destino);
            resultadoConversion.setText(String.valueOf(resultado));

            // Manejar excepciones
        } catch (IllegalArgumentException e) {
            resultadoConversion.setText("Error: " + e.getMessage());
        }

    }

    public void convertirAlmacenamiento() {
        String origenStr = listaUnidadesOrigen.getSelectedItem().toString();
        String destinoStr = listaUnidadesDestino.getSelectedItem().toString();

        try {
            // Convertir a enum
            storageUnits origen = storageUnits.getUnit(origenStr);
            storageUnits destino = storageUnits.getUnit(destinoStr);
            double valor = Double.parseDouble(introValor.getText().toString());

            // Realizar la conversion
            double resultado = conversor.convertirAlmacenamiento(valor, origen, destino);
            resultadoConversion.setText(String.valueOf(resultado));

            // Manejar excepciones
        } catch (IllegalArgumentException e) {
            resultadoConversion.setText("Error: " + e.getMessage());
        }

    }

    public void convertirVelocidad() {
        String origenStr = listaUnidadesOrigen.getSelectedItem().toString();
        String destinoStr = listaUnidadesDestino.getSelectedItem().toString();

        try {
            // Convertir a enum
            speedUnits origen = speedUnits.getUnit(origenStr);
            speedUnits destino = speedUnits.getUnit(destinoStr);
            double valor = Double.parseDouble(introValor.getText().toString());

            // Realizar la conversion
            double resultado = conversor.convertirVelocidad(valor, origen, destino);
            resultadoConversion.setText(String.valueOf(resultado));

            // Manejar excepciones
        } catch (IllegalArgumentException e) {
            resultadoConversion.setText("Error: " + e.getMessage());
        }
    }
    private void convertirEnergia() {
        String origenStr = listaUnidadesOrigen.getSelectedItem().toString();
        String destinoStr = listaUnidadesDestino.getSelectedItem().toString();

        try {
            // Convertir a enum
            energyUnits origen = energyUnits.getUnit(origenStr);
            energyUnits destino = energyUnits.getUnit(destinoStr);
            double valor = Double.parseDouble(introValor.getText().toString());

            // Realizar la conversion
            double resultado = conversor.convertirEnergia(valor, origen, destino);
            resultadoConversion.setText(String.valueOf(resultado));

            // Manejar excepciones
        } catch (IllegalArgumentException e) {
            resultadoConversion.setText("Error: " + e.getMessage());
        }

    }

}