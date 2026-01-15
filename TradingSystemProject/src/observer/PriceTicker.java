package observer;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PriceTicker implements Subject {

    private final List<Observer> observers = new CopyOnWriteArrayList<>();

    @Override
    public void attach(Observer observer) {
        if (observer != null) observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String symbol, BigDecimal newPrice) {
        for (Observer observer : observers) {
            observer.update(symbol, newPrice);
        }
    }

    // pt a notifica observatorii despre schimbările de preț
    public void priceChanged(String symbol, BigDecimal newPrice) {
        notifyObservers(symbol, newPrice);
    }
}
