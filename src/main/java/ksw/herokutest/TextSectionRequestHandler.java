package ksw.herokutest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ksw.kwutil.JSONWriter.JSONWriteException;
import ksw.kwutil.simpledb.JsonDb;

public class TextSectionRequestHandler extends RestHandler
{

    private JsonDb _db;
    
    public TextSectionRequestHandler(JsonDb db)
    {
        _db = db;
    }

    @Override
    public String getSingularName()
    {
        return "textsection";
    }

    @Override
    public TextSection createFromClientJSON(Map<String, Object> data)
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
        return (ClientJSON)_db.find(TextSection.class, docId);
    }
    
    @Override
    public List<ClientJSON> findAll()
    {
        List<ClientJSON> allTSections = (List<ClientJSON>)_db.findAll(TextSection.class);
        return allTSections;
    }

    @Override
    public List<ClientJSON> findByIds(Collection<Integer> ids)
    {
        List<ClientJSON> ts = (List<ClientJSON>)_db.findByIds(TextSection.class, ids);
        return ts;
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
}
