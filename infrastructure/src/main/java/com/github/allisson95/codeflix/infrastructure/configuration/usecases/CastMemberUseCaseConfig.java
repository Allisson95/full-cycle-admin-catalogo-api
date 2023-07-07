package com.github.allisson95.codeflix.infrastructure.configuration.usecases;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.allisson95.codeflix.application.castmember.create.CreateCastMemberUseCase;
import com.github.allisson95.codeflix.application.castmember.create.DefaultCreateCastMemberUseCase;
import com.github.allisson95.codeflix.application.castmember.delete.DefaultDeleteCastMemberUseCase;
import com.github.allisson95.codeflix.application.castmember.delete.DeleteCastMemberUseCase;
import com.github.allisson95.codeflix.application.castmember.retrieve.get.DefaultGetCastMemberByIdUseCase;
import com.github.allisson95.codeflix.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import com.github.allisson95.codeflix.application.castmember.retrieve.list.DefaultListCastMembersUseCase;
import com.github.allisson95.codeflix.application.castmember.retrieve.list.ListCastMembersUseCase;
import com.github.allisson95.codeflix.application.castmember.update.DefaultUpdateCastMemberUseCase;
import com.github.allisson95.codeflix.application.castmember.update.UpdateCastMemberUseCase;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;

@Configuration
public class CastMemberUseCaseConfig {

    private final CastMemberGateway castMemberGateway;

    public CastMemberUseCaseConfig(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = castMemberGateway;
    }

    @Bean
    public CreateCastMemberUseCase createCastMemberUseCase() {
        return new DefaultCreateCastMemberUseCase(this.castMemberGateway);
    }

    @Bean
    public DeleteCastMemberUseCase deleteCastMemberUseCase() {
        return new DefaultDeleteCastMemberUseCase(this.castMemberGateway);
    }

    @Bean
    public GetCastMemberByIdUseCase getCastMemberByIdUseCase() {
        return new DefaultGetCastMemberByIdUseCase(this.castMemberGateway);
    }

    @Bean
    public ListCastMembersUseCase listCastMembersUseCase() {
        return new DefaultListCastMembersUseCase(this.castMemberGateway);
    }

    @Bean
    public UpdateCastMemberUseCase updateCastMemberUseCase() {
        return new DefaultUpdateCastMemberUseCase(this.castMemberGateway);
    }

}
