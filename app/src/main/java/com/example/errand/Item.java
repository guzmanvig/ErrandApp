package com.example.errand;

import androidx.annotation.NonNull;

public class Item {

    public String name;
    public String size;
    public String amount;

    public Item(String name, String size, String amount) {
        this.name = name;
        this.size = size;
        this.amount = amount;
    }

    @NonNull
    @Override
    public String toString() {
        if (size.equals(""))
            return amount + " " + name;

        return amount + " " + name + " of " + size;
    }
}
