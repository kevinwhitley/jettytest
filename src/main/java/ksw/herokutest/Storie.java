package ksw.herokutest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ksw.kwutil.JSONWriter;
import ksw.kwutil.simpledb.SimpleDbObject;

public class Storie extends SimpleDbObject implements RestHandler.ClientJSON
{

    private String _title;
    private Integer _ownerId;
    private User _owner;
    private List<TextSectionDisp> _textSectionDisps;

    public Storie()
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

    public void addTextSectionDisp(TextSectionDisp tv)
    {
        // create a join entry
        JoinTextSectionDispStorie join = new JoinTextSectionDispStorie(tv.getId(), getId());
        getDb().save(join);
        
        // tell the textsection view about the connection
        tv.setStorie(this);
        
        // if we've already instantiated the text sections, add this one
        if (_textSectionDisps != null) {
            _textSectionDisps.add(tv);
        }
    }
    
    public List<TextSectionDisp> getTextSectionDisps()
    {
        if (_textSectionDisps == null) {
            _textSectionDisps = getDb().findManyToManyFromRight(getId(), TextSectionDisp.class, JoinTextSectionDispStorie.class);
        }
        
        // return a copy of the array
        List<TextSectionDisp> result = new ArrayList<TextSectionDisp>(_textSectionDisps.size());
        result.addAll(_textSectionDisps);
        return result;
    }
    
    public void toClientJSON(JSONWriter jwriter)
    {
        jwriter.addItem("id", getId());
        jwriter.addItem("title", _title);
        jwriter.addArrayToObject("textsectiondisp_ids");
        List<TextSectionDisp> tSections = getTextSectionDisps();
        for (TextSectionDisp ts : tSections) {
            jwriter.addItem(null, ts.getId());
        }
        jwriter.endArray();
    }
    
    public void updateFromClientJSON(Map<String, Object> clientData)
    {
        String updatedTitle = (String)clientData.get("title");
        setTitle(updatedTitle);
    }
    
}
