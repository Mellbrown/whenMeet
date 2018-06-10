package com.g2utools.whenmeet.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mlyg2 on 2018-03-07.
 */

public class SheetDataControler implements ValueEventListener {
    private String refData;
    private OnChangeDataSetLisnter onChangeDataSetLisnter;
    private DataSnapshot dataSnapshot;

    public SheetDataControler(String refData, OnChangeDataSetLisnter onChangeDataSetLisnter){
        this.refData = refData;
        this.onChangeDataSetLisnter = onChangeDataSetLisnter;
    }

    public void start(){
        FirebaseDatabase.getInstance().getReference(refData).addValueEventListener(this);
    }

    public void stop(){
        FirebaseDatabase.getInstance().getReference(refData).removeEventListener(this);
    }

    public int getCount(String key){
        if(dataSnapshot == null) return 0;
        return ((int) dataSnapshot.child(key).getChildrenCount());
    }

    public void pushUID(String key, String uid){
        FirebaseDatabase.getInstance().getReference(refData).child(key).push().setValue(uid);
    }

    public void popUID(String key, String uid){
        for(DataSnapshot o : dataSnapshot.child(key).getChildren())
            if(o.getValue().equals(uid))
                FirebaseDatabase.getInstance().getReference(refData).child(key).child(o.getKey()).removeValue();
    }

    public boolean isUID(String key, String uid){
        if(dataSnapshot == null) return false;
        for(DataSnapshot o : dataSnapshot.child(key).getChildren())
            if(o.getValue().equals(uid))
                return true;
        return false;
    }

    public Set<String> getUIDs(String key){
        Set<String> uids = new HashSet<>();
        for(DataSnapshot o : dataSnapshot.child(key).getChildren()){
            uids.add(o.getValue(String.class));
        }
        return uids;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        this.dataSnapshot = dataSnapshot;
        onChangeDataSetLisnter.onChangDataSetLisnter();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public interface OnChangeDataSetLisnter{
        void onChangDataSetLisnter();
    }
}
