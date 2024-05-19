package com.github.allisson95.codeflix.infrastructure.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.shaded.json.JSONObject;

@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@EnableWebSecurity
@Configuration
class SecurityConfig {

    private static final String ROLE_ADMIN = "CATALOGO_ADMIN";
    private static final String ROLE_CASTMEMBERS = "CATALOGO_CASTMEMBERS";
    private static final String ROLE_CATEGORIES = "CATALOGO_CATEGORIES";
    private static final String ROLE_GENRES = "CATALOGO_GENRES";
    private static final String ROLE_VIDEOS = "CATALOGO_VIDEOS";

    @Bean
    SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions().sameOrigin())
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("/cast_members*").hasAnyRole(ROLE_ADMIN, ROLE_CASTMEMBERS)
                        .antMatchers("/categories*").hasAnyRole(ROLE_ADMIN, ROLE_CATEGORIES)
                        .antMatchers("/genres*").hasAnyRole(ROLE_ADMIN, ROLE_GENRES)
                        .antMatchers("/videos*").hasAnyRole(ROLE_ADMIN, ROLE_VIDEOS)
                        .anyRequest().hasRole(ROLE_ADMIN))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    static class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

        private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter;

        public KeycloakJwtAuthenticationConverter() {
            this.jwtGrantedAuthoritiesConverter = new KeycloakGrantedAuthoritiesConverter();
        }

        @Override
        public AbstractAuthenticationToken convert(final Jwt jwt) {
            return new JwtAuthenticationToken(jwt, extractAuthorities(jwt), extractprincipal(jwt));
        }

        private Collection<? extends GrantedAuthority> extractAuthorities(final Jwt jwt) {
            return jwtGrantedAuthoritiesConverter.convert(jwt);
        }

        private String extractprincipal(final Jwt jwt) {
            return jwt.getSubject();
        }

    }

    static class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        private static final String REALM_ACCESS = "realm_access";
        private static final String RESOURCE_ACCESS = "resource_access";
        private static final String ROLES = "roles";
        private static final String SEPARATOR = "_";
        private static final String ROLE_PREFIX = "ROLE_";

        @Override
        public Collection<GrantedAuthority> convert(final Jwt jwt) {
            final var realmRoles = extractRealmRoles(jwt);
            final var resourceRoles = extractResourceRoles(jwt);
            return Stream.concat(realmRoles, resourceRoles)
                    .map(it -> ROLE_PREFIX + it)
                    .map(String::toUpperCase)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }

        @SuppressWarnings("unchecked")
        private Stream<String> extractRealmRoles(final Jwt jwt) {
            return Optional.ofNullable(jwt)
                    .map(it -> it.getClaimAsMap(REALM_ACCESS))
                    .map(it -> (Collection<String>) it.get(ROLES))
                    .orElse(Collections.emptyList())
                    .stream();
        }

        @SuppressWarnings("unchecked")
        private Stream<String> extractResourceRoles(final Jwt jwt) {
            final Function<Map.Entry<String, Object>, Stream<String>> mapResource = resource -> {
                final var key = resource.getKey();
                final var value = (JSONObject) resource.getValue();
                final var roles = (Collection<String>) value.get(ROLES);

                return roles.stream().map(role -> key.concat(SEPARATOR).concat(role));
            };

            final Function<Set<Entry<String, Object>>, Collection<String>> mapResources = resources -> resources
                    .stream()
                    .flatMap(mapResource)
                    .toList();

            return Optional.ofNullable(jwt)
                    .map(it -> it.getClaimAsMap(RESOURCE_ACCESS))
                    .map(Map::entrySet)
                    .map(mapResources)
                    .orElse(Collections.emptyList())
                    .stream();
        }

    }

}
