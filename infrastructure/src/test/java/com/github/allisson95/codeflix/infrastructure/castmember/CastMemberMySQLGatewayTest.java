package com.github.allisson95.codeflix.infrastructure.castmember;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.allisson95.codeflix.Fixture;
import com.github.allisson95.codeflix.MySQLGatewayTest;
import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberRepository;

@MySQLGatewayTest
class CastMemberMySQLGatewayTest {

    @Autowired
    private CastMemberMySQLGateway castMemberMySQLGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Test
    void testDependencies() {
        assertNotNull(castMemberMySQLGateway);
        assertNotNull(castMemberRepository);
    }

    @Test
    void Given_AValidCastMember_When_CallsCreate_Should_PersistIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var aMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = aMember.getId();

        assertEquals(0, this.castMemberRepository.count());

        final var actualMember = this.castMemberMySQLGateway.create(CastMember.with(aMember));

        assertEquals(1, this.castMemberRepository.count());

        assertEquals(expectedId, actualMember.getId());
        assertEquals(expectedName, actualMember.getName());
        assertEquals(expectedType, actualMember.getType());
        assertEquals(aMember.getCreatedAt(), actualMember.getCreatedAt());
        assertEquals(aMember.getUpdatedAt(), actualMember.getUpdatedAt());

        final var persistedMember = this.castMemberRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedId.getValue(), persistedMember.getId());
        assertEquals(expectedName, persistedMember.getName());
        assertEquals(expectedType, persistedMember.getType());
        assertEquals(aMember.getCreatedAt(), persistedMember.getCreatedAt());
        assertEquals(aMember.getUpdatedAt(), persistedMember.getUpdatedAt());
    }

}
