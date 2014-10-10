package org.hisp.dhis.coldchain.equipment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType_AttributeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_AttributeStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DefaultEquipmentType_AttributeService.java Jun 14, 2012 3:19:02 PM	
 */

public class DefaultEquipmentType_AttributeService implements EquipmentType_AttributeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private EquipmentType_AttributeStore equipmentType_AttributeStore;

    public void setEquipmentType_AttributeStore( EquipmentType_AttributeStore equipmentType_AttributeStore )
    {
        this.equipmentType_AttributeStore = equipmentType_AttributeStore;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------
    @Transactional
    @Override
    public void addEquipmentType_Attribute( EquipmentType_Attribute equipmentType_Attribute )
    {
        equipmentType_AttributeStore.addEquipmentType_Attribute( equipmentType_Attribute );
    }
    
    @Transactional
    @Override
    public void deleteEquipmentType_Attribute( EquipmentType_Attribute equipmentType_Attribute )
    {
        equipmentType_AttributeStore.deleteEquipmentType_Attribute( equipmentType_Attribute );
    }
    
    @Transactional
    @Override
    public void updateEquipmentType_Attribute( EquipmentType_Attribute equipmentType_Attribute )
    {
        equipmentType_AttributeStore.updateEquipmentType_Attribute( equipmentType_Attribute );
    }
    
    @Transactional
    @Override
    public Collection<EquipmentType_Attribute> getAllEquipmentTypeAttributes()
    {
        return equipmentType_AttributeStore.getAllEquipmentTypeAttributes();
    }
    
    @Transactional
    @Override
    public EquipmentType_Attribute getEquipmentTypeAttribute( EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute )
    {
        return equipmentType_AttributeStore.getEquipmentTypeAttribute( equipmentType, equipmentTypeAttribute );
    }
    
    @Transactional
    @Override
    public Collection<EquipmentType_Attribute> getAllEquipmentTypeAttributesByEquipmentType( EquipmentType equipmentType )
    {
        return equipmentType_AttributeStore.getAllEquipmentTypeAttributesByEquipmentType( equipmentType );
    }
    
    @Transactional
    @Override
    public Collection<EquipmentTypeAttribute> getListEquipmentTypeAttribute( EquipmentType equipmentType )
    {
        return equipmentType_AttributeStore.getListEquipmentTypeAttribute( equipmentType );
    }

    @Transactional
    @Override
    public EquipmentType_Attribute getEquipmentTypeAttributeForDisplay( EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, boolean display)
    {
        return equipmentType_AttributeStore.getEquipmentTypeAttributeForDisplay( equipmentType, equipmentTypeAttribute, display );
    }
    
    @Transactional
    @Override
    public Collection<EquipmentType_Attribute> getAllEquipmentTypeAttributeForDisplay( EquipmentType equipmentType, boolean display )
    {
        return equipmentType_AttributeStore.getAllEquipmentTypeAttributeForDisplay( equipmentType, display );
    }
    
    
    public Map<String, String> getOrgUnitAttributeDataValue( String orgUnitIdsByComma, String orgUnitAttribIdsByComma )
    {
        Map<String, String> orgUnitAttributeDataValueMap = new HashMap<String, String>();
        try
        {
            String query = "SELECT organisationunitattributevalues.organisationunitid, attributevalue.attributeid, value FROM attributevalue "+
                                " INNER JOIN organisationunitattributevalues ON attributevalue.attributevalueid = organisationunitattributevalues.attributevalueid "+
                                " WHERE attributeid IN ("+orgUnitAttribIdsByComma+") AND " +
                                    " organisationunitattributevalues.organisationunitid IN ("+ orgUnitIdsByComma +")";
                        
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitID = rs.getInt( 1 );
                Integer attribId = rs.getInt( 2 );
                String value = rs.getString( 3 );

                orgUnitAttributeDataValueMap.put( orgUnitID+":"+attribId, value );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return orgUnitAttributeDataValueMap;
    }
    
    
   
    public Collection<OrganisationUnit> searchOrgUnitByAttributeValue( String orgUnitIdsByComma, Attribute attribute, String searchText )
    {
        //String sql = searchPatientSql( false, searchKeys, orgunit, min, max );
        
        /*
        System.out.println( "--- orgUnitIdsByComma" + orgUnitIdsByComma  );
        
        System.out.println( "--- attribute" + attribute.getName() );
        
        System.out.println( "--- searchText" + searchText );
        */
        
        String sql = "SELECT distinct organisationunitattributevalues.organisationunitid as organisationunitid, attributevalue.attributeid, value FROM attributevalue " +
        
                     "INNER JOIN organisationunitattributevalues ON attributevalue.attributevalueid = organisationunitattributevalues.attributevalueid " +
           
                     "WHERE attributeid = " + attribute.getId() + 
           
                     " AND organisationunitattributevalues.organisationunitid IN ("+ orgUnitIdsByComma +")" +  
                     
                     " AND value like '%" + searchText + "%' " ;

        //System.out.println( "---" + sql );
        
        Collection<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();

        try
        {
            organisationUnits = jdbcTemplate.query( sql, new RowMapper<OrganisationUnit>()
            {
                public OrganisationUnit mapRow( ResultSet rs, int rowNum ) throws SQLException
                {
                    //System.out.println( "--- " + rs.getString( "organisationunitid" ) );
                    //return organisationUnitService.getOrganisationUnit( rs.getString( "organisationunitid" ) );
                    return organisationUnitService.getOrganisationUnit( rs.getInt( "organisationunitid" ) );
                }
            } );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        /*
        System.out.println( "--- Service " + organisationUnits.size() );
        
        for( OrganisationUnit orgUnit : organisationUnits )
        {
            System.out.println( "--- " + orgUnit.getId() + "----" + orgUnit.getName() );
        }
        */
        return organisationUnits;
    }

    
    public Map<Integer, String> getEquipmentCountByOrgUnitList( String orgUnitIdsByComma )
    {
        Map<Integer, String> equipmentTypeCountMap = new HashMap<Integer, String>();
        try
        {
            String query = "SELECT equipment.equipmenttypeid,count(*) as total from equipment "+
                           " WHERE organisationunitid IN (" + orgUnitIdsByComma + " ) " +
                           " group by equipment.equipmenttypeid ";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer equipmentTypeID = rs.getInt( 1 );
                String equipmentCount = rs.getString( 2 );
                
                //System.out.println( "--- " + equipmentTypeID + "----" + equipmentCount );
                
                equipmentTypeCountMap.put( equipmentTypeID, equipmentCount );
            }
        }    
            
       catch( Exception e )
       {
           throw new RuntimeException( "Exception: ", e );
       }
            
       
       //System.out.println( "--- Map Size " + equipmentTypeCountMap.size() );
       return equipmentTypeCountMap;
    }
    
    
}
