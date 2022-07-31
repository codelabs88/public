package io.codelabs.devtools.conf;

import io.vertx.core.json.JsonObject;
import org.apache.commons.text.StringSubstitutor;

public class CustomStringSubstitutor extends StringSubstitutor {

    public CustomStringSubstitutor(JsonObject consulConf) {
        super();
        this.setVariableResolver(new ConsulStringLookup(consulConf));
    }

    @Override
    public String replace(String source) {
        return super.replace(source);
    }

    public CustomStringSubstitutor enableUndefinedVariableException() {
        super.setEnableUndefinedVariableException(true);
        return this;
    }

    public CustomStringSubstitutor enableSubstitutionInVariables() {
        super.setEnableSubstitutionInVariables(true);
        return this;
    }
}
