package com.bridle.properties;

import org.apache.camel.component.rest.springboot.RestComponentConfiguration;
import org.apache.camel.model.rest.RestsDefinition;
import org.apache.camel.model.rest.VerbDefinition;
import org.apache.camel.spi.RestConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Validated
public class HttpConsumerConfiguration extends RestConfiguration{

    @NotEmpty
    private List<GenericVerbDefinition> routes;

    @Validated
    public static class GenericVerbDefinition extends VerbDefinition{

        private HttpMethod httpMethod;
        @NotNull
        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        public void setHttpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
        }

        @Override
        public String asVerb() {
            return getHttpMethod().name();
        }

        @NotEmpty
        @Override
        public String getPath() {
            return super.getPath();
        }
    }

    public List<GenericVerbDefinition> getRoutes() {
        return routes;
    }

    public void setRoutes(List<GenericVerbDefinition> routes) {
        this.routes = routes;
    }
}
