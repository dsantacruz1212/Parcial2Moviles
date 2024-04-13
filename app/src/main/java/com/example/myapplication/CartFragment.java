package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import java.text.DecimalFormat;
import java.util.ArrayList;
import android.widget.Toast;
import java.util.List;
import android.widget.Button;
import java.text.SimpleDateFormat;
import java.util.Date;
import androidx.annotation.NonNull;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.tasks.OnSuccessListener;

public class CartFragment extends Fragment {
    private static final String TAG = "CartFragment";
    private TextView txtTotalAPagar;
    private LinearLayout layoutPlatillos;
    private List<String> nombresPlatillos = new ArrayList<>();
    private List<Double> preciosPlatillos = new ArrayList<>();
    private List<Integer> cantidadesPlatillos = new ArrayList<>();
    private Button btnPagar;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        layoutPlatillos = view.findViewById(R.id.CartLayout); // Obtener referencia al LinearLayout
        layoutPlatillos.setVisibility(View.VISIBLE); // Asegurarse de que el layout esté visible
        txtTotalAPagar = view.findViewById(R.id.txtTotalAPagar);
        btnPagar = view.findViewById(R.id.btnPagar);
        // Clear the list of items when the fragment is created
        nombresPlatillos.clear();
        preciosPlatillos.clear();
        cantidadesPlatillos.clear();

