package io.codelabs.devtools;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MainVerticle.class.getName());
        WebClientOptions options = new WebClientOptions()
            .setMaxPoolSize(10000)
            .setKeepAlive(true)
            .setSsl(false);

        WebClient client = WebClient.create(vertx, options);

        /*IntStream.range(0, 30000)
            .parallel()
            .forEach(i -> {
                long start = System.currentTimeMillis();
                client.get(8080, "localhost", "/")
                    .send()
                    .onSuccess(response -> {
                        logger.info("OK: {} ms", System.currentTimeMillis() - start);
                    })
                    .onFailure(throwable -> {
                        throwable.printStackTrace();
                    });
            });*/
    }
}
