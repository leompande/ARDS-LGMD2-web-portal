package org.hisp.dhis.mobile.sms.utils;

import java.io.File;
import java.io.FilenameFilter;

public class XMLFilter implements FilenameFilter
{
    public boolean accept(File dir, String name) 
    {
        return ( name.endsWith(".xml") );
    }
}
