package org.stalexman.currencyconvertor.converter;

import org.stalexman.currencyconvertor.BasePresenter;
import org.stalexman.currencyconvertor.BaseView;
import org.stalexman.currencyconvertor.data.model.Currency;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public interface ConverterContract {

    interface View extends BaseView<Presenter>{
        void showResult (String result);

        void showLoadingIndicator(boolean show);

        void setCurrencies(TreeMap<String, String> currencies);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {
        void convert(double input, String inputCurr, String outputCurr);

        void updateCurrencyList();
    }
}
