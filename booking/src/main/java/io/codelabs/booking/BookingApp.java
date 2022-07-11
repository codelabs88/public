package io.codelabs.booking;

import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.NetworkConfig;
import io.codelabs.booking.verticle.Agent;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class BookingApp {

    private static final Logger logger = LoggerFactory.getLogger(BookingApp.class);
    private static final int CLUSTER_MEMBERS = 5;
    private static final int CLUSTER_QUORUM = 1;
    public static final int BOOKING_SESSION_PARTITIONS = 10;

    public static void main(String[] args) {
        IntStream.range(0, CLUSTER_MEMBERS).forEach(i -> startNode(i == (CLUSTER_MEMBERS - 1)));
    }

    private static void startNode(boolean isBootstrap) {

        Config hazelcastConfig = new Config()
            .setClusterName("booking-service")
            .setNetworkConfig(new NetworkConfig()
                .setInterfaces(new InterfacesConfig()
                    .setEnabled(true)
                    .addInterface("192.168.*.*"))
            );

        ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);

        VertxOptions options = new VertxOptions()
            .setClusterManager(mgr)
            .setHAEnabled(true)
            .setQuorumSize(CLUSTER_QUORUM)
            .setHAGroup(isBootstrap ? "bootstrap" : "deployment");

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                if (isBootstrap) {
                    vertx.setTimer(15000L, aLong -> IntStream.range(0, BOOKING_SESSION_PARTITIONS).forEach(i -> {
                        try {
                            vertx.eventBus().send(Agent.EB_CONSUMER_ADDRESS_DEPLOY_BS_MANAGER, i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }));
                } else {
                    DeploymentOptions deploymentOptions = new DeploymentOptions()
                        .setHa(true)
                        .setInstances(1);
                    vertx.deployVerticle(Agent.class.getName(), deploymentOptions);
                }
            } else {
                logger.error("Failed to start cluster node", res.cause());
            }
        });

    }

}
