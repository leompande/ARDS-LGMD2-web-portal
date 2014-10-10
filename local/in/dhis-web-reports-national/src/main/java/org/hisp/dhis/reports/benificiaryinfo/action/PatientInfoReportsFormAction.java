package org.hisp.dhis.reports.benificiaryinfo.action;

import java.util.Collection;

import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;

import com.opensymphony.xwork2.Action;

public class PatientInfoReportsFormAction implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Collection<PatientAttribute> patientAttributes;

    public Collection<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        
        patientAttributes = patientAttributeService.getAllPatientAttributes();
        /*
        programs = programService.getAllPrograms();

        organisationUnit = selectionManager.getSelectedOrganisationUnit();

        if ( organisationUnit == null )
        {
            status = 1;
        }
        else if ( !organisationUnit.isHasPatients() )
        {
            status = 2;
        }
    */
        return SUCCESS;
    }
}
