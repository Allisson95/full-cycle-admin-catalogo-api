package com.github.allisson95.codeflix.application;

import static org.mockito.Mockito.reset;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.allisson95.codeflix.domain.Identifier;

@Tag("unitTest")
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

    protected List<String> asString(final List<? extends Identifier> ids) {
        return ids.stream()
                .map(Identifier::getValue)
                .toList();
    }

    protected Set<String> asString(final Set<? extends Identifier> ids) {
        return ids.stream()
                .map(Identifier::getValue)
                .collect(Collectors.toSet());
    }

}
