package com.sixbynine.movieoracle.manager;

import android.os.Handler;
import android.os.Looper;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by steviekideckel on 10/26/14.
 */
public abstract class Manager {

    private Set<UpdateListener> mListeners;
    private Handler mHandler;

    public Manager(){
        mHandler = new Handler(Looper.getMainLooper());
    }

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
            mHandler.post(new UpdateRunnable(e, data));
        }
    }

    private class UpdateRunnable implements Runnable{
        private UpdateEvent e;
        private Object[] data;

        public UpdateRunnable(UpdateEvent e, Object... data){
            this.e = e;
            this.data = data;
        }

        public void run(){
            for (UpdateListener listener : mListeners) {
                listener.update(e, data);
            }
        }
    }

}
