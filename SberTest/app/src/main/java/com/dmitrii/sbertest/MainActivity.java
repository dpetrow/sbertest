package com.dmitrii.sbertest;

import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "MainActivity";

    @Override protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView result = (TextView) findViewById(R.id.result);
        final EditText edit = (EditText) findViewById(R.id.summ);

        final Runnable run = new Runnable() {
            @Override public void run() {
                try {
                    float value = Float.parseFloat(edit.getText().toString());
                    result.setText(String.valueOf(mValue * value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    result.post(run);
                    return true;
                }
                return false;
            }
        });


        final AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.spinner);
        final String[] str = new String[]{"1", "2", "3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, str);
        spinner.setAdapter(adapter);
        //spinner.setAdapter();

        new XmlLoader().loadXmlStream(new XmlLoader.OnLoadListener() {
            @Override public void onLoaded(CbrXmlParser.ValuteList list) {
                mList = list;
                for (CbrXmlParser.Valute valute : list.valuteList) {
                    valute.mValue = Float.parseFloat(valute.value.replace(',', '.'));
                    Log.i(TAG, " " + valute.value + " " + valute.name + " " + valute.mValue);
                }

                spinner.post(new Runnable() {
                    @Override public void run() {
                        ArrayList<String> strs = new ArrayList<>();
                        for (CbrXmlParser.Valute valute : mList.valuteList) {
                            strs.add(valute.name);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, strs);
                        spinner.setAdapter(adapter);
                    }
                });
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mValue = mList.valuteList.get(position).mValue;
                result.post(run);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });


    }

    private CbrXmlParser.ValuteList mList;
    private float mValue;

}
