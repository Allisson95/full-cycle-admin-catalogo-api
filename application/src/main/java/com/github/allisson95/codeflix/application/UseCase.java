package com.github.allisson95.codeflix.application;

public abstract class UseCase<IN, OUT> {

    public abstract OUT execute(IN anIn);

}
