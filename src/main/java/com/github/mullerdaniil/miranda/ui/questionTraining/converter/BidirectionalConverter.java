package com.github.mullerdaniil.miranda.ui.questionTraining.converter;

public interface BidirectionalConverter<X, Y> {
    Y convertTo(X obj);
    X convertFrom(Y obj);
}