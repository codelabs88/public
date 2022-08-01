package io.codelabs.devtools;

import io.codelabs.devtools.conf.ConfUtils;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PocConfLoader {

    private static final Logger log = LoggerFactory.getLogger(PocConfLoader.class);

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        ConfUtils.loadConf(vertx)
            .onSuccess(conf -> {
                log.info("System configuration initialized successfully\n{}", conf.encodePrettily());
                log.info("{}", conf.getJsonObject("test").getJsonArray("dep").getString(1));
                // do something like deploying verticle, start http server, and so on
            })
            .onFailure(throwable -> {
                log.error("Failed to initialize system configuration", throwable);
                vertx.close();
            });
    }



}
