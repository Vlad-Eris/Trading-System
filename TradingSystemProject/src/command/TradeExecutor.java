package command;

import service.MarketService;
import service.PortfolioService;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public final class TradeExecutor {

    private final MarketService market;
    private final PortfolioService portfolioService;

    private final Deque<Order> undoStack = new ArrayDeque<>();
    private final Deque<Order> redoStack = new ArrayDeque<>();

    public TradeExecutor(MarketService market, PortfolioService portfolioService) {
        this.market = Objects.requireNonNull(market);
        this.portfolioService = Objects.requireNonNull(portfolioService);
    }

    public MarketService market() { return market; }
    public PortfolioService portfolioService() { return portfolioService; }

    public void execute(Order order) {
        Objects.requireNonNull(order).execute();
        undoStack.push(order);
        redoStack.clear();
    }

    public boolean undo() {
        if (undoStack.isEmpty()) return false;
        Order order = undoStack.pop();
        order.undo();
        redoStack.push(order);
        return true;
    }

    public boolean redo() {
        if (redoStack.isEmpty()) return false;
        Order order = redoStack.pop();
        order.execute();
        undoStack.push(order);
        return true;
    }
}
