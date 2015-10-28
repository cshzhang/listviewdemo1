package cn.hzh.listviewdemo1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.hzh.listviewdemo1.bean.Contact;

/**
 * Created by hzh on 2015/10/27.
 */
public class ContactAdapter extends ArrayAdapter<Contact>
{
    private LayoutInflater mInflater;
    private Context mContext;

    private AlphabetIndexer mIndexer;

    public ContactAdapter(Context context, List<Contact> datas)
    {
        super(context, -1, datas);

        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = mInflater.inflate(R.layout.item, parent, false);
        }

        TextView sectionTv = (TextView) convertView.findViewById(R.id.id_tv_section);
        TextView nameTv = (TextView) convertView.findViewById(R.id.id_name_tv);
        LinearLayout sectionLayout = (LinearLayout) convertView.findViewById(R.id.id_section_layout);

        nameTv.setText(getItem(position).getName());

        int section = mIndexer.getSectionForPosition(position);
        //if成立，表示当前位置的item就是section的第一个item，需要显示sectionLayout
        if(position == mIndexer.getPositionForSection(section))
        {
            sectionTv.setText(getItem(position).getSortKey());
            sectionLayout.setVisibility(View.VISIBLE);
        }else
        {
            sectionLayout.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     *     接收AlphabetIndexer对象
     */
    public void setAlphabetIndexer(AlphabetIndexer indexer)
    {
        mIndexer = indexer;
    }
}
