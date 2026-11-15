package com.example.sprint2conversor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sprint2conversor.logic.Categories;
import com.google.android.material.snackbar.Snackbar;

// activity 1: el usuario elige una categoria.
//
// al tocar una tarjeta se muestra su imagen (cargada como bitmap) y al pulsar
// "abrir conversor" se abre la segunda activity enviando la categoria por intent.
// esta es la activity 1: aqui se elige la categoria
// aqui estan 3 requisitos gordos del enunciado: seleccionar categoria, bitmap y cambio de activity con intent
public class MainActivity extends AppCompatActivity {

    // clave pa guardar la categoria elegida cuando se gira el movil (onsaveinstancestate)
    private static final String STATE_SELECTED = "selected_key";

    // datos de cada tarjeta: id de la tarjeta, clave que viaja en el intent,
    // nombre, descripcion e imagen asociada
    // clase pequena pa agrupar los datos de cada tarjeta, asi no repito codigo 12 veces
    private static final class CategoryItem {
        // id de la tarjeta en el xml (r.id.catlength, r.id.catweight...)
        final int cardId;
        // clave que se manda a la otra pantalla por el intent
        final String key;
        final String name;
        final String description;
        // id de la imagen en drawable (es un numero que genera android solo)
        final int imageRes;
        // la vista de la tarjeta, se rellena en oncreate con findviewbyid
        View card;

        // constructor: solo guarda los datos
        CategoryItem(int cardId, String key, String name,
                     String description, int imageRes) {
            this.cardId = cardId;
            this.key = key;
            this.name = name;
            this.description = description;
            this.imageRes = imageRes;
        }
    }

    // para anadir una categoria nueva: una tarjeta en el xml + una linea aqui
    // lista con las 12 categorias
    // pa anadir una nueva: una tarjeta en el xml + una linea aqui + su imagen
    private final CategoryItem[] items = {
            new CategoryItem(R.id.catLength, Categories.LENGTH, "Longitud",
                    "Metros, kilómetros, millas, pies...", R.drawable.img_cat_length),
            new CategoryItem(R.id.catWeight, Categories.WEIGHT, "Peso",
                    "Kilos, gramos, libras, onzas...", R.drawable.img_cat_weight),
            new CategoryItem(R.id.catTemperature, Categories.TEMPERATURE, "Temperatura",
                    "Celsius, Fahrenheit y Kelvin", R.drawable.img_cat_temperature),
            new CategoryItem(R.id.catSpeed, Categories.SPEED, "Velocidad",
                    "km/h, m/s, mph, nudos...", R.drawable.img_cat_speed),
            new CategoryItem(R.id.catVolume, Categories.VOLUME, "Volumen",
                    "Litros, mililitros, galones...", R.drawable.img_cat_volume),
            new CategoryItem(R.id.catMath, Categories.MATH, "Matemáticas",
                    "Fracciones, porcentajes, raíces, potencias y ángulos", R.drawable.img_cat_math),
            new CategoryItem(R.id.catCurrency, Categories.CURRENCY, "Monedas",
                    "Euros, dólares, libras, yenes...", R.drawable.img_cat_currency),
            new CategoryItem(R.id.catProgrammer, Categories.PROGRAMMER, "Programación",
                    "Bases numéricas, ASCII y hexadecimal", R.drawable.img_cat_programmer),
            new CategoryItem(R.id.catTime, Categories.TIME, "Tiempo",
                    "Segundos, minutos, horas, días...", R.drawable.img_cat_time),
            new CategoryItem(R.id.catDigital, Categories.DIGITAL, "Datos",
                    "Bits, bytes, KB, MB, GB, TB", R.drawable.img_cat_digital),
            new CategoryItem(R.id.catArea, Categories.AREA, "Área",
                    "m², hectáreas, acres, km²...", R.drawable.img_cat_area),
            new CategoryItem(R.id.catPressure, Categories.PRESSURE, "Presión",
                    "Pascales, bar, atmósferas, psi...", R.drawable.img_cat_pressure),
            new CategoryItem(R.id.catEnergy, Categories.ENERGY, "Energía",
                    "Joules, kilovatios, kilocalorías, kcal...", R.drawable.img_cat_energy)
    };

    // referencias a las vistas del layout (se enlazan en oncreate)
    private ScrollView homeScroll;
    private View sectionCategories;
    private Button btnStart;
    private ImageView ivCategoryPreview;
    private TextView tvSelectedCategory, tvSelectedHint;

    // categoria elegida ahora mismo, es null si aun no se ha tocado ninguna
    private CategoryItem selectedItem;

