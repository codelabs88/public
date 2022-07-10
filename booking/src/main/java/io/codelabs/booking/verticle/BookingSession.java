package io.codelabs.booking.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;

public class BookingSession extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        EventBus eb = vertx.eventBus();

    }
}
