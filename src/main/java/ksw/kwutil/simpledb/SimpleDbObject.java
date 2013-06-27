package ksw.kwutil.simpledb;

import ksw.kwutil.JSONReader.JSONReadableBySetters;
import ksw.kwutil.JSONWriter.JSONWriteableByGet;

public class SimpleDbObject implements JSONReadableBySetters, JSONWriteableByGet
{
    private SimpleDb _db;
    private Integer _id;
    private String _nonce;
    
    public SimpleDbObject()
    {
        
    }
    
    // set the database on this object
    // normally only called when creating or reconstituting the object (by the db)
    public void setDb(SimpleDb db)
    {
        _db = db;
    }
    
    public SimpleDb getDb()
    {
        return _db;
    }
    
    public void setId(Integer id)
    {
        _id = id;
    }
    
    public Integer getId()
    {
        return _id;
    }
    
    public void setNonce(String nc)
    {
        _nonce = nc;
    }
    
    public String getNonce()
    {
        return _nonce;
    }

    @Override
    public void postJSONRead()
    {
    }

    @Override
    public Object getWriteable()
    {
        return this;
    }

    @Override
    public String[] getFieldNames()
    {
        SimpleDbObjectClassMeta meta = _db.getMeta(this.getClass());
        return meta.getWriteableFieldNames();
    }
}
