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
package com.okta.cli.commands.apps.templates;

import com.okta.cli.common.model.OidcProperties;
import com.okta.cli.console.PromptOption;
import com.okta.sdk.resource.application.OpenIdConnectApplicationType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum WebAppTemplate implements PromptOption<WebAppTemplate> {

    OKTA_SPRING_BOOT("Okta Spring Boot Starter", OidcProperties.oktaEnv(), "src/main/resources/application.properties", "http://localhost:8080/login/oauth2/code/okta", null),
    SPRING_BOOT("Spring Boot", OidcProperties.spring("okta"), "src/main/resources/application.properties", "http://localhost:8080/login/oauth2/code/okta", null),
    JHIPSTER("JHipster",
            OidcProperties.spring("oidc"),
            ".okta.env",
            List.of("http://localhost:8080/login/oauth2/code/oidc",
                    "http://localhost:8761/login/oauth2/code/oidc"),
            "groups",
            Set.of("ROLE_USER", "ROLE_ADMIN")),
    QUARKUS("Quarkus", OidcProperties.quarkus(OpenIdConnectApplicationType.WEB), "src/main/resources/application.properties", "http://localhost:8080/callback", null),
    GENERIC("Other", OidcProperties.oktaEnv(), ".okta.env", "http://localhost:8080/callback", null);

    private static final Map<String, WebAppTemplate> nameToTemplateMap = Arrays.stream(values()).collect(Collectors.toMap(it -> it.friendlyName, it -> it));

    private final String friendlyName;
    private final OidcProperties oidcProperties;
    private final String defaultConfigFileName;
    private final List<String> defaultRedirectUris;
    private final String groupsClaim;
    public final Set<String> groupsToCreate;

    WebAppTemplate(String friendlyName, OidcProperties oidcProperties, String defaultConfigFileName, List<String> defaultRedirectUris, String groupsClaim, Set<String> groupsToCreate) {
        this.friendlyName = friendlyName;
        this.oidcProperties = oidcProperties;
        this.defaultConfigFileName = defaultConfigFileName;
        this.defaultRedirectUris = Collections.unmodifiableList(defaultRedirectUris);
        this.groupsClaim = groupsClaim;
        this.groupsToCreate = Collections.unmodifiableSet(groupsToCreate);
    }

    WebAppTemplate(String friendlyName, OidcProperties oidcProperties, String defaultConfigFileName, String defaultRedirectUri, String groupsClaim) {
        this(friendlyName, oidcProperties, defaultConfigFileName, Collections.singletonList(defaultRedirectUri), groupsClaim, Collections.emptySet());
    }

    public OidcProperties getOidcProperties() {
        return oidcProperties;
    }

    public String getDefaultConfigFileName() {
        return defaultConfigFileName;
    }

    public List<String> getDefaultRedirectUris() {
        return defaultRedirectUris;
    }

    public String getGroupsClaim() {
        return groupsClaim;
    }

    public Set<String> getGroupsToCreate() {
        return groupsToCreate;
    }

    @Override
    public String displayName() {
        return friendlyName;
    }

    @Override
    public WebAppTemplate value() {
        return this;
    }
}
