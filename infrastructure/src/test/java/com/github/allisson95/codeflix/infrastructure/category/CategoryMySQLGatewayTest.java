package com.github.allisson95.codeflix.infrastructure.category;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.allisson95.codeflix.infrastructure.MySQLGatewayTest;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryRepository;

@MySQLGatewayTest
class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void test_injected_dependencies() {
        assertNotNull(categoryRepository);
        assertNotNull(categoryGateway);
    }

}
