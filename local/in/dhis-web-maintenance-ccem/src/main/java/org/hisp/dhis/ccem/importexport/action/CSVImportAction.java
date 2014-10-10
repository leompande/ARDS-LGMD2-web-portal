package org.hisp.dhis.ccem.importexport.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.jdbc.core.JdbcTemplate;

import com.csvreader.CsvReader;
import com.opensymphony.xwork2.Action;

public class CSVImportAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
     
    private ModelTypeAttributeService modelTypeAttributeService;
    
    public void setModelTypeAttributeService( ModelTypeAttributeService modelTypeAttributeService )
    {
        this.modelTypeAttributeService = modelTypeAttributeService;
    }

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
    
    private EquipmentTypeService equipmentTypeService;
    
    public void setEquipmentTypeService( EquipmentTypeService equipmentTypeService )
    {
        this.equipmentTypeService = equipmentTypeService;
    }

    private EquipmentService equipmentService;

    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }

    private EquipmentTypeAttributeService equipmentTypeAttributeService;
    
    public void setEquipmentTypeAttributeService( EquipmentTypeAttributeService equipmentTypeAttributeService) 
    {
        this.equipmentTypeAttributeService = equipmentTypeAttributeService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    private File upload;

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    private String fileName;

    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    private String fileFormat;

    public void setFileFormat( String fileFormat )
    {
        this.fileFormat = fileFormat;
    }

    private String message = "";

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        message += "<br><font color=blue>Importing StartTime : " + new Date() + "  - By " + currentUserService.getCurrentUsername() + "</font><br>";

        try
        {
            ZipInputStream zis = new ZipInputStream( new FileInputStream( upload ) );
            
            String uncompressedFolderPath = unZip( zis  );
            
            Map<String, List<String>> lookupDataMap = getLookupData( uncompressedFolderPath );
            
            Map<Integer, List<String>> assetMap = getAssetData( uncompressedFolderPath );
            
            /**
             * TODO - Need to use parameter / constant for timebeing directly used name
             */
            ModelType refrigeratorModel = modelTypeService.getModelTypeByName( "Refrigerator Catalog" );
            
            EquipmentType refrigeratorEquipment = equipmentTypeService.getEquipmentTypeByName( "Refrigerators" );
            
            EquipmentType coldroomsEquipment = equipmentTypeService.getEquipmentTypeByName( "Coldrooms" );
            
            Map<String, OrganisationUnit> orgUnitMap = importAdminHierarchy( uncompressedFolderPath );
            
            Map<String, OrganisationUnit> faclityMap = importFacility( uncompressedFolderPath, orgUnitMap, lookupDataMap );
            
            Map<String, Model> refrigeratorModelMap = importRefrigeratorCatalogData( uncompressedFolderPath, refrigeratorModel, lookupDataMap );
            
            importRefrigetaorDetails( uncompressedFolderPath, refrigeratorEquipment, assetMap, faclityMap, refrigeratorModelMap, lookupDataMap );
            
            importColdroomsDetails( uncompressedFolderPath, coldroomsEquipment, assetMap, faclityMap, lookupDataMap );
            
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            message += "<br><font color=red><strong>Please check the file format : " + fileName + "<br>Detailed Log Message: " + e.getMessage() + "</font></strong>";
        }

        message += "<br><br><font color=blue>Importing EndTime : " + new Date() + "  - By " + currentUserService.getCurrentUsername() + "</font>";

        return SUCCESS;
    }
    
    
    public Map<String, OrganisationUnit> importFacility( String facilityCSVFilePath, Map<String, OrganisationUnit> orgUnitMap, Map<String, List<String>> lookupDataMap )
    {
        facilityCSVFilePath += File.separator + "Facility.csv";
        
        message += "<br><font color=black>Importing started for Facility.csv " + new Date() + " </font><br>";
        
        // Creating missing Orgunitgroups from lookup data
        List<String> facilityTypes = lookupDataMap.get( "FacilityType" );
        //facilityTypes.addAll( lookupDataMap.get( "Ownership" ) );        
        Map<String, OrganisationUnitGroup> orgunitGroups = new HashMap<String, OrganisationUnitGroup>();
        for( String facilityType : facilityTypes )
        {
            if( facilityType != null && !facilityType.trim().equals("") )
            {
                List<OrganisationUnitGroup> organisationUnitGroups  = organisationUnitGroupService.getOrganisationUnitGroupByName( facilityType );
                if( organisationUnitGroups == null || organisationUnitGroups.isEmpty() )
                {
                    OrganisationUnitGroup organisationUnitGroup = new OrganisationUnitGroup();
                    organisationUnitGroup.setName( facilityType );
                    organisationUnitGroup.setShortName( facilityType );
                    
                    organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup );
                    orgunitGroups.put( facilityType, organisationUnitGroup );
                }
                else
                {
                    orgunitGroups.put( facilityType, organisationUnitGroups.get( 0 ) );
                }                
            }
        }
        
        List<String> ownership = lookupDataMap.get( "Ownership" );        
        Map<String, OrganisationUnitGroup> ownershiOrgunitGroups = new HashMap<String, OrganisationUnitGroup>();
        for( String ownershiptype : ownership )
        {
            if( ownershiptype != null && !ownershiptype.trim().equals("") )
            {
                List<OrganisationUnitGroup> organisationUnitGroups  = organisationUnitGroupService.getOrganisationUnitGroupByName( ownershiptype );
                if( organisationUnitGroups == null || organisationUnitGroups.isEmpty() )
                {
                    OrganisationUnitGroup organisationUnitGroup = new OrganisationUnitGroup();
                    organisationUnitGroup.setName( ownershiptype );
                    organisationUnitGroup.setShortName( ownershiptype );
                    
                    organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup );
                    
                    ownershiOrgunitGroups.put( ownershiptype, organisationUnitGroup );
                }
                else
                {
                    ownershiOrgunitGroups.put( ownershiptype, organisationUnitGroups.get( 0 ) );
                }
            }
        }
        
       
        //System.out.println("************************************************************************************************************");
        //System.out.println("************************************************************************************************************");
        
        List<OrganisationUnitGroup> ouGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( EquipmentAttributeValue.HEALTHFACILITY ) ); 
        OrganisationUnitGroup ouGroup = null;
        if( ouGroups == null || ouGroups.isEmpty() )
        {
            ouGroup = new OrganisationUnitGroup();
            ouGroup.setName( EquipmentAttributeValue.HEALTHFACILITY );
            ouGroup.setShortName( EquipmentAttributeValue.HEALTHFACILITY );
            
            organisationUnitGroupService.addOrganisationUnitGroup( ouGroup );
        }
        else
        {
            ouGroup = ouGroups.get( 0 );
        }

        
        List<DataSet> dataSets = new ArrayList<DataSet>( dataSetService.getDataSetByShortName( "FMD" ) ); 
        DataSet dataSet = null;
        if( dataSets != null )
        {
            dataSet = dataSets.get( 0 );
        }
        
        
        Date curDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "YYYY" );
        String curPeriodISOString = simpleDateFormat.format( curDate );
        Period period = PeriodType.getPeriodFromIsoString( curPeriodISOString );
        
        String storedBy = currentUserService.getCurrentUsername() ;

        try
        {
            CsvReader csvReader = new CsvReader( facilityCSVFilePath, ',', Charset.forName( "UTF-8" ) );
            csvReader.readHeaders();
            String headers[] = csvReader.getHeaders();

            //Creating map for DataElements
            Map<String, DataElement> dataElementMap = new HashMap<String, DataElement>();
            for( int i = 8; i < headers.length; i++ )
            {
                DataElement dataElement = dataElementService.getDataElementByShortName( headers[i] );
                if( dataElement != null )
                {
                    dataElementMap.put( headers[i], dataElement );
                }
            }
            DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();

            Integer colCount = headers.length;
            while( csvReader.readRecord() )
            {
                String nodeId = csvReader.get( "FacilityID" );
                String ouName = csvReader.get( "FacilityName" );
                //Integer ouLevel = Integer.parseInt( csvReader.get( "Level" ) );                         
                String parentId = csvReader.get( "AdminRegion" );
                //String ouCode = csvReader.get( "Code" );
                String gisCoordinates = csvReader.get( "GisCoordinates" );
                Integer facilityType = Integer.parseInt(  csvReader.get( "FacilityType" ) ) - 1;
                Integer facilityOwnership = Integer.parseInt( csvReader.get( "FacilityOwnership" ) ) - 1;
                String contactInfo = csvReader.get( "ContactInformation" );
                
                //if( ouCode != null && ouCode.trim().equals("") )
                //        ouCode = null;
                
                OrganisationUnit organisationUnit = new OrganisationUnit( ouName, ouName, nodeId, new Date(), null, true, parentId );
                if( parentId != null && Integer.parseInt( parentId ) != -1 )
                {
                    OrganisationUnit parentOU = orgUnitMap.get( parentId );
                    organisationUnit.setParent( parentOU );
                    if( gisCoordinates != null && !gisCoordinates.trim().equals( "" ) )
                    {
                        organisationUnit.setFeatureType( OrganisationUnit.FEATURETYPE_POINT );
                        organisationUnit.setCoordinates( "["+gisCoordinates+"]" );
                    }
                    
                    if( contactInfo != null && !contactInfo.trim().equals("") )
                    {
                        String[] contactDetails = contactInfo.split( " " );
                        if( contactDetails!= null )                            
                        {
                            if( contactDetails.length == 3 )
                            {
                                organisationUnit.setContactPerson( contactDetails[0] );
                                organisationUnit.setPhoneNumber( contactDetails[1] );
                                organisationUnit.setEmail( contactDetails[2] );
                            }
                            else if( contactDetails.length == 2 )
                            {
                                organisationUnit.setContactPerson( contactDetails[0] );
                                organisationUnit.setPhoneNumber( contactDetails[1] );
                            }
                            else
                            {
                                organisationUnit.setContactPerson( contactInfo );
                            }
                        }
                    }
                    
                    int orgUnitId = organisationUnitService.addOrganisationUnit( organisationUnit );
                    orgUnitMap.put( nodeId, organisationUnit );
                    ouGroup.addOrganisationUnit( organisationUnit );
                    //organisationUnitGroupService.updateOrganisationUnitGroup( ouGroup );
                    
                    //System.out.println( facilityType + " : " + facilityTypes.get( facilityType ) );
                    if( facilityType != null )
                    {
                        //System.out.println( facilityType + " : " + facilityTypes.get( facilityType ) + " : " + orgunitGroups.get( facilityTypes.get( facilityType ) ) );
                        OrganisationUnitGroup orgUnitGroup = orgunitGroups.get( facilityTypes.get( facilityType ) );
                        if( orgUnitGroup != null )
                        {
                            orgUnitGroup.addOrganisationUnit( organisationUnit );
                            orgunitGroups.put( facilityTypes.get( facilityType ), orgUnitGroup );
                            //System.out.println( orgUnitGroup.getName() +" Size : " + orgUnitGroup.getMembers().size() );
                            //organisationUnitGroupService.updateOrganisationUnitGroup( orgUnitGroup );
                        }
                    }

                    if( facilityOwnership != null )
                    {
                        //System.out.println( facilityOwnership + " : " + ownership.get( facilityOwnership ) + " : " + ownershiOrgunitGroups.get( ownership.get( facilityOwnership ) ) );
                        OrganisationUnitGroup orgUnitGroup = ownershiOrgunitGroups.get( ownership.get( facilityOwnership ) );
                        if( orgUnitGroup != null )
                        {
                            orgUnitGroup.addOrganisationUnit( organisationUnit );
                            ownershiOrgunitGroups.put( ownership.get( facilityOwnership ), orgUnitGroup );
                            //System.out.println( orgUnitGroup.getName() +" Size : " + orgUnitGroup.getMembers().size() );
                            //organisationUnitGroupService.updateOrganisationUnitGroup( orgUnitGroup );
                        }
                    }

                    if( dataSet != null )
                    {
                        dataSet.getSources().add( organisationUnit );                        
                    }

                    for( int i = 8; i < headers.length; i++ )
                    {
                        DataElement dataElement = dataElementMap.get( headers[i] );
                        
                        if( dataElement == null )
                        {
                            message += "<br><font color=red>Dataelememnt with the name/shortname : " + headers[i] +" is missing</font><br>";
                            continue;
                        }
                        
                        String value = null;
                        if( dataElement.getOptionSet() != null )
                        {
                            List<String> lookupOptions = lookupDataMap.get( headers[i] );
                            if( lookupOptions != null )
                            {
                                try
                                {                                       
                                    value = lookupOptions.get( Integer.parseInt( csvReader.get( headers[i] ) ) - 1  );
                                }
                                catch( Exception e )
                                {
                                                
                                }
                            }
                        }
                        else
                        {
                            value = csvReader.get( headers[i] );
                        }
                        
                        DataValue dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, decoc );
                        if ( dataValue == null )
                        {
                            if ( value != null )
                            {
                                dataValue = new DataValue( dataElement, period, organisationUnit, decoc, null, value, storedBy, curDate, null  );
                                dataValueService.addDataValue( dataValue );
                            }
                        }
                        else
                        {
                            dataValue.setValue( value );
                            dataValue.setTimestamp( curDate );
                            dataValue.setStoredBy( storedBy );

                            dataValueService.updateDataValue( dataValue );
                        }
                    }
                }
            }

            for( String facilityType : facilityTypes )
            {
                OrganisationUnitGroup orgUnitGroup = orgunitGroups.get( facilityType );
                
                if( orgUnitGroup != null )
                {
                    organisationUnitGroupService.updateOrganisationUnitGroup( orgUnitGroup );
                }
            }

            for( String ownershipType : ownership )
            {
                OrganisationUnitGroup orgUnitGroup = orgunitGroups.get( ownershipType );
                
                if( orgUnitGroup != null )
                {
                    organisationUnitGroupService.updateOrganisationUnitGroup( orgUnitGroup );
                }
            }

            organisationUnitGroupService.updateOrganisationUnitGroup( ouGroup );
            
            dataSetService.updateDataSet( dataSet );
            
            csvReader.close();                       
        }
        catch( Exception e )
        {
            e.printStackTrace();
            message += "<br><font color=red><b>Error while importing Facility.csv " + e.getMessage()+"</b></font><br>";
        }

        message += "<br><font color=black>Importing finished for Facility.csv " + new Date() + " </font><br>";
        
        return orgUnitMap;
    }


    public Map<String, OrganisationUnit> importAdminHierarchy( String adminHierarchyCSVFilePath )
    {
    	adminHierarchyCSVFilePath += File.separator + "AdminHierarchy.csv";
    	
    	Map<Integer, List<OrganisationUnit>> levelwiseOrgunits = new HashMap<Integer, List<OrganisationUnit>>();
    	Map<String, OrganisationUnit> orgUnitMap = new HashMap<String, OrganisationUnit>();
    	
    	List<Integer> ouLevels = new ArrayList<Integer>();
    	
    	message += "<br><font color=black>Importing started for AdminHierarchy.csv " + new Date() + " </font><br>";
    	
    	try
    	{
    	    CsvReader csvReader = new CsvReader( adminHierarchyCSVFilePath, ',', Charset.forName( "UTF-8" ) );
    	    csvReader.readHeaders();
    	    String headers[] = csvReader.getHeaders();

    	    Integer colCount = headers.length;
    	    while( csvReader.readRecord() )
    	    {
    	        String nodeId = csvReader.get( "NodeID" );
    	        String ouName = csvReader.get( "Name" );
    	        Integer ouLevel = Integer.parseInt( csvReader.get( "Level" ) );    			
    	        String parentId = csvReader.get( "Parent" );
    	        String ouCode = csvReader.get( "Code" );

    	        if( ouCode != null && ouCode.trim().equals("") )
    	            ouCode = null;
			
    	        List<OrganisationUnit> tempList = levelwiseOrgunits.get( ouLevel );
    	        if( tempList == null )
    	        {
    	            tempList = new ArrayList<OrganisationUnit>();                        
    	        }

    	        OrganisationUnit organisationUnit = new OrganisationUnit( ouName, ouName, ouCode, new Date(), null, true, parentId );
    	        organisationUnit.setDescription( nodeId );
    	        orgUnitMap.put( nodeId, organisationUnit );
                          
    	        tempList.add( organisationUnit );
            
    	        levelwiseOrgunits.put( ouLevel, tempList );
    	    }
    		
            csvReader.close();
            
            ouLevels.addAll( levelwiseOrgunits.keySet() );
            Collections.sort( ouLevels );
            
            for( Integer ouLevel : ouLevels )
            {
            	List<OrganisationUnit> orgUnits = levelwiseOrgunits.get( ouLevel );
            	for( OrganisationUnit ou : orgUnits )
            	{
            	    String parentId = ou.getComment();
            	    String nodeId = ou.getDescription();
            	    //ou.setComment( null );
            	    //ou.setAlternativeName( null );
            	    if( parentId == null || Integer.parseInt( parentId ) == -1 )
            	    {
            	        parentId = null;
            	        int orgUnitId = organisationUnitService.addOrganisationUnit( ou );
            	        orgUnitMap.put( nodeId, ou );
            	    }
            	    else
            	    {
            	        OrganisationUnit parentOU = orgUnitMap.get( parentId );
            	        ou.setParent( parentOU );
            	        int orgUnitId = organisationUnitService.addOrganisationUnit( ou );
            	        orgUnitMap.put( nodeId, ou );
            	    }
            	}
            }
    	}
        catch( Exception e )
        {
            e.printStackTrace();
            message += "<br><font color=red><b>Error while importing AdminHierarchy.csv " + e.getMessage()+"</b></font><br>";
        }

    	message += "<br><font color=black>Importing finished for AdminHierarchy.csv " + new Date() + " </font><br>";
    	
    	return orgUnitMap;
    }
    
    public void importRefrigetaorDetails( String refrigeratorDetailsCSVFilePath, EquipmentType refrigeratorEquipment, Map<Integer, List<String>> assetMap, Map<String, OrganisationUnit> faclityMap, Map<String, Model> refrigeratorModelMap, Map<String, List<String>> lookupDataMap )
    {
    	refrigeratorDetailsCSVFilePath += File.separator + "Refrigerators.csv";
    	
    	message += "<br><font color=black>Importing started for Refrigerators.csv " + new Date() + " </font><br>";
    	
        try
        {
            CsvReader csvReader = new CsvReader( refrigeratorDetailsCSVFilePath, ',', Charset.forName( "UTF-8" ) );
            
            csvReader.readHeaders();

            Map<String, EquipmentTypeAttribute> equipmentTypeAttributeMap = new HashMap<String, EquipmentTypeAttribute>();
            String headers[] = csvReader.getHeaders();            
            for( int i = 2; i < headers.length; i++ )
            {
            	EquipmentTypeAttribute equipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttributeByDescription( headers[i] );
            	equipmentTypeAttributeMap.put( headers[i], equipmentTypeAttribute );
            }

            while( csvReader.readRecord() )
            {
            	
            	String uniqueId = csvReader.get( "UniqueID" );
            	String catalogID = csvReader.get( "ModelID" );
            	
            	List<String> tempList = assetMap.get( Integer.parseInt( uniqueId ) );
            	
            	if( tempList == null )
            	{
            	    //System.out.println( "tempList is null for : " + uniqueId );
            	    message += "<br><font color=red>Orgunit with the id : " + uniqueId +" is missing</font><br>";
            	    continue;
            	}
            	
            	OrganisationUnit orgUnit = faclityMap.get( tempList.get(0) );
            	
            	if( orgUnit == null )
            	{
            	    //System.out.println(" ********************* " + tempList.get(0) + " IS NULL ********************* ");
            	    message += "<br><font color=red>Orgunit with the id : " + tempList.get(0) +" is missing</font><br>";
            	    continue;
            	}
            	
            	Equipment equipment = new Equipment();	            
            	equipment.setEquipmentType( refrigeratorEquipment );
	        equipment.setOrganisationUnit( orgUnit );
	            
	        Model model = refrigeratorModelMap.get( catalogID );
	        if( model == null )
	        {
	            //System.out.println( "model is null for : " + catalogID );
	            message += "<br><font color=red>Catalog with the id : " + catalogID +" is missing</font><br>";
	            continue;
	        }
	        
	        equipment.setModel( model );

	        List<EquipmentAttributeValue> equipmentAttributeValueDetailsList = new ArrayList<EquipmentAttributeValue>();

	        for( int i = 2; i < headers.length; i++ )
                {
	            EquipmentTypeAttribute equipmentTypeAttribute = equipmentTypeAttributeMap.get( headers[i] );
                    
                    if ( equipmentTypeAttribute != null )
                    {
                    	EquipmentAttributeValue equipmentAttributeValueDetails = new EquipmentAttributeValue();
                        equipmentAttributeValueDetails.setEquipment( equipment );
                        equipmentAttributeValueDetails.setEquipmentTypeAttribute( equipmentTypeAttribute );
                        
                        if( equipmentTypeAttribute.getOptionSet() != null )
                        {
                            List<String> lookupOptions = lookupDataMap.get( headers[i] );
                            if( lookupOptions != null )
                            {
                        	try
                        	{
                        	    //System.out.println( orgUnit.getName() + " : " + equipmentTypeAttribute.getName() + " : " + csvReader.get( headers[i] ) + " : " + lookupOptions.get( Integer.parseInt( csvReader.get( headers[i] ) ) + 1  ) );
                        	    equipmentAttributeValueDetails.setValue( lookupOptions.get( Integer.parseInt( csvReader.get( headers[i] ) ) - 1  ) );
                        	    equipmentAttributeValueDetailsList.add( equipmentAttributeValueDetails );
                        	}
                        	catch( Exception e )
                        	{
                        			
                        	}
                            }
                            else
                            {
                        		
                            }
                        }
                        else
                        {
                            if( csvReader.get( headers[i] ) != null && !csvReader.get( headers[i] ).trim().equals( "" ) )
                            {
                                //System.out.print( orgUnit.getName()  + " : " );
                                //System.out.print( equipmentTypeAttribute.getName() + " : " );
                                //System.out.println( csvReader.get( headers[i] ) );
                                equipmentAttributeValueDetails.setValue( csvReader.get( headers[i] ) );
                                equipmentAttributeValueDetailsList.add( equipmentAttributeValueDetails );
                            }
                        }
                    }
	            	
                }
	            
	        // -----------------------------------------------------------------------------
	        // Creating EquipmentAttributeValue Instance and saving equipmentAttributeValue data
	        // -----------------------------------------------------------------------------
	        Integer id = equipmentService.createEquipment( equipment, equipmentAttributeValueDetailsList );
            }
            
            csvReader.close();

        }
	catch( Exception e )
	{
	    e.printStackTrace();
	    message += "<br><font color=red>Error while importing for Refrigerators.csv " + e.getMessage() + " </font><br>";
	}

        message += "<br><font color=black>Importing finished for Refrigerators.csv " + new Date() + " </font><br>";
    }

    public void importColdroomsDetails( String coldroomDetailsCSVFilePath, EquipmentType coldroomsEquipment, Map<Integer, List<String>> assetMap, Map<String, OrganisationUnit> faclityMap, Map<String, List<String>> lookupDataMap )
    {
        coldroomDetailsCSVFilePath += File.separator + "Coldrooms.csv";
        
        message += "<br><font color=black>Importing started for Coldrooms.csv " + new Date() + " </font><br>";
        
        try
        {
            CsvReader csvReader = new CsvReader( coldroomDetailsCSVFilePath, ',', Charset.forName( "UTF-8" ) );
            
            csvReader.readHeaders();

            Map<String, EquipmentTypeAttribute> equipmentTypeAttributeMap = new HashMap<String, EquipmentTypeAttribute>();
            String headers[] = csvReader.getHeaders();            
            for( int i = 0; i < headers.length; i++ )
            {
                EquipmentTypeAttribute equipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttributeByDescription( headers[i] );
                equipmentTypeAttributeMap.put( headers[i], equipmentTypeAttribute );
            }

            while( csvReader.readRecord() )
            {
                
                String uniqueId = csvReader.get( "UniqueID" );
                
                List<String> tempList = assetMap.get( Integer.parseInt( uniqueId ) );
                if( tempList == null )
                {
                    //System.out.println( "tempList is null for : " + uniqueId );
                    message += "<br><font color=red>Orgunit with the id : " + uniqueId +" is missing</font><br>";
                    continue;
                }
                
                OrganisationUnit orgUnit = faclityMap.get( tempList.get(0) );
                
                if( orgUnit == null )
                {
                    //System.out.println(" ********************* " + tempList.get(0) + " IS NULL ********************* ");
                    message += "<br><font color=red>Orgunit with the id : " + tempList.get(0) +" is missing</font><br>";
                    continue;
                }
                
                Equipment equipment = new Equipment();              
                equipment.setEquipmentType( coldroomsEquipment );
                equipment.setOrganisationUnit( orgUnit );
                    
                List<EquipmentAttributeValue> equipmentAttributeValueDetailsList = new ArrayList<EquipmentAttributeValue>();

                for( int i = 0; i < headers.length; i++ )
                {
                    EquipmentTypeAttribute equipmentTypeAttribute = equipmentTypeAttributeMap.get( headers[i] );
                    
                    if ( equipmentTypeAttribute != null )
                    {
                        EquipmentAttributeValue equipmentAttributeValueDetails = new EquipmentAttributeValue();
                        equipmentAttributeValueDetails.setEquipment( equipment );
                        equipmentAttributeValueDetails.setEquipmentTypeAttribute( equipmentTypeAttribute );
                        
                        if( equipmentTypeAttribute.getOptionSet() != null )
                        {
                            List<String> lookupOptions = lookupDataMap.get( headers[i] );
                            if( lookupOptions != null )
                            {
                                try
                                {
                                    //System.out.println( orgUnit.getName() + " : " + equipmentTypeAttribute.getName() + " : " + csvReader.get( headers[i] ) + " : " + lookupOptions.get( Integer.parseInt( csvReader.get( headers[i] ) ) + 1  ) );
                                    equipmentAttributeValueDetails.setValue( lookupOptions.get( Integer.parseInt( csvReader.get( headers[i] ) ) - 1  ) );
                                    equipmentAttributeValueDetailsList.add( equipmentAttributeValueDetails );
                                }
                                catch( Exception e )
                                {
                                                
                                }
                            }
                            else
                            {
                                        
                            }
                        }
                        else
                        {
                            if( csvReader.get( headers[i] ) != null && !csvReader.get( headers[i] ).trim().equals( "" ) )
                            {
                                equipmentAttributeValueDetails.setValue( csvReader.get( headers[i] ) );
                                equipmentAttributeValueDetailsList.add( equipmentAttributeValueDetails );
                            }
                        }
                    }
                        
                }
                    
                // -----------------------------------------------------------------------------
                // Creating EquipmentAttributeValue Instance and saving equipmentAttributeValue data
                // -----------------------------------------------------------------------------
                Integer id = equipmentService.createEquipment( equipment, equipmentAttributeValueDetailsList );
            }
            
            csvReader.close();

        }
        catch( Exception e )
        {
            e.printStackTrace();
            message += "<br><font color=red>Error while importing for Coldrooms.csv " + e.getMessage() + " </font><br>";
        }

        message += "<br><font color=black>Importing finished for Coldrooms.csv " + new Date() + " </font><br>";
    }

    
    public Map<String, Model> importRefrigeratorCatalogData( String refrigeratorCatalogDataCSVFilePath, ModelType refrigeratorModel, Map<String, List<String>> lookupDataMap )
    {
        refrigeratorCatalogDataCSVFilePath += File.separator + "RefrigeratorCatalog.csv";
        
        message += "<br><font color=black>Importing started for RefrigeratorCatalog.csv " + new Date() + " </font><br>";
        
        Map<String, Model> refrigeratorModelMap = new HashMap<String, Model>();
        try
        {
            CsvReader csvReader = new CsvReader( refrigeratorCatalogDataCSVFilePath, ',', Charset.forName( "UTF-8" ) );
            
            csvReader.readHeaders();
            
            Map<String, ModelTypeAttribute> modelTypeAttributeMap = new HashMap<String, ModelTypeAttribute>();
            String headers[] = csvReader.getHeaders();            
            for( int i = 0; i < headers.length; i++ )
            {
                ModelTypeAttribute modelTypeAttribute = modelTypeAttributeService.getModelTypeAttributeByDescription( headers[i] );
                //Model model = modelTypeAttributeService.getM.getModelByDescription( headers[i] );
                modelTypeAttributeMap.put( headers[i], modelTypeAttribute );
            }
            
            Integer colCount = headers.length;
            
            while( csvReader.readRecord() )
            {
                String catalogId = csvReader.get( "CatalogID" );
                String modelName = csvReader.get( "ModelName" );
                /*
                String manufacturer = csvReader.get( "Manufacturer" );
                String refPowerSource = csvReader.get( "RefPowerSource" );
                String refType = csvReader.get( "RefType" );
                String climateZone = csvReader.get( "ClimateZone" );
                String dataSource = csvReader.get( "DataSource" );
                String refGrossVolume = csvReader.get( "RefGrossVolume" );
                String refNetVolume = csvReader.get( "RefNetVolume" );
                String freezeGrossVolume = csvReader.get( "FreezeGrossVolume" );
                String freezeNetVolume = csvReader.get( "FreezeNetVolume" );
                */
                
                if( catalogId == null || catalogId.trim().equals("") || modelName == null || modelName.trim().equals("") )
                {
                    message += "<br><font color=red>Catalog with the id : " + catalogId +"/"+ modelName +" is missing</font><br>";
                    continue;
                }
                
                Model model = new Model();
                model.setName( catalogId + " + " + modelName );
                model.setDescription( catalogId + " + " + modelName );
                model.setModelType( refrigeratorModel );
                
                List<ModelAttributeValue> modelAttributeValues = new ArrayList<ModelAttributeValue>();
                
                for( int i = 0; i < headers.length; i++ )
                {
                    ModelTypeAttribute modelTypeAttribute = modelTypeAttributeMap.get( headers[i] );
                    
                    if ( modelTypeAttribute != null )
                    {
                        ModelAttributeValue modelAttributeValue = new ModelAttributeValue();
                        modelAttributeValue.setModel( model );
                        modelAttributeValue.setModelTypeAttribute( modelTypeAttribute );
                        
                        if( modelTypeAttribute.getOptionSet() != null )
                        {
                            List<String> lookupOptions = lookupDataMap.get( headers[i] );
                            if( lookupOptions != null )
                            {
                                try
                                {                        	        
                                    modelAttributeValue.setValue( lookupOptions.get( Integer.parseInt( csvReader.get( headers[i] ) ) - 1  ) );
                                    modelAttributeValues.add( modelAttributeValue );
                                }
                                catch( Exception e )
                                {
                        			
                                }
                            }
                            else
                            {
                        	
                            }
                        }
                        else
                        {
                            modelAttributeValue.setValue( csvReader.get( headers[i] ) );
                            modelAttributeValues.add( modelAttributeValue );
                        }
                    }
                }
                
                // -------------------------------------------------------------------------
                // Save model
                // -------------------------------------------------------------------------                    
                modelService.createModel(  model, modelAttributeValues );
                
                refrigeratorModelMap.put( catalogId, model );

            }
            
            csvReader.close();

        }
        catch( Exception e )
        {
            e.printStackTrace();
            message += "<br><font color=red>Error while importing for RefrigeratorCatalog.csv " + e.getMessage() + " </font><br>";
        }
        
        message += "<br><font color=black>Importing finished for RefrigeratorCatalog.csv " + new Date() + " </font><br>";
        return refrigeratorModelMap;
    }

    public Map<Integer, List<String>> getAssetData( String assetListCSVFilePath )
    {
        Map<Integer, List<String>> assetMap = new HashMap<Integer, List<String>>();
        
        assetListCSVFilePath += File.separator + "AssetList.csv";
        
        message += "<br><font color=black>Importing started for AssetList.csv " + new Date() + " </font><br>";
        
        try
        {
            CsvReader csvReader = new CsvReader( assetListCSVFilePath, ',', Charset.forName( "UTF-8" ) );
            
            csvReader.readHeaders();
            
            while( csvReader.readRecord() )
            {
                Integer equipmentId = Integer.parseInt(  csvReader.get( "EquipmentID" ) );
                List<String> tempList = new ArrayList<String>();
                tempList.add( csvReader.get( "FacilityID" ) );
                tempList.add( csvReader.get( "AssetType" ) );
                    
                assetMap.put( equipmentId, tempList );
            }
            
            csvReader.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            message += "<br><font color=red><b>Error while importing AssetList.csv " + e.getMessage()+"</b></font><br>";
        }
        
        message += "<br><font color=black>Importing ended for AssetList.csv " + new Date() + " </font><br>";
        
        return assetMap;
    }

    public Map<String, List<String>> getLookupData( String lookupsCSVFilePath )
    {
        Map<String, List<String>> lookupDataMap = new HashMap<String, List<String>>();
        
        lookupsCSVFilePath += File.separator + "Lookups.csv";
        
        message += "<br><font color=black>Importing started for Lookups.csv " + new Date() + " </font><br>";
        
        try
        {
            List<String> lookupHeaders = new ArrayList<String>();
            lookupHeaders.add( "FacilityType" );
            lookupHeaders.add( "Ownership" );
            lookupHeaders.add( "StorageType" );
            lookupHeaders.add( "DeliveryType" );
            lookupHeaders.add( "ElectricitySource" );
            lookupHeaders.add( "GridAvailability" );
            lookupHeaders.add( "FuelAvailability" );
            lookupHeaders.add( "YesOrNo" );
            lookupHeaders.add( "TempartureZone" );
            lookupHeaders.add( "VaccineMode" );
            lookupHeaders.add( "AssetType" );
            lookupHeaders.add( "WorkingStatus" );
            lookupHeaders.add( "ReasonNotWorking" );
            lookupHeaders.add( "Utilization" );
            lookupHeaders.add( "PowerSource" );
            lookupHeaders.add( "RefPowerSource" );
            lookupHeaders.add( "RefType" );
            lookupHeaders.add( "StorageTemp" );
            lookupHeaders.add( "DataSource" );
                        
            CsvReader csvReader = new CsvReader( lookupsCSVFilePath, ',', Charset.forName( "UTF-8" ) );
            
            csvReader.readHeaders();
            
            String headers[] = csvReader.getHeaders();
            
            Integer colCount = headers.length;
            
            while( csvReader.readRecord() )
            {
                for( int i = 0; i < colCount; i++ )
                {
                    List<String> tempList = lookupDataMap.get( headers[i] );
                    if( tempList == null )
                    {
                        tempList = new ArrayList<String>();                        
                    }
                    tempList.add( csvReader.get( headers[i] ) );
                    lookupDataMap.put( headers[i], tempList );
                }
            }
            
            csvReader.close();
        }
        catch( Exception e )
        {
            message += "<br><font color=red><b>Error while importing Lookups.csv " + e.getMessage()+"</b></font><br>";
            e.printStackTrace();
        }
        
        message += "<br><font color=black>Importing ended for Lookups.csv " + new Date() + " </font><br>";
        
        return lookupDataMap;
    }

    public String unZip( ZipInputStream zis  )
    {
        byte[] buffer = new byte[1024];
        
        String outputReportPath = "";

        try
        {
            outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  "temp";

            File newdir = new File( outputReportPath );
            if( !newdir.exists() )
            {
                newdir.mkdirs();
            }
            outputReportPath += File.separator + UUID.randomUUID().toString();
           
            //ZipInputStream zis = new ZipInputStream( new FileInputStream( zipFile ) );

            ZipEntry ze = zis.getNextEntry();

            while ( ze != null )
            {
                String individualFileName = ze.getName();
                File newFile = new File( outputReportPath + File.separator + individualFileName );

                //System.out.println( "file unzip : " + newFile.getAbsoluteFile() );

                // create all non exists folders
                // else you will hit FileNotFoundException for compressed folder
                new File( newFile.getParent() ).mkdirs();

                FileOutputStream fos = new FileOutputStream( newFile );

                int len;
                while ( (len = zis.read( buffer )) > 0 )
                {
                    fos.write( buffer, 0, len );
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            
            zis.close();
        }
        catch ( IOException ex )
        {
            ex.printStackTrace();
        }
        
        return outputReportPath;
    }
}
