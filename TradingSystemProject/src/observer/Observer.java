package observer;

import java.math.BigDecimal;

public interface Observer {
    void update(String symbol, BigDecimal newPrice);
}
