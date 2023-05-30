package com.github.allisson95.codeflix.application;

import com.github.allisson95.codeflix.domain.castmember.CastMemberType;

import net.datafaker.Faker;

public final class Fixture {

    private static final Faker FAKER = new Faker();

    private Fixture() throws IllegalAccessException {
        throw new IllegalAccessException("Cannot construct a instance of Fixture");
    }

    public static String name() {
        return FAKER.name().fullName();
    }

    public static class CastMember {

        private CastMember() throws IllegalAccessException {
            throw new IllegalAccessException("Cannot construct a instance of Fixture.CastMember");
        }

        public static CastMemberType type() {
            return FAKER.options()
                    .option(
                            CastMemberType.ACTOR,
                            CastMemberType.DIRECTOR);
        }

    }

}
