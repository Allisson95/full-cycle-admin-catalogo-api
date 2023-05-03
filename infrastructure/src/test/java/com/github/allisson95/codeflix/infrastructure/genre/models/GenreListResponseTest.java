package com.github.allisson95.codeflix.infrastructure.genre.models;

import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import com.github.allisson95.codeflix.JacksonTest;

@JacksonTest
class GenreListResponseTest {

    @Autowired
    private JacksonTester<GenreListResponse> json;

    @Test
    void testMarshall() throws Exception {
        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCreatedAt = Instant.now();
        final var expectedDeletedAt = Instant.now();

        final var response = new GenreListResponse(
                expectedId,
                expectedName,
                expectedIsActive,
                expectedCreatedAt,
                expectedDeletedAt);

        final var actualJson = this.json.write(response);

        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.id", expectedId)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.is_active", expectedIsActive)
                .hasJsonPathValue("$.created_at", expectedCreatedAt.toString())
                .hasJsonPathValue("$.deleted_at", expectedDeletedAt.toString());
    }

    @Test
    void testUnmarshall() throws Exception {
        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCreatedAt = Instant.now();
        final var expectedDeletedAt = Instant.now();

        final var json = """
                {
                  "id": "%s",
                  "name": "%s",
                  "is_active": %s,
                  "created_at": "%s",
                  "deleted_at": "%s"
                }
                """.formatted(
                expectedId,
                expectedName,
                expectedIsActive,
                expectedCreatedAt.toString(),
                expectedDeletedAt.toString());

        final var actualJson = this.json.parse(json);

        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("active", expectedIsActive)
                .hasFieldOrPropertyWithValue("createdAt", expectedCreatedAt)
                .hasFieldOrPropertyWithValue("deletedAt", expectedDeletedAt);
    }

}