        mostrarTotalAPagar();
        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Método que maneja el evento de clic en el botón "Pagar"
                pagar();
            }
        });

        return view;
    }
    public void recibirPedido(String nombrePlatillo, double precioPlatillo, int cantidad) {

        // Verificar si el nombre del platillo ya está en la lista
        int index = nombresPlatillos.indexOf(nombrePlatillo);
        if (index != -1) {
            // Si el platillo ya está en la lista, incrementar la cantidad
            int cantidadActual = cantidadesPlatillos.get(index);
            cantidadesPlatillos.set(index, cantidadActual + cantidad);
            // Mostrar en el log los datos recibidos con la cantidad actualizada
            Log.d(TAG, "Platillo recibido - Nombre: " + nombrePlatillo + ", Precio: " + precioPlatillo + " , Cantidad actualizada: " + (cantidadActual + cantidad));

            // Mostrar el platillo con la cantidad actualizada
            actualizarPlatillo(index, nombrePlatillo, precioPlatillo, cantidadActual + cantidad);
        } else {
            // Si el platillo no está en la lista, agregarlo con la cantidad especificada
            nombresPlatillos.add(nombrePlatillo);
            preciosPlatillos.add(precioPlatillo);
            cantidadesPlatillos.add(cantidad);
            Log.d(TAG, "Platillo recibido - Nombre: " + nombrePlatillo + ", Precio: " + precioPlatillo + ", Cantidad actualizada: " + (cantidad));

            // Mostrar el platillo con la cantidad inicial
            mostrarPlatillo(nombrePlatillo, precioPlatillo, cantidad);
            mostrarTotalAPagar();
        }
    }
    private void mostrarPlatillo(String nombrePlatillo, double precioPlatillo, int cantidad) {
        // Inflar el diseño de un platillo individual
        View platilloView = LayoutInflater.from(getContext()).inflate(R.layout.item_cart, null);
        mostrarTotalAPagar();
        // Obtener referencias a los elementos de la vista del platillo
        TextView txtNombrePlatillo = platilloView.findViewById(R.id.txtPlatilloNombre);
        TextView txtPrecioPlatillo = platilloView.findViewById(R.id.txtPlatilloPrecio);
        TextView txtCantidadPlatillo = platilloView.findViewById(R.id.txtPlatilloCantidad);
        ImageButton btnRestar = platilloView.findViewById(R.id.btnRestar);
        ImageButton btnSumar = platilloView.findViewById(R.id.btnSumar);
        ImageButton btnEliminar = platilloView.findViewById(R.id.btnEliminarPlatillo);

        // Configurar los valores del platillo
        txtNombrePlatillo.setText(nombrePlatillo);
        txtPrecioPlatillo.setText(String.valueOf("$" +precioPlatillo));
        txtCantidadPlatillo.setText(String.valueOf(cantidad));
        mostrarTotalAPagar();

        // Agregar click listeners a los botones
        btnRestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restarPlatillo(platilloView);
                mostrarTotalAPagar(); // Actualizar el total después de restar
            }
        });

        btnSumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumarPlatillo(platilloView);
                mostrarTotalAPagar(); // Actualizar el total después de sumar
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarPlatillo(platilloView);
                mostrarTotalAPagar(); // Actualizar el total después de eliminar
            }
        });

        // Agregar la vista del platillo al layout
        layoutPlatillos.addView(platilloView);

        // Log para verificar si se están pasando los datos correctamente
        Log.d(TAG, "Platillo mostrado - Nombre: " + nombrePlatillo + ", Precio: " + precioPlatillo + ", Cantidad: " + cantidad);
    }
    private void actualizarPlatillo(int index, String nombrePlatillo, double precioPlatillo, int cantidad) {

        // Obtener referencias a los elementos de la vista del platillo
        View platilloView = layoutPlatillos.getChildAt(index);
        TextView txtNombrePlatillo = platilloView.findViewById(R.id.txtPlatilloNombre);
        TextView txtPrecioPlatillo = platilloView.findViewById(R.id.txtPlatilloPrecio);
        TextView txtCantidadPlatillo = platilloView.findViewById(R.id.txtPlatilloCantidad);

        // Configurar los valores del platillo
        txtNombrePlatillo.setText(nombrePlatillo);
        txtPrecioPlatillo.setText(String.valueOf("$" +precioPlatillo));
        txtCantidadPlatillo.setText(String.valueOf(cantidad));

        // Log para verificar si se están pasando los datos correctamente
        Log.d(TAG, "Platillo actualizado - Nombre: " + nombrePlatillo + ", Precio: " + precioPlatillo + ", Cantidad: " + cantidad);
        mostrarTotalAPagar();
    }
    private void mostrarTotalAPagar() {
        double totalAPagar = 0;
        for (int i = 0; i < layoutPlatillos.getChildCount(); i++) {
            View platilloView = layoutPlatillos.getChildAt(i);
            TextView txtCantidadPlatillo = platilloView.findViewById(R.id.txtPlatilloCantidad);
            TextView txtPrecioPlatillo = platilloView.findViewById(R.id.txtPlatilloPrecio);
            int cantidad = Integer.parseInt(txtCantidadPlatillo.getText().toString());
            String precioString = txtPrecioPlatillo.getText().toString().replace("$", ""); // Eliminar el símbolo "$"
            double precio = Double.parseDouble(precioString);
            totalAPagar += precio * cantidad;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Formato para dos decimales
        String totalFormateado = decimalFormat.format(totalAPagar);
        txtTotalAPagar.setText("Total a pagar: $" + totalFormateado);
    }
    private void restarPlatillo(View platilloView) {
        TextView txtCantidadPlatillo = platilloView.findViewById(R.id.txtPlatilloCantidad);
        int cantidad = Integer.parseInt(txtCantidadPlatillo.getText().toString());
        if (cantidad > 1) {
            cantidad--;
            txtCantidadPlatillo.setText(String.valueOf(cantidad));
        } else {
            // Si la cantidad es 1, establecerla a cero y esperar un segundo antes de eliminar el platillo
            txtCantidadPlatillo.setText("0");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutPlatillos.removeView(platilloView);
                    // Remover el platillo de las listas también
                    TextView txtNombrePlatillo = platilloView.findViewById(R.id.txtPlatilloNombre);
                    String nombrePlatillo = txtNombrePlatillo.getText().toString();
                    int index = nombresPlatillos.indexOf(nombrePlatillo);
                    if (index != -1) {
                        nombresPlatillos.remove(index);
                        preciosPlatillos.remove(index);
                        cantidadesPlatillos.remove(index);
                    }
                }
            }, 500); // Esperar 1 segundo antes de eliminar el platillo
        }
    }
    private void sumarPlatillo(View platilloView) {
        TextView txtCantidadPlatillo = platilloView.findViewById(R.id.txtPlatilloCantidad);
        int cantidad = Integer.parseInt(txtCantidadPlatillo.getText().toString());
        cantidad++;
        txtCantidadPlatillo.setText(String.valueOf(cantidad));
    }
    private void eliminarPlatillo(View platilloView) {
        layoutPlatillos.removeView(platilloView);
        // Remover el platillo de las listas
        TextView txtNombrePlatillo = platilloView.findViewById(R.id.txtPlatilloNombre);
        String nombrePlatillo = txtNombrePlatillo.getText().toString();
        int index = nombresPlatillos.indexOf(nombrePlatillo);
        if (index != -1) {
            nombresPlatillos.remove(index);
            preciosPlatillos.remove(index);
            cantidadesPlatillos.remove(index);
        }
    }

    private void pagar() {
        // Crear un ArrayList para almacenar los datos de los platillos
        ArrayList<HashMap<String, Object>> listaPlatillos = new ArrayList<>();

        // Recorrer los elementos del layoutPlatillos
        for (int i = 0; i < layoutPlatillos.getChildCount(); i++) {
            View platilloView = layoutPlatillos.getChildAt(i);
            TextView txtNombrePlatillo = platilloView.findViewById(R.id.txtPlatilloNombre);
            TextView txtPrecioPlatillo = platilloView.findViewById(R.id.txtPlatilloPrecio);
            TextView txtCantidadPlatillo = platilloView.findViewById(R.id.txtPlatilloCantidad);

            // Crear un HashMap para almacenar los datos del platillo actual
            HashMap<String, Object> platillo = new HashMap<>();
            platillo.put("nombre", txtNombrePlatillo.getText().toString());
            platillo.put("precio", Double.parseDouble(txtPrecioPlatillo.getText().toString().replace("$", ""))); // Eliminar el símbolo "$"
            platillo.put("cantidad", Integer.parseInt(txtCantidadPlatillo.getText().toString()));

            // Agregar el platillo al ArrayList
            listaPlatillos.add(platillo);
        }

        // Calcular el total pagado
        double totalPagado = calcularTotalPagado(listaPlatillos);

        // Obtener la fecha actual
        String fechaActual = obtenerFechaActual();

        // Agregar el total pagado y la fecha al primer elemento del ArrayList
        if (!listaPlatillos.isEmpty()) {
            HashMap<String, Object> primerPlatillo = listaPlatillos.get(0);
            primerPlatillo.put("total_pagado", totalPagado);
            primerPlatillo.put("fecha", fechaActual);
        }

        // Ahora puedes enviar la lista de platillos a Firebase
        // Por ejemplo, puedes utilizar Firebase Realtime Database o Cloud Firestore para esto
        // Aquí un ejemplo de cómo podrías hacerlo utilizando Realtime Database:
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("pedidos");
        databaseReference.push().setValue(listaPlatillos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Éxito al enviar los datos a Firebase
                        Toast.makeText(getContext(), "Pedido realizado con éxito", Toast.LENGTH_SHORT).show();

                        // Limpiar el carrito después de un pedido exitoso
                        limpiarCarrito();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al enviar los datos a Firebase
                        Log.e(TAG, "Error al enviar el pedido a Firebase", e);
                        Toast.makeText(getContext(), "Error al realizar el pedido", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private double calcularTotalPagado(ArrayList<HashMap<String, Object>> listaPlatillos) {
        double totalPagado = 0;
        for (HashMap<String, Object> platillo : listaPlatillos) {
            double precio = (double) platillo.get("precio");
            int cantidad = (int) platillo.get("cantidad");
            totalPagado += precio * cantidad;
        }
        return totalPagado;
    }

    private String obtenerFechaActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void limpiarCarrito() {
        // Limpiar el layoutPlatillos
        layoutPlatillos.removeAllViews();

        // Limpiar las listas de platillos
        nombresPlatillos.clear();
        preciosPlatillos.clear();
        cantidadesPlatillos.clear();

        // Actualizar el total a pagar a cero
        mostrarTotalAPagar();

        // Mostrar una alerta indicando que el pedido ha sido pagado
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("¡Pedido pagado!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cerrar la alerta y continuar
                        dialog.dismiss();
                    }
                });
        // Crear y mostrar la alerta
        AlertDialog alert = builder.create();
        alert.show();
    }



}




