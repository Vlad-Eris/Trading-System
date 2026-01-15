package model;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public final class Stock extends Asset {

    private static final LocalTime OPEN = LocalTime.of(9, 0);
    private static final LocalTime CLOSE = LocalTime.of(18, 0);

    public Stock(String name, String symbol, BigDecimal currentPrice) {
        super(name, symbol, currentPrice);
    }

    @Override
    public boolean isTradableAt(ZonedDateTime time) {
        DayOfWeek day = time.getDayOfWeek();
        boolean isWeekday = day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
        LocalTime currentTime = time.toLocalTime();
        boolean isInHours = !currentTime.isBefore(OPEN) && !currentTime.isAfter(CLOSE);
        return isWeekday && isInHours;
    }
}
