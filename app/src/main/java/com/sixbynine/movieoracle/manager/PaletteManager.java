package com.sixbynine.movieoracle.manager;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.sixbynine.movieoracle.object.RottenTomatoesSummary;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steviekideckel on 11/19/14.
 */
public class PaletteManager extends Manager{

    private static PaletteManager instance = new PaletteManager();
    private Map<String, Palette> mPalettes;

    public static PaletteManager getInstance(){
        return instance;
    }

    private PaletteManager(){
        mPalettes = new HashMap<String, Palette>();
    }

    public void loadPalette(final RottenTomatoesSummary summary, Bitmap bmp){
        if(mPalettes.get(summary.getId()) != null){
            publish(UpdateEvent.PALETTE_LOADED, summary, mPalettes.get(summary.getId()));
        }else{
            Palette.generateAsync(bmp,
                    new Palette.PaletteAsyncListener() {
                        @Override public void onGenerated(Palette palette) {
                            mPalettes.put(summary.getId(), palette);
                            publish(UpdateEvent.PALETTE_LOADED, summary, palette);
                        }
                    });
        }
    }

}
