package service;

import model.Transaction;
import model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Deque;

public final class PortfolioService {

    private static final BigDecimal DAY_TRADING_FEE_RATE = new BigDecimal("0.05");

    public void applyBuy(User user,
                         String symbol,
                         int quantity,
                         BigDecimal unitPrice,
                         BigDecimal extraFee,
                         ZonedDateTime time) {

        BigDecimal value = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal totalCost = value.add(extraFee);

        if (user.getCash().compareTo(totalCost) < 0) {
            throw new IllegalStateException("Insufficient cash. Need=" + totalCost + " have=" + user.getCash());
        }

        user.setCash(user.getCash().subtract(totalCost));
        user.addHolding(symbol, quantity);
        user.addTransaction(new Transaction(user.getId(), symbol, Transaction.Type.BUY, quantity, unitPrice, extraFee, time));
    }

    public void applySell(User user,
                          String symbol,
                          int quantity,
                          BigDecimal unitPrice,
                          BigDecimal fee,
                          ZonedDateTime time) {

        int holding = user.holdingOf(symbol);
        if (holding < quantity) {
            throw new IllegalStateException("Insufficient holdings. Need=" + quantity + " have=" + holding);
        }

        BigDecimal value = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal proceeds = value.subtract(fee);

        user.setCash(user.getCash().add(proceeds));
        user.addHolding(symbol, -quantity);
        user.addTransaction(new Transaction(user.getId(), symbol, Transaction.Type.SELL, quantity, unitPrice, fee, time));
    }

    public BigDecimal dayTradingFee(User user,
                                    String symbol,
                                    int quantityToSell,
                                    BigDecimal unitPrice,
                                    LocalDate today) {

        Deque<User.Lot> lots = user.lotsOf(symbol);
        if (lots.isEmpty()) return BigDecimal.ZERO;

        int remaining = quantityToSell;
        int taxableQty = 0;

        for (User.Lot lot : lots) {
            if (remaining <= 0) break;

            int take = Math.min(remaining, lot.quantity());
            remaining -= take;

            if (!lot.buyDate().equals(today)) {
                taxableQty += take;
            }
        }

        if (taxableQty <= 0) return BigDecimal.ZERO;

        BigDecimal taxableValue = unitPrice.multiply(BigDecimal.valueOf(taxableQty));
        return taxableValue.multiply(DAY_TRADING_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}
