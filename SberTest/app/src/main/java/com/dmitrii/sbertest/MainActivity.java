package com.dmitrii.sbertest;

import android.animation.Animator;
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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "MainActivity";

    private AppCompatSpinner mSpinner;
    private AppCompatSpinner mSpinnerDest;

    private View mMainLayout;
    private View mProgress;

    @Override protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgress = findViewById(R.id.progress);
        mMainLayout = findViewById(R.id.main);

        final TextView result = (TextView) findViewById(R.id.result);
        final EditText edit = (EditText) findViewById(R.id.summ);

        final Runnable runCalc = new Runnable() {
            @Override public void run() {
                String string = edit.getText().toString();
                if (TextUtils.isEmpty(string)) {
                    return;
                }

                try {
                    float value = Float.parseFloat(string);
                    float valuteFrom = ((ValuteAdapter)mSpinner.getAdapter()).getValueByPosition(mSpinner.getSelectedItemPosition());
                    float valuteTo = ((ValuteAdapter)mSpinnerDest.getAdapter()).getValueByPosition(mSpinnerDest.getSelectedItemPosition());
                    result.setText(String.valueOf((valuteFrom / valuteTo) * value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        if (BuildConfig.FAST_CALC) {
            edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        result.post(runCalc);
                        return true;
                    }
                    return false;
                }
            });
        }

        findViewById(R.id.btn_calc).setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v) {
                result.post(runCalc);
            }
        });

        mSpinner = (AppCompatSpinner) findViewById(R.id.spinner);
        mSpinnerDest = (AppCompatSpinner) findViewById(R.id.spinner2);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainPrefs.saveSelectedValuteFrom(view.getContext(), mList.valuteList.get(position).code);
                if (BuildConfig.FAST_CALC) {
                    result.post(runCalc);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        mSpinnerDest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainPrefs.saveSelectedValuteTo(view.getContext(), mList.valuteList.get(position).code);
                if (BuildConfig.FAST_CALC) {
                    result.post(runCalc);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mList = MainPrefs.getValutesList(getApplicationContext());
        new XmlLoader().loadXmlStream(this, new XmlLoader.OnLoadListener() {
            @Override public void onLoaded(CbrXmlParser.ValuteList list) {
                if (list == null) {
                    return;
                }
                mList = list;
                if (!mRunned)
                    runOnUiThread(mValutesRunnable);
            }
        });
    }

    private boolean mRunned;
    private final Runnable mValutesRunnable = new Runnable() {
        @Override public void run() {
            if (mRunned || mList == null) {
                return;
            }

            if (mMainLayout.getVisibility() != View.VISIBLE) {
                mProgress.animate().alpha(0).setListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {}
                    @Override public void onAnimationEnd(Animator animation) {
                        mProgress.setVisibility(View.GONE);
                    }

                    @Override public void onAnimationCancel(Animator animation) {}
                    @Override public void onAnimationRepeat(Animator animation) {}
                }).start();

                mMainLayout.setAlpha(0.0f);
                mMainLayout.setVisibility(View.VISIBLE);
                mMainLayout.animate().alpha(1.0f).start();
            }


            mRunned = true;
            String[] codes = new String[]{MainPrefs.getSelectedValuteFrom(MainActivity.this),
                    MainPrefs.getSelectedValuteTo(MainActivity.this)};
            int selected[] = new int[]{-1, -1};
            AppCompatSpinner[] spinners = new AppCompatSpinner[]{mSpinner, mSpinnerDest};
            for (int i = 0; i < codes.length; ++i) {
                for (int k = 0; k < mList.valuteList.size(); ++k) {
                    CbrXmlParser.Valute valute = mList.valuteList.get(k);
                    if (TextUtils.equals(valute.code, codes[i])) {
                        selected[i]   = k;
                    }
                }

                ValuteAdapter adapter = new ValuteAdapter(MainActivity.this, android.R.layout.simple_spinner_item).setValuteList(mList);
                spinners[i].setAdapter(adapter);
                if (selected[i] >= 0) {
                    spinners[i].setSelection(selected[i]);
                }
            }
        }
    };

    /**
     * Load on every entrance, which can be done after pause (home btn)
     * */
    @Override
    protected void onResume() {
        super.onResume();

        if (mList == null) {
            mMainLayout.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);
        }

        if (mList != null && !mRunned) {
            runOnUiThread(mValutesRunnable);
        }
    }

    private CbrXmlParser.ValuteList mList;

    @Override protected void onPause() {
        super.onPause();
        mRunned = false;
    }
}
