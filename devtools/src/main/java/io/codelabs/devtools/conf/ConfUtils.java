package io.codelabs.devtools.conf;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.config.yaml.YamlProcessor;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

public class ConfUtils {

    public static Future<JsonObject> loadConf(Vertx vertx) {

        Promise<JsonObject> promise = Promise.promise();

        vertx.fileSystem().readFile("conf/config.yaml", yamlRs -> {
            if (yamlRs.succeeded()) {
                loadConsulConf(vertx)
                    .onSuccess(consulRs -> {
                        try {
                            String yaml = yamlRs.result().toString();
                            String resolvedJson = new CustomStringSubstitutor(consulRs)
                                .enableUndefinedVariableException()
                                .enableSubstitutionInVariables()
                                .replace(yaml);

                            YamlProcessor yamlProcessor = new YamlProcessor();
                            yamlProcessor.process(vertx, null, Buffer.buffer(resolvedJson))
                                .onSuccess(promise::complete)
                                .onFailure(promise::fail);

                        } catch (Exception e) {
                            promise.fail(e);
                        }
                    })
                    .onFailure(promise::fail);
            } else {
                promise.fail(yamlRs.cause());
            }
        });

        return promise.future();
    }

    private static Future<JsonObject> loadConsulConf(Vertx vertx) {

        Promise<JsonObject> promise = Promise.promise();
        ConfigStoreOptions store = new ConfigStoreOptions()
            .setType("consul")
            .setConfig(new JsonObject()
                .put("host", System.getenv("CONSUL_HOST"))
                .put("port", System.getenv("CONSUL_PORT"))
                .put("acl-token", System.getenv("CONSUL_TOKEN"))
                .put("prefix", System.getenv("CONSUL_ENV") + "/")
                .put("raw-data", false)
            );

        ConfigRetriever retriever = ConfigRetriever.create(vertx,
            new ConfigRetrieverOptions().addStore(store));

        retriever.getConfig(rs -> {
            if (rs.succeeded()) {
                promise.complete(rs.result());
            } else {
                promise.fail(rs.cause());
            }
        });

        return promise.future();
    }
}