    // cache de bitmaps: evita decodificar la misma imagen cada vez que se toca una tarjeta
    // lrucache = diccionario con limite de memoria: si se llena borra lo que lleva mas tiempo sin usarse
    // clave = id de la imagen, valor = el bitmap ya cargado
    private LruCache<Integer, Bitmap> bitmapCache;

    // oncreate se ejecuta al abrir la pantalla, aqui se prepara todo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // carga el diseno xml (activity_main.xml)
        setContentView(R.layout.activity_main);

        // conectamos los elementos principales
        // findviewbyid busca una vista del xml por su id pa poder usarla desde java
        btnStart = findViewById(R.id.btnStart);
        homeScroll = findViewById(R.id.homeScroll);
        sectionCategories = findViewById(R.id.sectionCategories);
        ivCategoryPreview = findViewById(R.id.ivCategoryPreview);
        tvSelectedCategory = findViewById(R.id.tvSelectedCategory);
        tvSelectedHint = findViewById(R.id.tvSelectedHint);

        // evita que el contenido se meta debajo de la barra de arriba o de abajo del movil
        applySystemBarInsets();
        // creamos la cache de imagenes antes de usarla
        createBitmapCache();

        // imagen inicial (aun no hay ninguna categoria elegida)
        // r.drawable.xxx es el id de una imagen guardada en res/drawable-nodpi
        showBitmap(R.drawable.img_cat_default);

        // conectamos las tarjetas
        // recorremos las 12 categorias y a cada tarjeta le ponemos su descripcion y su click
        for (CategoryItem item : items) {
            item.card = findViewById(item.cardId);

            // descripcion para accesibilidad
            item.card.setContentDescription(
                    item.name + ". " + item.description + ". Pulsa para seleccionar"
            );

            // al tocar la tarjeta se llama a selectcategory (cambia la imagen y los textos)
            // v -> ... es una lambda: una funcion corta que se ejecuta cuando haces click
            item.card.setOnClickListener(v -> selectCategory(item, true));
        }

        // boton principal: abre la segunda activity con la categoria elegida
        // este es el boton que abre la segunda activity
        btnStart.setOnClickListener(v -> openSelectedCategory());

