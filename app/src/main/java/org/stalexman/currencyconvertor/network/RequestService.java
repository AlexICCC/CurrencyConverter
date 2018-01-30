package org.stalexman.currencyconvertor.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.simpleframework.xml.core.Persister;
import org.stalexman.currencyconvertor.R;
import org.stalexman.currencyconvertor.data.Constants;
import org.stalexman.currencyconvertor.data.DBHelper;
import org.stalexman.currencyconvertor.data.model.Currency;
import org.stalexman.currencyconvertor.data.model.ValCurs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class RequestService extends IntentService {

    public static final String BROADCAST_ACTION = "org.stalexman.currencyconvertor.requestservice";
    public static final String BROADCAST_TAG_MESSAGE = "BROADCAST_TAG_MESSAGE";
    public static final String BROADCAST_TAG_SUCCESS = "BROADCAST_TAG_SUCCESS";

    public RequestService() {
        super("RequestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent broadcastMessage = new Intent(BROADCAST_ACTION);
        String message;
        boolean success;

        if (!isOnline()){
            message = getString(R.string.is_error_no_internet);
            broadcastMessage.putExtra(BROADCAST_TAG_MESSAGE, message);
            broadcastMessage.putExtra(BROADCAST_TAG_SUCCESS, false);
            sendBroadcast(broadcastMessage);
            return;
        }

        try {
            String xml = getXML(Constants.CURRENCY_URL);
            ValCurs valCurs = parseXML(xml);

            saveDate(valCurs.getName());
            List<Currency> list = valCurs.getCurrencyList();
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            if (list != null) {
                for (Currency currency : list) {
                    dbHelper.insertOrUpdateCurrency(currency);
                }
            }

            message = getString(R.string.is_success);
            success = true;
        } catch (IOException ioe) {
            message = getString(R.string.is_error_network);
            success = false;
        } catch (Exception e) {
            message = getString(R.string.is_error_parsing);
            success = false;
        }

        broadcastMessage.putExtra(BROADCAST_TAG_MESSAGE, message);
        broadcastMessage.putExtra(BROADCAST_TAG_SUCCESS, success);
        sendBroadcast(broadcastMessage);
    }

    private static ValCurs parseXML (String xml) throws Exception {
        Reader reader = new StringReader(xml);
        Persister serializer = new Persister();
        return serializer.read(ValCurs.class, reader, false);
    }

    private static String getXML(String urlString) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultXmlString = null;
        try {
            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) throw new IOException();
            InputStreamReader isr = new InputStreamReader(inputStream, "windows-1251");
            reader = new BufferedReader(isr);

            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                throw new IOException();
            }
            resultXmlString = buffer.toString().replaceAll(",", ".");
            Log.i("LOG", resultXmlString);
        } catch (IOException e) {
            // Exception пробрасывается дальше, чтобы закрыть соединение в finally
            throw new IOException(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                reader.close();
            }
        }
        return resultXmlString;
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void saveDate(String date) {
        if (date == null) return;

        SharedPreferences preferences = getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.PREF_DATE, date);
        editor.apply();
    }
}
