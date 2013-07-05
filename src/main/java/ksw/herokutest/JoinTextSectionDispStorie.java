package ksw.herokutest;

import ksw.kwutil.simpledb.ManyToManyJoin;
import ksw.kwutil.simpledb.SimpleDbObject;

public class JoinTextSectionDispStorie extends SimpleDbObject implements ManyToManyJoin
{
    private Integer _textSectionDispId;
    private Integer _storieId;

    public JoinTextSectionDispStorie()
    {
        
    }
    
    public JoinTextSectionDispStorie(Integer textSectionId, Integer storieId)
    {
        _textSectionDispId = textSectionId;
        _storieId = storieId;
    }

    public void setTextSectionId(Integer val)
    {
        _textSectionDispId = val;
    }
    
    public Integer getTextSectionId()
    {
        return _textSectionDispId;
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
        return _textSectionDispId;
    }

    @Override
    public Integer rightId()
    {
        return _storieId;
    }
    
    
}
