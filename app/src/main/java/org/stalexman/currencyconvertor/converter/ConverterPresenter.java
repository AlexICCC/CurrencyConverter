package org.stalexman.currencyconvertor.converter;

import org.stalexman.currencyconvertor.data.DBHelper;
import org.stalexman.currencyconvertor.data.model.Currency;

import java.util.List;
import java.util.Locale;

public class ConverterPresenter implements ConverterContract.Presenter {

    private DBHelper mDBHelper;
    private ConverterContract.View mView;
    private List<Currency> currencyList;

    public ConverterPresenter(DBHelper dbHelper, ConverterContract.View mView) {
        this.mDBHelper = dbHelper;
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void start() {
        currencyList = mDBHelper.getCurrencyAll();
        if (currencyList.isEmpty()) {
            mView.showLoadingIndicator(true);
        } else {
            mView.setCurrencies(mDBHelper.getCurrNameToCharCode());
            mView.showLoadingIndicator(false);
        }
    }

    @Override
    public void updateCurrencyList(){
        currencyList = mDBHelper.getCurrencyAll();
        mView.setCurrencies(mDBHelper.getCurrNameToCharCode());
        mView.showLoadingIndicator(false);
    }

    @Override
    public void convert(double input, String inputCurr, String outputCurr) {
        try {
            double value = convertINPUTtoRUB(input, inputCurr);
            double result = convertRUBtoOUTPUT(value, outputCurr);
            mView.showResult(String.format(Locale.US, "%.2f", result));
        } catch (Exception e) {
            mView.showResult("");
        }
    }

    private double convertINPUTtoRUB(double input, String inputCurr) {
        Currency currency = getCurrencyByCharCode(inputCurr);
        return input * currency.getValue() / (double)currency.getNominal();
    }

    private double convertRUBtoOUTPUT(double value, String outputCurr){
        Currency currency = getCurrencyByCharCode(outputCurr);
        return value * (double) currency.getNominal() / currency.getValue();
    }

    private Currency getCurrencyByCharCode(String charCode){
        for (Currency currency: currencyList){
            if (currency.getCharCode().equals(charCode)){
                return currency;
            }
        }
        return null;
    }



}
