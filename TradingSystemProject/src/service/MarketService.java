package service;

import model.Asset;
import model.Stock;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class MarketService {

    private static volatile MarketService INSTANCE;

    private final Map<String, Asset> assets = new HashMap<>();
    private final Clock clock;

    private MarketService(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }

    public static MarketService init(Clock clock) {
        if (INSTANCE == null) {
            synchronized (MarketService.class) {
                if (INSTANCE == null) INSTANCE = new MarketService(clock);
            }
        }
        return INSTANCE;
    }

    public ZonedDateTime now() {
        return ZonedDateTime.now(clock);
    }

    public void registerAsset(Asset asset) {
        assets.put(asset.getSymbol(), Objects.requireNonNull(asset));
    }

    public Optional<Asset> findAsset(String symbol) {
        return Optional.ofNullable(assets.get(symbol));
    }

    public BigDecimal getPrice(String symbol) {
        Asset asset = assets.get(symbol);
        if (asset == null) throw new IllegalArgumentException("Unknown symbol: " + symbol);
        return asset.getCurrentPrice();
    }

    public void updatePrice(String symbol, BigDecimal newPrice) {
        Asset asset = assets.get(symbol);
        if (asset == null) throw new IllegalArgumentException("Unknown symbol: " + symbol);
        asset.setCurrentPrice(newPrice);
    }

    // Metoda care validează dacă un activ poate fi tranzacționat
    public void validateTradable(Asset asset) {
        if (asset instanceof Stock) {
            Stock stock = (Stock) asset;
            if (!stock.isTradableAt(now())) {
                throw new IllegalStateException("Asset " + asset.getSymbol() + " is NOT tradable at " + now());
            }
        }
    }

    public boolean isTradable(Asset asset) {
        return false;
    }
}
