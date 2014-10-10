package org.hisp.dhis.excelimport.api;

/**
 * Report is to store/capture the information regarding one Report which will
 * available from xml file
 */

public class Report
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
     * type is whether it is monthly report, yearly, daily etc
     */
    private String type;

    /**
     * model is whether this report is static or dynamic
     */
    private String model;

    /**
     * fileName is the Template File Name for this report
     */
    private String fileName;

    /**
     * checkerFileName represents the xml file we use to verify the authenticity of the excel file to import
     */
    private String chckerFileName;

    /**
     * dataset id
     */
    private String datasetId;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Report()
    {

    }

    public Report( String id, String name, String type, String model, String fileName, String datasetId )
    {
        this.id = id;
        this.name = name;
        this.type = type;
        this.model = model;
        this.fileName = fileName;
        //this.chckerFileName = checkerFileName;
        this.datasetId = datasetId;
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

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel( String model )
    {
        this.model = model;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName( String fileName )
    {
        this.fileName = fileName;
    }

    public String getCheckerFileName()
    {
        return chckerFileName;
    }

    public void setCheckerFileName( String chckerFileName )
    {
        this.chckerFileName = chckerFileName;
    }

    public String getDatasetId()
    {
        return datasetId;
    }

    public void setDatasetId( String datasetId )
    {
        this.datasetId = datasetId;
    }
}
