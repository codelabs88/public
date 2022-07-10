package io.codelabs.booking.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.spi.cluster.ClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookingSessionManager extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(BookingSessionManager.class);
    private int partitionId;

    @Override
    public void start(Promise<Void> startPromise) {

        this.partitionId = config().getInteger("partition_id");
        VertxInternal vertxInternal = (VertxInternal) vertx;
        ClusterManager clusterManager = vertxInternal.getClusterManager();
        String nodeId = clusterManager.getNodeId();
        logger.info("Booking session manager has been deployed with partition id {} on node {}", partitionId, nodeId);

        EventBus eb = vertx.eventBus();

        startPromise.complete();
    }

    @Override
    public void stop() {
        logger.info("Un-deployed Booking session manager with partition id {}", partitionId);
    }

}
