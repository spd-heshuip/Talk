package com.example.shuip.talk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.shuip.talk.model.BaseEntity;

import java.util.List;

/**
 * Created by Administrator on 15-9-18.
 */
public abstract class AbstractAdapter<T extends BaseEntity> extends BaseAdapter {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mDatas;
    protected String TAG = this.getClass().getSimpleName();

    public AbstractAdapter(Context context,List<T> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public long getItemId(int position) {
        Long id = mDatas.get(position).getId();
        return id == null ? 0 : id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        IViewHolder viewHolder;
        if (convertView == null){
            viewHolder = getViewHolder(viewType);
            convertView = mInflater.inflate(getViewLayout(viewType), parent, false);
            ViewInjector.injectView(viewHolder,convertView);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (IViewHolder) convertView.getTag();
        }

        viewHolder.bindData(getItem(position),position);
        return convertView;
    }

    public void addData(List<T> arrayData) {

        if (arrayData != null) {
            mDatas.addAll(arrayData);
        }
        notifyDataSetChanged();
    }

    public void addData(T data) {

        if (data != null) {
            mDatas.add(data);
        }
        notifyDataSetChanged();
    }

    public  void clear()
    {
        mDatas.clear();
        notifyDataSetChanged();
    }

    abstract IViewHolder getViewHolder(int viewType);
    abstract int getViewLayout(int viewType);

}
