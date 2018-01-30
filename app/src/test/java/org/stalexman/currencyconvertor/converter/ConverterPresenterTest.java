package org.stalexman.currencyconvertor.converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.stalexman.currencyconvertor.data.DBHelper;
import java.util.TreeMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConverterPresenterTest {

    @Mock
    private DBHelper mDBHelper;
    @Mock
    private ConverterContract.View mView;

    private ConverterPresenter mPresenter;

    @Before
    public void setupMocksAndView() {
        MockitoAnnotations.initMocks(this);

        when(mView.isActive()).thenReturn(true);
    }

    @Test
    public void createPresenter(){
        mPresenter = new ConverterPresenter(mDBHelper, mView);

        verify(mView).setPresenter(mPresenter);
    }

    @Test
    public void showResult(){
        mPresenter = new ConverterPresenter(mDBHelper, mView);

        mPresenter.convert(100.00d, "USD", "RUB");
        verify(mView).showResult(any(String.class));
    }

    @Test
    public void updateCurrencyList(){
        mPresenter = new ConverterPresenter(mDBHelper, mView);

        mPresenter.updateCurrencyList();
        verify(mView).setCurrencies(new TreeMap<String, String>());
        verify(mView).showLoadingIndicator(false);
    }
}
