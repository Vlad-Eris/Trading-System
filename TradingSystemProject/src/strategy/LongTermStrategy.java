package strategy;

import command.BuyOrder;
import command.SellOrder;
import command.TradeExecutor;
import model.Asset;
import model.User;
import service.MarketService;
import service.PortfolioService;

import java.math.BigDecimal;
import java.util.Objects;

public final class LongTermStrategy implements TradingStrategy {

    private final MarketService market;
    private final TradeExecutor executor;

    public LongTermStrategy(MarketService market, TradeExecutor executor) {
        this.market = Objects.requireNonNull(market);
        this.executor = Objects.requireNonNull(executor);
    }

    @Override
    public void execute(User user, Asset asset, int quantity) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(asset);

        market.validateTradable(asset);

        if (quantity == 0) return;

        PortfolioService portfolioService = executor.portfolioService();

        if (quantity > 0) {
            executor.execute(new BuyOrder(market, portfolioService, user, asset, quantity, BigDecimal.ZERO, true));
        } else {
            int sellQty = Math.abs(quantity);
            executor.execute(new SellOrder(market, portfolioService, user, asset, sellQty, BigDecimal.ZERO, true));
        }
    }
}
