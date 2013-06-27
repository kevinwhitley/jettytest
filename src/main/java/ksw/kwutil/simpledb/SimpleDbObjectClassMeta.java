package ksw.kwutil.simpledb;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ksw.kwutil.JSONWriter.JSONWriteableByGet;

public class SimpleDbObjectClassMeta
{
    private Class _theClass;
    
    // the list of field names that are not references to other objects in the db
    private String[] _writeableFieldNames;
    
    public SimpleDbObjectClassMeta(Class theClass)
    {
        _theClass = theClass;
    }
    
    public String[] getWriteableFieldNames()
    {
        if (_writeableFieldNames == null) {
            // we'll make all the fields xxx writable that have a getter getXxx but that
            // do not have a getter getXxxId (so that we only persist cross references as ids)
            Method[] methods = _theClass.getMethods();
            // stash all the getter method names in a Set for easy lookup
            Set methodNames = new HashSet<String>(methods.length);
            for (Method method : methods) {
                String methodName = method.getName();
                if (!methodName.startsWith("get")) {
                    continue;
                }
                Class returnType = method.getReturnType();
                if (returnType.isPrimitive() || returnType == String.class || returnType == Boolean.class ||
                        returnType == Integer.class || returnType == Long.class || JSONWriteableByGet.class.isAssignableFrom(returnType)) {
                    methodNames.add(methodName);
                }
            }
            
            List<String> fieldNames = new ArrayList<String>(methodNames.size());
            Iterator<String> namesIt = methodNames.iterator();
            while(namesIt.hasNext()) {
                String methodName = namesIt.next();
                if (methodNames.contains(methodName+"Id")) {
                    continue;  // skip this cross reference
                }
                fieldNames.add(Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4));
            }
            
            Collections.sort(fieldNames);
            
            _writeableFieldNames = new String[fieldNames.size()];
            fieldNames.toArray(_writeableFieldNames);
            
            /*
            System.out.println("For class " + _theClass.getCanonicalName() + " the writeable fields are");
            for (String fn : _writeableFieldNames) {
                System.out.println(fn);
            }
            System.out.println();
            */
        }
        
        return _writeableFieldNames;
    }
}
