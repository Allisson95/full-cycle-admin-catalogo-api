package com.github.allisson95.codeflix.application.castmember.retrieve.list;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.github.allisson95.codeflix.Fixture;
import com.github.allisson95.codeflix.IntegrationTest;
import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberRepository;

@IntegrationTest
class ListCastMembersUseCaseIT {

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private ListCastMembersUseCase useCase;

    @Test
    void Given_AValidQuery_When_CallsListCastMembers_Should_ReturnAll() {
        final var members = List.of(
            CastMember.newMember(Fixture.name(), Fixture.CastMember.type()),
            CastMember.newMember(Fixture.name(), Fixture.CastMember.type())
        );

        this.castMemberRepository.saveAllAndFlush(members.stream().map(CastMemberJpaEntity::from).toList());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var expectedItems = members.stream()
                .map(CastMemberListOutput::from)
                .toList();

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        final var actualOutput = useCase.execute(aQuery);

        assertEquals(expectedPage, actualOutput.currentPage());
        assertEquals(expectedPerPage, actualOutput.perPage());
        assertEquals(expectedTotal, actualOutput.total());
        assertEquals(expectedItems, actualOutput.items());
        assertThat(actualOutput.items(), containsInAnyOrder(expectedItems.toArray()));

        verify(castMemberGateway).findAll(any());
    }

    @Test
    void Given_AValidQuery_When_CallsListCastMembersAndResultIsEmpty_Should_ReturnEmpty() {
        final var members = List.<CastMember>of();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var expectedItems = members.stream()
                .map(CastMemberListOutput::from)
                .toList();

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        final var actualOutput = useCase.execute(aQuery);

        assertEquals(expectedPage, actualOutput.currentPage());
        assertEquals(expectedPerPage, actualOutput.perPage());
        assertEquals(expectedTotal, actualOutput.total());
        assertEquals(expectedItems, actualOutput.items());
        assertThat(actualOutput.items(), containsInAnyOrder(expectedItems.toArray()));

        verify(castMemberGateway).findAll(any());
    }

    @Test
    void Given_AValidQuery_When_CallsListCastMembersAndGatewayThrowsException_Then_ReturnException() {
        final var members = List.of(
            CastMember.newMember(Fixture.name(), Fixture.CastMember.type()),
            CastMember.newMember(Fixture.name(), Fixture.CastMember.type())
        );

        this.castMemberRepository.saveAllAndFlush(members.stream().map(CastMemberJpaEntity::from).toList());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = " ";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedErrorMessage = "Gateway error";

        final var aQuery = new SearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection);

        doThrow(new IllegalStateException(expectedErrorMessage))
                .when(castMemberGateway).findAll(aQuery);

        final var actualException = assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(aQuery));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(castMemberGateway).findAll(any());
    }

}
