package com.g2utools.whenmeet.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mlyg2 on 2018-03-06.
 */

@IgnoreExtraProperties
public class SheetData implements Comparable<SheetData>, Serializable {

    @Exclude
    public static String TYPE_MONTH = "month";
    @Exclude
    public static String TYPE_DAY = "day";

    public String type;
    public String title;
    public String refData;

    public Long date;
    public Long timestamp;

    public SheetData(){}

    @Override
    public int compareTo(@NonNull SheetData sheetData) {
        return timestamp.compareTo(sheetData.timestamp);
    }
}
