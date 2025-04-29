package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.Invoice;
import pl.edu.agh.mwo.invoice.product.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        Invoice.resetInvoiceNumberForTest();
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertThat(new BigDecimal("20"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertThat(new BigDecimal("1000"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingNullProduct() {
        invoice.addProduct(null);
    }

    @Test
    public void testInvoiceNumber(){
        int number = invoice.getNumber();
        Assert.assertThat(number, Matchers.greaterThan(0));
    }

    @Test
    public void invoiceNumberShouldBeImmutable() {
        int numberBefore = invoice.getNumber();
        int numberAfter = invoice.getNumber();
        assertEquals(numberBefore, numberAfter);
    }

    @Test
    public void testInvoiceNumberHaveConsequentNumber(){
        Invoice invoice1 = new Invoice();
        Invoice invoice2 = new Invoice();
        Invoice invoice3 = new Invoice();

        assertEquals(invoice1.getNumber() + 1, invoice2.getNumber());
        assertEquals(invoice2.getNumber() + 1, invoice3.getNumber());
    }

    @Test
    public void testFormattedNumberHasCorrectStructure() {
        assertTrue(invoice.getFormattedNumber().matches("\\d{5}/\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    public void testFormattedNumberContainsCurrentDate() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        assertTrue(invoice.getFormattedNumber().endsWith("/" + today));
    }

    @Test
    public void testSummaryIncludesInvoiceNumber() {
        String summary = invoice.getProductSummary();
        assertTrue(summary.startsWith("Faktura nr: 00001"));
    }

    @Test
    public void testSummaryWithSingleProduct() {
        invoice.addProduct(new DairyProduct("Mleko", new BigDecimal("3.50")), 2);

        String summary = invoice.getProductSummary();
        assertTrue(summary.contains("Mleko, 2 szt., 3.50 zł"));
        assertTrue(summary.contains("Liczba pozycji: 1"));
    }

    @Test
    public void testSummaryWithMultipleProducts() {
        invoice.addProduct(new DairyProduct("Mleko", new BigDecimal("3.50")), 2);
        invoice.addProduct(new OtherProduct("Mydło", new BigDecimal("2.00")), 1);

        String summary = invoice.getProductSummary();

        assertTrue(summary.contains("Mleko, 2 szt., 3.50 zł"));
        assertTrue(summary.contains("Mydło, 1 szt., 2.00 zł"));
        assertTrue(summary.contains("Liczba pozycji: 2"));
    }

    @Test
    public void testEmptyInvoiceSummary() {
        String summary = invoice.getProductSummary();
        assertTrue(summary.contains("Faktura nr: 00001"));
        assertTrue(summary.contains("Liczba pozycji: 0"));
    }

    @Test
    public void testAddingSameProductTwiceIncreasesQuantity() {
        DairyProduct product1 = new DairyProduct("Mleko", new BigDecimal("3.50"));

        invoice.addProduct(product1, 2);
        invoice.addProduct(product1, 3);

        String summary = invoice.getProductSummary();

        assertTrue(summary.contains("Mleko, 5 szt., 3.50 zł"));
        assertTrue(summary.contains("Liczba pozycji: 1"));
    }

    @Test
    public void testFuelCanisterPriceIncludesTaxAndExcise() {
        FuelCanister fuel = new FuelCanister("ON", new BigDecimal("100.00"));
        fuel.setTestDate(LocalDate.of(2025, 4, 29));

        BigDecimal expected = new BigDecimal("100.00")
                .multiply(new BigDecimal("1.23"))
                .add(new BigDecimal("5.56"));

        assertEquals(0, expected.compareTo(fuel.getPriceWithTax()));
    }

    @Test
    public void testFuelCanisterPriceWithoutTaxOnMothersInLawDay() {
        FuelCanister fuel = new FuelCanister("ON", new BigDecimal("100.00"));
        fuel.setTestDate(LocalDate.of(2025, 3, 5));

        BigDecimal expected = new BigDecimal("100.00").add(new BigDecimal("5.56"));

        assertEquals(0, expected.compareTo(fuel.getPriceWithTax()));
    }

    @Test
    public void testInvoiceWithMixedProducts_RegularDay() {
        DairyProduct bread = new DairyProduct("Chleb", new BigDecimal("10.00"));
        BottleOfWine wine = new BottleOfWine("Wino", new BigDecimal("20.00"));
        FuelCanister fuel = new FuelCanister("ON", new BigDecimal("100.00"));

        invoice.addProduct(bread, 1);
        invoice.addProduct(wine, 1);
        fuel.setTestDate(LocalDate.of(2025, 4, 29));
        invoice.addProduct(fuel, 1);

        BigDecimal netExpected = new BigDecimal("10.00")
                .add(new BigDecimal("20.00"))
                .add(new BigDecimal("100.00"));

        BigDecimal grossExpected =
                bread.getPrice().multiply(new BigDecimal("1.08"))
                        .add(wine.getPrice().multiply(new BigDecimal("1.23")).add(new BigDecimal("5.56")))
                        .add(fuel.getPrice().multiply(new BigDecimal("1.23")).add(new BigDecimal("5.56")));

        assertEquals(0, netExpected.compareTo(invoice.getNetTotal()));
        assertEquals(0, grossExpected.compareTo(invoice.getGrossTotal()));
    }
}
