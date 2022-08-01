package io.codelabs.devtools.servicediscovery;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.ext.consul.CheckOptions;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.Service;
import io.vertx.ext.consul.ServiceOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ServiceUtils {

    private static final Logger log = LoggerFactory.getLogger(ServiceUtils.class);

    public static Future<Void> serviceRegister(ConsulClient consulClient, String serviceId,
                                               String serviceName, String address, int port, String... tags) {

        Promise<Void> promise = Promise.promise();

        ServiceOptions opts = new ServiceOptions()
            .setId(serviceId)
            .setName(serviceName)
            .setTags(Arrays.asList(tags))
            .setCheckOptions(new CheckOptions().setTtl("10s"))
            .setAddress(address)
            .setPort(port);

        consulClient.registerService(opts, res -> {
            if (res.succeeded()) {
                promise.complete();
                log.info("Service successfully registered");
            } else {
                promise.fail(res.cause());
                res.cause().printStackTrace();
            }

        });

        return promise.future();
    }


    public static Future<Void> serviceLookup(ConsulClient consulClient, String serviceName) {

        Promise<Void> promise = Promise.promise();

        consulClient.catalogServiceNodes(serviceName, res -> {
            if (res.succeeded()) {
                log.info("found {} services", res.result().getList().size());
                log.info("consul state index: {}", res.result().getIndex());
                for (Service service : res.result().getList()) {
                    log.info("Service node: {}", service.getNode());
                    log.info("Service address: {}", service.getAddress());
                    log.info("Service port: {}", service.getPort());
                }
                promise.complete();
            } else {
                res.cause().printStackTrace();
                promise.fail(res.cause());
            }
        });

        return promise.future();
    }
}
