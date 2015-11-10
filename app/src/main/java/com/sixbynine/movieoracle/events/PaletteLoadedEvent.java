package com.sixbynine.movieoracle.events;

import android.support.v7.graphics.Palette;

public final class PaletteLoadedEvent {

    private String id;
    private Palette palette;

    public PaletteLoadedEvent(String id, Palette palette) {
        this.id = id;
        this.palette = palette;
    }

    public String getId() {
        return id;
    }

    public Palette getPalette() {
        return palette;
    }
}
