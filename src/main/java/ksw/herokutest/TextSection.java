package ksw.herokutest;

import ksw.kwutil.simpledb.SimpleDbObject;

public class TextSection extends SimpleDbObject
{
    private String _title;
    private String _text;
    private Integer _ownerId;
    private User _owner;

    public TextSection()
    {
        
    }
    
    public void setTitle(String val)
    {
        _title = val;
    }
    
    public String getTitle()
    {
        return _title;
    }
    
    public void setText(String val)
    {
        _text = val;
    }
    
    public String getText()
    {
        return _text;
    }
    
    public void setOwnerId(Integer val)
    {
        _ownerId = val;
    }

    public Integer getOwnerId()
    {
        return _ownerId;
    }
    
    public void setOwner(User val)
    {
        _owner = val;
        _ownerId = _owner.getId();
    }

    public User getOwner()
    {
        if (_owner == null) {
            _owner = (User)getDb().find(User.class, _ownerId);
        }
        return _owner;
    }
}
