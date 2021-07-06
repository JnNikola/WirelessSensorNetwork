package Algorithm_2D;

import java.util.concurrent.atomic.AtomicLong;

public final class IDCreator {

    private static AtomicLong idCounter = new AtomicLong();

    public static String createID()
    {
        return String.valueOf(idCounter.getAndIncrement());
    }
}
