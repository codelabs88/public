package io.codelabs.booking;

import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.NetworkConfig;
import io.codelabs.booking.verticle.BookingSessionManager;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class BookingApp {

    private static final Logger logger = LoggerFactory.getLogger(BookingApp.class);
    private static final int CLUSTER_MEMBERS_NUM = 3;
    private static final int CLUSTER_QUORUM_SIZE = 3;
    public static final int BOOKING_SESSION_PARTITIONS_NUM = 10;

    public static void main(String[] args) {

        IntStream.range(0, CLUSTER_MEMBERS_NUM).forEach(i -> startNode(i == 0));
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
            .setQuorumSize(CLUSTER_QUORUM_SIZE);

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();

                if (isBootstrap) {
                    IntStream.range(0, BookingApp.BOOKING_SESSION_PARTITIONS_NUM)
                        .forEach(i -> {
                            DeploymentOptions deploymentOptions = new DeploymentOptions()
                                .setHa(true)
                                .setInstances(1)
                                .setConfig(new JsonObject().put("partition_id", i));
                            vertx.deployVerticle(BookingSessionManager.class.getName(), deploymentOptions);
                        });

                    vertx.setPeriodic(5000L, l -> {
                        logger.info("Deployment counter: {}", vertx.deploymentIDs().size());
                        logger.info("List of deployment {}", vertx.deploymentIDs());
                    });
                }
            } else {
                logger.error("Failed to start cluster node", res.cause());
            }
        });

    }

}
