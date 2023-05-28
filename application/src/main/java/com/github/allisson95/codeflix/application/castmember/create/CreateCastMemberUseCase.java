package com.github.allisson95.codeflix.application.castmember.create;

import com.github.allisson95.codeflix.application.UseCase;

public abstract sealed class CreateCastMemberUseCase
        extends UseCase<CreateCastMemberCommand, CreateCastMemberOutput>
        permits DefaultCreateCastMemberUseCase {

}
