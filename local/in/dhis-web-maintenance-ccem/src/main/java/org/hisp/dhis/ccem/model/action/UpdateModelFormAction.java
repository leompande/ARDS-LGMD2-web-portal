package org.hisp.dhis.ccem.model.action;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelAttributeValueService;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.coldchain.model.comparator.ModelTypeAttributeOptionComparator;

import com.opensymphony.xwork2.Action;

public class UpdateModelFormAction
implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ModelService modelService;
    
    public void setModelService( ModelService modelService )
    {
        this.modelService = modelService;
    }
    
    private ModelTypeService modelTypeService;
    
    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }
    
    private ModelAttributeValueService modelAttributeValueService;
    
    public void setModelAttributeValueService( ModelAttributeValueService modelAttributeValueService )
    {
        this.modelAttributeValueService = modelAttributeValueService;
    }


    // -------------------------------------------------------------------------
    // Input/Output and Getter / Setter
    // -------------------------------------------------------------------------

    private int id;
    
    public void setId( int id )
    {
        this.id = id;
    }

    private Model model;
    

    public Model getModel()
    {
        return model;
    }
    
    private Map<Integer, String> modelTypeAttributeValueMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getModelTypeAttributeValueMap()
    {
        return modelTypeAttributeValueMap;
    }

    private List<ModelType> modelTypes;
    
    public List<ModelType> getModelTypes()
    {
        return modelTypes;
    }
    /*
    private Collection<ModelTypeAttribute> modelTypeAttributes;
    
    public Collection<ModelTypeAttribute> getModelTypeAttributes()
    {
        return modelTypeAttributes;
    }
    */
    
    private List<ModelTypeAttribute> modelTypeAttributes = new ArrayList<ModelTypeAttribute>();
    
    public List<ModelTypeAttribute> getModelTypeAttributes()
    {
        return modelTypeAttributes;
    }
    
    private String modelImage;
    
    
    private byte[] bimage;
    
    public byte[] getBimage()
    {
        return bimage;
    }
    
    private OutputStream outPutStream;
    
    public OutputStream getOutPutStream()
    {
        return outPutStream;
    }
    
    private BufferedImage bufferedImage;
    
    public BufferedImage getBufferedImage()
    {
        return bufferedImage;
    }
    
    private URL url;
    
    public URL getUrl()
    {
        return url;
    }


 
    public String getModelImage()
    {
        return modelImage;
    }
    
    private Map<Integer, List<ModelTypeAttributeOption>> modelTypeAttributesOptionsMap = new HashMap<Integer, List<ModelTypeAttributeOption>>();
    
    public Map<Integer, List<ModelTypeAttributeOption>> getModelTypeAttributesOptionsMap()
    {
        return modelTypeAttributesOptionsMap;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        
        model = modelService.getModel( id );
        
        modelTypes = new ArrayList<ModelType>( modelTypeService.getAllModelTypes());
        
        
        modelImage = model.getModelImage();
        
        //String outputFilePath = System.getenv( "DHIS2_HOME" ) + File.separator +  Model.DEFAULT_CCEMFOLDER + File.separator + modelImage;
        
        System.out.println( "Model Image Name is   :" + modelImage );
        
        modelImage = System.getenv( "DHIS2_HOME" ) + File.separator +  Model.DEFAULT_CCEMFOLDER + File.separator + modelImage;
        
        System.out.println( "Complete Path of Image  is   :" + modelImage );
        
        
        /*
        HttpServletResponse response = ServletActionContext.getResponse();
        response.reset();
        response.setContentType("multipart/form-data"); 

        
        bimage = model.getImage();

        OutputStream out = response.getOutputStream();
        out.write( bimage );
        out.flush();
        out.close();
        */
        
        /*
        bimage = model.getImage();
        System.out.println( " Image  is   :" + bimage );
        System.out.println( " lenght is     :" + bimage.length );
        System.out.println( " String is     :" + bimage.toString() );
        try
        {
            
            int len = bimage.toString().length();
            byte [] rb = new byte[len];
            //InputStream readImg = rs.getBinaryStream(1);
            //int index=readImg.read(rb, 0, len);  
            outPutStream.write( rb,0,len );
            
            
            
            //outPutStream
            //FileOutputStream fos = new FileOutputStream("modelImage"); 
           // outPutStream = new FileOutputStream( modelImage ); 
            //outPutStream.write( bimage );
            //outPutStream.close();
        }
        catch(Exception e)
        {
            //e.printStackTrace();
        }
        
        */
        
        
        
        /*
        String filePath = modelImage;
        File f1 = new File( filePath );

        ImageInputStream imgStream1 = ImageIO.createImageInputStream( f1 );
        long size = imgStream1.length();

        
        //bufferedImage = ImageIO.read( f1 );
        
        BufferedImage bufferedImage1 = ImageIO.read( f1 );
        //boolean success = ImageIO.write( bufferedImage1,"gif",socket.getOutputStream());
        
        
        try 
        {
            url = new URL( getCodeBase(), modelImage );
            
            bufferedImage = ImageIO.read( url );
         } 
        catch (IOException e) 
        {
            
        }
        
        System.out.println( "IMAGE  is   :" + bufferedImage );
        System.out.println( "URL is   :" + url );
        
       
        //outPutStream.w.write( bufferedImage1 );
        
        */
        
        
        // -------------------------------------------------------------------------
        // Get model attribute values
        // -------------------------------------------------------------------------

        Model tempModel = modelService.getModel( id );
        
        ModelType modelType = modelTypeService.getModelType( tempModel.getModelType().getId() );
        
        //modelTypeAttributes = modelType.getModelTypeAttributes();
        
        modelTypeAttributes = new ArrayList<ModelTypeAttribute> ( modelType.getModelTypeAttributes());
        //Collections.sort( modelTypeAttributes, new ModelTypeAttributeComparator() );
        
        List<ModelAttributeValue> modelAttributeValues = new ArrayList<ModelAttributeValue>( modelAttributeValueService.getAllModelAttributeValuesByModel( modelService.getModel( id )) );
        
        
        for( ModelAttributeValue modelAttributeValue : modelAttributeValues )
        {
            if ( ModelTypeAttribute.TYPE_COMBO.equalsIgnoreCase( modelAttributeValue.getModelTypeAttribute().getValueType() ) )
            {
                modelTypeAttributeValueMap.put( modelAttributeValue.getModelTypeAttribute().getId(), modelAttributeValue.getModelTypeAttributeOption().getName() );
            }
            
            else
            {
                modelTypeAttributeValueMap.put( modelAttributeValue.getModelTypeAttribute().getId(), modelAttributeValue.getValue() );
            }
        }
       /*
        System.out.println( "Size of model Data Values Map  :" + modelTypeAttributeValueMap.size() );
        for( ModelAttributeValue  tempmodelAttributeValue  : modelAttributeValues )
        {
            System.out.println( "Map value is ------- :" + modelTypeAttributeValueMap.get( tempmodelAttributeValue.getModelTypeAttribute().getId() ));
            
        }
        */
        for( ModelTypeAttribute modelTypeAttribute : modelTypeAttributes )
        {
            List<ModelTypeAttributeOption> modelTypeAttributesOptions = new ArrayList<ModelTypeAttributeOption>();
            if( ModelTypeAttribute.TYPE_COMBO.equalsIgnoreCase( modelTypeAttribute.getValueType() ) )
            {
                modelTypeAttributesOptions = new ArrayList<ModelTypeAttributeOption>( modelTypeAttribute.getAttributeOptions() );
                Collections.sort( modelTypeAttributesOptions, new ModelTypeAttributeOptionComparator() );
                modelTypeAttributesOptionsMap.put( modelTypeAttribute.getId(), modelTypeAttributesOptions );
            }
        }
        
        return SUCCESS;

    }
/*
    private URL getCodeBase()
    {
        //URL url = null;
        try 
        {
            url = new URL( "model.getModelImage()" );
        } 
        catch (IOException e) 
        {
            
        }
        System.out.println( "URL in method is   :" + url );
        return url;
    }
*/
}

