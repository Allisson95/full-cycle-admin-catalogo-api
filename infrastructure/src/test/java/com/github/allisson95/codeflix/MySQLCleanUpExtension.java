package com.github.allisson95.codeflix;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryRepository;
import com.github.allisson95.codeflix.infrastructure.genre.persistence.GenreRepository;

public class MySQLCleanUpExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final var applicationContext = SpringExtension.getApplicationContext(context);

        cleanUp(List.of(
                applicationContext.getBean(GenreRepository.class),
                applicationContext.getBean(CategoryRepository.class)));

        final var entityManager = applicationContext.getBean(TestEntityManager.class);

        entityManager.flush();
        entityManager.clear();
    }

    private void cleanUp(final Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }

}