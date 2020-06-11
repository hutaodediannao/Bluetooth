package com.sf.bluetoothcommunication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 姓名:胡涛
 * 工号:80004074
 * 创建日期:2020/6/11 0011 9:28
 * 功能描述:公共的ViewHolder
 */
public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {

    private SparseArrayCompat<View> mViewSparseArrayCompat;

    private BaseRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mViewSparseArrayCompat = new SparseArrayCompat<>();
    }

    public static BaseRecyclerViewHolder getInstance(ViewGroup parent, int layoutId, Context context) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new BaseRecyclerViewHolder(itemView);
    }

    private <T> T getView(int viewId) {
        View v = mViewSparseArrayCompat.get(viewId);
        if (v == null) {
            v = this.itemView.findViewById(viewId);
            mViewSparseArrayCompat.put(viewId, v);
        }
        return (T) v;
    }

    /**
     * 设置TextView
     * @param viewId
     * @param text
     * @return
     */
    public BaseRecyclerViewHolder setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    /**
     * 设置TextView
     * @param viewId
     * @param textResId
     * @return
     */
    public BaseRecyclerViewHolder setText(int viewId, int textResId) {
        TextView textView = getView(viewId);
        textView.setText(textResId);
        return this;
    }

    /**
     * 设置ImageView
     * @param viewId
     * @param resId
     * @return
     */
    public BaseRecyclerViewHolder setImageView(int viewId, int resId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resId);
        return this;
    }

    /**
     * 设置CheckBox
     * @param viewId
     * @param checked
     * @return
     */
    public BaseRecyclerViewHolder setCheckBox(int viewId, boolean checked) {
        CheckBox checkBox = getView(viewId);
        checkBox.setChecked(checked);
        return this;
    }

    /**
     * 设置RadioButton选择性
     * @param viewId
     * @param checked
     * @return
     */
    public BaseRecyclerViewHolder setRadioButton(int viewId, boolean checked) {
        RadioButton radioButton = getView(viewId);
        radioButton.setChecked(checked);
        return this;
    }
}
