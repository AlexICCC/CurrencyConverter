package org.stalexman.currencyconvertor.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * <Valute ID="R01010">
 *     <NumCode>036</NumCode>
 *     <CharCode>AUD</CharCode>
 *     <Nominal>1</Nominal>
 *     <Name>Австралийский доллар</Name>
 *     <Value>44,9261</Value>
 * </Valute>
 */

@Root(name="Valute")
public class Currency {

    @Element(name="CharCode")
    private String charCode;

    @Element(name="Nominal")
    private int nominal;

    @Element(name="Name")
    private String name;

    @Element(name="Value")
    private Double value;

    public Currency(){}

    public Currency(String charCode, int nominal, String name, Double value) {
        this.charCode = charCode;
        this.nominal = nominal;
        this.name = name;
        this.value = value;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public int getNominal() {
        return nominal;
    }

    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
