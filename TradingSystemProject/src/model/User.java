package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

public final class User {

    private final String id;
    private final String username;

    private BigDecimal cash;
    private final Map<String, Integer> holdings = new HashMap<>();
    private final Map<String, Deque<Lot>> lots = new HashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();

    private final Set<String> watchlist = new HashSet<>();

    public User(String id, String username, BigDecimal initialCash) {
        this.id = Objects.requireNonNull(id);
        this.username = Objects.requireNonNull(username);
        this.cash = Objects.requireNonNull(initialCash);
    }

    public String getId() { return id; }
    public String getUsername() { return username; }

    public BigDecimal getCash() { return cash; }
    public void setCash(BigDecimal cash) { this.cash = Objects.requireNonNull(cash); }

    public Map<String, Integer> getHoldings() { return Collections.unmodifiableMap(holdings); }
    public Set<String> getWatchlist() { return watchlist; }
    public List<Transaction> getTransactions() { return Collections.unmodifiableList(transactions); }

    public int holdingOf(String symbol) { return holdings.getOrDefault(symbol, 0); }

    public void addHolding(String symbol, int delta) {
        int now = holdings.getOrDefault(symbol, 0) + delta;
        if (now < 0) throw new IllegalStateException("Negative holdings for " + symbol);
        if (now == 0) holdings.remove(symbol);
        else holdings.put(symbol, now);
    }

    public void addLot(String symbol, int qty, LocalDate buyDate) {
        lots.computeIfAbsent(symbol, k -> new ArrayDeque<>()).addLast(new Lot(qty, buyDate));
    }

    public Deque<Lot> lotsOf(String symbol) {
        return lots.computeIfAbsent(symbol, k -> new ArrayDeque<>());
    }

    public void addTransaction(Transaction t) { transactions.add(Objects.requireNonNull(t)); }

    public UserState snapshot() {
        Map<String, Integer> holdingsCopy = new HashMap<>(holdings);

        Map<String, Deque<Lot>> lotsCopy = new HashMap<>();
        for (var e : lots.entrySet()) {
            lotsCopy.put(e.getKey(), new ArrayDeque<>(e.getValue())); // Lot is immutable
        }

        return new UserState(cash, holdingsCopy, lotsCopy, transactions.size());
    }

    public void restore(UserState state) {
        this.cash = state.cash();

        holdings.clear();
        holdings.putAll(state.holdings());

        lots.clear();
        for (var e : state.lots().entrySet()) {
            lots.put(e.getKey(), new ArrayDeque<>(e.getValue()));
        }

        while (transactions.size() > state.transactionsSize()) {
            transactions.remove(transactions.size() - 1);
        }
    }

    public String prettyPrint() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", cash=" + cash +
                ", holdings=" + holdings +
                ", lots=" + lots +
                ", watchlist=" + watchlist +
                '}';
    }

    public record Lot(int quantity, LocalDate buyDate) { }

    public record UserState(BigDecimal cash,
                            Map<String, Integer> holdings,
                            Map<String, Deque<Lot>> lots,
                            int transactionsSize) {
    }
}
