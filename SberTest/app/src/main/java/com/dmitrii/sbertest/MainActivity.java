package com.dmitrii.sbertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "MainActivity";

    private AppCompatSpinner mSpinner;
    private AppCompatSpinner mSpinnerDest;

    @Override protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView result = (TextView) findViewById(R.id.result);
        final EditText edit = (EditText) findViewById(R.id.summ);

        final Runnable run = new Runnable() {
            @Override public void run() {
                String string = edit.getText().toString();
                if (TextUtils.isEmpty(string) || mValueTo <= Float.MIN_VALUE) {
                    return;
                }

                try {
                    float value = Float.parseFloat(string);
                    result.setText(String.valueOf((mValueFrom / mValueTo) * value));
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

        findViewById(R.id.btn_calc).setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                result.post(run);
            }
        });

        mSpinner = (AppCompatSpinner) findViewById(R.id.spinner);
        mSpinnerDest = (AppCompatSpinner) findViewById(R.id.spinner2);
        //final String[] str = new String[]{"1", "2", "3"};
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, str);
        //mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainPrefs.saveSelectedValuteFrom(view.getContext(), mList.valuteList.get(position).code);
                mValueFrom = mList.valuteList.get(position).mValue;
                result.post(run);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        mSpinnerDest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainPrefs.saveSelectedValuteTo(view.getContext(), mList.valuteList.get(position).code);
                mValueTo = mList.valuteList.get(position).mValue;
                result.post(run);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Load on every entrance, which can be done after pause (home btn)
     * */
    @Override
    protected void onResume() {
        super.onResume();
        new XmlLoader().loadXmlStream(this, new XmlLoader.OnLoadListener() {
            @Override public void onLoaded(CbrXmlParser.ValuteList list) {
                if (list == null) {
                    list = MainPrefs.getValutesList(getApplicationContext());
                    if (list == null) {
                        return;
                    }
                }
                mList = list;
                for (CbrXmlParser.Valute valute : list.valuteList) {
                    valute.mValue = Float.parseFloat(valute.value.replace(',', '.'));
                    Log.i(TAG, " " + valute.value + " " + valute.name + " " + valute.mValue);
                }

                mSpinner.post(new Runnable() {
                    @Override public void run() {
                        ArrayList<String> strs = new ArrayList<>();
                        String code = MainPrefs.getSelectedValuteFrom(MainActivity.this);
                        String codeTo = MainPrefs.getSelectedValuteTo(MainActivity.this);
                        int selected = -1;
                        int selectedTo = -1;
                        for (int i = 0; i < mList.valuteList.size(); ++i) {
                            CbrXmlParser.Valute valute = mList.valuteList.get(i);
                            strs.add(valute.name);
                            if (TextUtils.equals(valute.code, code)) {
                                selected   = i;
                            }
                            if (TextUtils.equals(valute.code, codeTo)) {
                                selectedTo = i;
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, strs);
                        mSpinner.setAdapter(adapter);
                        if (selected >= 0) {
                            mSpinner.setSelection(selected);
                        }

                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, strs);
                        mSpinnerDest.setAdapter(adapter2);
                        if (selected >= 0) {
                            mSpinnerDest.setSelection(selectedTo);
                        }
                    }
                });
            }
        });
    }

    private CbrXmlParser.ValuteList mList;
    private float mValueFrom;
    private float mValueTo;

}
