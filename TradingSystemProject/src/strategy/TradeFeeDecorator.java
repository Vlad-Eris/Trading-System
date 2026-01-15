package strategy;

import command.BuyOrder;
import command.SellOrder;
import command.TradeExecutor;
import model.Asset;
import model.Stock;
import model.User;
import service.MarketService;
import service.PortfolioService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;


public final class TradeFeeDecorator implements TradingStrategy {

    private static final BigDecimal TIME_VIOLATION_FEE_RATE = new BigDecimal("0.05");

    private final MarketService market;
    private final TradeExecutor executor;
    private final PortfolioService portfolioService;
    private final TradingStrategy delegate;

    public TradeFeeDecorator(MarketService market,
                             TradeExecutor executor,
                             PortfolioService portfolioService,
                             TradingStrategy delegate) {
        this.market = Objects.requireNonNull(market);
        this.executor = Objects.requireNonNull(executor);
        this.portfolioService = Objects.requireNonNull(portfolioService);
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public void execute(User user, Asset asset, int quantity) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(asset);

        boolean isStock = asset instanceof Stock;
        boolean tradableNow = market.isTradable(asset);

        if (!isStock || tradableNow) {
            delegate.execute(user, asset, quantity);
            return;
        }

        if (quantity == 0) return;

        BigDecimal price = market.getPrice(asset.getSymbol());
        BigDecimal value = price.multiply(BigDecimal.valueOf(Math.abs(quantity)));
        BigDecimal penalty = value.multiply(TIME_VIOLATION_FEE_RATE).setScale(2, RoundingMode.HALF_UP);

        if (quantity > 0) {
            executor.execute(new BuyOrder(
                    market,
                    portfolioService,
                    user,
                    asset,
                    quantity,
                    penalty,
                    false // allow after-hours
            ));
        } else {
            int sellQty = Math.abs(quantity);

            BigDecimal dayFee = portfolioService.dayTradingFee(
                    user,
                    asset.getSymbol(),
                    sellQty,
                    price,
                    market.now().toLocalDate()
            );

            BigDecimal totalFee = penalty.add(dayFee).setScale(2, RoundingMode.HALF_UP);

            executor.execute(new SellOrder(
                    market,
                    portfolioService,
                    user,
                    asset,
                    sellQty,
                    totalFee,
                    false // allow after-hours
            ));
        }
    }
}
