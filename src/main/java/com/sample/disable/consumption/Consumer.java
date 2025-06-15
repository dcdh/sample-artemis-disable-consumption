package com.sample.disable.consumption;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Consumer {
    private final List<String> messageConsumed = new ArrayList<>();

    void consumed(final String message) {
        Log.infov("Message {0} consumed", message);
        this.messageConsumed.add(message);
    }

    public List<String> getMessageConsumed() {
        return this.messageConsumed;
    }
}
