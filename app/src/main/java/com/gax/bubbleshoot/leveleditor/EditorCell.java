package com.gax.bubbleshoot.leveleditor;

import com.gax.bubbleshoot.R;

/**
 * BubbleSystem.java ke char-legend se EXACTLY match karta hai (level-parsing
 * ka single source of truth waha hai). Har tap is cycle mein agla state deta
 * hai. Drawables wahi hain jo asli gameplay use karta hai (BubbleColor.java
 * se), taaki editor bilkul gameplay screen jaisa dikhe.
 */
public enum EditorCell {
    EMPTY('0', 0),
    RED('r', R.drawable.bubble_red),
    YELLOW('y', R.drawable.bubble_yellow),
    BLUE('b', R.drawable.bubble_blue),
    GREEN('g', R.drawable.bubble_green),
    LOCKED_RED('R', R.drawable.bubble_ice),
    LOCKED_YELLOW('Y', R.drawable.bubble_ice),
    LOCKED_BLUE('B', R.drawable.bubble_ice),
    LOCKED_GREEN('G', R.drawable.bubble_ice),
    OBSTACLE_SMALL('o', R.drawable.bubble_wood),
    OBSTACLE_LARGE('O', R.drawable.bubble_large_wood_01),
    ITEM_SMALL('x', R.drawable.bubble_nut),
    ITEM_LARGE('X', R.drawable.bubble_large_nut),
    DUMMY('+', R.drawable.bubble_blank);

    public final char code;
    public final int drawableRes;

    EditorCell(char code, int drawableRes) {
        this.code = code;
        this.drawableRes = drawableRes;
    }

    /** Tap pe agla state -> cycle wraps around. */
    public EditorCell next() {
        EditorCell[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

    public static EditorCell fromCode(char code) {
        for (EditorCell c : values()) {
            if (c.code == code) return c;
        }
        return EMPTY;
    }
}
