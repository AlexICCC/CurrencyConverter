package org.stalexman.currencyconvertor;

import android.app.Application;
import android.content.Intent;

import org.stalexman.currencyconvertor.network.RequestService;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Intent requestIntent = new Intent(this, RequestService.class);
        startService(requestIntent);
    }
}
