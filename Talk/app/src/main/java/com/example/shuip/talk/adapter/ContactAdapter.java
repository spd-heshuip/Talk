package com.example.shuip.talk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
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
public class ContactAdapter extends AbstractAdapter<Contact> implements StickyListHeadersAdapter,SectionIndexer{

    public ContactAdapter(Context context, List<Contact> datas) {
        super(context, datas);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeadViewHolder headViewHolder;
        if (convertView == null){
            headViewHolder = new HeadViewHolder();
            convertView = mInflater.inflate(R.layout.template_text,parent,false);
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
    IViewHolder getViewHolder(int viewType) {
        return new ViewHolder();
    }

    @Override
    int getViewLayout(int viewType) {
        return R.layout.template_contack;
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

        for (int i = 0;i < mDatas.size();i++){
            String sortStr = mDatas.get(i).getPinyin();
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

    private static class ViewHolder implements IViewHolder<Contact>{
        //特别注意：此处的引用命名一定要与加载的布局文件中对应的控件id名字，否则ViewInjector.injectView方法中findviewbyid将会返回null值
        ImageView imageviewHead;
        TextView textName;

        @Override
        public void bindData(Contact contact, int position) {
            textName.setText(contact.getName());
            ImageUtil.displayImageUseDefOption(contact.getHeadbig(),imageviewHead);
        }
    }

    private static class HeadViewHolder{
        TextView text;
    }


}

