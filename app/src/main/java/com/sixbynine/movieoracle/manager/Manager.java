package com.sixbynine.movieoracle.manager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by steviekideckel on 10/26/14.
 */
public abstract class Manager {

    private Set<UpdateListener> mListeners;

    public boolean subscribe(UpdateListener listener){
        if(mListeners == null){
            mListeners = new HashSet<UpdateListener>();
        }
        return mListeners.add(listener);
    }

    public boolean unSubscribe(UpdateListener listener){
        if(mListeners != null){
            return mListeners.remove(listener);
        }
        return false;
    }

    protected void publish(UpdateEvent e, Object... data){
        if(mListeners != null) {
            for (UpdateListener listener : mListeners) {
                listener.update(e, data);
            }
        }
    }

}
