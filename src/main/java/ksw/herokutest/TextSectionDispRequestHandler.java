package ksw.herokutest;

import java.util.Map;

import ksw.kwutil.simpledb.SimpleDb;

public class TextSectionDispRequestHandler extends SimpleDbRestHandler
{

    public TextSectionDispRequestHandler(SimpleDb db)
    {
        super(db, TextSectionDisp.class);
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
}
