package ksw.herokutest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ksw.kwutil.JSONWriter.JSONWriteException;
import ksw.kwutil.simpledb.JsonDb;

public class TextSectionDispRequestHandler extends RestHandler
{

    private JsonDb _db;
    
    public TextSectionDispRequestHandler(JsonDb db)
    {
        _db = db;
    }

    
    @Override
    public void commitEdit()
    {
        try {
            _db.write();
        } catch (JSONWriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getSingularName()
    {
        return "textsectiondisp";
    }

    @Override
    public ClientJSON createFromClientJSON(Map<String, Object> data)
    {
        return null;
    }

    @Override
    public void delete(int docId)
    {
    }

    @Override
    public ClientJSON find(int docId)
    {
        return (ClientJSON)_db.find(TextSectionDisp.class, docId);
    }

    @Override
    public List<ClientJSON> findAll()
    {
        return (List<ClientJSON>)_db.findAll(TextSectionDisp.class);
    }

    @Override
    public List<ClientJSON> findByIds(Collection<Integer> ids)
    {
        List<ClientJSON> ts = (List<ClientJSON>)_db.findByIds(TextSectionDisp.class, ids);
        return ts;
    }

}