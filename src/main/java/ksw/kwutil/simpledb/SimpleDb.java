package ksw.kwutil.simpledb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// a SimpleDb provides storage of objects that are only base types, and id references to other objects,
// (and lists, and subobjects of simple objects) - like mongodb storage
// it provides methods for basic query and update

public abstract class SimpleDb
{
    private Map<Class, SimpleDbObjectClassMeta> _classMetas;
    
    public SimpleDb()
    {
        _classMetas = new HashMap<Class, SimpleDbObjectClassMeta>(20);
    }

    public void addMetaForClass(Class theClass)
    {
        SimpleDbObjectClassMeta meta = new SimpleDbObjectClassMeta(theClass);
        _classMetas.put(theClass, meta);
    }
    
    public SimpleDbObjectClassMeta getMeta(Class theClass)
    {
        return _classMetas.get(theClass);
    }

    public abstract void createDb();
    public abstract void addCollection(Class theClass);
    public abstract void save(SimpleDbObject obj);
    
    public abstract List<SimpleDbObject> findAll(Class theClass);
    public abstract SimpleDbObject find(Class theClass, Integer id);
    public abstract List<SimpleDbObject> findWhere(Class theClass, String fieldName, Object value);
    public abstract List findManyToManyFromLeft(Integer leftId, Class rightClass, Class manyToManyClass);
    public abstract List findManyToManyFromRight(Integer rightId, Class leftClass, Class manyToManyClass);

    public abstract Object getNextNonce();
}
