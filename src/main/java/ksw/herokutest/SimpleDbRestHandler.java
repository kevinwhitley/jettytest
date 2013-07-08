package ksw.herokutest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import ksw.kwutil.simpledb.SimpleDb;

public abstract class SimpleDbRestHandler extends RestHandler
{
    private SimpleDb _db;
    private Class _theClass;
    
    public SimpleDbRestHandler(SimpleDb db, Class theClass)
    {
        _db = db;
        _theClass = theClass;
    }
    
    
    @Override
    public void delete(int docId)
    {
        _db.delete(_theClass, docId);
    }
    
    @Override
    public ClientJSON find(int docId)
    {
        ClientJSON obj = (ClientJSON)_db.find(_theClass, docId);
        return obj;
    }
    
    @Override
    public List<ClientJSON> findAll()
    {
        List<ClientJSON> objs = (List<ClientJSON>)_db.findAll(_theClass);
        return objs;
    }

    @Override
    public List<ClientJSON> findByIds(Collection<Integer> ids)
    {
        List<ClientJSON> objs = (List<ClientJSON>)_db.findByIds(_theClass, ids);
        return objs;
    }
    
    @Override
    public void commitEdit()
    {
        try {
            _db.persistToStorage();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
