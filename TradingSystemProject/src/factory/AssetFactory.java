package factory;

import model.Asset;
import model.Crypto;
import model.Stock;

import java.math.BigDecimal;

public final class AssetFactory {

    private AssetFactory() {}

    public static Asset createAsset(String type, String name, String symbol, BigDecimal currentPrice) {
        if (type == null) throw new IllegalArgumentException("type is null");

        return switch (type.toLowerCase()) {
            case "stock" -> new Stock(name, symbol.toUpperCase(), currentPrice);
            case "crypto" -> new Crypto(name, symbol.toUpperCase(), currentPrice);
            default -> throw new IllegalArgumentException("Unknown asset type: " + type);
        };
    }
}
