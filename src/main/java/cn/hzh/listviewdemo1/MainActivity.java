package cn.hzh.listviewdemo1;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AlphabetIndexer;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.hzh.listviewdemo1.bean.Contact;

public class MainActivity extends AppCompatActivity
{

    //用于对字母表进行分组
    private AlphabetIndexer mIndexer;
    //定义排序规则
    private String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    //adapter数据源
    private List<Contact> mDatas = new ArrayList<Contact>();

    //listview的adapter
    private ContactAdapter mAdapter;

    private ListView mListView;
    private LinearLayout mSectionLayout;
    private TextView mSectionTv;
    private Button mAlphabetButton;
    private TextView mSectionFloatingTv;

    //记录滑动过程中，listview的上一个第一个子view的索引
    private int mLastFirstVisibleItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        initEvent();
    }

    private void initView()
    {
        mSectionLayout = (LinearLayout) findViewById(R.id.id_section_layout);
        mListView = (ListView) findViewById(R.id.id_listview);
        mSectionTv = (TextView) findViewById(R.id.id_tv_section);
        mSectionFloatingTv = (TextView) findViewById(R.id.id_float_section_tv);
        mAlphabetButton = (Button) findViewById(R.id.id_section_btn);

        //读取手机联系人数据的uri
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //查询表中两列数据："display_name", "sort_key"，并按sort_key排序
        Cursor cursor = getContentResolver().query(uri, new String[]{"display_name", "sort_key"},
                null, null, "sort_key");

        Contact contact = null;
        //构建adapter的数据源
        while(cursor.moveToNext())
        {
            String name = cursor.getString(cursor.getColumnIndex("display_name"));
            String sortKey = cursor.getString(cursor.getColumnIndex("sort_key"));
            sortKey = getSortKey(sortKey);
            contact = new Contact(sortKey, name);
            mDatas.add(contact);
        }
        //将cursor的生命周期交给activity管理
        startManagingCursor(cursor);

        //利用android提供的辅助类，AlphabetIndexer对cursor进行分组
        mIndexer = new AlphabetIndexer(cursor, 1, alphabet);

        mAdapter = new ContactAdapter(this, mDatas);
        mAdapter.setAlphabetIndexer(mIndexer);

        mListView.setAdapter(mAdapter);
        if(mDatas.size() > 0)
        {
            mSectionTv.setText(mDatas.get(0).getSortKey());
        }
    }

    private void initEvent()
    {
        //监听listview的OnScrollListener，实现挤压功能。
        mListView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }
            //list的scroll完成之后，回调该方法。
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount)
            {
                if(mDatas.size() < 1)
                    return;

                int setction = mIndexer.getSectionForPosition(firstVisibleItem);
                int nextSectionFirstPos = mIndexer.getPositionForSection(setction+1);

                //第一个子view的发生改变，就更新mSectionLayout和mSectionTv
                if(firstVisibleItem != mLastFirstVisibleItem)
                {
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mSectionLayout.getLayoutParams();
                    lp.topMargin = 0;
                    mSectionLayout.setLayoutParams(lp);
                    mSectionTv.setText(String.valueOf(alphabet.charAt(setction)));
                }

                //一旦这个条件满足，就代表两个section要碰上了，在里面判断并改变mSectionLayout的topMargin
                if(nextSectionFirstPos == firstVisibleItem + 1)
                {
                    View child = view.getChildAt(0);
                    if(child != null)
                    {
                        int sectionHeight = mSectionLayout.getHeight();
                        int bottom = child.getBottom();

                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mSectionLayout.getLayoutParams();
                        if(bottom < sectionHeight+1)
                        {
                            float distance = bottom - sectionHeight;
                            lp.topMargin = (int) distance;
                            mSectionLayout.setLayoutParams(lp);
                        }else
                        {
                            if(lp.topMargin != 0)
                            {
                                lp.topMargin = 0;
                                mSectionLayout.setLayoutParams(lp);
                            }
                        }
                    }
                }

                mLastFirstVisibleItem = firstVisibleItem;
            }
        });

        //实现字母表快速滚动
        mAlphabetButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(mDatas.size() < 1)
                    return true;

                float y = event.getY();
                float buttonHeight = mAlphabetButton.getHeight();
                //根据touch的y值，计算section值
                int section = (int) ((y / buttonHeight) / (1f / 27f));
                section = section < 0 ? 0 : section;
                section = section > 26 ? 26 : section;
                int position = mIndexer.getPositionForSection(section);
                Contact contact = mDatas.get(position);

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        mAlphabetButton.setBackgroundResource(R.mipmap.a_z_click);
                        mSectionFloatingTv.setVisibility(View.VISIBLE);
                        mSectionFloatingTv.setText(contact.getSortKey());
                        //回调onScroll方法
                        mListView.setSelection(position);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mAlphabetButton.setBackgroundResource(R.mipmap.a_z_click);
                        mSectionFloatingTv.setVisibility(View.VISIBLE);
                        mSectionFloatingTv.setText(contact.getSortKey());

                        mListView.setSelection(position);
                        break;
                    case MotionEvent.ACTION_UP:
                        mSectionFloatingTv.setVisibility(View.GONE);
                        mAlphabetButton.setBackgroundResource(R.mipmap.a_z);
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 获取sortkey的首个英文字母，如果是英文字母就返回，否则返回 #
     * @param sortKey 数据库读出的sort Key
     * @return 英文字母或者#
     */
    private String getSortKey(String sortKey)
    {
        String key = sortKey.substring(0, 1).toUpperCase();
        if(key.matches("[A-Z]"))
            return key;
        return "#";
    }

}
