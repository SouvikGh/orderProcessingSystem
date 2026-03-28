package com.assignment.orderprocessingsystem.exceptions;

public class OpsNotAllowed extends RuntimeException {
    public OpsNotAllowed(String message) {
        super(message);
    }
}
