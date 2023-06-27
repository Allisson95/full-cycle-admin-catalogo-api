package com.github.allisson95.codeflix.infrastructure.castmember;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.allisson95.codeflix.Fixture;
import com.github.allisson95.codeflix.MySQLGatewayTest;
import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberJpaEntity;
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

    @Test
    void Given_AValidCastMember_When_CallsUpdate_Should_RefreshIt() {
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;

        final var aMember = CastMember.newMember("vind", CastMemberType.DIRECTOR);
        final var expectedId = aMember.getId();

        final var prePersistedMember = this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        assertEquals(1, this.castMemberRepository.count());

        assertEquals(expectedId.getValue(), prePersistedMember.getId());
        assertEquals("vind", prePersistedMember.getName());
        assertEquals(CastMemberType.DIRECTOR, prePersistedMember.getType());

        final var actualMember = this.castMemberMySQLGateway
                .update(CastMember.with(aMember).update(expectedName, expectedType));

        assertEquals(1, this.castMemberRepository.count());

        assertEquals(expectedId, actualMember.getId());
        assertEquals(expectedName, actualMember.getName());
        assertEquals(expectedType, actualMember.getType());
        assertEquals(aMember.getCreatedAt(), actualMember.getCreatedAt());
        assertTrue(aMember.getUpdatedAt().isBefore(actualMember.getUpdatedAt()));

        final var persistedMember = this.castMemberRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedId.getValue(), persistedMember.getId());
        assertEquals(expectedName, persistedMember.getName());
        assertEquals(expectedType, persistedMember.getType());
        assertEquals(aMember.getCreatedAt(), persistedMember.getCreatedAt());
        assertTrue(aMember.getUpdatedAt().isBefore(persistedMember.getUpdatedAt()));
    }

    @Test
    void Given_AValidCastMemberID_When_CallsDeleteById_Should_RemoveIt() {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        final var expectedId = aMember.getId();

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        assertEquals(1, this.castMemberRepository.count());

        this.castMemberMySQLGateway.deleteById(expectedId);

        assertEquals(0, this.castMemberRepository.count());
    }

    @Test
    void Given_AInvalidCastMemberID_When_CallsDeleteById_Should_DoNothing() {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        assertEquals(1, this.castMemberRepository.count());

        this.castMemberMySQLGateway.deleteById(CastMemberID.from("123"));

        assertEquals(1, this.castMemberRepository.count());
    }

    @Test
    void Given_AValidCastMemberID_When_CallsFindById_Should_ReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var aMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = aMember.getId();

        final var prePersistedMember = this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        assertEquals(1, this.castMemberRepository.count());

        assertEquals(expectedId.getValue(), prePersistedMember.getId());
        assertEquals(expectedName, prePersistedMember.getName());
        assertEquals(expectedType, prePersistedMember.getType());
        assertEquals(aMember.getCreatedAt(), prePersistedMember.getCreatedAt());
        assertEquals(aMember.getUpdatedAt(), prePersistedMember.getUpdatedAt());

        final var actualMember = this.castMemberMySQLGateway.findById(expectedId).get();

        assertEquals(expectedId, actualMember.getId());
        assertEquals(expectedName, actualMember.getName());
        assertEquals(expectedType, actualMember.getType());
        assertEquals(aMember.getCreatedAt(), actualMember.getCreatedAt());
        assertEquals(aMember.getUpdatedAt(), actualMember.getUpdatedAt());
    }

    @Test
    void Given_AInvalidCastMemberID_When_CallsFindById_Should_ReturnEmpty() {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        assertEquals(1, this.castMemberRepository.count());

        final var actualMember = this.castMemberMySQLGateway.findById(CastMemberID.from("123"));

        assertTrue(actualMember.isEmpty());
    }

}
