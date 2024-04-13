package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;
import java.util.List;
import android.content.DialogInterface;

public class MenuFragment extends Fragment {
    private List<Platillo> listaDePlatillos = new ArrayList<>();
    public interface OnPlatilloAddedListener {
        void onPlatilloAdded(String nombrePlatillo, double precioPlatillo);
    }
    private OnPlatilloAddedListener onPlatilloAddedListener; // Interfaz para comunicarse con la actividad principal

    // Clase interna Platillo
    public class Platillo {
        private String nombre;
        private double precio;
        private int cantidad;

        public Platillo(String nombre, double precio, int cantidad) {
            this.nombre = nombre;
            this.precio = precio;
            this.cantidad = cantidad;
        }

        // Getters y setters
        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public double getPrecio() {
            return precio;
        }

        public void setPrecio(double precio) {
            this.precio = precio;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }
    }

    // Lista para almacenar los platillos seleccionados
    private ArrayList<Platillo> platillosSeleccionados = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verificar si la actividad implementa la interfaz OnPlatilloAddedListener
        if (context instanceof OnPlatilloAddedListener) {
            onPlatilloAddedListener = (OnPlatilloAddedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnPlatilloAddedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        LinearLayout menuLayout = view.findViewById(R.id.menuLayout);

        try {
            JSONObject jsonMenu = new JSONObject(loadJSONFromAsset());
            Log.d("MenuFragment", "JSON cargado correctamente: " + jsonMenu.toString());

            JSONObject menu = jsonMenu.getJSONObject("menu");
            Log.d("MenuFragment", "Objeto 'menu' obtenido correctamente: " + menu.toString());

            // Obtener el JSONArray de platillos del objeto menu
            JSONArray platillos = menu.getJSONArray("platillos");

            // Iterar sobre cada platillo y configurar la vista correspondiente
            for (int i = 0; i < platillos.length(); i++) {
                JSONObject platillo = platillos.getJSONObject(i);

                // Inflar el layout del item de menú
                View menuItemView = inflater.inflate(R.layout.fragment_menu, null);

                TextView itemNameTextView = menuItemView.findViewById(R.id.menuItemName);
                TextView itemDescriptionTextView = menuItemView.findViewById(R.id.menuItemDescription);
                TextView itemPriceTextView = menuItemView.findViewById(R.id.menuItemPrice);
                ImageView itemImageView = menuItemView.findViewById(R.id.menuItemImage);
                ImageButton menuItemAddButton = menuItemView.findViewById(R.id.menuItemAddButton);

                // Configurar los elementos del menú con los datos del JSON
                final String nombrePlatillo = platillo.getString("nombre");
                final double precioPlatillo = platillo.getDouble("precio");
                itemNameTextView.setText(nombrePlatillo);
                itemDescriptionTextView.setText(platillo.getString("descripcion"));
                itemPriceTextView.setText("$" + precioPlatillo);

                // Mostrar los elementos del menú
                itemNameTextView.setVisibility(View.VISIBLE);
                itemDescriptionTextView.setVisibility(View.VISIBLE);
                itemPriceTextView.setVisibility(View.VISIBLE);
                itemImageView.setVisibility(View.VISIBLE);
                menuItemAddButton.setVisibility(View.VISIBLE);

                // Obtener la URL de la imagen del platillo
                String imageUrl = platillo.getString("imagen");

                // Cargar la imagen usando Glide desde la URL y mostrarla en el ImageView
                Glide.with(requireContext()).load(imageUrl).into(itemImageView);

                // Agregar la vista del elemento de menú al layout
                menuLayout.addView(menuItemView);

                // Listener para el botón de agregar
                menuItemAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Mostrar la alerta de platillo agregado
                        mostrarAlerta(nombrePlatillo);
                        // Log para imprimir los datos que se están enviando
                        Log.d("MenuFragment", "Platillo agregado - Nombre: " + nombrePlatillo + ", Precio: " + precioPlatillo);
                        // Llamar al método de la interfaz para enviar los datos al CartFragment
                        onPlatilloAddedListener.onPlatilloAdded(nombrePlatillo, precioPlatillo);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("MenuFragment", "Error al analizar el JSON: " + e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return view;
    }

    // Método para cargar el archivo JSON desde la carpeta assets
    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = requireActivity().getAssets().open("menu_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Log.d("JSON", json);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    // Método para mostrar la alerta de platillo agregado
    private void mostrarAlerta(String nombrePlatillo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Platillo Agregado")
                .setMessage("Se ha agregado " + nombrePlatillo + " al carrito.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Acción a realizar al hacer clic en el botón "Aceptar"
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
