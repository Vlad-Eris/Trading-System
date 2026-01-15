import command.BuyOrder;
import command.SellOrder;
import command.TradeExecutor;
import factory.AssetFactory;
import model.Asset;
import model.User;
import service.MarketService;
import service.PortfolioService;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner IN = new Scanner(System.in);

    private static final Map<String, User> USERS = new HashMap<>();

    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal("100000.00");

    private static final String S1 = "MSFT"; // stock
    private static final String S2 = "NVDA"; // stock
    private static final String C1 = "BTC";  // crypto
    private static final String C2 = "SOL";  // crypto

    public static void main(String[] args) {
        MarketService market = MarketService.init(Clock.systemDefaultZone());
        PortfolioService portfolioService = new PortfolioService();

        registerDefaultAssets(market);

        while (true) {
            User user = login();
            if (user == null) {
                System.out.println("Bye!");
                return;
            }

            TradeExecutor executor = new TradeExecutor(market, portfolioService);

            while (true) {
                printState(user);
                printMenu();

                int choice = readInt("Alege: ");
                try {
                    switch (choice) {
                        case 0 -> {
                            // logout
                            System.out.println("Logout...\n");
                            user = null;
                            break;
                        }
                        case 1 -> doBuy(user, market, portfolioService, executor);
                        case 2 -> doSell(user, market, portfolioService, executor);
                        case 3 -> System.out.println(executor.undo() ? "UNDO OK" : "Nimic de undo");
                        case 4 -> System.out.println(executor.redo() ? "REDO OK" : "Nimic de redo");
                        case 5 -> printMarket(market);
                        case 6 -> showPortfolio(user);
                        default -> System.out.println("Opțiune invalidă.");
                    }
                } catch (Exception e) {
                    System.out.println("Eroare: " + e.getMessage());
                }

                System.out.println("\n(ENTER pentru continuare)");
                IN.nextLine();

                if (user == null) break;
            }
        }
    }


    private static User login() {
        System.out.println("\n=== Erys's TRADING ===");
        System.out.println("Introdu username (sau 0 pentru exit)");
        String username = readString("Username: ").trim();

        if ("0".equals(username)) return null;
        if (username.isBlank()) {
            System.out.println("Username invalid.");
            return login();
        }

        return USERS.computeIfAbsent(username, u -> new User(u, u, DEFAULT_BALANCE));
    }


    private static void registerDefaultAssets(MarketService market) {
        Asset msft = AssetFactory.createAsset("stock", "Microsoft", S1, new BigDecimal("410.00"));
        Asset nvda = AssetFactory.createAsset("stock", "NVIDIA",   S2, new BigDecimal("600.00"));

        Asset btc  = AssetFactory.createAsset("crypto", "Bitcoin",  C1, new BigDecimal("42000.00"));
        Asset sol  = AssetFactory.createAsset("crypto", "Solana",   C2, new BigDecimal("95.00"));

        market.registerAsset(msft);
        market.registerAsset(nvda);
        market.registerAsset(btc);
        market.registerAsset(sol);
    }


    private static void doBuy(User user, MarketService market, PortfolioService portfolioService, TradeExecutor executor) {
        Asset asset = pickAsset(market);
        int qty = readInt("Cantitate (qty > 0): ");
        if (qty <= 0) throw new IllegalArgumentException("Cantitatea trebuie > 0");

        executor.execute(new BuyOrder(
                market,
                portfolioService,
                user,
                asset,
                qty,
                BigDecimal.ZERO,
                false // nu aplic restricția de orar
        ));

        System.out.println("BUY OK: " + qty + " " + asset.getSymbol() + " @ " + market.getPrice(asset.getSymbol()));
    }

    private static void doSell(User user, MarketService market, PortfolioService portfolioService, TradeExecutor executor) {
        Asset asset = pickAsset(market);
        int qty = readInt("Cantitate (qty > 0): ");
        if (qty <= 0) throw new IllegalArgumentException("Cantitatea trebuie > 0");

        executor.execute(new SellOrder(
                market,
                portfolioService,
                user,
                asset,
                qty,
                BigDecimal.ZERO,
                false //nu aplic restricția de orar
        ));

        System.out.println("SELL OK: " + qty + " " + asset.getSymbol() + " @ " + market.getPrice(asset.getSymbol()));
    }

    private static void printMarket(MarketService market) {
        System.out.println("\n--- PIATA (PRETURI STANDARD) ---");
        System.out.println(S1 + " (Microsoft) price=" + market.getPrice(S1));
        System.out.println(S2 + " (NVIDIA)     price=" + market.getPrice(S2));
        System.out.println(C1 + " (Bitcoin)    price=" + market.getPrice(C1));
        System.out.println(C2 + " (Solana)     price=" + market.getPrice(C2));
    }

    private static void showPortfolio(User user) {
        System.out.println("\n--- PORTOFOLIU ---");
        System.out.println("User: " + user.getUsername());
        System.out.println("Balance: " + user.getCash());
        System.out.println("Holdings: " + user.getHoldings());

        System.out.println("\n--- TRANZACTII ---");
        if (user.getTransactions().isEmpty()) {
            System.out.println("(none)");
        } else {
            user.getTransactions().forEach(System.out::println);
        }
    }


    private static void printMenu() {
        System.out.println("""
                1) BUY
                2) SELL
                3) UNDO
                4) REDO
                5) PIATA (preturi)
                6) PORTOFOLIU
                0) Logout
                """);
    }

    private static void printState(User user) {
        System.out.println("\n------------------------------");
        System.out.println("User: " + user.getUsername());
        System.out.println("Balance: " + user.getCash());
        System.out.println("Holdings: " + user.getHoldings());
        System.out.println("------------------------------");
    }

    private static Asset pickAsset(MarketService market) {
        System.out.println("\nAlege ce cumperi/vinzi:");
        System.out.println("1) " + S1 + " (Microsoft) price=" + market.getPrice(S1));
        System.out.println("2) " + S2 + " (NVIDIA)     price=" + market.getPrice(S2));
        System.out.println("3) " + C1 + " (Bitcoin)    price=" + market.getPrice(C1));
        System.out.println("4) " + C2 + " (Solana)     price=" + market.getPrice(C2));

        int c = readInt("Alege (1-4): ");
        return switch (c) {
            case 1 -> market.findAsset(S1).orElseThrow();
            case 2 -> market.findAsset(S2).orElseThrow();
            case 3 -> market.findAsset(C1).orElseThrow();
            case 4 -> market.findAsset(C2).orElseThrow();
            default -> throw new IllegalArgumentException("Alege între 1 și 4");
        };
    }


    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = IN.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Introdu un număr valid.");
            }
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return IN.nextLine();
    }
}
