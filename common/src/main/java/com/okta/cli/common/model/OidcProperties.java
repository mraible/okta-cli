/*
 * Copyright 2020-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.cli.common.model;

import com.okta.sdk.resource.application.OpenIdConnectApplicationType;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public abstract class OidcProperties {


    public static OktaEnvOidcProperties oktaEnv() {
        return new OktaEnvOidcProperties();
    }

    public static SpringOidcProperties spring() {
        return spring("oidc");
    }

    public static SpringOidcProperties spring(String tenantId) {
        return new SpringOidcProperties(tenantId);
    }

    public static QuarkusOidcProperties quarkus() {
        return quarkus(OpenIdConnectApplicationType.SERVICE);
    }

    public static QuarkusOidcProperties quarkus(OpenIdConnectApplicationType applicationType) {
        return new QuarkusOidcProperties(applicationType);
    }

    public final String issuerUriPropertyName;
    public final String clientIdPropertyName;
    public final String clientSecretPropertyName;

    String issuerUri;
    String clientId;
    String clientSecret;
    List<String> redirectUris;

    OidcProperties(String issuerUriPropertyName, String clientIdPropertyName, String clientSecretPropertyName) {
        this.issuerUriPropertyName = issuerUriPropertyName;
        this.clientIdPropertyName = clientIdPropertyName;
        this.clientSecretPropertyName = clientSecretPropertyName;
    }

    public void setIssuerUri(String issuerUri) {
        this.issuerUri = issuerUri;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    abstract Map<String, String> getOidcClientProperties();

    public Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put(issuerUriPropertyName, issuerUri);
        properties.put(clientIdPropertyName, clientId);
        properties.put(clientSecretPropertyName, clientSecret);
        properties.putAll(getOidcClientProperties());

        return properties;
    }

    public static class SpringOidcProperties extends OidcProperties {
        public SpringOidcProperties(String tenantId) {
            super(
                    format("spring.security.oauth2.client.provider.%s.issuer-uri", tenantId),
                    format("spring.security.oauth2.client.registration.%s.client-id", tenantId),
                    format("spring.security.oauth2.client.registration.%s.client-secret", tenantId)
            );
        }

        @Override
        Map<String, String> getOidcClientProperties() {
            return Collections.emptyMap();
        }
    }

    public static class OktaEnvOidcProperties extends OidcProperties {
        public OktaEnvOidcProperties() {
            super(
                    "okta.oauth2.issuer",
                    "okta.oauth2.client-id",
                    "okta.oauth2.client-secret"
            );
        }

        @Override
        Map<String, String> getOidcClientProperties() {
            return Collections.emptyMap();
        }
    }

    public static class QuarkusOidcProperties extends OidcProperties {
        public final String applicationType;

        public QuarkusOidcProperties(OpenIdConnectApplicationType applicationType) {
            super(
                    "quarkus.oidc.auth-server-url",
                    "quarkus.oidc.client-id",
                    "quarkus.oidc.credentials.secret"
            );
            if (applicationType == OpenIdConnectApplicationType.WEB) {
                this.applicationType = "web-app";
            } else {
                this.applicationType = "service";
            }
        }

        @Override
        Map<String, String> getOidcClientProperties() {
            String redirectUri = "/";
            if (redirectUris != null && !redirectUris.isEmpty()) {
                redirectUri = redirectUris.get(0);
            }

            return Map.of(
                    "quarkus.oidc.application-type", applicationType,
                    "quarkus.oidc.authentication.redirect-path", URI.create(redirectUri).getPath()
            );
        }
    }

}
