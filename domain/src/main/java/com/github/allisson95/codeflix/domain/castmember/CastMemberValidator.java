package com.github.allisson95.codeflix.domain.castmember;

import com.github.allisson95.codeflix.domain.validation.ValidationHandler;
import com.github.allisson95.codeflix.domain.validation.Validator;

public class CastMemberValidator extends Validator {

    private final CastMember castMember;

    public CastMemberValidator(final CastMember aMember, final ValidationHandler aHandler) {
        super(aHandler);
        this.castMember = aMember;
    }

    @Override
    public void validate() { }

}
