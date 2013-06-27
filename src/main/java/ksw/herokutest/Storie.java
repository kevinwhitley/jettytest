package ksw.herokutest;

import java.util.ArrayList;
import java.util.List;

import ksw.kwutil.simpledb.SimpleDbObject;

public class Storie extends SimpleDbObject
{

    private String _title;
    private Integer _ownerId;
    private User _owner;
    private List<TextSection> _textSections;

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

    public void addTextSection(TextSection ts)
    {
        // create a join entry
        JoinTextSectionStorie join = new JoinTextSectionStorie(ts.getId(), getId());
        getDb().save(join);
        
        // if we've already instantiated the text sections, add this one
        if (_textSections != null) {
            _textSections.add(ts);
        }
    }
    
    public List<TextSection> getTextSections()
    {
        if (_textSections == null) {
            _textSections = getDb().findManyToManyFromRight(getId(), TextSection.class, JoinTextSectionStorie.class);
        }
        
        // return a copy of the array
        List<TextSection> result = new ArrayList<TextSection>(_textSections.size());
        result.addAll(_textSections);
        return result;
    }
}
