package ksw.herokutest;

import java.util.Map;

import ksw.kwutil.JSONWriter;
import ksw.kwutil.simpledb.SimpleDbObject;

public class TextSection extends SimpleDbObject implements RestHandler.ClientJSON
{
    private String _title;
    private String _text;
    private Integer _ownerId;
    private User _owner;

    public TextSection()
    {
        
    }
    
    // convenience constructor
    public TextSection(String title, String text, User owner)
    {
        _title = title;
        _text = text;
        _owner = owner;
        _ownerId = owner.getId();
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

    @Override
    public void toClientJSON(JSONWriter jWriter)
    {
        jWriter.addItem("id", getId());
        jWriter.addItem("title", _title);
        jWriter.addItem("textcontent", _text);
    }

    @Override
    public void updateFromClientJSON(Map<String, Object> data)
    {
        setTitle((String)data.get("title"));
        setText((String)data.get("textcontent"));
    }
}
