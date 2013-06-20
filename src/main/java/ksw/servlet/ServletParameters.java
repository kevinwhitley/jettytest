/*
    Copyright (c) 1998-2003 Kevin S. Whitley
    All rights reserved.
*/

package ksw.servlet;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

/**
   Provides handling for servlet parameters, both for GET and POST
*/
public class ServletParameters
{
    private Hashtable m_values;
    private String m_action;

    //-------------------------------------------------------------
    /**
       Construct the parameter data, from the request
       @param req The HttpServletRequest being handled
    */
    public ServletParameters (HttpServletRequest req)
    {
        m_values = new Hashtable();
        m_action = "";
        Enumeration names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            if (name.startsWith(ServletHelp.ActionPrefix)) {
                // this was the name of the button that was pressed
                // not a regular parameter
                m_action = name.substring(ServletHelp.ActionPrefix.length());
                //System.out.println("Action is " + m_action);
            }
            else {
                // normal parameter
                String[] values = req.getParameterValues(name);
                //System.out.println("parameter " + name + " has value " + values[0]);
                m_values.put(name, values);
            }
        }
    }

    //-------------------------------------------------------------
    /**
       Get the name of the action.
       The action is roughly the name of the button that got us here
       @return the name of the action, or an empty string if no action
       was found.
    */
    public String getAction ()
    {
        return m_action;
    }

    //-------------------------------------------------------------
    /**
       Get the number of values we have for a parameter.
       Non-existent parameters have 0 values.  Some parameters will
       have repeated values, in which case the result is greater
       than 1.
       @param name The name of the parameter we're counting
       @return the number of values available
    */
    public int getValueCount (String name)
    {
        String[] argarray = (String[])m_values.get(name);
        return (argarray == null) ? 0 : argarray.length;
    }

    //-------------------------------------------------------------
    /**
       Get a value, as a string.
       This version of the routine will return the first value for the
       parameter name, if there are multiple values.  If the value is
       missing, it will return an empty string.
       @param name The name of the parameter we're fetching
       @return The parameter value, or an empty string
    */
    public String getValueString (String name)
    {
        return getValueString(name, 0, "");
    }

    //-------------------------------------------------------------
    /**
       Get a value, as a string.
       This version of the routine will return the first value for the
       parameter name, if there are multiple values.
       @param name The name of the parameter we're fetching
       @param defaultValue The value that is returned if the parameter
       for this index doesn't exist
       @return The parameter value, or defaultValue if it doesn't exist
    */
    public String getValueString (String name, String defaultValue)
    {
        return getValueString(name, 0, defaultValue);
    }

    //-------------------------------------------------------------
    /**
       Get a value, as a string.
       @param name The name of the parameter we're fetching
       @param index The index of the parameter value we want
       @param defaultValue The value that is returned if the parameter
       for this index doesn't exist
       @return The parameter value, or defaultValue if it doesn't exist
    */
    public String getValueString (String name, int index, String defaultValue)
    {
        String[] argarray = (String[])m_values.get(name);
        if (argarray == null || index < 0 || index >= argarray.length) {
            return defaultValue;
        }

        return argarray[index];
    }

    //-------------------------------------------------------------
    /**
       Get a value, as an integer.
       @param name The name of the parameter we're fetching
       @param index The index of the parameter value we want
       @param defaultValue The value that is returned if the parameter
       for this index doesn't exist
       @return The parameter value, or defaultValue if it doesn't exist
       or can't be parsed
    */
    public int getValueInt (String name, int index, int defaultValue)
    {
        int retval = 0;
        try
        {
            String sval = getValueString(name, index, null);
            if (sval == null) {
                return defaultValue;
            }
            retval = Integer.parseInt(sval);
        }
        catch(Exception exc)
        {
            retval = defaultValue;
        }
    
        return retval;
    }

}


