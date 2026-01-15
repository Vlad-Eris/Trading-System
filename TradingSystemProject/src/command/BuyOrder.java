package command;

import model.Asset;
import model.User;
import service.MarketService;
import service.PortfolioService;

import java.math.BigDecimal;
import java.util.Objects;

public final class BuyOrder implements Order {

    private final MarketService market;
    private final PortfolioService portfolioService;
    private final User user;
    private final Asset asset;
    private final int quantity;
    private final BigDecimal extraFee; // optional (decorator)
    private final boolean enforceTradingHours;

    private User.UserState beforeState;
    private boolean executed;

    public BuyOrder(MarketService market,
                    PortfolioService portfolioService,
                    User user,
                    Asset asset,
                    int quantity,
                    BigDecimal extraFee,
                    boolean enforceTradingHours) {
        this.market = Objects.requireNonNull(market);
        this.portfolioService = Objects.requireNonNull(portfolioService);
        this.user = Objects.requireNonNull(user);
        this.asset = Objects.requireNonNull(asset);
        if (quantity <= 0) throw new IllegalArgumentException("BUY quantity must be > 0");

        this.quantity = quantity;
        this.extraFee = extraFee == null ? BigDecimal.ZERO : extraFee;
        this.enforceTradingHours = enforceTradingHours;
    }

    @Override
    public void execute() {
        if (enforceTradingHours) {
            market.validateTradable(asset);
        }

        beforeState = user.snapshot();

        BigDecimal price = market.getPrice(asset.getSymbol());
        portfolioService.applyBuy(user, asset.getSymbol(), quantity, price, extraFee, market.now());

        executed = true;
    }

    @Override
    public void undo() {
        if (!executed) return;
        user.restore(beforeState);
        executed = false;
    }

    @Override
    public String description() {
        return "BUY " + quantity + " " + asset.getSymbol();
    }
}
