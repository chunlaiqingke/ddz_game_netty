package com.handsome.ddz.game;

import lombok.Data;

@Data
public class Card {

    private Integer value;

    private Integer shape;

    private Integer king;

    private int index = -1;

    public Card(Integer value, Integer shape, Integer king) {
        this.value = value;
        this.shape = shape;
        this.king = king;
    }
}
