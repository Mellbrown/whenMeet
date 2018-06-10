package com.g2utools.whenmeet.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mlyg2 on 2018-02-10.
 */

abstract public class BaseListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{

    @NonNull
    public ArrayList<T> dataList = new ArrayList<>();
    protected int mModelLayout;
    Class<VH> mViewHolderClass;

    public BaseListAdapter(int modelLayout, Class<VH> viewHolderClass){
        mModelLayout = modelLayout;
        mViewHolderClass = viewHolderClass;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(mModelLayout, parent, false);
        try {
            Constructor<VH> constructor = mViewHolderClass.getConstructor(View.class);
            VH vh = constructor.newInstance(view);
            onCreateAfterViewHolder(vh);
            return vh;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void onCreateAfterViewHolder(VH holder){}

    @Override
    public void onBindViewHolder(VH holder, int position) {
        dataConvertViewHolder(holder,dataList.get(position));
    }

    abstract public void dataConvertViewHolder(VH holder, T data);
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
