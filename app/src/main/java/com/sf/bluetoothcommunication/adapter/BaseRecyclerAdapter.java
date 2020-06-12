package com.sf.bluetoothcommunication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * 姓名:胡涛
 * 工号:80004074
 * 创建日期:2020/6/11 0011 9:49
 * 功能描述:公共适配器
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    private List<T> mList;
    private Context mContext;

    public BaseRecyclerAdapter(List<T> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseRecyclerViewHolder.getInstance(parent, getLayoutId(), mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewHolder holder, final int position) {
        bindHolder(holder, position, mList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickItemListener != null) mClickItemListener.onItemClick(mList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public interface ClickItemListener<T>{
        void onItemClick(T t);
    }

    private ClickItemListener mClickItemListener;

    public ClickItemListener getmClickItemListener() {
        return mClickItemListener;
    }

    public void setmClickItemListener(ClickItemListener mClickItemListener) {
        this.mClickItemListener = mClickItemListener;
    }

    abstract void bindHolder(BaseRecyclerViewHolder holder, int position, T t);

    abstract int getLayoutId();
}
