package ksw.kwutil.simpledb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ksw.kwutil.FileUtil;
import ksw.kwutil.JSONReader;
import ksw.kwutil.StringUtil;
import ksw.kwutil.JSONReader.JSONParseException;
import ksw.kwutil.JSONWriter.JSONWriteException;
import ksw.kwutil.JSONWriter;

// given a directory, read/write "tables" of classes into json files in that directory
// the object instances are all kept in memory, in maps by a key field
public class JsonDb extends SimpleDb
{
    private File _dataFile;
    private Map<Class, Map<Integer, SimpleDbObject>> _data;  // key: class, value: objects for the class
    
    private int _currentNonce;
    private int _currentId;
    
    private static final Integer CurrentVersion = 1;
    private static final String VersionKey = "version";
    private static final String NonceKey = "nonce";
    private static final String IdKey = "id";
    private static final String CollectionsKey = "collections";
    private static final String ClassnameKey = "classname";
    private static final String CountKey = "count";
    private static final String ObjectsKey = "objects";
    
    public JsonDb()
    {
    }
    
    @Override
    public void createDb()
    {
        _dataFile = null;
        _currentNonce = 0;
        _currentId = 0;
        _data = new HashMap<Class, Map<Integer, SimpleDbObject>>(20);
    }
    
    @Override
    public void addCollection(Class theClass)
    {
        if (getMeta(theClass) == null) {
            addMetaForClass(theClass);
            Map<Integer, SimpleDbObject>records = new HashMap<Integer, SimpleDbObject>(30);
            _data.put(theClass, records);            
        }
    }
    
    @Override
    public SimpleDbObject find(Class theClass, Integer id)
    {
        Map<Integer, SimpleDbObject> collection = _data.get(theClass);
        return collection.get(id);
    }
    
    @Override
    public List<SimpleDbObject> findAll(Class theClass)
    {
        Map<Integer, SimpleDbObject> collection = _data.get(theClass);
        List<SimpleDbObject> result = new ArrayList<SimpleDbObject>(collection.size());
        result.addAll(collection.values());
        
        return result;
    }

