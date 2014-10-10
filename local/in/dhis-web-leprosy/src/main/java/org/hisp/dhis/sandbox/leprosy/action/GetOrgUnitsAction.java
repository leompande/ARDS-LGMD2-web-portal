package org.hisp.dhis.sandbox.leprosy.action;

import java.io.FileInputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;

import com.opensymphony.xwork2.ActionSupport;

public class GetOrgUnitsAction extends ActionSupport {
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) {
        this.organisationUnitService = organisationUnitService;
    }
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
    private Integer orgUnitId;

    public void setOrgUnitId(Integer orgUnitId) {
        this.orgUnitId = orgUnitId;
    }
    private OrganisationUnit orgUnit;

    public OrganisationUnit getOrgUnit() {
        return orgUnit;
    }
    private OrganisationUnit parentOrgUnit;

    public OrganisationUnit getParentOrgUnit() {
        return parentOrgUnit;
    }
    private String trcNumber;

    public String getTrcNumber() {
        return trcNumber;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception {
        int maxPatientIdentifier = 0;

        if (orgUnitId != null) {
            orgUnit = organisationUnitService.getOrganisationUnit(orgUnitId);

            parentOrgUnit = orgUnit.getParent();

            Date date = new Date();
            int year = date.getYear() + 1900;

            String parentOrgUnitCode = parentOrgUnit.getCode() + year;

            getOpenmrsContext();

            PatientService patientService = Context.getPatientService();

            List<PatientIdentifier> patientIdentifierList = patientService.getPatientIdentifiers(new PatientIdentifierType(1));

            for (PatientIdentifier pi : patientIdentifierList) {
                String piStr = pi.getIdentifier();

                System.out.println("Existing PID : " + piStr);
                if (piStr.contains(parentOrgUnitCode)) {
                    String counterAndChar = piStr.substring(parentOrgUnitCode.length());
                    String counter = counterAndChar.split("-")[0];
                    System.out.println("Counter : " + counter);

                    if (maxPatientIdentifier < Integer.parseInt(counter)) {
                        maxPatientIdentifier = Integer.parseInt(counter);
                    }
                }
            }

            maxPatientIdentifier++;
            trcNumber = parentOrgUnitCode + maxPatientIdentifier;

            System.out.println("TRC Number :" + trcNumber);
            Context.closeSession();
        }

        return SUCCESS;
    }

    public void getOpenmrsContext() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(System.getenv("DHIS2_HOME") + "/hibernate.properties"));
            String user = props.getProperty("hibernate.connection.username");
            String pass = props.getProperty("hibernate.connection.password");
            Context.startup("jdbc:mysql://localhost:3306/openmrs_leprosy?autoReconnect=true", user, pass, new Properties());
            Context.openSession();
            Context.authenticate("admin", "test");
            System.out.println("NLEP>>>Context Session Opened");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
