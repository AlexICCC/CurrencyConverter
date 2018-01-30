package org.stalexman.currencyconvertor.data.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * <ValCurs Date="24.01.2018" name="Foreign Currency Market">
 *     <Valute ID="R01010">
 *         <NumCode>036</NumCode>
 *         <CharCode>AUD</CharCode>
 *         <Nominal>1</Nominal>
 *         <Name>Австралийский доллар</Name>
 *         <Value>44,9261</Value>
 *      </Valute>
 * </ValCurs>
 */

@Root(name="ValCurs")
public class ValCurs {

    @Attribute(name="Date")
    private String name;

    @ElementList(inline=true, name="Valute")
    private List<Currency> currencyList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Currency> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<Currency> currencyList) {
        this.currencyList = currencyList;
    }
}
