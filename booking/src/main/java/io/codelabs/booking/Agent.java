package io.codelabs.booking;

import io.codelabs.booking.verticle.BookingSessionManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.util.stream.IntStream;

public class Agent extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {

        vertx.setTimer(10000L, aLong -> {
            IntStream.range(0, BookingApp.BOOKING_SESSION_PARTITIONS_NUM)
                .forEach(i -> {
                    DeploymentOptions deploymentOptions = new DeploymentOptions()
                        .setHa(true)
                        .setInstances(1)
                        .setConfig(new JsonObject().put("partition_id", i));
                    vertx.deployVerticle(BookingSessionManager.class.getName(), deploymentOptions);
                });
        });

    }
}
