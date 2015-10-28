# listviewdemo1
防android系统联系人功能------分组导航，挤压动画，快速滚动

# 效果图
<img src="listviewdemo1.gif" width="320px"/>

# 知识点
* 1.SectionIndexer接口，这个接口可以有效的帮助我们对分组进行控制。我们直接使用android系统提供的AlphabetIndexer类。
这个类实现了SectionIndexer接口，用这个类实现联系人分组功能已经足够。AlphabetIndexer构造方法：
```java
public AlphabetIndexer (Cursor cursor, int sortedColumnIndex, CharSequence alphabet)
```
需要三个参数。第一个参数是cursor，就是把我们从数据库中查出来的cursor传递进去。第二个参数是sortedColumnIndex指明
我们使用哪一列进行分组排序。第三个参数是排序规则，如果按字母表顺序则传字符串"#ABCDEFGHIJKLMNOPQRSTUVWXYZ"
* 2.查询系统联系人；使用 ContactsContract.CommonDataKinds.Phone.CONTENT_URI来查询。并且需要结果集中display_name，sort_key两列
* 3.关于adapter的代码大部分都是按常规编写，就一个地方比较特殊:
```java
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
```
* 4.剩下的功能；比如挤压功能，字母表快速滑动等就请各位看官直接看代码