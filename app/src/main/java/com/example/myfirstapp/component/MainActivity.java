package com.example.myfirstapp.component;

import android.content.res.Resources;
import android.os.Bundle;

//import com.google.android.material.snackbar.Snackbar;

//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

//import androidx.recyclerview.widget.RecyclerView;

import com.example.myfirstapp.R;
import com.example.myfirstapp.communication.FragmentLabel;
import com.example.myfirstapp.communication.UIEventCode;
import com.example.myfirstapp.databinding.ActivityMainBinding;
import com.example.myfirstapp.model.RRViewModel;
import com.google.android.material.snackbar.Snackbar;

//import android.view.LayoutInflater;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private RRViewModel rrvm;
    private Resources res;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rrvm = new ViewModelProvider(this).get(RRViewModel.class);
        res = getResources();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation
                .findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private String barcode = "";
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (rrvm.getFragment() != FragmentLabel.DEMO_COMPLETE) {
            if (e.getAction() == KeyEvent.ACTION_DOWN
                    && e.getKeyCode() != KeyEvent.KEYCODE_ENTER) { //Not Adding ENTER_KEY to barcode String
                char pressedKey = (char) e.getUnicodeChar();
                barcode += pressedKey;
            }
            if (e.getAction() == KeyEvent.ACTION_DOWN
                    && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                handleUIEvent(rrvm.receivedBarcode(barcode, this), barcode);
                barcode = "";
            }
            return false;
        } else {
            return super.dispatchKeyEvent(e);
        }
    }

    private void handleUIEvent(UIEventCode code) {
        handleUIEvent(code, null);
    }

    private void handleUIEvent(UIEventCode code, String s) {
        if (code == UIEventCode.ISBN_NOT_FOUND) {
            Snackbar.make(
                    findViewById(R.id.recycler_view_cart),
                    String.format(res.getString(R.string.isbn_not_found_message), s),
                    Snackbar.LENGTH_LONG
            ).show();
        } else if (code == UIEventCode.CART_FULL) {
            Snackbar.make(
                    findViewById(R.id.recycler_view_cart),
                    res.getString(R.string.cart_full_message),
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }

}