package com.dmitrii.sbertest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.renderscript.Sampler;
import android.support.v4.net.ConnectivityManagerCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by Dmitrii on 11.04.2017.
 */

public class XmlLoader {
    public static final String XML_URL = "http://www.cbr.ru/scripts/XML_daily.asp";

    public interface OnLoadListener {
        public void onLoaded(CbrXmlParser.ValuteList list);
    };

    public void loadXmlStream(final Context context, final OnLoadListener listener) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            listener.onLoaded(null);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(XML_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    CbrXmlParser.ValuteList list = CbrXmlParser.parseStream(in);

                    if (list != null && list.valuteList != null) {
                        CbrXmlParser.Valute valute = new CbrXmlParser.Valute();
                        valute.code = "RUB";
                        valute.name = context.getResources().getString(R.string.rub);
                        valute.setValue(1.0f);
                        list.valuteList.add(0, valute);
                    }

                    if (list != null) {
                        listener.onLoaded(list);
                        MainPrefs.saveValutesList(context, list);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }).start();
    }
}
