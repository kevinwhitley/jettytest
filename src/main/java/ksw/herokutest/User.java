package ksw.herokutest;

import ksw.kwutil.simpledb.SimpleDbObject;

public class User extends SimpleDbObject
{
    private String _name;
    private String _email;

    public User()
    {
        
    }
    
    public void setName(String val)
    {
        _name = val;
    }
    
    public String getName()
    {
        return _name;
    }
    
    public void setEmail(String val)
    {
        _email = val;
    }
    
    public String getEmail()
    {
        return _email;
    }
}
