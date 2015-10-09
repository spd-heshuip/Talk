package com.example.shuip.talk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.shuip.talk.R;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.util.ImageUtil;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Administrator on 15-9-11.
 */
public class ContactAdapter extends BaseAdapter implements StickyListHeadersAdapter,SectionIndexer{

    private Context mContext;
    private List<Contact> mContactList;
    public ContactAdapter(Context context, List<Contact> contact) {
        mContext = context;
        mContactList = contact;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeadViewHolder headViewHolder;
        if (convertView == null){
            headViewHolder = new HeadViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.template_text,parent,false);
            headViewHolder.text = (TextView) convertView.findViewById(R.id.head);

            convertView.setTag(headViewHolder);
        }else {
            headViewHolder = (HeadViewHolder) convertView.getTag();
        }

        String pinyin = "" + getItem(position).getPinyin().subSequence(0,1).charAt(0);
        headViewHolder.text.setText(pinyin);

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return getItem(position).getPinyin().subSequence(0,1).charAt(0);
    }

    @Override
    public int getCount() {
        return mContactList.size();
    }

    @Override
    public Contact getItem(int position) {
        return mContactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        Long id = getItem(position).getId();
        return id == null ? 0 : id;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.template_contack,parent,false);
            holder.imageHead = (ImageView) convertView.findViewById(R.id.imageviewHead);
            holder.nameText = (TextView)convertView.findViewById(R.id.textName);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

//        holder.imageHead.setImageResource(R.drawable.default_head);
        holder.nameText.setText(getItem(position).getName());
        ImageUtil.displayImageUseDefOption(getItem(position).getHeadbig(),holder.imageHead);
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if (sectionIndex == '#'){
            return 0;
        }

        for (int i = 0;i < mContactList.size();i++){
            String sortStr = mContactList.get(i).getPinyin();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return getItem(position).getPinyin().subSequence(0,1).charAt(0);
    }

    private static class ViewHolder{
        ImageView imageHead;
        TextView nameText;
    }

    private static class HeadViewHolder{
        TextView text;
    }


}

