package com.github.allisson95.codeflix.application.castmember.retrieve.get;

import com.github.allisson95.codeflix.application.UseCase;

public abstract sealed class GetCastMemberByIdUseCase
        extends UseCase<String, CastMemberOutput>
        permits DefaultGetCastMemberByIdUseCase {

}
