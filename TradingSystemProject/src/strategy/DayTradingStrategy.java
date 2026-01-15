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

public final class DayTradingStrategy implements TradingStrategy {

    private final MarketService market;
    private final TradeExecutor executor;
    private final PortfolioService portfolioService;

    public DayTradingStrategy(MarketService market, TradeExecutor executor, PortfolioService portfolioService) {
        this.market = Objects.requireNonNull(market);
        this.executor = Objects.requireNonNull(executor);
        this.portfolioService = Objects.requireNonNull(portfolioService);
    }

    @Override
    public void execute(User user, Asset asset, int quantity) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(asset);

        market.validateTradable(asset);

        if (quantity == 0) return;

        if (quantity > 0) {
            executor.execute(new BuyOrder(
                    market,
                    portfolioService,
                    user,
                    asset,
                    quantity,
                    BigDecimal.ZERO,
                    true
            ));
        } else {
            int sellQty = Math.abs(quantity);
            BigDecimal price = market.getPrice(asset.getSymbol());

            BigDecimal dayFee = portfolioService.dayTradingFee(
                    user,
                    asset.getSymbol(),
                    sellQty,
                    price,
                    market.now().toLocalDate()
            );

            executor.execute(new SellOrder(
                    market,
                    portfolioService,
                    user,
                    asset,
                    sellQty,
                    dayFee,
                    true
            ));
        }
    }
}
