
package com.example.sprint2conversor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private ScrollView homeScroll;
    private View sectionCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Conectamos los elementos principales
        btnStart = findViewById(R.id.btnStart);
        homeScroll = findViewById(R.id.homeScroll);
        sectionCategories = findViewById(R.id.sectionCategories);

        // Boton principal: desplazarse hasta las categorias
        btnStart.setOnClickListener(v -> {
            homeScroll.smoothScrollTo(
                    0,
                    sectionCategories.getTop()
            );
        });

        // Recuperamos las tarjetas
        View catLength = findViewById(R.id.catLength);
        View catWeight = findViewById(R.id.catWeight);
        View catTemperature = findViewById(R.id.catTemperature);
        View catSpeed = findViewById(R.id.catSpeed);
        View catVolume = findViewById(R.id.catVolume);
        View catMath = findViewById(R.id.catMath);

        // Descripciones para accesibilidad
        catLength.setContentDescription(
                "Longitud. Conversor próximamente disponible"
        );

        catWeight.setContentDescription(
                "Peso. Abrir conversor de masa"
        );

        catTemperature.setContentDescription(
                "Temperatura. Conversor próximamente disponible"
        );

        catSpeed.setContentDescription(
                "Velocidad. Conversor próximamente disponible"
        );

        catVolume.setContentDescription(
                "Volumen. Abrir conversor de volumen"
        );

        catMath.setContentDescription(
                "Matemáticas. Abrir herramientas matemáticas"
        );

        // Categoria: Peso
        catWeight.setOnClickListener(v -> {
            openConverter("Peso");
        });

        // Categoria: Volumen
        catVolume.setOnClickListener(v -> {
            openConverter("Volumen");
        });

        // Categoria: Matematicas
        catMath.setOnClickListener(v -> {
            openConverter("Matematicas");
        });

        // Categorias pendientes de implementar
        catLength.setOnClickListener(v -> {
            showMessage(
                    "El conversor de longitud estará disponible próximamente"
            );
        });

        catTemperature.setOnClickListener(v -> {
            showMessage(
                    "El conversor de temperatura estará disponible próximamente"
            );
        });

        catSpeed.setOnClickListener(v -> {
            showMessage(
                    "El conversor de velocidad estará disponible próximamente"
            );
        });
    }

    // Abrimos la pantalla del conversor
    private void openConverter(String category) {

        Intent intent = new Intent(
                MainActivity.this,
                ConverterActivity.class
        );

        // Enviamos la categoria seleccionada
        intent.putExtra("category", category);

        startActivity(intent);
    }

    // Mensajes mediante Snackbar
    private void showMessage(String message) {

        Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT
        ).show();
    }
}
