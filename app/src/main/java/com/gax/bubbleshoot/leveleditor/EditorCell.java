package com.gax.bubbleshoot.leveleditor;

/**
 * BubbleSystem.java ke char-legend se EXACTLY match karta hai (level-parsing
 * ka single source of truth waha hai). Har tap is cycle mein agla state deta
 * hai -> editor mein naya cell-type add karna ho toh yahan bas ek entry
 * badhao, BubbleSystem ka legend already sab handle karta hai.
 */
public enum EditorCell {
    EMPTY('0', "", "#33FFFFFF"),
    RED('r', "R", "#F44336"),
    YELLOW('y', "Y", "#FBC02D"),
    BLUE('b', "B", "#1E88E5"),
    GREEN('g', "G", "#43A047"),
    LOCKED_RED('R', "R\uD83D\uDD12", "#7F0000"),
    LOCKED_YELLOW('Y', "Y\uD83D\uDD12", "#8D6E00"),
    LOCKED_BLUE('B', "B\uD83D\uDD12", "#0D47A1"),
    LOCKED_GREEN('G', "G\uD83D\uDD12", "#1B5E20"),
    OBSTACLE_SMALL('o', "OB", "#795548"),
    OBSTACLE_LARGE('O', "OB+", "#4E342E"),
    ITEM_SMALL('x', "IT", "#FF9800"),
    ITEM_LARGE('X', "IT+", "#E65100"),
    DUMMY('+', "+", "#9E9E9E");

    public final char code;
    public final String label;
    public final String colorHex;

    EditorCell(char code, String label, String colorHex) {
        this.code = code;
        this.label = label;
        this.colorHex = colorHex;
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
