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

public final class ThresholdStrategy implements TradingStrategy {

    private final MarketService market;
    private final TradeExecutor executor;

    private final BigDecimal buyBelow;
    private final BigDecimal sellAbove;
    private final int fixedQty;

    public ThresholdStrategy(MarketService market,
                             TradeExecutor executor,
                             BigDecimal buyBelow,
                             BigDecimal sellAbove,
                             int fixedQty) {
        this.market = Objects.requireNonNull(market);
        this.executor = Objects.requireNonNull(executor);
        this.buyBelow = Objects.requireNonNull(buyBelow);
        this.sellAbove = Objects.requireNonNull(sellAbove);
        if (fixedQty <= 0) throw new IllegalArgumentException("fixedQty must be > 0");
        this.fixedQty = fixedQty;
    }

    @Override
    public void execute(User user, Asset asset, int quantityIgnored) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(asset);

        BigDecimal price = market.getPrice(asset.getSymbol());
        PortfolioService portfolioService = executor.portfolioService();

        if (price.compareTo(buyBelow) <= 0) {
            executor.execute(new BuyOrder(market, portfolioService, user, asset, fixedQty, BigDecimal.ZERO, true));
        } else if (price.compareTo(sellAbove) >= 0) {
            int holding = user.holdingOf(asset.getSymbol());
            if (holding > 0) {
                int toSell = Math.min(fixedQty, holding);
                executor.execute(new SellOrder(market, portfolioService, user, asset, toSell, BigDecimal.ZERO, true));
            }
        }
    }
}
