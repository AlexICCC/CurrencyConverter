package org.stalexman.currencyconvertor.converter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.stalexman.currencyconvertor.R;
import org.stalexman.currencyconvertor.data.Constants;

import java.util.TreeMap;


public class ConverterFragment extends Fragment implements ConverterContract.View, SharedPreferences.OnSharedPreferenceChangeListener {

    private ConverterContract.Presenter mPresenter;
    private TreeMap<String, String> currencies;
    private SharedPreferences prefs;
    private IntentFilter intentFilter = new IntentFilter();

    private EditText et_input;
    private TextView tv_inputCurr;
    private TextView tv_outputCurr;
    private TextView tv_result;
    private TextView tv_date;
    private ImageButton btn_exchange;
    private ProgressBar progressBar;

    private View.OnClickListener onShowDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogFragment dialog = DialogSearch.newInstance(v.getId(), currencies);
            dialog.show(getFragmentManager(), "dialog");
        }
    };
    private View.OnClickListener onChangeCurrenciesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String temp = tv_inputCurr.getText().toString();
            tv_inputCurr.setText(tv_outputCurr.getText().toString());
            tv_outputCurr.setText(temp);
            convert();
        }
    };
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {convert();}
        @Override
        public void afterTextChanged(Editable s) {}
    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            if (intent.getAction().equals(DialogSearch.BROADCAST_ACTION)) {
                int id = intent.getIntExtra(DialogSearch.BROADCAST_TAG_SENDER_ID, 0);
                String text = intent.getStringExtra(DialogSearch.BROADCAST_TAG_TEXT);
                setText(id, text);
            }
        }
    };

    public ConverterFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_converter, container, false);
        intentFilter.addAction(DialogSearch.BROADCAST_ACTION);

        et_input = (EditText) rootView.findViewById(R.id.et_input);
        tv_date = (TextView) rootView.findViewById(R.id.tv_date);
        tv_inputCurr = (TextView) rootView.findViewById(R.id.tv_input_curr);
        tv_outputCurr = (TextView) rootView.findViewById(R.id.tv_output_curr);
        tv_result = (TextView) rootView.findViewById(R.id.tv_result);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        btn_exchange = (ImageButton) rootView.findViewById(R.id.btn_exchange);

        et_input.addTextChangedListener(textWatcher);

        tv_inputCurr.setOnClickListener(onShowDialogListener);
        tv_outputCurr.setOnClickListener(onShowDialogListener);
        btn_exchange.setOnClickListener(onChangeCurrenciesListener);

        prefs = getActivity().getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE);
        String date = prefs.getString(Constants.PREF_DATE, null);
        if (date != null) {
            tv_date.setText(getString(R.string.cf_last_update, date));
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null){
            tv_inputCurr.setText("RUB");
            tv_outputCurr.setText("USD");
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        prefs.registerOnSharedPreferenceChangeListener(this);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
        mPresenter.start();
        super.onStart();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(broadcastReceiver);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    public void showResult(String result) {
        tv_result.setText(result);
    }

    @Override
    public void showLoadingIndicator(boolean show) {
        if (show){
            progressBar.setVisibility(View.VISIBLE);
            et_input.setVisibility(View.GONE);
            tv_inputCurr.setVisibility(View.GONE);
            tv_outputCurr.setVisibility(View.GONE);
            tv_result.setVisibility(View.GONE);
            btn_exchange.setVisibility(View.GONE);
            tv_date.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            et_input.setVisibility(View.VISIBLE);
            tv_inputCurr.setVisibility(View.VISIBLE);
            tv_outputCurr.setVisibility(View.VISIBLE);
            tv_result.setVisibility(View.VISIBLE);
            btn_exchange.setVisibility(View.VISIBLE);
            tv_date.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setPresenter(ConverterContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setCurrencies(TreeMap<String, String> currencies) {
        this.currencies = currencies;
    }


    public void setText(int id, String text){
        if (id == tv_inputCurr.getId()){
            tv_inputCurr.setText(text);
        }
        if (id == tv_outputCurr.getId()){
            tv_outputCurr.setText(text);
        }
        convert();
    }

    private void convert(){
        double input;
        String inputCurr = tv_inputCurr.getText().toString();
        String outputCurr = tv_outputCurr.getText().toString();
        String str = et_input.getText().toString();
        try {
            input = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            tv_result.setText("");
            return;
        }
        mPresenter.convert(input, inputCurr, outputCurr);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
        if (key.equals(Constants.PREF_DATE)) {
            String date = pref.getString(Constants.PREF_DATE, "");
            tv_date.setText(getString(R.string.cf_last_update, date));
        }
    }
}
