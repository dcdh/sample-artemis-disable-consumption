package com.sample.disable.consumption;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@QuarkusTest
class MyResourceEndpointTest {

    @Inject
    Consumer consumer;

    @Inject
    Config config;

    @Test
    public void dumpProperties() {
        config.getPropertyNames()
                .forEach(name -> {
                    if (name.startsWith("quarkus.ironjacamar")) {
                        try {
                            System.out.println(name + " = " + config.getValue(name, String.class));
                        } catch (Exception e) {
                            System.out.println(name + " = [non lisible]");
                        }
                    }
                });
    }

    @Test
    public void shouldNotConsumeMessage() throws InterruptedException {
        // Given
        final String connectionParameters = ConfigProvider.getConfig().getValue("quarkus.ironjacamar.ra.config.connection-parameters", String.class);
        final String port = connectionParameters.split(";")[1].split("=")[1];
        try (final ActiveMQQueueConnectionFactory connectionFactory = new ActiveMQQueueConnectionFactory(
                String.format("tcp://localhost:%s", port),
                "guest",
                "guest");
             final JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {
            final JMSProducer producer = context.createProducer();
            for (int messageIndex = 0; messageIndex < 10; messageIndex++) {
                producer.send(context.createQueue("jms.queue.MyQueue"), String.format("should not consumed message %d", messageIndex));
            }
        }

        TimeUnit.SECONDS.sleep(5);

        // When && Then
        Assertions.assertEquals(0, consumer.getMessageConsumed().size(),
                String.format("Message should not be consumed, consumed messages: %s", consumer.getMessageConsumed()));
    }
}
