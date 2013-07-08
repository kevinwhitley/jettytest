package ksw.herokutest;

import java.util.Map;

import ksw.kwutil.simpledb.SimpleDb;

public class TextSectionRequestHandler extends SimpleDbRestHandler
{
    public TextSectionRequestHandler(SimpleDb db)
    {
        super(db, TextSection.class);
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
    
}
