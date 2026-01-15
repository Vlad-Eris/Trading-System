package model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

public abstract class Asset {
    private final String name;
    private final String symbol;
    private BigDecimal currentPrice;

    protected Asset(String name, String symbol, BigDecimal currentPrice) {
        this.name = Objects.requireNonNull(name);
        this.symbol = Objects.requireNonNull(symbol);
        this.currentPrice = Objects.requireNonNull(currentPrice);
    }

    public String getName() { return name; }
    public String getSymbol() { return symbol; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = Objects.requireNonNull(currentPrice);
    }

    public abstract boolean isTradableAt(ZonedDateTime time);
}
