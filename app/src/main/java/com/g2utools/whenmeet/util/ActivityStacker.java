package com.g2utools.whenmeet.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mlyg2 on 2018-02-13.
 */

public class ActivityStacker {

    private static HashMap<String, ArrayList<Activity>> stacker = new HashMap<>();
    public static void push(String stack, Activity activity){
        if (!stacker.containsKey(stack)) {
            stacker.put(stack,new ArrayList<Activity>());
        }
        ArrayList<Activity> activities = stacker.get(stack);
        activities.add(activity);
    }
    public static void pop(String stack, Activity activity){
        if (stacker.containsKey(stack) && stacker.get(stack).contains(activity)) {
            ArrayList<Activity> activities = stacker.get(stack);
            activities.remove(activity);
            if ((activities.size()==0)) {
                stacker.remove(stack);
            }
        }
    }

    public static void relase(String stack){
        if (stacker.containsKey(stack)) {
            stacker.get(stack).clear();
            stacker.remove(stack);
        }
    }
    public static void kill(String stack){
        if (stacker.containsKey(stack)) {
            ArrayList<Activity> activities = stacker.get(stack);
            for (int i = activities.size() - 1; i >= 0; i--) {
                activities.get(i).finish();
            }
        }
    }
}
