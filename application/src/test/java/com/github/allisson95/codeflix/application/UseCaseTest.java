package com.github.allisson95.codeflix.application;

import static org.mockito.Mockito.reset;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class UseCaseTest implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        final List<Object> mocksForClean = getMocksForClean();
        if (Objects.nonNull(mocksForClean)) {
            reset(mocksForClean.toArray());
        }
    }

    protected abstract List<Object> getMocksForClean();

}
