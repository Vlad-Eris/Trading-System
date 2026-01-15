package observer;

import model.User;

import java.math.BigDecimal;
import java.util.Objects;

public final class UserNotifier implements Observer {

    private final User user;

    public UserNotifier(User user) {
        this.user = Objects.requireNonNull(user);
    }

    @Override
    public void update(String symbol, BigDecimal newPrice) {
        if (user.getWatchlist().contains(symbol)) {
            System.out.printf("[ALERT] User=%s watchlist update: %s -> %s%n",
                    user.getUsername(), symbol, newPrice);
        }
    }
}
