package io.codelabs.devtools;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * reference: https://commons.apache.org/proper/commons-text/apidocs/org/apache/commons/text/StringSubstitutor.html
 */
public class ApacheStringEval {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        // example 1
        log.info(StringSubstitutor.replaceSystemProperties("java.version = ${java.version} and os.name = ${os.name}"));

        // example 2
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("animal", "quick brown fox");
        valuesMap.put("target", "lazy dog");
        String templateString = "The ${animal} jumped over the ${target}";
        StringSubstitutor sub2 = new StringSubstitutor(valuesMap);
        String resolvedString = sub2.replace(templateString);
        log.info(resolvedString);

        // example 3
        StringSubstitutor sub3 = new StringSubstitutor();
        sub2.setEnableUndefinedVariableException(true);
        log.info(sub2.replace("My name is ${name:-Quy}"));
        //logger.info(sub2.replace("My name is ${name:-Quy}. I'm ${age} years old"));

        // example 4
        Map<String, String> valuesMap4 = new HashMap<>();
        valuesMap4.put("grandfather", "father");
        valuesMap4.put("father", "child");
        StringSubstitutor sub4 = new StringSubstitutor(valuesMap4);
        sub4.setEnableSubstitutionInVariables(true);
        log.info(sub4.replace("Hierarchy grandfather -> ${grandfather} -> ${${grandfather}}"));

        // example 5
        final StringSubstitutor interpolator = StringSubstitutor.createInterpolator();
        interpolator.setEnableSubstitutionInVariables(true); // Allows for nested $'s.
        final String text = interpolator.replace("Base64 Decoder:        ${base64Decoder:SGVsbG9Xb3JsZCE=}\n"
            + "java.version = ${java.version} \n"
            + "Base64 Encoder:        ${base64Encoder:HelloWorld!}\n"
            + "Java Constant:         ${const:java.awt.event.KeyEvent.VK_ESCAPE}\n"
            + "Date:                  ${date:yyyy-MM-dd}\n" + "DNS:                   ${dns:address|apache.org}\n"
            + "Environment Variable:  ${env:USERNAME}\n"
            + "File Content:          ${file:UTF-8:src/test/resources/document.properties}\n"
            + "Java:                  ${java:version}\n" + "Localhost:             ${localhost:canonical-name}\n"
            + "Properties File:       ${properties:src/test/resources/document.properties::mykey}\n"
            + "Resource Bundle:       ${resourceBundle:org.example.testResourceBundleLookup:mykey}\n"
            + "Script:                ${script:javascript:3 + 4}\n" + "System Property:       ${sys:user.dir}\n"
            + "URL Decoder:           ${urlDecoder:Hello%20World%21}\n"
            + "URL Encoder:           ${urlEncoder:Hello World!}\n"
            + "URL Content (HTTP):    ${url:UTF-8:http://www.apache.org}\n"
            + "URL Content (HTTPS):   ${url:UTF-8:https://www.apache.org}\n"
            + "URL Content (File):    ${url:UTF-8:file:///${sys:user.dir}/src/test/resources/document.properties}\n"
            + "XML XPath:             ${xml:src/test/resources/document.xml:/root/path/to/node}\n");
        log.info(text);
    }
}
