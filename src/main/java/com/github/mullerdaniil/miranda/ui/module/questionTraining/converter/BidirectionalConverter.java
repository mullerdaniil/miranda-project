package com.github.mullerdaniil.miranda.ui.module.questionTraining.converter;

public interface BidirectionalConverter<X, Y> {
    Y convertTo(X obj);
    X convertFrom(Y obj);
}