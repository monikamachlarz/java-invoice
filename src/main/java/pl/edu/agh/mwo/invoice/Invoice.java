package pl.edu.agh.mwo.invoice;

import pl.edu.agh.mwo.invoice.product.Product;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Invoice {
    private final Map<Product, Integer> productIntegerMap = new HashMap<>();

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity cannot be null or less than 1.");
        }
        productIntegerMap.put(product, quantity);
    }

    public BigDecimal getNetPrice() {
        BigDecimal netPrice = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> productWithCount : productIntegerMap.entrySet()) {
            netPrice = netPrice.add(productWithCount.getKey().getPrice().multiply(new BigDecimal(productWithCount.getValue())));
        }
        return netPrice;
    }

    public BigDecimal getTax() {
        BigDecimal tax = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> productWithCount : productIntegerMap.entrySet()) {
            tax = tax.add(productWithCount.getKey().getPrice().multiply(new BigDecimal(productWithCount.getValue())).multiply(productWithCount.getKey().getTaxPercent()));
        }
        return tax;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> productWithCount : productIntegerMap.entrySet()) {
            total = total.add(productWithCount.getKey().getPriceWithTax().multiply(new BigDecimal(productWithCount.getValue())));
        }
        return total;
    }
}
