package com.github.allisson95.codeflix.infrastructure.genre.models;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import com.github.allisson95.codeflix.JacksonTest;

@JacksonTest
class UpdateGenreRequestTest {

    @Autowired
    private JacksonTester<UpdateGenreRequest> json;

    @Test
    void testMarshall() throws Exception {
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123", "456");
        final var expectedIsActive = false;

        final var request = new UpdateGenreRequest(
                expectedName,
                expectedCategories,
                expectedIsActive);

        final var actualJson = this.json.write(request);

        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.categories_id", expectedCategories)
                .hasJsonPathValue("$.is_active", expectedIsActive);
    }

    @Test
    void testUnmarshall() throws Exception {
        final var expectedName = "Ação";
        final var expectedCategories = "456";
        final var expectedIsActive = false;

        final var json = """
                {
                  "name": "%s",
                  "categories_id": ["%s"],
                  "is_active": %s
                }
                """.formatted(
                expectedName,
                expectedCategories,
                expectedIsActive);

        final var actualJson = this.json.parse(json);

        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("categories", List.of(expectedCategories))
                .hasFieldOrPropertyWithValue("active", expectedIsActive);
    }

}
