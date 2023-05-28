package com.github.allisson95.codeflix.application.castmember.create;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.allisson95.codeflix.application.UseCaseTest;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;

class CreateCastMemberUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultCreateCastMemberUseCase useCase;

    @Override
    protected List<Object> getMocksForClean() {
        return List.of(castMemberGateway);
    }

    @Test
    void Given_AValidCommand_When_CallCreateCastMember_Should_ReturnCastMemberId() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var aCommand = CreateCastMemberCommand.with(expectedName, expectedType);

        when(castMemberGateway.create(any())).thenAnswer(returnsFirstArg());

        final var createdMember = useCase.execute(aCommand);

        assertNotNull(createdMember);
        assertNotNull(createdMember.id());

        verify(castMemberGateway, times(1))
                .create(argThat(
                        aMember -> Objects.equals(expectedName, aMember.getName())
                                && Objects.equals(expectedType, aMember.getType())
                                && Objects.nonNull(aMember.getId())
                                && Objects.nonNull(aMember.getCreatedAt())
                                && Objects.nonNull(aMember.getUpdatedAt())));
    }

}
