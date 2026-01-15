package strategy;

import model.Asset;
import model.User;

public interface TradingStrategy {
    void execute(User user, Asset asset, int quantity);
}
