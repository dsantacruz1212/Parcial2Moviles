package com.example.myapplication;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;

import android.view.View;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.math.RoundingMode;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.LayoutInflater;

import android.view.ViewGroup;




public class HistorialFragment extends Fragment {

    private static final String TAG = "HistorialFragment"; // Etiqueta para los logs
    private LinearLayout linearLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        // Llamar al método para obtener y mostrar los datos
        mostrarPedidos();

        return view;
    }

    private void mostrarPedidos() {
        // Obtener una referencia a la ubicación de los pedidos en Firebase
        DatabaseReference pedidosRef = FirebaseDatabase.getInstance().getReference("pedidos");

        // Escuchar los cambios en esa ubicación
        pedidosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getChildren() != null) {
                    for (DataSnapshot pedidoSnapshot : dataSnapshot.getChildren()) {
                        String fecha = pedidoSnapshot.child("0").child("fecha").getValue(String.class);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                        Date fechaDate;
                        try {
                            fechaDate = dateFormat.parse(fecha);
                            SimpleDateFormat fechaFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            fecha = fechaFormat.format(fechaDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e("DateFormatError", "Error al formatear la fecha");
                        }

// Redondear el total pagado
                        Double totalPagado = pedidoSnapshot.child("0").child("total_pagado").getValue(Double.class);
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                        String totalPagadoString = decimalFormat.format(totalPagado);

                        Log.d("FirebaseData", "Fecha: " + fecha + ", Total pagado: " + totalPagado);

                        // Inflar el layout fecha_item.xml
                        View fechaItemView = LayoutInflater.from(getContext()).inflate(R.layout.fecha_item, null);

                        if (fechaItemView == null) {
                            Log.e("InflateError", "Error al inflar fecha_item.xml");
                            return;
                        }

                        // Obtener referencias a los TextView dentro de la vista de fecha_item
                        TextView orderDateTextView = fechaItemView.findViewById(R.id.orderDate);
                        TextView orderTotalTextView = fechaItemView.findViewById(R.id.orderTotal);

                        if (orderDateTextView == null || orderTotalTextView == null) {
                            Log.e("TextViewError", "TextView no encontrado en fecha_item.xml");
                            return;
                        }

                        // Establecer el contenido de los TextView con la fecha y el total pagado
                        orderDateTextView.setText("Fecha: " + fecha);
                        orderTotalTextView.setText("Total: $" + totalPagado);

                        // Agregar la vista de fecha_item al contenedor principal (LinearLayout) fragment_historial
                        LinearLayout fragmentHistorialLayout = getView().findViewById(R.id.principal);
                        fragmentHistorialLayout.setVisibility(View.VISIBLE);
                        if (fragmentHistorialLayout == null) {
                            Log.e("LinearLayoutError", "LinearLayout fragment_historial no encontrado en la vista");
                            return;
                        }
                        fragmentHistorialLayout.addView(fechaItemView);




                    }
                } else {
                    Log.e("FirebaseData", "DataSnapshot o sus hijos son nulos");
                }
            }

            private void mostrarDetallesPedido(DataSnapshot dataSnapshot) {
                // Iterar sobre los hijos del nodo pedido
                for (DataSnapshot detalleSnapshot : dataSnapshot.getChildren()) {

                }
            }








            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de cancelación, si es necesario
                Log.e(TAG, "Error al obtener datos de Firebase: " + databaseError.getMessage());
            }
        });
    }
}
