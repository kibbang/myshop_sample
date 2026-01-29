package sample.myshop.utils;

import java.security.SecureRandom;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.*;

public class OrderGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    private OrderGenerator() {}

    public static String generateOrderNo() {
        String currentDate = LocalDate.now().format(BASIC_ISO_DATE);

        long bound = 1_000_000_000_000L;
        long randomBound = RANDOM.nextLong(bound);

        String randomNumber = String.format("%012d", randomBound);

        return currentDate + randomNumber;
    }
}
