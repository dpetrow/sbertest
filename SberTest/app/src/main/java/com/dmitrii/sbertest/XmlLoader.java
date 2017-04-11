package com.dmitrii.sbertest;

import android.renderscript.Sampler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dmitrii on 11.04.2017.
 */

public class XmlLoader {
    public static final String XML_URL = "http://www.cbr.ru/scripts/XML_daily.asp";

    public interface OnLoadListener {
        public void onLoaded(CbrXmlParser.ValuteList list);
    };

    public void loadXmlStream(final OnLoadListener listener) {
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
                    listener.onLoaded(list);
                    return;
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
