package org.hisp.dhis.coldchain.model.action;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOptionService;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;

import com.opensymphony.xwork2.Action;

public class UpdateModelTypeAttributeAction implements Action
{
    public static final String PREFIX_ATTRIBUTE_OPTION = "attrOption";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ModelTypeAttributeService modelTypeAttributeService;

    public void setModelTypeAttributeService( ModelTypeAttributeService modelTypeAttributeService )
    {
        this.modelTypeAttributeService = modelTypeAttributeService;
    }
    
    private ModelTypeAttributeOptionService modelTypeAttributeOptionService;
    
    public void setModelTypeAttributeOptionService( ModelTypeAttributeOptionService modelTypeAttributeOptionService )
    {
        this.modelTypeAttributeOptionService = modelTypeAttributeOptionService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private String valueType;

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    private boolean mandatory;

    public void setMandatory( boolean mandatory )
    {
        this.mandatory = mandatory;
    }



    private List<String> attrOptions;

    public void setAttrOptions( List<String> attrOptions )
    {
        this.attrOptions = attrOptions;
    }

    private Integer noChars;

    public void setNoChars( Integer noChars )
    {
        this.noChars = noChars;
    }
    
    private boolean display = false;
    
    public void setDisplay( boolean display )
    {
        this.display = display;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        ModelTypeAttribute modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( id );

        modelTypeAttribute.setName( name );
        modelTypeAttribute.setDescription( description );
        modelTypeAttribute.setValueType( valueType );
        modelTypeAttribute.setMandatory( mandatory );
       
        modelTypeAttribute.setNoChars( noChars );
        modelTypeAttribute.setDisplay( display );
        
       
        HttpServletRequest request = ServletActionContext.getRequest();
        
        Collection<ModelTypeAttributeOption> attributeOptions = modelTypeAttributeOptionService.getModelTypeAttributeOptions( modelTypeAttribute );

        if ( attributeOptions != null && attributeOptions.size() > 0 )
        {
            String value = null;
            for ( ModelTypeAttributeOption option : attributeOptions )
            {
                value = request.getParameter( PREFIX_ATTRIBUTE_OPTION + option.getId() );
                if ( StringUtils.isNotBlank( value ) )
                {
                    option.setName( value.trim() );
                    
                    modelTypeAttributeOptionService.updateModelTypeAttributeOption( option );
                }
            }
        }

        if ( attrOptions != null )
        {
            ModelTypeAttributeOption option = null;
            for ( String optionName : attrOptions )
            {
                
                option = modelTypeAttributeOptionService.getModelTypeAttributeOptionName( modelTypeAttribute, optionName );
                if ( option == null )
                {
                    option = new ModelTypeAttributeOption();
                    option.setName( optionName );
                    option.setModelTypeAttribute( modelTypeAttribute );
                    
                    modelTypeAttributeOptionService.addModelTypeAttributeOption( option );
                }
            }
        }
        
        modelTypeAttributeService.updateModelTypeAttribute( modelTypeAttribute );
    
        return SUCCESS;
    }
}

