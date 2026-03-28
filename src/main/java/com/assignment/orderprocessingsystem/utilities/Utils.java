package com.assignment.orderprocessingsystem.utilities;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Utils {

    private final String ORDER_PREFIX = "PI";

    public String generateReferenceNumber() {
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMDDssmmhh"));
        return ORDER_PREFIX + timeStamp;
    }
}
