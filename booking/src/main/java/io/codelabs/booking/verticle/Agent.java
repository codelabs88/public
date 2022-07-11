package io.codelabs.booking.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Agent extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Agent.class);
    public static final String EB_CONSUMER_ADDRESS_DEPLOY_BS_MANAGER = "agent-booking-session-manager-deployment";

    @Override
    public void start(Promise<Void> startPromise) {
        String nodeId = getNodeId();
        EventBus eb = vertx.eventBus();
        logger.info("An Agent has been deployed on node {}", nodeId);
        eb.consumer(EB_CONSUMER_ADDRESS_DEPLOY_BS_MANAGER, message -> {
            Integer partitionId = (Integer) message.body();
            DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setHa(true)
                .setInstances(1)
                .setConfig(new JsonObject().put("partition_id", partitionId));
            vertx.deployVerticle(BookingSessionManager.class.getName(), deploymentOptions)
                .onSuccess(s -> logger.info("Succeed to deploy Booking session manager {} on node {}", partitionId, nodeId))
                .onFailure(throwable -> logger.error("Failed to deploy Booking session manager {} on node {}", partitionId, nodeId));
        });
    }

    private String getNodeId() {
        VertxInternal vertxInternal = (VertxInternal) vertx;
        ClusterManager clusterManager = vertxInternal.getClusterManager();
        return clusterManager.getNodeId();
    }
}
