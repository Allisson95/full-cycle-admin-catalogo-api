package com.github.allisson95.codeflix.application.castmember.update;

import com.github.allisson95.codeflix.application.UseCase;

public abstract sealed class UpdateCastMemberUseCase
        extends UseCase<UpdateCastMemberCommand, UpdateCastMemberOutput>
        permits DefaultUpdateCastMemberUseCase {

}
