package org.hisp.dhis.coldchain.model.action;

import java.io.File;

import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.system.util.StreamUtils;

import com.opensymphony.xwork2.Action;

public class UploadModelTypeImageAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ModelTypeService modelTypeService;
    
    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private String fileName;

    private File upload;
    
    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }
    
   
    public void setUpload( File upload )
    {
        this.upload = upload;
    }
    
    private String message;

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }
    
    String imageName = "";
    
    private File outputFile;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        outputFile = null;
        
        ModelType modelType = modelTypeService.getModelType( id );
        
        String fileType = fileName.substring(fileName.indexOf( '.' )+1, fileName.length());
        
        if ( ! ( fileType.equalsIgnoreCase( "jpg" ) || fileType.equalsIgnoreCase( "png" ) || fileType.equalsIgnoreCase( "gif" ) ||  fileType.equalsIgnoreCase( "jpeg" ) ||  fileType.equalsIgnoreCase( "tiff" ) || fileType.equalsIgnoreCase( "bmp" ) ) )
            //if ( !fileType.equalsIgnoreCase( "jpg" ) || !fileType.equalsIgnoreCase( "png" ) || !fileType.equalsIgnoreCase( "gif" ) || !fileType.equalsIgnoreCase( "bmp" ) )
            {
                message = "The file you are trying to import is not an image file";
                
                return SUCCESS;
            }
        
        imageName = modelType.getName()+"."+ fileType;
        
        modelType.setModelTypeImage( imageName );    
        
        modelTypeService.updateModelType( modelType );
        

        String outputFilePath = System.getenv( "DHIS2_HOME" ) + File.separator +  Model.DEFAULT_CCEMFOLDER;
        
        File newdir = new File( outputFilePath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        
        outputFile = new File( newdir, imageName );
        try
        {        
            StreamUtils.write( upload, outputFile );
            
            message = "Image successfully uploaded";
        }
        
        catch( Exception e )
        {
           
            e.printStackTrace();
            message += "<br><font color=red><strong>Some problem occured while Importing the file : "+ fileName + "<br>Error Message: "+e.getMessage()+"</font></strong>";
            
        }         
        

        return SUCCESS;
    }
}

