package com.g2utools.whenmeet.util;

/**
 * Created by mlyg2 on 2018-02-11.
 */

public class NullCheck {
    static public boolean equal(Object o1, Object o2){
        if (o1 == null && o2 != null) {
            return false;
        } else if(o1 != null && o2 == null){
            return false;
        }else if(o1 == null && o2 == null){
            return true;
        }else{
            return o1.equals(o2);
        }
    }

    static public int comapareTo(Comparable o1, Comparable o2){
        if (o1 == null && o2 != null) {
            return -1;
        } else if(o1 != null && o2 == null){
            return 1;
        }else if(o1 == null && o2 == null){
            return 0;
        }else{
            return o1.compareTo(o2);
        }
    }
}
