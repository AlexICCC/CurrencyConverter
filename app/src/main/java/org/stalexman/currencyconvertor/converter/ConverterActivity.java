package org.stalexman.currencyconvertor.converter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.stalexman.currencyconvertor.R;
import org.stalexman.currencyconvertor.data.DBHelper;
import org.stalexman.currencyconvertor.network.RequestService;

public class ConverterActivity extends AppCompatActivity {

    public static final String FRAGMENT_TAG = "ConverterFragment";

    private ConverterFragment converterFragment;
    private ConverterPresenter converterPresenter;
    private IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            if (intent.getAction().equals(RequestService.BROADCAST_ACTION)) {
                if (intent.getBooleanExtra(RequestService.BROADCAST_TAG_SUCCESS, false) &&
                        converterPresenter != null) {
                    converterPresenter.updateCurrencyList();
                } else {
                    showRetryMessage(intent.getStringExtra(RequestService.BROADCAST_TAG_MESSAGE));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        intentFilter.addAction(RequestService.BROADCAST_ACTION);

        if (savedInstanceState == null) {
            converterFragment = new ConverterFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.contentFrame, converterFragment)
                    .commit();
        } else {
            converterFragment = (ConverterFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, FRAGMENT_TAG);
        }

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        converterPresenter = new ConverterPresenter(dbHelper, converterFragment);
    }

    @Override
    protected void onStart() {
        registerReceiver(broadcastReceiver, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        registerReceiver(broadcastReceiver, intentFilter);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, FRAGMENT_TAG, converterFragment);
    }

    private void showRetryMessage(String message){
        if (getCurrentFocus() != null) {
            Snackbar snackBar = Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_LONG);
            snackBar.show();
        }
    }
}
