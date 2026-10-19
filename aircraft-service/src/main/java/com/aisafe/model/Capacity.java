package com.aisafe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Capacity {

    @Column(name = "max_capacity")
    private int value;

    protected Capacity() {}

    public Capacity(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("A capacidade de lugares deve ser um valor positivo.");
        }
        this.value = value;
    }
}