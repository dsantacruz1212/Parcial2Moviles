package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements MenuFragment.OnPlatilloAddedListener {

    MenuFragment menuFragment = new MenuFragment();
    CartFragment cartFragment = new CartFragment();
    HistorialFragment historialFragment = new HistorialFragment();
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.botton_navigation);

        // Agregar los fragmentos al contenedor, pero ocultarlos inicialmente
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, menuFragment, "menuFragment")
                .add(R.id.container, cartFragment, "cartFragment")
                .add(R.id.container, historialFragment, "historialFragment")
                .hide(cartFragment)
                .hide(historialFragment)
                .commit();

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.menu) {
                    getSupportFragmentManager().beginTransaction()
                            .show(menuFragment)
                            .hide(cartFragment)
                            .hide(historialFragment)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.cart) {
                    getSupportFragmentManager().beginTransaction()
                            .hide(menuFragment)
                            .show(cartFragment)
                            .hide(historialFragment)
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.historial) {
                    getSupportFragmentManager().beginTransaction()
                            .hide(menuFragment)
                            .hide(cartFragment)
                            .show(historialFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onPlatilloAdded(String nombrePlatillo, double precioPlatillo) {
        // Mostrar en el log los datos que se están pasando
        Log.d("MainActivity", "Platillo agregado - Nombre: " + nombrePlatillo + ", Precio: " + precioPlatillo);
        // Llamar al método recibirPedido del fragmento después de que se haya creado y las vistas se hayan inicializado
        cartFragment.recibirPedido(nombrePlatillo, precioPlatillo, 1);
    }
}