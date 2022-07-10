package io.codelabs.booking.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookingSessionManager extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(BookingSessionManager.class);
    public static final String EB_CONSUMER_ADDRESS_PING = "booking-session-manager-ping-%s";
    private int partitionId;

    @Override
    public void start(Promise<Void> startPromise) {

        this.partitionId = config().getInteger("partition_id");
        EventBus eb = vertx.eventBus();
        eb.request(getPingAddress(), "PING", new DeliveryOptions().setSendTimeout(1000L),
            result -> {
                if (result.succeeded()) {
                    String errMsg = String.format("Booking session manager %s already deployed", partitionId);
                    logger.error(errMsg);
                    startPromise.fail(errMsg);
                } else {
                    eb.consumer(getPingAddress(), message -> {
                        logger.info("Booking session manager %s received a PING message");
                        message.reply("PONG");
                    });
                    startPromise.complete();
                }
            });
    }

    @Override
    public void stop() {
        logger.info("Un-deployed Booking session manager with partition id {}", partitionId);
    }

    private String getPingAddress() {
        return String.format(EB_CONSUMER_ADDRESS_PING, partitionId);
    }

}
