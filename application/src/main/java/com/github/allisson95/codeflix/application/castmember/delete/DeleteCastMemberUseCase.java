package com.github.allisson95.codeflix.application.castmember.delete;

import com.github.allisson95.codeflix.application.UnitUseCase;

public abstract sealed class DeleteCastMemberUseCase
        extends UnitUseCase<String>
        permits DefaultDeleteCastMemberUseCase {

}
