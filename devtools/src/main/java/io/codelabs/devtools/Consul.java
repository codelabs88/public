package io.codelabs.devtools;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.Watch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consul {

    private static final Logger log = LoggerFactory.getLogger(Consul.class);

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        consulStore(vertx);
        consulClient(vertx);
        consulWatch(vertx);
    }

    private static void consulStore(Vertx vertx) {

        ConfigStoreOptions store = new ConfigStoreOptions()
            .setType("consul")
            .setConfig(new JsonObject()
                .put("host", "localhost")
                .put("port", 8500)
                .put("acl-token", "a382680a-df7a-fc78-a630-14f0935c1dfb")
                .put("prefix", "staging/")
                .put("raw-data", false)
            );

        ConfigRetriever retriever = ConfigRetriever.create(vertx,
            new ConfigRetrieverOptions().addStore(store));

        retriever.getConfig(result -> {
            if (result.succeeded()) {
                log.info("Succeed to load config: {}", result.result());
            } else {
                log.error("Failed to load configuration");
            }
        });

        retriever.listen(configChange -> log.info("Config has been changed: {}", configChange.getNewConfiguration()));
    }

    private static void consulClient(Vertx vertx) {

        ConsulClientOptions options = new ConsulClientOptions()
            .setHost("localhost")
            .setPort(8500)
            .setDc("dc1");

        ConsulClient client = ConsulClient.create(vertx, options);
        client.getValue("staging/postgres/host", res -> {
            if (res.succeeded()) {
                System.out.println("retrieved value: " + res.result().getValue());
                System.out.println("modify index: " + res.result().getModifyIndex());
            } else {
                res.cause().printStackTrace();
            }
        });

    }

    private static void consulWatch(Vertx vertx) {

        ConsulClientOptions options = new ConsulClientOptions()
            .setHost("localhost")
            .setPort(8500)
            .setDc("dc1");

        Watch.key("staging/postgres/host", vertx, options)
            .setHandler(res -> {
                if (res.succeeded()) {
                    log.info("Watching {}:{} ", res.nextResult().getKey(), res.nextResult().getValue());
                } else {
                    res.cause().printStackTrace();
                }
            }).start();
    }
}
