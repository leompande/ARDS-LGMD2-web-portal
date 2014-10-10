package org.hisp.dhis.reports.benificiaryinfo.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import com.opensymphony.xwork2.Action;

public class GetPatientDetailsAction
implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    
/*
    private PatientIdentifierService patientIdentifierService;
    
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    private ProgramService programService;
    
    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }



    private PatientAttributeService patientAttributeService;
    
    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientAttributeGroupService patientAttributeGroupService;
    
    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;
    
    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }
*/

    // -------------------------------------------------------------------------
    // Input/Output and its Getter / Setter
    // -------------------------------------------------------------------------

    private int id;
    
    public void setId( int id )
    {
        this.id = id;
    }

    private Patient patient;
    
    public Patient getPatient()
    {
        return patient;
    }
/*
    private Patient representative;
    
    public Patient getRepresentative()
    {
        return representative;
    }
*/
    private PatientIdentifier patientIdentifier;
    
    public PatientIdentifier getPatientIdentifier()
    {
        return patientIdentifier;
    }
    /*
    private Collection<Program> programs;
    
    public Collection<Program> getPrograms()
    {
        return programs;
    }
    */
    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }

    private Collection<PatientAttribute> noGroupAttributes;
    
    public Collection<PatientAttribute> getNoGroupAttributes()
    {
        return noGroupAttributes;
    }
    
    private List<PatientAttributeGroup> attributeGroups;
    
    public List<PatientAttributeGroup> getAttributeGroups()
    {
        return attributeGroups;
    }

    private Collection<PatientIdentifierType> identifierTypes;
    
    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }
/*
    private Map<Integer, String> identiferMap;
    
    public Map<Integer, String> getIdentiferMap()
    {
        return identiferMap;
    }

    private String childContactName;
    
    public String getChildContactName()
    {
        return childContactName;
    }

    private String childContactType;
    
    public String getChildContactType()
    {
        return childContactType;
    }
 */
    private String systemIdentifier;
   
    public String getSystemIdentifier()
    {
        return systemIdentifier;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        patient = patientService.getPatient( id );

       // patientIdentifier = patientIdentifierService.getPatientIdentifier( patient );

        // = patientIdentifierTypeService.getAllPatientIdentifierTypes();
        
        /*
        identiferMap = new HashMap<Integer, String>();

        PatientIdentifierType idType = null;
        representative = patient.getRepresentative();

        if ( patient.isUnderAge() && representative != null )
        {
            for ( PatientIdentifier representativeIdentifier : representative.getIdentifiers() )
            {
                if ( representativeIdentifier.getIdentifierType() != null
                    && representativeIdentifier.getIdentifierType().isRelated() )
                {
                    //identiferMap.put( representativeIdentifier.getIdentifierType().getId(), representativeIdentifier.getIdentifier() );
                }
            }
        }

        for ( PatientIdentifier identifier : patient.getIdentifiers() )
        {
            idType = identifier.getIdentifierType();

            if ( idType != null )
            {
                //identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
            }
            else
            {
                systemIdentifier = identifier.getIdentifier();
            }
        }
    */
        for ( PatientAttribute patientAttribute : patient.getAttributes() )
        {
            patientAttributeValueMap.put( patientAttribute.getId(), PatientAttributeValue.UNKNOWN );
        }

        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService.getPatientAttributeValues( patient );

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttributeValue.getPatientAttribute()
                .getValueType() ) )
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(),
                    patientAttributeValue.getPatientAttributeOption().getName() );
            }
            else
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(),
                    patientAttributeValue.getValue() );
            }
        }
        /*
        programs = programService.getAllPrograms();

        noGroupAttributes = patientAttributeService.getPatientAttributesNotGroup();

        attributeGroups = new ArrayList<PatientAttributeGroup>( patientAttributeGroupService.getAllPatientAttributeGroups() );
        Collections.sort( attributeGroups, new PatientAttributeGroupSortOrderComparator() );
        */
        return SUCCESS;

    }

}

