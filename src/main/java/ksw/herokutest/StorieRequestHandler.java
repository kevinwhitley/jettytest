package ksw.herokutest;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ksw.kwutil.JSONWriter;
import ksw.kwutil.JSONWriter.JSONWriteException;
import ksw.kwutil.simpledb.JsonDb;

public class StorieRequestHandler extends RestHandler
{
    private JsonDb _db;
    
    public StorieRequestHandler(JsonDb db)
    {
        _db = db;
    }

    @Override
    public String getSingularName()
    {
        return "storie";
    }

    @Override
    public String getPluralName()
    {
        return "storiez";
    }

    @Override
    public boolean doesSideload()
    {
        return false;
    }

    @Override
    public Storie createFromClientJSON(Map<String, Object> data)
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
        // plain /storiez should return all the storiez
        // /storiez/3 (pathInfo is /3) should return just the storie with id 3
        // in both cases, also return all the text sections
        
        /*
        System.out.println("contextPath: " + request.getContextPath());
        System.out.println("method: " + request.getMethod());
        System.out.println("pathInfo: " + request.getPathInfo());
        System.out.println("pathTranslated: " + request.getPathTranslated());
        System.out.println("queryString: " + request.getQueryString());
        System.out.println("requestURI: " + request.getRequestURI());
        System.out.println("servletPath: " + request.getServletPath());
        */
        Storie singleStorie = (Storie)_db.find(Storie.class, docId);
        return singleStorie;
    }
    
    @Override
    public List<ClientJSON> findAll()
    {
        List<ClientJSON> allStories = (List<ClientJSON>)_db.findAll(Storie.class);
        return allStories;
    }

    @Override
    public List<ClientJSON> findByIds(Collection<Integer> ids)
    {
        List<ClientJSON> stories = (List<ClientJSON>)_db.findByIds(Storie.class, ids);
        return stories;
    }

    @Override
    public void sideload(JSONWriter jWriter, List<ClientJSON> docs)
    {
        // get all the text sections (some sections may be used by multiple docs)
        Set<TextSection> sections = new HashSet<TextSection>(100);
        Set<TextSectionDisp> sectionViews = new HashSet<TextSectionDisp>(100);
        for (ClientJSON cd : docs) {
            Storie st = (Storie)cd;
            for (TextSectionDisp ts : st.getTextSectionDisps()) {
                sectionViews.add(ts);
                sections.add(ts.getTextSection());
            }
        }
        
        // now write out all those textsections and views
        jWriter.addArrayToObject("textsectiondisps");
        Iterator<TextSectionDisp> viewsI = sectionViews.iterator();
        while (viewsI.hasNext()) {
            TextSectionDisp tv = viewsI.next();
            jWriter.startObject();
            tv.toClientJSON(jWriter);
            jWriter.endObject();
        }
        jWriter.endArray();
        
        jWriter.addArrayToObject("textsections");
        Iterator<TextSection> sectionI = sections.iterator();
        while (sectionI.hasNext()) {
            TextSection ts = sectionI.next();
            jWriter.startObject();
            ts.toClientJSON(jWriter);
            jWriter.endObject();
        }
        jWriter.endArray();
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
