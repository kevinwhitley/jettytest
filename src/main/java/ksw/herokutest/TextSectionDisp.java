package ksw.herokutest;

import java.util.Map;

import ksw.kwutil.JSONWriter;
import ksw.kwutil.simpledb.SimpleDbObject;

public class TextSectionDisp extends SimpleDbObject implements RestHandler.ClientJSON
{
    private Storie _storie;  // owning storie
    private Integer _storieId;
    private int _left;
    private int _top;
    private int _width;
    private int _height;
    private boolean _open;
    private TextSection _textSection;
    private Integer _textSectionId;

    public TextSectionDisp()
    {
    }
    
    public TextSectionDisp(int left, int top, int width, int height, boolean open, TextSection ts)
    {
        _left = left;
        _top = top;
        _width = width;
        _height = height;
        _open = open;
        _textSection = ts;
        _textSectionId = ts.getId();
    }
    
    public void setLeft(int val)
    {
        _left = val;
    }
    
    public int getLeft()
    {
        return _left;
    }
    
    public void setTop(int val)
    {
        _top = val;
    }
    
    public int getTop()
    {
        return _top;
    }
    
    public void setWidth(int val)
    {
        _width = val;
    }
    
    public int getWidth()
    {
        return _width;
    }
    
    public void setHeight(int val)
    {
        _height = val;
    }
    
    public int getHeight()
    {
        return _height;
    }
    
    public void setOpen(boolean val)
    {
        _open = val;
    }
    
    public boolean getOpen()
    {
        return _open;
    }
    
    public void setStorie(Storie st)
    {
        _storie = st;
        _storieId = st.getId();
    }
    
    public Storie getStorie()
    {
        if (_storie == null) {
            _storie = (Storie)getDb().find(Storie.class, _storieId);
        }
        return _storie;
    }
    
    public void setStorieId(Integer val)
    {
        _storieId = val;
    }
    
    public Integer getStorieId()
    {
        return _storieId;
    }
    
    public void setTextSection(TextSection ts)
    {
        _textSectionId = ts.getId();
        _textSection = ts;
    }
    
    public TextSection getTextSection()
    {
        if (_textSection == null) {
            _textSection = (TextSection)getDb().find(TextSection.class, _textSectionId);
        }
        
        return _textSection;
    }
    
    public void setTextSectionId(Integer val)
    {
        _textSectionId = val;
    }

    public Integer getTextSectionId()
    {
        return _textSectionId;
    }
    
    @Override
    public void toClientJSON(JSONWriter jWriter)
    {
        jWriter.addItem("id", getId());
        jWriter.addItem("left", getLeft());
        jWriter.addItem("top", getTop());
        jWriter.addItem("width", getWidth());
        jWriter.addItem("height", getHeight());
        jWriter.addItem("open", getOpen());
        jWriter.addItem("storie_id", getStorieId());
        jWriter.addItem("textsection_id", getTextSectionId());
    }

    @Override
    public void updateFromClientJSON(Map<String, Object> data)
    {
        // we can update geometry, but not the id or textsection id
        setLeft((Integer)data.get("left"));
        setTop((Integer)data.get("top"));
        setWidth((Integer)data.get("width"));
        setHeight((Integer)data.get("height"));
        setOpen((Boolean)data.get("open"));
    }

}
