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
    private String[] longitudes = {"Milimetros","Centimetros", "Decimetros","Metros", "Kilometros", "Pies", "Pulgadas", "Yardas", "Millas"};
    private String[] tiempos = {"Segundos", "Minutos", "Horas", "Dias", "Semanas", "Meses", "Años"};
    private String[] almacenamiento = {"Bit", "Byte", "Kilobyte", "Megabyte", "Gigabyte", "Terabyte"};
    private String[] velocidad = {"m/s", "Km/h", "ft/s", "mph", "Match", "kn", "Luz"};
    private ArrayAdapter<String> adapterLongitud;
    private ArrayAdapter<String> adapterTiempo;
    private ArrayAdapter<String> adapterAlmacenamiento;
    private ArrayAdapter<String> adapterVelocidad;

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

        String[] conversiones = {"Longitud", "Tiempo", "Almacenamiento", "Velocidad"};


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
            default:
                break;
        }

    }

    public void convertirLongitud() {
        lengthUnits origen = lengthUnits.valueOf(listaUnidadesOrigen.getSelectedItem().toString().toUpperCase());
        lengthUnits destino = lengthUnits.valueOf(listaUnidadesDestino.getSelectedItem().toString().toUpperCase());
        double valor = Double.parseDouble(introValor.getText().toString());

        try {
            double resultado = conversor.convertirLongitud(valor, origen, destino);
            resultadoConversion.setText(String.valueOf(resultado));
        } catch (IllegalArgumentException e) {
            resultadoConversion.setText("Error: " + e.getMessage());
        }

    }

    public void convertirTiempo() {
        timeUnits origen = timeUnits.valueOf(listaUnidadesOrigen.getSelectedItem().toString().toUpperCase());
        timeUnits destino = timeUnits.valueOf(listaUnidadesDestino.getSelectedItem().toString().toUpperCase());
        double valor = Double.parseDouble(introValor.getText().toString());

        try {
            double resultado = conversor.convertirTiempo(valor, origen, destino);
            resultadoConversion.setText(String.valueOf(resultado));
        } catch (IllegalArgumentException e) {
            resultadoConversion.setText("Error: " + e.getMessage());
        }

    }

    public void convertirAlmacenamiento() {
        storageUnits origen = storageUnits.valueOf(listaUnidadesOrigen.getSelectedItem().toString().toUpperCase());
        storageUnits destino = storageUnits.valueOf(listaUnidadesDestino.getSelectedItem().toString().toUpperCase());
        double valor = Double.parseDouble(introValor.getText().toString());

        try {
            double resultado = conversor.convertirAlmacenamiento(valor, origen, destino);
            resultadoConversion.setText(String.valueOf(resultado));
        } catch (IllegalArgumentException e) {
            resultadoConversion.setText("Error: " + e.getMessage());
        }

    }

    public void convertirVelocidad() {
        // Aqui no se puede usar el mismo metodo que el resto al tener un nombre diferente al de la clase Enum
        String origen = listaUnidadesOrigen.getSelectedItem().toString();
        switch (origen){
            case "m/s":
                origen = "METROS_POR_SEGUNDO";
                break;
            case "Km/h":
                origen = "KILOMETROS_POR_HORA";
                break;
            case "ft/s":
                origen = "PIE_POR_SEGUNDO";
                break;
            case "mph":
                origen = "MILLAS_POR_HORA";
                break;
            case "Match":
                origen = "MATCH";
                break;
            case "kn":
                origen = "NUDO";
                break;
            case "Luz":
                origen = "C";
                break;
            default:
                break;

        }
        String destino = listaUnidadesDestino.getSelectedItem().toString();
        switch (destino){
            case "m/s":
                destino = "METROS_POR_SEGUNDO";
                break;
            case "Km/h":
                destino = "KILOMETROS_POR_HORA";
                break;
            case "ft/s":
                destino = "PIE_POR_SEGUNDO";
                break;
            case "mph":
                destino = "MILLAS_POR_HORA";
                break;
            case "Match":
                destino = "MATCH";
                break;
            case "kn":
                destino = "NUDO";
                break;
            case "Luz":
                destino = "C";
                break;
            default:
                break;

        }

        speedUnits origenEnum = speedUnits.valueOf(origen);
        speedUnits destinoEnum = speedUnits.valueOf(destino);
        double valor = Double.parseDouble(introValor.getText().toString());

        try {
            double resultado = conversor.convertirVelocidad(valor, origenEnum, destinoEnum);
            resultadoConversion.setText(String.valueOf(resultado));
        } catch (IllegalArgumentException e) {
            resultadoConversion.setText("Error: " + e.getMessage());
        }

    }

}