        // recuperamos la seleccion tras girar la pantalla
        // savedinstancestate es null la primera vez que se abre y trae datos si la pantalla se recrea (al girar)
        if (savedInstanceState != null) {
            String key = savedInstanceState.getString(STATE_SELECTED);

            // buscamos cual era la categoria guardada y la volvemos a marcar
            for (CategoryItem item : items) {
                if (item.key.equals(key)) {
                    selectCategory(item, false);
                    break;
                }
            }
        }
    }

    // android llama a esto antes de destruir la pantalla (por ejemplo al girar), guardamos la categoria
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (selectedItem != null) {
            outState.putString(STATE_SELECTED, selectedItem.key);
        }
    }

    // marca una tarjeta como elegida y actualiza la imagen y los textos
    // scrolltotop = true si hay que subir la pantalla, false al restaurar tras girar
    private void selectCategory(CategoryItem item, boolean scrollToTop) {

        // recordamos cual es la elegida
        selectedItem = item;

        // la tarjeta elegida destaca; el resto se atenua
        // isselected es true solo pa la tarjeta que se ha tocado
        for (CategoryItem other : items) {
            boolean isSelected = other == item;

            // animate() hace una animacion suave: alpha = transparencia, scale = tamano
            other.card.animate()
                    .alpha(isSelected ? 1f : 0.55f)
                    .scaleX(isSelected ? 1.03f : 1f)
                    .scaleY(isSelected ? 1.03f : 1f)
                    .setDuration(150)
                    .start();
        }

        // imagen de la categoria (bitmap)
        // aqui se usa el bitmap (requisito del enunciado)
        showBitmap(item.imageRes);
        ivCategoryPreview.setContentDescription("Imagen de " + item.name);

        tvSelectedCategory.setText(item.name);
        tvSelectedHint.setText(item.description);
        // cambiamos el texto del boton pa que ponga 'abrir peso', 'abrir tiempo'...
        btnStart.setText("Abrir " + item.name + "     →");

        // subimos para ver la imagen y el boton
        if (scrollToTop) {
            homeScroll.smoothScrollTo(0, 0);
        }
    }

    // abre la segunda activity (o avisa si todavia no se ha elegido nada)
    // se llama al pulsar el boton principal
    private void openSelectedCategory() {

        // si no hay nada elegido avisamos con un snackbar (el enunciado prohibe el toast)
        if (selectedItem == null) {
            showMessage("Selecciona una categoría para continuar");

            // llevamos al usuario hasta las categorias
            homeScroll.smoothScrollTo(0, sectionCategories.getTop());
            return;
        }

        // un intent es un mensaje pa pedirle a android que abra otra pantalla
        // primer parametro: desde donde salimos; segundo: la pantalla que queremos abrir
        Intent intent = new Intent(
                MainActivity.this,
                ConverterActivity.class
        );

        // enviamos la categoria seleccionada
        // putextra mete un dato en el intent (clave, valor)
        // la otra pantalla lo lee con getstringextra usando la misma clave
        intent.putExtra(ConverterActivity.EXTRA_CATEGORY, selectedItem.key);

        // abre la segunda activity
        startActivity(intent);
    }

    // bitmap

    // monta la cache de bitmaps
    private void createBitmapCache() {

        // usamos 1/8 de la memoria disponible; el tamano se mide en kb
        // memoria maxima que android deja usar a la app, en kb
        int maxKb = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // usamos un octavo de esa memoria pa la cache
        bitmapCache = new LruCache<Integer, Bitmap>(maxKb / 8) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                // cuanto ocupa cada imagen en kb, la cache lo necesita pa saber cuando esta llena
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    // muestra una imagen en el imageview usando un bitmap (no setimageresource)
    // primero mira en la cache; si no esta, la carga del disco y la guarda
    private void showBitmap(int resId) {

        // get devuelve null si esa imagen aun no esta en la cache
        Bitmap bitmap = bitmapCache.get(resId);

        if (bitmap == null) {
            // no estaba: la cargamos reducida al tamano que hace falta en pantalla
            bitmap = decodeSampledBitmap(
                    resId,
                    getResources().getDisplayMetrics().widthPixels,
                    Math.round(150 * getResources().getDisplayMetrics().density)
            );

            if (bitmap != null) {
                // la guardamos pa la proxima vez
                bitmapCache.put(resId, bitmap);
            }
        }

        if (bitmap != null) {
            // setimagebitmap pone el bitmap en el imageview, esto es lo que pide el enunciado
            // (si usara setimageresource ya no seria un bitmap)
            ivCategoryPreview.setImageBitmap(bitmap);
        }
    }

    // carga el bitmap reducido al tamano que se necesita para no gastar memoria de mas
    // bitmapfactory es la clase de android que convierte un archivo de imagen en un bitmap
    private Bitmap decodeSampledBitmap(int resId, int reqWidth, int reqHeight) {

        // 1) solo leemos las dimensiones (sin reservar memoria para los pixeles)
        BitmapFactory.Options options = new BitmapFactory.Options();
        // true = solo lee el tamano de la imagen, no carga los pixeles (rapido y casi no gasta memoria)
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);

        // 2) calculamos cuanto hay que reducir la imagen
        // insamplesize = cuanto se reduce la imagen: 2 = mitad de ancho y de alto, 4 = la cuarta parte...
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 3) decodificamos de verdad
        options.inJustDecodeBounds = false;
        // ahora si, carga la imagen de verdad (ya reducida)
        return BitmapFactory.decodeResource(getResources(), resId, options);
    }

    // insamplesize debe ser potencia de 2 (2 = mitad de ancho y alto)
    // ejemplo: imagen de 1280 de ancho y pantalla de 640 -> devuelve 2
    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {

        int sampleSize = 1;
        int halfWidth = options.outWidth / 2;
        int halfHeight = options.outHeight / 2;

        // mientras la imagen reducida siga siendo mas grande de lo que necesitamos, duplicamos el factor
        while (halfWidth / sampleSize >= reqWidth
                && halfHeight / sampleSize >= reqHeight) {
            sampleSize *= 2;
        }

        return sampleSize;
    }

    // otros

    // evita que el contenido quede bajo la barra de estado / navegacion
    // (android 15 fuerza el modo edge-to-edge)
    // insets = el espacio que ocupan las barras del sistema (hora, bateria, botones de abajo)
    private void applySystemBarInsets() {

        ViewCompat.setOnApplyWindowInsetsListener(homeScroll, (view, windowInsets) -> {

            // pedimos el tamano de las barras del sistema y del notch (si lo hay)
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
            );

            // separamos el contenido tanto como ocupen las barras
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);

            return windowInsets;
        });
    }

    // mensajes mediante snackbar
    // snackbar = mensaje pequeno y temporal abajo de la pantalla (el enunciado manda usarlo en vez de toast)
    private void showMessage(String message) {

        // make(vista, texto, duracion).show() lo crea y lo muestra
        // android.r.id.content = la vista raiz de la pantalla
        Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT
        ).show();
    }
}