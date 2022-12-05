package com.whitebox.bankaccount;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class ServiceUtils {

    public static UUID formatUuid(String accountId) {
        accountId = accountId.replace("-", "");
        String formatted = String.format(
                accountId.substring(0, 8) + "-" +
                        accountId.substring(8, 12) + "-" +
                        accountId.substring(12, 16) + "-" +
                        accountId.substring(16, 20) + "-" +
                        accountId.substring(20, 32)
        );
        return UUID.fromString(formatted);
    }

    public static <K, V> Stream<K> keys(Map<K, V> map, V value) {
        return map
                .entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey);
    }
}
