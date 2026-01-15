package model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public final class Crypto extends Asset {
    public Crypto(String name, String symbol, BigDecimal currentPrice) {
        super(name, symbol, currentPrice);
    }

    @Override
    public boolean isTradableAt(ZonedDateTime time) {
        return true; // 24/7
    }
}
