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

        String serviceName = "myservice";
        String serviceId = "ec335072-e850-4176-9394-e5c088d576a7";
        ConsulClient client = ConsulClient.create(vertx, options);
        ServiceUtils.registerService(client, serviceId, serviceName, "192.168.1.101", 443, "tag1", "tag2")
            .onSuccess(unused ->
                ServiceUtils.lookupService(client, serviceName)
                    .onComplete(voidAsyncResult -> ServiceUtils.deregisterService(client, serviceId)
                        .onComplete(voidAsyncResult1 -> vertx.close())))
            .onFailure(throwable -> vertx.close());
    }

}
