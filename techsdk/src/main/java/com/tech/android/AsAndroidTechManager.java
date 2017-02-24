package com.tech.android;

import android.content.Context;
import android.content.Intent;

import com.tech.TechManager;


/**
 * Created by tianyang on 16/9/29.
 */
public class AsAndroidTechManager extends TechManager {

    private AsAndroidTechManager() {
        threadName = Thread.currentThread().getName();
    }

    @Override
    public void init(Object object) {
        Context context = (Context) object;
        context.startService(new Intent(context, TechService.class));
    }
}
