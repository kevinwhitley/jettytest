package ksw.herokutest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ksw.kwutil.JSONWriter;
import ksw.kwutil.simpledb.SimpleDb;

public class StorieRequestHandler extends SimpleDbRestHandler
{
    public StorieRequestHandler(SimpleDb db)
    {
        super(db, Storie.class);
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
        //return true; - works, but means we sideload for all requests
        // even when the sideloaded objects are already on client
    }

    @Override
    public Storie createFromClientJSON(Map<String, Object> data)
    {
        return null;
    }
    
    // currently not used because doesSideload is returning false
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
}
