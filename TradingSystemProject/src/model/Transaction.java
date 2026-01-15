package model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

public final class Transaction {

    public enum Type { BUY, SELL }

    private final String userId;
    private final String symbol;
    private final Type type;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal fee;
    private final ZonedDateTime time;

    public Transaction(String userId,
                       String symbol,
                       Type type,
                       int quantity,
                       BigDecimal unitPrice,
                       BigDecimal fee,
                       ZonedDateTime time) {
        this.userId = Objects.requireNonNull(userId);
        this.symbol = Objects.requireNonNull(symbol);
        this.type = Objects.requireNonNull(type);
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        this.quantity = quantity;
        this.unitPrice = Objects.requireNonNull(unitPrice);
        this.fee = fee == null ? BigDecimal.ZERO : fee;
        this.time = Objects.requireNonNull(time);
    }

    public String getUserId() { return userId; }
    public String getSymbol() { return symbol; }
    public Type getType() { return type; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getFee() { return fee; }
    public ZonedDateTime getTime() { return time; }

    @Override
    public String toString() {
        return "Transaction{" +
                "userId='" + userId + '\'' +
                ", symbol='" + symbol + '\'' +
                ", type=" + type +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", fee=" + fee +
                ", time=" + time +
                '}';
    }
}
