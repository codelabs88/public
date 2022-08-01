package io.codelabs.devtools;

import io.codelabs.devtools.servicediscovery.ServiceUtils;
import io.vertx.core.Vertx;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.ConsulClientOptions;

public class PocServiceDiscovery {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        ConsulClientOptions options = new ConsulClientOptions()
            .setHost("localhost")
            .setPort(8500)
            .setAclToken("token_here")
            .setDc("dc1");

        ConsulClient client = ConsulClient.create(vertx, options);
        ServiceUtils.serviceRegister(client, "myservice_id_1", "myservice", "192.168.1.101", 443, "tag1", "tag2")
            .onSuccess(unused -> ServiceUtils.serviceLookup(client, "myservice").onComplete(voidAsyncResult -> vertx.close()))
            .onFailure(throwable -> vertx.close());
    }

}
