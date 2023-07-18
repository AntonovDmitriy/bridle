package com.bridle.properties;

import org.apache.camel.model.rest.VerbDefinition;
import org.apache.camel.spi.RestConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
public class HttpConsumerConfiguration extends RestConfiguration {

    @NotEmpty
    private List<GenericVerbDefinition> routes;

    @NotEmpty
    private String contentType = "application/json";

    @Positive
    private int errorHttpResponseCode = 500;
    @Positive
    private int validationErrorHttpResponseCode = 400;

    public List<GenericVerbDefinition> getRoutes() {
        return routes;
    }

    public void setRoutes(List<GenericVerbDefinition> routes) {
        this.routes = routes;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getErrorHttpResponseCode() {
        return errorHttpResponseCode;
    }

    public void setErrorHttpResponseCode(int errorHttpResponseCode) {
        this.errorHttpResponseCode = errorHttpResponseCode;
    }

    public int getValidationErrorHttpResponseCode() {
        return validationErrorHttpResponseCode;
    }

    public void setValidationErrorHttpResponseCode(int validationErrorHttpResponseCode) {
        this.validationErrorHttpResponseCode = validationErrorHttpResponseCode;
    }

    @Validated
    public static class GenericVerbDefinition extends VerbDefinition {

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
}
