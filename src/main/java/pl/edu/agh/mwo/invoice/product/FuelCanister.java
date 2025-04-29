package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FuelCanister extends Product {
    private static final BigDecimal excise = new BigDecimal("5.56");
    private static final int month = 3;
    private static final int day = 5;

    private LocalDate testDate = null; // tylko do testów

    public FuelCanister(String name, BigDecimal price) {
        super(name, price, new BigDecimal("0.23"));
    }

    @Override
    public BigDecimal getPriceWithTax() {
        LocalDate date = (testDate != null) ? testDate : LocalDate.now();

        BigDecimal base = getPrice();
        BigDecimal vat = isTaxFreeDay(date) ? BigDecimal.ZERO : base.multiply(getTaxPercent());
        return base.add(vat).add(excise);
    }

    private boolean isTaxFreeDay(LocalDate date) {
        return date.getMonthValue() == month && date.getDayOfMonth() == day;
    }

    // tylko do testów
    public void setTestDate(LocalDate date) {
        this.testDate = date;
    }
}
