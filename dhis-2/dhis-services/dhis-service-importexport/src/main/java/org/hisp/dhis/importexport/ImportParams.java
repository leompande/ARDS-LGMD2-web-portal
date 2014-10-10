package org.hisp.dhis.importexport;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Date;

/**
 * @author Lars Helge Overland
 * @version $Id: ImportParams.java 6425 2008-11-22 00:08:57Z larshelg $
 */
public class ImportParams
{
    public static final String ATTRIBUTE_NAMESPACE = "xmlns";
    
    private ImportType type;
    
    private boolean extendedMode;

    private ImportStrategy importStrategy;
    
    private boolean dataValues;
    
    private boolean skipCheckMatching;
    
    private Date lastUpdated;
    
    private String namespace;
    
    private String minorVersion;
    
    private String owner;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ImportParams()
    {
    }

    public ImportParams(ImportType type, ImportStrategy importStrategy, boolean dataValues)
    {
        this.type = type;
        this.importStrategy = importStrategy;
        this.dataValues = dataValues;

    }

    //Constructor used for DHIS 1.4 imports
    public ImportParams( ImportType type, ImportStrategy importStrategy, boolean dataValues, String owner )
    {
        this.type = type;
        this.importStrategy = importStrategy;
        this.dataValues = dataValues;
        this.owner = owner;
    }
    
    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean minorVersionGreaterOrEqual( String requiredVersion )
    {
        if ( requiredVersion == null || minorVersion == null )
        {
            return false;
        }
        
        double _minorVersion = Double.parseDouble( minorVersion ) * 1000;
        double _requiredVersion = Double.parseDouble( requiredVersion ) * 1000;
        
        return (int)_minorVersion >= (int)_requiredVersion;
    }
    
    public boolean isImport()
    {
        return type.equals( ImportType.IMPORT );
    }
    
    public boolean isPreview()
    {
        return type.equals( ImportType.PREVIEW );
    }
    
    public boolean isAnalysis()
    {
        return type.equals( ImportType.ANALYSIS );
    }
    
    public boolean skipMapping()
    {
        return !type.equals( ImportType.IMPORT );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public ImportType getType()
    {
        return type;
    }

    public void setType( ImportType type )
    {
        this.type = type;
    }
    
    public boolean isExtendedMode()
    {
        return extendedMode;
    }

    public void setExtendedMode( boolean extendedMode )
    {
        this.extendedMode = extendedMode;
    }

    public ImportStrategy getImportStrategy()
    {
        return importStrategy;
    }

    public void setImportStrategy( ImportStrategy importStrategy )
    {
        this.importStrategy = importStrategy;
    }

    public boolean isDataValues()
    {
        return dataValues;
    }

    public void setDataValues( boolean dataValues )
    {
        this.dataValues = dataValues;
    }

    public boolean isSkipCheckMatching()
    {
        return skipCheckMatching;
    }

    public void setSkipCheckMatching( boolean checkMatching )
    {
        this.skipCheckMatching = checkMatching;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( Date lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }

    public String getNamespace()
    {
        return namespace;
    }

    public void setNamespace( String namespace )
    {
        this.namespace = namespace;
    }

    public String getMinorVersion()
    {
        return minorVersion;
    }

    public void setMinorVersion( String minorVersion )
    {
        this.minorVersion = minorVersion;
    }
    
    public  void setOwner(String owner)
    {
        this.owner = owner;
    }
    
    public String getOwner()
    {
        return owner;
    }
}
