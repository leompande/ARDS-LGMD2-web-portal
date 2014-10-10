package org.hisp.dhis.reports.util;


public class ProgramDetail
{
    /**
     * Unique Id
     */
    private String id;

    /**
     * Report Name
     */
    private String name;
    
    /**
     * excelTemplateName is the xls Template File Name for this report
     */
    private String excelTemplateName;
    
    /**
     * xmlTemplateName is the xml Template File Name for this report
     */
    private String xmlTemplateName;
    
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramDetail()
    {

    }

    public ProgramDetail( String id, String name )
    {
        this.id = id;
        this.name = name;
    }
    
    public ProgramDetail( String id, String name, String excelTemplateName, String xmlTemplateName )
    {
        this.id = id;
        this.name = name;
        this.excelTemplateName = excelTemplateName;
        this.xmlTemplateName = xmlTemplateName;
    }

    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof ProgramDetail ) )
        {
            return false;
        }

        final ProgramDetail other = ( ProgramDetail ) o;

        return name.equals( other.getName() );
    }

    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getExcelTemplateName()
    {
        return excelTemplateName;
    }

    public void setExcelTemplateName( String excelTemplateName )
    {
        this.excelTemplateName = excelTemplateName;
    }

    public String getXmlTemplateName()
    {
        return xmlTemplateName;
    }

    public void setXmlTemplateName( String xmlTemplateName )
    {
        this.xmlTemplateName = xmlTemplateName;
    }
    
}
