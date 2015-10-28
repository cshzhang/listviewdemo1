package cn.hzh.listviewdemo1.bean;

/**
 * Created by hzh on 2015/10/27.
 */
public class Contact
{
    private String name;
    private String sortKey;

    public Contact(String sortKey, String name)
    {
        this.sortKey = sortKey;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSortKey()
    {
        return sortKey;
    }

    public void setSortKey(String sortKey)
    {
        this.sortKey = sortKey;
    }

    @Override
    public String toString()
    {
        return "Contact{" +
                "name='" + name + '\'' +
                ", sortKey='" + sortKey + '\'' +
                '}';
    }
}
