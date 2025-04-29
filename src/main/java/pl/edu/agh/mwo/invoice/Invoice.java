package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private static AtomicInteger lastInvoiceNumber =  new AtomicInteger(0);

    private final int invoiceNumber;

    public Invoice() {
        this.invoiceNumber = lastInvoiceNumber.incrementAndGet();
    }

    public int getNumber(){
        return this.invoiceNumber;
    };

    public String getFormattedNumber() {
        LocalDate today = LocalDate.now();
        String datePart = today.format(DateTimeFormatter.ISO_DATE);
        String numberPart = String.format("%05d", invoiceNumber);
        return numberPart + "/" + datePart;
    }

    public static void resetInvoiceNumberForTest() {
        lastInvoiceNumber = new AtomicInteger(0);
    }

    private Map<Product, Integer> products = new HashMap<Product, Integer>();

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
        products.merge(product, quantity, Integer::sum);
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
        }
        return totalGross;
    }
    public String getProductSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Faktura nr: ").append(getFormattedNumber()).append("\n");

        int count = 0;
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            sb.append(product.getName())
                    .append(", ")
                    .append(quantity)
                    .append(" szt., ")
                    .append(product.getPrice())
                    .append(" zł\n");
            count++;
        }

        sb.append("Liczba pozycji: ").append(count);
        return sb.toString();
    }

}
