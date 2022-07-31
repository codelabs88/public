package io.codelabs.devtools.conf;

import io.vertx.core.json.JsonObject;
import org.apache.commons.text.lookup.StringLookup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsulStringLookup implements StringLookup {

    private static final String CONSUL_STORE_PREFIX = "cs:";

    private final JsonObject consulConf;

    public ConsulStringLookup(JsonObject consulConf) {
        this.consulConf = consulConf;
    }

    @Override
    public String lookup(String s) {

        if (s.startsWith(CONSUL_STORE_PREFIX)) {
            String key = s.substring(CONSUL_STORE_PREFIX.length());
            return get(consulConf, key).toString();
        }

        // fallback to environment variable
        return System.getenv(s);
    }

    private Object get(JsonObject object, String key) {
        String [] keys = key.split("\\.");
        if (keys.length == 1) {
            return object.getValue(key);
        } else {
            List<String> subKeys = new ArrayList<>(Arrays.asList(keys).subList(1, keys.length));
            return get(object.getJsonObject(keys[0]), String.join(".", subKeys));
        }
    }
}