    // expensive reflection-based (full table scan) implementation of a search
    @Override
    public List<SimpleDbObject> findWhere(Class theClass, String fieldName, Object value)
    {
        Map<Integer, SimpleDbObject> collection = _data.get(theClass);
        List<SimpleDbObject> result = new LinkedList<SimpleDbObject>();
        
        Method getter;
        try {
            getter = theClass.getMethod(StringUtil.upperFirst("get", fieldName));
        }
        catch (NoSuchMethodException exc) {
            getter = null;
        }
        catch(SecurityException exc) {
            getter = null;
        }
        if (getter == null) {
            System.err.println("findWhere using invalid field " + fieldName + " for class " + theClass.getCanonicalName());
            return null;
        }
        if (getter.getReturnType() != value.getClass()) {
            System.err.println("findWhere trying to mismatch types");
            return null;
        }
        
        for (SimpleDbObject obj : collection.values()) {
            try {
                if (value.equals(getter.invoke(obj))) {
                    result.add(obj);
                }
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        return result;
    }

    @Override
    public List findManyToManyFromLeft(Integer leftId, Class rightClass, Class manyToManyClass)
    {
        return findManyToMany(leftId, rightClass, manyToManyClass, true);
    }
    
    @Override
    public List findManyToManyFromRight(Integer rightId, Class leftClass, Class manyToManyClass)
    {
        return findManyToMany(rightId, leftClass, manyToManyClass, false);
    }
    
    private List findManyToMany(Integer rootId, Class<SimpleDbObject> targetClass, Class<ManyToManyJoin> manyToManyClass, boolean isFromLeft)
    {
        // build a set of all otherside ids that are linked by the join
        Set<Integer> hitIds = new HashSet<Integer>(100);
        Map<Integer, SimpleDbObject> collection = _data.get(manyToManyClass);
        for (SimpleDbObject joinObj : collection.values()) {
            ManyToManyJoin join = (ManyToManyJoin)joinObj;
            if (isFromLeft) {
                if (join.leftId() == rootId) {
                    hitIds.add(join.rightId());
                }
            }
            else {
                if (join.rightId() == rootId) {
                    hitIds.add(join.leftId());
                }
            }
        }
        
        // now get all instances of right class that have one of those ids
        List<SimpleDbObject> result = new LinkedList<SimpleDbObject>();
        collection = _data.get(targetClass);
        for (SimpleDbObject obj : collection.values()) {
            if (hitIds.contains(obj.getId())) {
                result.add(obj);
            }
        }
        
        return result;
    }

    // save an object into the database
    // this does not guarantee persistence!  a call to write() is needed
    public void save(SimpleDbObject obj)
    {
        // assign an id
        obj.setId(++_currentId);
        // and a nonce
        obj.setNonce(Integer.toString(++_currentNonce));
        // hook to db
        obj.setDb(this);
        
        // and actually put into the data structure
        Map<Integer, SimpleDbObject> collection = _data.get(obj.getClass());
        if (collection == null) {
            System.err.println("no collection for class " + obj.getClass().getCanonicalName());
        }
        collection.put(obj.getId(), obj);
    }

    // write the database out to the same file it was read from
    public void write() throws JSONWriteException, IOException
    {
        write(null);
    }
    
    // write the database out to a file (if datafile is null, use the read-from file)
    public void write(File datafile) throws JSONWriteException, IOException
    {
        // create the JSON data structure
        JSONWriter jwriter = new JSONWriter();
        jwriter.startObject();
        jwriter.addItem(VersionKey, CurrentVersion);
        jwriter.addItem(NonceKey, _currentNonce);
        jwriter.addItem(IdKey, _currentId);
        jwriter.addArrayToObject(CollectionsKey);
        
        for (Class collectionClass : _data.keySet()) {
            jwriter.startObject();
            jwriter.addItem(ClassnameKey, collectionClass.getCanonicalName());
            Map<Integer, SimpleDbObject> objects = _data.get(collectionClass);            
            jwriter.addItem(CountKey, objects.size());
            jwriter.addArrayToObject(ObjectsKey);
            for (SimpleDbObject object : objects.values()) {
                jwriter.addItem(null, object);
            }
            jwriter.endArray();
            jwriter.endObject();
        }
        
        jwriter.endArray();
        jwriter.endObject();
        
        // now write that to file
        File outFile = (datafile != null) ? datafile : _dataFile;
        FileWriter fwriter = new FileWriter(outFile);
        fwriter.write(jwriter.toString());
        fwriter.close();
    }
    
    public Object getNextNonce()
    {
        return Integer.valueOf(++_currentNonce);
    }

    // read all the data
    public void read(File dataFile) throws IOException, JSONParseException
    {
        _dataFile = dataFile;
        _data = new HashMap<Class, Map<Integer, SimpleDbObject>>(20);
        _currentNonce = 0;
        _currentId = 0;

        // read in the text of the file and parse
        String dataS = FileUtil.fileAsString(_dataFile);
        if (dataS == null) {
            throw new JSONParseException("file not found", null);
        }
        
        JSONReader jreader = new JSONReader();
        Map<String, Object> parsed = (Map<String, Object>)jreader.parse(dataS);

        // at the top level we expect to see the current nonce, a version mark,
        // and an array of class storages
        Integer version = (Integer)parsed.get(VersionKey);
        if (!CurrentVersion.equals(version)) {
            throw new JSONParseException("bad version", null);
        }
        Integer nonce = (Integer)parsed.get(NonceKey);
        if (nonce == null) {
            throw new JSONParseException("missing nonce", null);
        }
        _currentNonce = nonce;
        Integer id = (Integer)parsed.get(IdKey);
        if (id == null) {
            throw new JSONParseException("missing id", null);
        }
        _currentId = id;
        
        List<Map<String, Object>> collections = (List<Map<String, Object>>)parsed.get(CollectionsKey);
        if (collections == null) {
            throw new JSONParseException("missing collections", null);
        }
        
        for (Map<String, Object> collection : collections) {
            readCollection(jreader, collection);
        }
    }
    
    private void readCollection(JSONReader jreader, Map<String, Object> collection) throws IOException, JSONParseException
    {
        Class theClass = null;
        ClassNotFoundException cnfe = null;
        String className = (String)collection.get(ClassnameKey);
        if (className != null) {
            try {
                theClass = Class.forName(className);
            }
            catch (ClassNotFoundException exc) {
                cnfe = exc;
            }
        }
        if (theClass == null) {
            throw new JSONParseException("missing class", cnfe);
        }
        if (!SimpleDbObject.class.isAssignableFrom(theClass)) {
            throw new JSONParseException("class " + className + " is not a SimpleDbObject", null);
        }
        
        addMetaForClass(theClass);
        
        // set up the storage for all the objects
        Integer count = (Integer)collection.get(CountKey);
        if (count == null) {
            // missing count is ok
            count = 100;
        }
        Map<Integer, SimpleDbObject>records = new HashMap<Integer, SimpleDbObject>(count);
        _data.put(theClass, records);
        
        // parse and save all the objects
        List<Map<String, Object>> objects = (List<Map<String, Object>>)collection.get(ObjectsKey);
        if (objects == null) {
            throw new JSONParseException("missing objects for " + className, null);
        }
        
        for (Map<String, Object> object : objects) {
            SimpleDbObject record = (SimpleDbObject)jreader.buildObjectUsingSetters(theClass, object);
            record.setDb(this);
            records.put(record.getId(), record);
        }
    }
    
}
