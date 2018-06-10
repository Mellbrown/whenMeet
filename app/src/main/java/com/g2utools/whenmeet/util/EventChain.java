package com.g2utools.whenmeet.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mlyg2 on 2018-02-14.
 */

public class EventChain {

    HashMap<String, Boolean> state = new HashMap<>();
    ArrayList<RunItem> runItems = new ArrayList<>();

    public void ready(String label){
        state.put(label,false);
        Log.i("EventChain", "Reday for " + label);
    }

    public void complete(String label){
        if (state.containsKey(label)) {
            if (!state.get(label)) {
                state.put(label,true);
                Log.i("EventChain","Complete " + label);
                internalCheck();
            } else{
                Log.w("EventChain", "already complete about " + label);
            }
        }else{
            Log.e("EventChain", "not ever been ready about " + label);
        }
    }

    private void internalCheck(){
        for(RunItem runItem : runItems){
            boolean isAllComplete = true;

            completeCheck: for(String label : runItem.labels){
                if (state.containsKey(label)) {
                    if (!state.get(label)) {
                        isAllComplete = false;
                        break completeCheck;
                    }
                }else{
                    Log.e("EventChain", "not rerfern about " + label);
                }
            }

            if(isAllComplete){
                Log.i("EventChain","meet a condition follow " + runItem.labels + " and run AndthenEvent");
                runItem.runWith.run();
                runItems.remove(runItem);
            }
        }
    }

    public void andthen(CallBack runWith, String... labels){
        RunItem runItem = new RunItem();
        runItem.labels = labels;
        runItem.runWith = runWith;
        runItems.add(runItem);
        internalCheck();
    }

    class RunItem{
        String[] labels;
        CallBack runWith;
    }

    public interface CallBack{
        void run();
    }
}
