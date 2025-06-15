package com.sample.disable.consumption;

import io.quarkiverse.ironjacamar.ResourceEndpoint;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

import java.util.Objects;

@ResourceEndpoint(activationSpecConfigKey="myqueue")
public final class MyResourceEndpoint implements MessageListener {

    private final Consumer consumer;

    public MyResourceEndpoint(final Consumer consumer) {
        this.consumer = Objects.requireNonNull(consumer);
    }

    @Override
    public void onMessage(final Message message) {
        try {
            this.consumer.consumed(message.getBody(String.class));
        } catch (final JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
