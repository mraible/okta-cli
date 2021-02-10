/*
 * Copyright 2021-Present Okta, Inc.
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
package com.okta.cli.commands.apps.templates

import com.okta.cli.common.model.OidcProperties
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class ServiceAppTemplateTest {

    @Test
    void jhipsterQuarkusServicePropertiesDetectionTest() {

        String packageJson ="""
            "devDependencies": {
                "generator-jhipster": "6.10.5",
                "generator-jhipster-quarkus": "1.0.0",
            }
        """.stripLeading()

        Map<String, String> expected = [
                "quarkus.oidc.auth-server-url": "https://issuer.example.com",
                "quarkus.oidc.client-id": "test-client-id",
                "quarkus.oidc.credentials.secret": "test-client-secret",
                "quarkus.oidc.application-type": "service"
        ]

        jhipsterPropertiesTest(packageJson, expected)
    }

    @Test
    void jhipsterSpringPropertiesDetectionTest() {

        String packageJson ="""
            "devDependencies": {
                "generator-jhipster": "6.10.5"
            }
        """.stripLeading()

        Map<String, String> expected = [
                "spring.security.oauth2.client.provider.oidc.issuer-uri": "https://issuer.example.com",
                "spring.security.oauth2.client.registration.oidc.client-id": "test-client-id",
                "spring.security.oauth2.client.registration.oidc.client-secret": "test-client-secret"
        ]

        jhipsterPropertiesTest(packageJson, expected)
    }

    @Test
    void jhipsterSpringPropertiesDetectionTest_noPackageJson() {
        Map<String, String> expected = [
                "spring.security.oauth2.client.provider.oidc.issuer-uri": "https://issuer.example.com",
                "spring.security.oauth2.client.registration.oidc.client-id": "test-client-id",
                "spring.security.oauth2.client.registration.oidc.client-secret": "test-client-secret"
        ]

        jhipsterPropertiesTest(null, expected)
    }

    @Test
    void jhipsterMicronautPropertiesDetectionTest() {

        String packageJson ="""
            "devDependencies": {
                "generator-jhipster": "6.10.5",
                "generator-jhipster-micronaut": "0.8.0",
            }
        """.stripLeading()

        Map<String, String> expected = [
                "micronaut.security.oauth2.clients.oidc.openid.issuer": "https://issuer.example.com",
                "micronaut.security.oauth2.clients.oidc.client-id": "test-client-id",
                "micronaut.security.oauth2.clients.oidc.client-secret": "test-client-secret",
        ]

        jhipsterPropertiesTest(packageJson, expected)
    }

    private static jhipsterPropertiesTest(String packageJsonText, Map<String, String> expectedProperties) {
        // set the current working directory to a test dir
        File workingDir = File.createTempDir("jhipsterPropertiesDetectionTest-", "-test")
        System.setProperty("user.dir", workingDir.absolutePath)

        if (packageJsonText != null) {
            File packageJson = new File(workingDir, "package.json")
            packageJson.write packageJsonText
        }

        OidcProperties props = ServiceAppTemplate.jhipster().getOidcProperties()
        props.setClientId("test-client-id")
        props.setIssuerUri("https://issuer.example.com")
        props.setClientSecret("test-client-secret")

        assertThat(props.getProperties(), is(expectedProperties))
    }
}
