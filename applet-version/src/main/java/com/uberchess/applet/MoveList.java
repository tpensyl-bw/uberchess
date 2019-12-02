package com.uberchess.applet;

import java.lang.reflect.Array;

public class MoveList {

    //This class is a list of Move objects.
    Move[] data;
    int max, increment, size;

    public MoveList(int max, int increment) {
        data = new Move[max];
        this.max = max;
        this.increment = increment;
        size = 0;
    }

    public void add(Move addee) {
        if (size >= max) {
            increaseSize(increment);
        }
        data[size] = addee;
        size++;
    }

    public void add(Move[] addee) {
        int addedSize = Array.getLength(addee);
        if ((size + addedSize) >= max) {
            increaseSize(size + addedSize - max + 1);
        }
        for (int i = 0; i < addedSize; i++) {
            add(addee[i]);
        }
    }

    public void increaseSize(int increase) {
        max = max + increase;
        Move[] tempData = data;
        data = new Move[max];
        for (int i = 0; i < size; i++) {
            data[i] = tempData[i];
        }
    }

    public Move[] toArray() {
        Move[] returnee = new Move[size];
        for (int i = 0; i < size; i++) {
            returnee[i] = data[i];
        }
        return returnee;
    }
}


