package com.sixbynine.movieoracle.manager;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.sixbynine.movieoracle.MyApplication;
import com.sixbynine.movieoracle.datamodel.rottentomatoes.moviequery.RTMovieQueryMovieSummary;
import com.sixbynine.movieoracle.events.PaletteLoadedEvent;

import java.util.HashMap;
import java.util.Map;

public final class PaletteManager {

    private static Map<String, Palette> palettes = new HashMap<>();

    public static Palette loadPalette(RTMovieQueryMovieSummary summary, Bitmap bmp) {
        final String id = summary.getId();
        Palette palette = palettes.get(id);
        if (palette == null && bmp != null) {
            Palette.from(bmp).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    palettes.put(id, palette);
                    MyApplication.getInstance().getBus().post(new PaletteLoadedEvent(id, palette));
                }
            });
        }
        return palette;
    }
}
