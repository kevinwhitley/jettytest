package ksw.herokutest;

import ksw.kwutil.simpledb.ManyToManyJoin;
import ksw.kwutil.simpledb.SimpleDbObject;

public class JoinTextSectionStorie extends SimpleDbObject implements ManyToManyJoin
{
    private Integer _textSectionId;
    private Integer _storieId;

    public JoinTextSectionStorie()
    {
        
    }
    
    public JoinTextSectionStorie(Integer textSectionId, Integer storieId)
    {
        _textSectionId = textSectionId;
        _storieId = storieId;
    }

    public void setTextSectionId(Integer val)
    {
        _textSectionId = val;
    }
    
    public Integer getTextSectionId()
    {
        return _textSectionId;
    }

    public void setStorieId(Integer val)
    {
        _storieId = val;
    }
    
    public Integer getStorieId()
    {
        return _storieId;
    }

    @Override
    public Integer leftId()
    {
        return _textSectionId;
    }

    @Override
    public Integer rightId()
    {
        return _storieId;
    }
    
    
}
