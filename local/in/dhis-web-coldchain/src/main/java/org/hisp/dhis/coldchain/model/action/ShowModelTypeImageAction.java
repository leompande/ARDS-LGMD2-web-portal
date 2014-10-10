package org.hisp.dhis.coldchain.model.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeService;

import com.opensymphony.xwork2.Action;

public class ShowModelTypeImageAction implements Action
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
    // Input/Output and Getter / Setter
    // -------------------------------------------------------------------------

    private int id;
    
    public void setId( int id )
    {
        this.id = id;
    }
    
    
    private byte[] bimage;
    
    public byte[] getBimage()
    {
        return bimage;
    }
    
    String modelImage = null;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        
        ModelType modelType = modelTypeService.getModelType( id );
        
        if ( modelType.getModelTypeImage() != null )
        {
            modelImage = System.getenv( "DHIS2_HOME" ) + File.separator +  Model.DEFAULT_CCEMFOLDER + File.separator + modelType.getModelTypeImage();
        }
        
        else
        {
            
        }
       
        File file = new File( modelImage );
        bimage = new byte[(int) file.length()];
        
        try 
        {
            FileInputStream fileInputStream = new FileInputStream( file );
            //convert file into array of bytes
            fileInputStream.read( bimage );
            fileInputStream.close();
       } 
       catch (Exception e) 
       {
            //e.printStackTrace();
       }
        
       try 
       {
           HttpServletResponse response = ServletActionContext.getResponse();
           response.reset();
           response.setContentType("multipart/form-data"); 

           OutputStream out = response.getOutputStream();
           out.write( bimage );
           out.flush();
           out.close();
       }
        
       catch (IOException e) 
       {
           //e.printStackTrace();
       }
        
        return SUCCESS;
    }
    
}


