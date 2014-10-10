package org.hisp.dhis.sandbox.leprosy.action;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TreeSet;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.sandbox.leprosy.NLEPDataElements;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Program;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;

import com.opensymphony.xwork2.Action;

public class PatientRegistrationAction implements Action {

    // <editor-fold defaultstate="collapsed" desc="TRC Card Fields">
    private String givenName;
    private String middleName;
    private String familyName;
    private String resAddress1;
    private String resAddress2;
    private String cityVillage;
    private String zipCode;
    private String region;
    private String subRegion;
    private String division;
    private String dob;
    private String gender;
    private String district;
    private String phc;
    private String category;
    private String diseaseType;
    private String detectionMode;
    private String caseDetection;
    private String regDate;
    private String firstDose;
    private String relapseSelect;
    private String contacts;
    private String dateRFT;
    private String voidedReason;
    private String deformity;
    private String deformityTime;
    private String deformityType;
    private String rcsEligible;
    private String rcsReferred;
    private String rcsPart;
    private String rcsDate;
    private String rcsCenterName;
    private String rcsRepeat;
    private String rcsPatientBPL;
    private String rcsReimbursementAmt;
    private String disabilityMDT;
    private String reactionTime;
    private String reactionMgmtAt;
    private String treatmentStartDate;
    private String treatmentEndDate;
    private String servicesGiven;
    private String socioEconomicServices;
    private String jobType;
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient Location Accessors">
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPhc() {
        return phc;
    }

    public void setPhc(String phc) {
        this.phc = phc;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient Name Accessors">
    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient Address Accessors">
    public String getResAddress1() {
        return resAddress1;
    }

    public void setResAddress1(String resAddress1) {
        this.resAddress1 = resAddress1;
    }

    public String getResAddress2() {
        return resAddress2;
    }

    public void setResAddress2(String resAddress2) {
        this.resAddress2 = resAddress2;
    }

    public String getCityVillage() {
        return cityVillage;
    }

    public void setCityVillage(String cityVillage) {
        this.cityVillage = cityVillage;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient Residential Status Accessors">
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSubRegion() {
        return subRegion;
    }

    public void setSubRegion(String subRegion) {
        this.subRegion = subRegion;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient DOB Accessors">
    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient Gender Accessors">
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient Caste Category Accessors">
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient DiseaseType Accessors">
    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType(String diseaseType) {
        this.diseaseType = diseaseType;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Detected By (detectionMode) Accessors">
    public String getDetectionMode() {
        return detectionMode;
    }

    public void setDetectionMode(String detectionMode) {
        this.detectionMode = detectionMode;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Program Registration Date (regDate) Accessors">
    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's First Encounter Date (firstDose) Accessors">
    public String getFirstDose() {
        return firstDose;
    }

    public void setFirstDose(String firstDose) {
        this.firstDose = firstDose;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's 1st CaseDetection (caseDetection, relapseSelect) Accessors">
    public String getCaseDetection() {
        return caseDetection;
    }

    public void setCaseDetection(String caseDetection) {
        this.caseDetection = caseDetection;
    }

    public String getRelapseSelect() {
        return relapseSelect;
    }

    public void setRelapseSelect(String relapseSelect) {
        this.relapseSelect = relapseSelect;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Contacts Accessors">
    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Date of Completion of Treatment (dateRFT)">
    public String getDateRFT() {
        return dateRFT;
    }

    public void setDateRFT(String dateRFT) {
        this.dateRFT = dateRFT;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Leprosy Program Voided Reason">
    public String getVoidedReason() {
        return voidedReason;
    }

    public void setVoidedReason(String voidedReason) {
        this.voidedReason = voidedReason;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Deformity Grade(deformity, deformityTime) Accessors">
    public String getDeformity() {
        return deformity;
    }

    public void setDeformity(String deformity) {
        this.deformity = deformity;
    }

    public String getDeformityTime() {
        return deformityTime;
    }

    public void setDeformityTime(String deformityTime) {
        this.deformityTime = deformityTime;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Deformity in Organ(deformityType) Accessors">
    public String getDeformityType() {
        return deformityType;
    }

    public void setDeformityType(String deformityType) {
        this.deformityType = deformityType;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's RCS (rcsEligible, rcsReferred, rcsPart, rcsDate, rcsCenterName, rcsRepeat, rcsPatientBPL, rcsReimbursementAmt) Accessors">
    public String getRcsDate() {
        return rcsDate;
    }

    public void setRcsDate(String rcsDate) {
        this.rcsDate = rcsDate;
    }

    public String getRcsEligible() {
        return rcsEligible;
    }

    public void setRcsEligible(String rcsEligible) {
        this.rcsEligible = rcsEligible;
    }

    public String getRcsPart() {
        return rcsPart;
    }

    public void setRcsPart(String rcsPart) {
        this.rcsPart = rcsPart;
    }

    public String getRcsReferred() {
        return rcsReferred;
    }

    public void setRcsReferred(String rcsReferred) {
        this.rcsReferred = rcsReferred;
    }

    public String getRcsCenterName() {
        return rcsCenterName;
    }

    public void setRcsCenterName(String rcsCenterName) {
        this.rcsCenterName = rcsCenterName;
    }

    public String getRcsRepeat() {
        return rcsRepeat;
    }

    public void setRcsRepeat(String rcsRepeat) {
        this.rcsRepeat = rcsRepeat;
    }

    public String getRcsPatientBPL() {
        return rcsPatientBPL;
    }

    public void setRcsPatientBPL(String rcsPatientBPL) {
        this.rcsPatientBPL = rcsPatientBPL;
    }

    public String getRcsReimbursementAmt() {
        return rcsReimbursementAmt;
    }

    public void setRcsReimbursementAmt(String rcsReimbursementAmt) {
        this.rcsReimbursementAmt = rcsReimbursementAmt;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Disability After MDT (disabilityMDT) Accessors">
    public String getDisabilityMDT() {
        return disabilityMDT;
    }

    public void setDisabilityMDT(String disabilityMDT) {
        this.disabilityMDT = disabilityMDT;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Reaction & Management (reactionTime, reactionMgmtAt) Accessors">
    public String getReactionMgmtAt() {
        return reactionMgmtAt;
    }

    public void setReactionMgmtAt(String reactionMgmtAt) {
        this.reactionMgmtAt = reactionMgmtAt;
    }

    public String getReactionTime() {
        return reactionTime;
    }

    public void setReactionTime(String reactionTime) {
        this.reactionTime = reactionTime;
    }

    public String getTreatmentEndDate() {
        return treatmentEndDate;
    }

    public void setTreatmentEndDate(String treatmentEndDate) {
        this.treatmentEndDate = treatmentEndDate;
    }

    public String getTreatmentStartDate() {
        return treatmentStartDate;
    }

    public void setTreatmentStartDate(String treatmentStartDate) {
        this.treatmentStartDate = treatmentStartDate;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LeprosyPatient's Socio-economic Services (servicesGiven, socioEconomicServices, jobType) Accessors">
    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getServicesGiven() {
        return servicesGiven;
    }

    public void setServicesGiven(String servicesGiven) {
        this.servicesGiven = servicesGiven;
    }

    public String getSocioEconomicServices() {
        return socioEconomicServices;
    }

    public void setSocioEconomicServices(String socioEconomicServices) {
        this.socioEconomicServices = socioEconomicServices;
    }
    //</editor-fold>
    private String patientId;

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    private String flagTB;

    public void setFlagTB(String flagTB) {
        this.flagTB = flagTB;
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

    public String execute() throws Exception {

        orgUnit = organisationUnitService.getOrganisationUnit(parentouIDTB);

        period = getCurrentMonthlyPeriod();

        try {
            if (givenName != null) {
                getOpenmrsContext();

                //Get PatientService and Patient.
                PatientService patientService = Context.getPatientService();
                Patient leprosyPatient = new Patient();

                // <editor-fold defaultstate="collapsed" desc="Add Identifier to LeprosyPatient">
                Location location = new Location(1);
                PatientIdentifierType identType = patientService.getPatientIdentifierType(1);
                Date date = new Date();
                int year = date.getYear() + 1900;
                String idWithoutCheckdigit = patientId;
                System.out.println("PatientId : " + patientId);
                System.out.println("NLEP>>>ID Without CheckDigit = " + idWithoutCheckdigit);
                int checkedDigit = checkDigit(idWithoutCheckdigit);
                System.out.println("NLEP>>>CheckDigit = " + checkedDigit);
                String patientIdentifierString = idWithoutCheckdigit + '-' + (Integer.toString(checkedDigit));
                System.out.println("NLEP>>>Patient Identifier = " + patientIdentifierString);
                PatientIdentifier patientIdentifier = new PatientIdentifier(patientIdentifierString, identType, location);
                leprosyPatient.addIdentifier(patientIdentifier);
                System.out.println("NLEP>>>Identifier = " + patientIdentifier + " added to patient");
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Add Name to LeprosyPatient">
                PersonName name = new PersonName();
                name.setGivenName(givenName);
                name.setMiddleName(middleName);
                name.setFamilyName(familyName);
                leprosyPatient.addName(name);
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Add Address to LeprosyPatient">
                PersonAddress address = new PersonAddress();
                address.setAddress1(resAddress1);
                address.setAddress2(resAddress2);
                address.setCityVillage(cityVillage);
                address.setPostalCode(zipCode);
                address.setRegion(region);
                address.setSubregion(subRegion);
                address.setTownshipDivision(division);
                leprosyPatient.addAddress(address);
                System.out.println("NLEP>>>Address = " + address + " added to patient");
                System.out.println("NLEP>>>Addresses set on patient");
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Add Birthdate & Gender to LeprosyPatient">
                SimpleDateFormat myDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                leprosyPatient.setBirthdate(myDateFormat.parse(dob));
                leprosyPatient.setGender(gender);
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Add Attributes (CasteCategory) to LeprosyPatient">
                PersonAttribute casteCategory = new PersonAttribute(new PersonAttributeType(8), category);
                TreeSet attribSet = new TreeSet();
                attribSet.add(casteCategory);
                leprosyPatient.setAttributes(attribSet);
                System.out.println("NLEP>>>Category = " + category + " is set on patient");
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Save LeprosyPatient">
                System.out.println("NLEP>>>Now Saving...");
                patientService.savePatient(leprosyPatient);
                System.out.println("NLEP>>>Saved Successfully");
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Add DiseaseType, regDate, detectionMode and voidedReason to Enroll LeprosyPatient">
                ProgramWorkflowService progService = Context.getProgramWorkflowService();
                Program program = new Program();
                if (diseaseType.equals("PB")) {
                    program = progService.getProgram(5);
                } else if (diseaseType.equals("MB")) {
                    program = progService.getProgram(6);
                }
                PatientProgram leprosyPatientProg = new PatientProgram();
                leprosyPatientProg.setProgram(program);
                leprosyPatientProg.setPatient(leprosyPatient);
                leprosyPatientProg.setDateEnrolled(myDateFormat.parse(regDate));
                leprosyPatientProg.setDateCompleted(myDateFormat.parse(dateRFT));
                UserService userService = Context.getUserService();
                leprosyPatientProg.setCreator(userService.getUserByUsername("DEFAULT" + detectionMode));
                if (!(voidedReason.equals(""))) {
                    leprosyPatientProg.setVoided(true);
                    leprosyPatientProg.setVoidReason(voidedReason);
                }
                progService.savePatientProgram(leprosyPatientProg);
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Add Initial Encounter (firstDose, caseDetection, relapseSelect, contacts) to LeprosyPatient">
                EncounterService encService = Context.getEncounterService();
                EncounterType encType = new EncounterType();
                if (leprosyPatient.getAge() <= 14) {
                    encType = encService.getEncounterType(3);
                } else {
                    encType = encService.getEncounterType(1);
                }
                Encounter initEnc = new Encounter();
                initEnc.setEncounterDatetime(myDateFormat.parse(firstDose));
                initEnc.setEncounterType(encType);
                // <editor-fold defaultstate="collapsed" desc="caseDetection Observation for Initial Encounter">
                Obs caseDetectionObs = new Obs();
                caseDetectionObs.setPerson(leprosyPatient);
                caseDetectionObs.setConcept(new Concept(1301));
                caseDetectionObs.setLocation(new Location(1));
                if (caseDetection.equals("RELAPSE")) {
                    caseDetectionObs.setValueText(caseDetection + "#" + relapseSelect);
                } else {
                    caseDetectionObs.setValueText(caseDetection);
                }
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="contacts Observation for Initial Encounter">
                Obs contactsObs = new Obs();
                contactsObs.setPerson(leprosyPatient);
                contactsObs.setConcept(new Concept(6102));
                contactsObs.setLocation(new Location(1));
                contactsObs.setValueText(contacts);
                //</editor-fold>
                initEnc.addObs(caseDetectionObs);
                initEnc.addObs(contactsObs);
                initEnc.setPatient(leprosyPatient);
                initEnc.setLocation(new Location(1));
                initEnc.setProvider(userService.getUserByUsername("DEFAULT" + detectionMode));
                encService.saveEncounter(initEnc);
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Deformity Encounters (deformity, deformityTime, deformityType)">
                if (leprosyPatient.getAge() <= 14) {
                    encType = encService.getEncounterType(4);
                } else {
                    encType = encService.getEncounterType(2);
                }
                Encounter deformityEnc = new Encounter();
                deformityEnc.setEncounterDatetime(new Date());
                deformityEnc.setEncounterType(encType);

                // <editor-fold defaultstate="collapsed" desc="Deformity Grade (deformity, deformityTime) Observation for Deformity Encounters">
                Obs deformityObs = new Obs();
                deformityObs.setPerson(leprosyPatient);
                deformityObs.setConcept(new Concept(6103));
                deformityObs.setLocation(new Location(1));
                deformityObs.setValueText(deformity + "#" + deformityTime);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Deformity In Part (deformityType) Observation for Deformity Encounters">
                Obs deformityTypeObs = new Obs();
                deformityTypeObs.setPerson(leprosyPatient);
                deformityTypeObs.setConcept(new Concept(6103));
                deformityTypeObs.setLocation(new Location(1));
                deformityTypeObs.setValueText(deformityType);
                //</editor-fold>

                deformityEnc.addObs(deformityObs);
                deformityEnc.addObs(deformityTypeObs);
                deformityEnc.setPatient(leprosyPatient);
                deformityEnc.setLocation(new Location(1));
                deformityEnc.setProvider(Context.getAuthenticatedUser());
                encService.saveEncounter(deformityEnc);
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="RCS Encounters (rcsEligible, rcsReferred, rcsPart, rcsDate, rcsCenterName, rcsRepeat)">
                if (leprosyPatient.getAge() <= 14) {
                    encType = encService.getEncounterType(4);
                } else {
                    encType = encService.getEncounterType(2);
                }
                Encounter rcsEnc = new Encounter();
                rcsEnc.setEncounterDatetime(new Date());
                rcsEnc.setEncounterType(encType);

                // <editor-fold defaultstate="collapsed" desc="RCS Eligible (rcsEligible) Observation for RCS Encounters">
                Obs rcsEligibleObs = new Obs();
                rcsEligibleObs.setPerson(leprosyPatient);
                rcsEligibleObs.setConcept(new Concept(6104));
                rcsEligibleObs.setLocation(new Location(1));
                rcsEligibleObs.setValueText("ELIGIBLE" + "#" + rcsEligible);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Referred (rcsReferred) Observation for RCS Encounters">
                Obs rcsReferredObs = new Obs();
                rcsReferredObs.setPerson(leprosyPatient);
                rcsReferredObs.setConcept(new Concept(6104));
                rcsReferredObs.setLocation(new Location(1));
                rcsReferredObs.setValueText("REFERRED" + "#" + rcsReferred);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Done on Part (rcsPart) Observation for RCS Encounters">
                Obs rcsPartObs = new Obs();
                rcsPartObs.setPerson(leprosyPatient);
                rcsPartObs.setConcept(new Concept(6104));
                rcsPartObs.setLocation(new Location(1));
                rcsPartObs.setValueText("PARTS" + "#" + rcsPart);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Done on Date (rcsDate) Observation for RCS Encounters">
                Obs rcsDateObs = new Obs();
                rcsDateObs.setPerson(leprosyPatient);
                rcsDateObs.setConcept(new Concept(6104));
                rcsDateObs.setLocation(new Location(1));
                rcsDateObs.setValueText("DATE" + "#" + rcsDate);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Center Name (rcsCenterName) Observation for RCS Encounters">
                Obs rcsCenterNameObs = new Obs();
                rcsCenterNameObs.setPerson(leprosyPatient);
                rcsCenterNameObs.setConcept(new Concept(6104));
                rcsCenterNameObs.setLocation(new Location(1));
                rcsCenterNameObs.setValueText("CENTER_NAME" + "#" + rcsCenterName);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Repeated on Parts (rcsRepeat) Observation for RCS Encounters">
                Obs rcsRepeatObs = new Obs();
                rcsRepeatObs.setPerson(leprosyPatient);
                rcsRepeatObs.setConcept(new Concept(6104));
                rcsRepeatObs.setLocation(new Location(1));
                rcsRepeatObs.setValueText("REPEAT_PARTS" + "#" + rcsRepeat);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Patient Below Poverty Line (rcsPatientBPL) Observation for RCS Encounters">
                Obs rcsPatientBPLObs = new Obs();
                rcsPatientBPLObs.setPerson(leprosyPatient);
                rcsPatientBPLObs.setConcept(new Concept(6104));
                rcsPatientBPLObs.setLocation(new Location(1));
                rcsPatientBPLObs.setValueText("BPL" + "#" + rcsPatientBPL);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="RCS Reimbursement Amount (rcsReimbursementAmt) Observation for RCS Encounters">
                Obs rcsReimbursementAmtObs = new Obs();
                rcsReimbursementAmtObs.setPerson(leprosyPatient);
                rcsReimbursementAmtObs.setConcept(new Concept(6104));
                rcsReimbursementAmtObs.setLocation(new Location(1));
                rcsReimbursementAmtObs.setValueText("REIMBURSEMENT_AMT" + "#" + rcsReimbursementAmt);
                //</editor-fold>

                rcsEnc.addObs(rcsEligibleObs);
                rcsEnc.addObs(rcsReferredObs);
                rcsEnc.addObs(rcsPartObs);
                rcsEnc.addObs(rcsDateObs);
                rcsEnc.addObs(rcsCenterNameObs);
                rcsEnc.addObs(rcsRepeatObs);
                rcsEnc.addObs(rcsPatientBPLObs);
                rcsEnc.addObs(rcsReimbursementAmtObs);
                rcsEnc.setPatient(leprosyPatient);
                rcsEnc.setLocation(new Location(1));
                rcsEnc.setProvider(Context.getAuthenticatedUser());
                encService.saveEncounter(rcsEnc);
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Disability for MDT Encounters (disabilityMDT)">
                if (leprosyPatient.getAge() <= 14) {
                    encType = encService.getEncounterType(4);
                } else {
                    encType = encService.getEncounterType(2);
                }
                Encounter disabilityEnc = new Encounter();
                disabilityEnc.setEncounterDatetime(new Date());
                disabilityEnc.setEncounterType(encType);

                // <editor-fold defaultstate="collapsed" desc="Disability after MDT (disabilityMDT) Observation for Disability Encounters">
                Obs disabilityMDTObs = new Obs();
                disabilityMDTObs.setPerson(leprosyPatient);
                disabilityMDTObs.setConcept(new Concept(6103));
                disabilityMDTObs.setLocation(new Location(1));
                disabilityMDTObs.setValueText("NEW_DISABILITIES" + "#" + disabilityMDT);
                //</editor-fold>

                disabilityEnc.addObs(disabilityMDTObs);
                disabilityEnc.setPatient(leprosyPatient);
                disabilityEnc.setLocation(new Location(1));
                disabilityEnc.setProvider(Context.getAuthenticatedUser());
                encService.saveEncounter(disabilityEnc);
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Reaction Encounters (reactionTime, reactionMgmtAt, treatmentStartDate, treatmentEndDate)">
                if (leprosyPatient.getAge() <= 14) {
                    encType = encService.getEncounterType(4);
                } else {
                    encType = encService.getEncounterType(2);
                }
                Encounter reactionEnc = new Encounter();
                reactionEnc.setEncounterDatetime(new Date());
                reactionEnc.setEncounterType(encType);

                // <editor-fold defaultstate="collapsed" desc="When the Reaction Happened (reactionTime) Observation for Reaction Encounters">
                Obs reactionTimeObs = new Obs();
                reactionTimeObs.setPerson(leprosyPatient);
                reactionTimeObs.setConcept(new Concept(6105));
                reactionTimeObs.setLocation(new Location(1));
                reactionTimeObs.setValueText("OCCURANCE" + "#" + reactionTime);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Where Reaction Managed (reactionMgmtAt) Observation for Reaction Encounters">
                Obs reactionMgmtAtObs = new Obs();
                reactionMgmtAtObs.setPerson(leprosyPatient);
                reactionMgmtAtObs.setConcept(new Concept(6105));
                reactionMgmtAtObs.setLocation(new Location(1));
                reactionMgmtAtObs.setValueText("MANAGED_AT" + "#" + reactionMgmtAt);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Date of Starting Treatment (treatmentStartDate) Observation for Reaction Encounters">
                Obs treatmentStartObs = new Obs();
                treatmentStartObs.setPerson(leprosyPatient);
                treatmentStartObs.setConcept(new Concept(6105));
                treatmentStartObs.setLocation(new Location(1));
                treatmentStartObs.setValueText("TREATMENT_START_DATE" + "#" + treatmentStartDate);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Date of Ending Treatment (treatmentEndDate) Observation for Reaction Encounters">
                Obs treatmentEndObs = new Obs();
                treatmentEndObs.setPerson(leprosyPatient);
                treatmentEndObs.setConcept(new Concept(6105));
                treatmentEndObs.setLocation(new Location(1));
                treatmentEndObs.setValueText("TREATMENT_END_DATE" + "#" + treatmentEndDate);
                //</editor-fold>

                reactionEnc.addObs(reactionTimeObs);
                reactionEnc.addObs(reactionMgmtAtObs);
                reactionEnc.addObs(treatmentStartObs);
                reactionEnc.addObs(treatmentEndObs);
                reactionEnc.setPatient(leprosyPatient);
                reactionEnc.setLocation(new Location(1));
                reactionEnc.setProvider(Context.getAuthenticatedUser());
                encService.saveEncounter(reactionEnc);
                //</editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Services Encounters (serviceGiven, socioEconomicServices, jobType)">
                if (leprosyPatient.getAge() <= 14) {
                    encType = encService.getEncounterType(4);
                } else {
                    encType = encService.getEncounterType(2);
                }
                Encounter servicesEnc = new Encounter();
                servicesEnc.setEncounterDatetime(new Date());
                servicesEnc.setEncounterType(encType);

                // <editor-fold defaultstate="collapsed" desc="Services Given (servicesGiven) Observation for Services Encounters">
                Obs servicesGivenObs = new Obs();
                servicesGivenObs.setPerson(leprosyPatient);
                servicesGivenObs.setConcept(new Concept(6105));
                servicesGivenObs.setLocation(new Location(1));
                servicesGivenObs.setValueText("SERVICES_GIVEN" + "#" + servicesGiven);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Socio-Economic Services Given (socioEconomicServices) Observation for Services Encounters">
                Obs socioEconomicServicesObs = new Obs();
                socioEconomicServicesObs.setPerson(leprosyPatient);
                socioEconomicServicesObs.setConcept(new Concept(6105));
                socioEconomicServicesObs.setLocation(new Location(1));
                socioEconomicServicesObs.setValueText("SOCIOECONOMIC_SERVICES" + "#" + reactionMgmtAt);
                //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="JobType (jobType) Observation for Services Encounters">
                Obs jobTypeObs = new Obs();
                jobTypeObs.setPerson(leprosyPatient);
                jobTypeObs.setConcept(new Concept(6105));
                jobTypeObs.setLocation(new Location(1));
                jobTypeObs.setValueText("JOBTYPE" + "#" + treatmentStartDate);
                //</editor-fold>

                servicesEnc.addObs(servicesGivenObs);
                servicesEnc.addObs(socioEconomicServicesObs);
                servicesEnc.addObs(jobTypeObs);
                servicesEnc.setPatient(leprosyPatient);
                servicesEnc.setLocation(new Location(1));
                servicesEnc.setProvider(Context.getAuthenticatedUser());
                encService.saveEncounter(servicesEnc);
                //</editor-fold>

                Context.closeSession();
                System.out.println("NLEP>>>Close Context Session");
            }
        } finally {
        }

        if (flagTB != null && flagTB.equalsIgnoreCase("1")) {
            populateDHISLeprosyData();
        }

        return SUCCESS;
    }

    public int checkDigit(String idWithoutCheckdigit) throws InvalidIdentifierException {
        System.out.println("NLEP>>>inside checkDigit");

        // allowable characters within identifier
        String validChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_"; // MHMUMBAITHANE1096

        // remove leading or trailing whitespace, convert to uppercase
        idWithoutCheckdigit = idWithoutCheckdigit.trim().toUpperCase();

        // this will be a running total
        int sum = 0;

        // loop through digits from right to left
        for (int i = 0; i < idWithoutCheckdigit.length(); i++) {
            //set ch to "current" character to be processed
            char ch = idWithoutCheckdigit.charAt(idWithoutCheckdigit.length() - i - 1);

            // throw exception for invalid characters
            if (validChars.indexOf(ch) == -1) {
                throw new InvalidIdentifierException("\"" + ch + "\" is an invalid character");
            }
            // our "digit" is calculated using ASCII value - 48
            int digit = (int) ch - 48;

            // weight will be the current digit's contribution to the running total
            int weight;
            if (i % 2 == 0) {
                // for alternating digits starting with the rightmost, we use our formula this is the same as multiplying x 2 and
                // adding digits together for values 0 to 9.  Using the following formula allows us to gracefully calculate a
                // weight for non-numeric "digits" as well (from their ASCII value - 48).
                weight = (2 * digit) - (int) (digit / 5) * 9;

            } else {
                // even-positioned digits just contribute their ascii value minus 48
                weight = digit;
            }
            // keep a running total of weights
            sum += weight;
        }
        // avoid sum less than 10 (if characters below "0" allowed, this could happen)
        sum = Math.abs(sum) + 10;

        // check digit is amount needed to reach next number divisible by ten
        return (10 - (sum % 10)) % 10;
    }

    private Period getCurrentMonthlyPeriod() {
        int monthDays[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        Date sysDate = new Date();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(sysDate);

        MonthlyPeriodType periodType = new MonthlyPeriodType();

        Calendar cal = Calendar.getInstance();
        cal.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), 1, 0, 0, 0);
        Date firstDay = new Date(cal.getTimeInMillis());


        if (calendar1.get(Calendar.YEAR) % 4 == 0 && calendar1.get(Calendar.MONTH) == 1) {
            cal.set(Calendar.DAY_OF_MONTH, monthDays[calendar1.get(Calendar.MONTH)] + 1);
        } else {
            cal.set(Calendar.DAY_OF_MONTH, monthDays[calendar1.get(Calendar.MONTH)]);
        }

        Date lastDay = new Date(cal.getTimeInMillis());

        Period newPeriod = periodService.getPeriod(firstDay, lastDay, periodType);

        return newPeriod;

    }

    private void saveData(String deString) {
        String partsOfdeString[] = deString.split("\\.");

        int dataElementId = Integer.parseInt(partsOfdeString[0]);
        int optionComboId = Integer.parseInt(partsOfdeString[1]);

        DataElement dataElement = dataElementService.getDataElement(dataElementId);
        DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo(optionComboId);

        if (dataElement == null || optionCombo == null) {
        } else {
            DataValue dataValue = dataValueService.getDataValue(orgUnit, dataElement, period, optionCombo);
            //DataValue dataValue = null;

            if (dataValue == null) {
                String value = "1";

                if (value != null) {
                    dataValue = new DataValue(dataElement, period, orgUnit, value, "admin", new Date(), null, optionCombo);
                    dataValueService.addDataValue(dataValue);
                }
            } else {
                int val = Integer.parseInt(dataValue.getValue());
                val++;

                dataValue.setValue("" + val);
                dataValue.setTimestamp(new Date());
                dataValue.setStoredBy("admin");

                dataValueService.updateDataValue(dataValue);
            }
        }
    }

    public double getAge() {
        Calendar calendar1 = Calendar.getInstance();

        int dayOfBirth = Integer.parseInt(dob.substring(0, 2));
        int monthOfBirth = Integer.parseInt(dob.substring(3, 5));
        int yearOfBirth = Integer.parseInt(dob.substring(6, 10));

        calendar1.set(yearOfBirth, monthOfBirth, dayOfBirth);

        Calendar calendar2 = Calendar.getInstance();
        Date sysDate = new Date();
        calendar2.setTime(sysDate);

        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        long diffYear = diff / (24 * 60 * 60 * 1000 * 365);
        System.out.println("Years : " + diffYear);

        return diffYear;
    }

    public void populateDHISLeprosyData() {

        // <editor-fold defaultstate="collapsed" desc="PART1 - Required for SIS Report">
        // <editor-fold defaultstate="collapsed" desc="NEW">
        if (caseDetection.equalsIgnoreCase("NEW")) {
            // <editor-fold defaultstate="collapsed" desc="New PB">
            if (diseaseType.equalsIgnoreCase("PB")) {
                // <editor-fold defaultstate="collapsed" desc="PB Male">
                if (gender.equalsIgnoreCase("M")) {
                    // <editor-fold defaultstate="collapsed" desc="PB Male SC">
                    if (category.equalsIgnoreCase("SC")) {
                        // <editor-fold defaultstate="collapsed" desc="PB Male SC - Adult/Child">
                        if (getAge() > 14) {
                            //No. of new leprosy cases ADULT PB, Male S.C - 2501
                            saveData(NLEPDataElements.NLEP_DE001);
                        } else {
                            //No. of new leprosy cases Child PB, Male S.C - 2513
                            saveData(NLEPDataElements.NLEP_DE013);
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Deformity PB Male SC">
                        if (deformity != null) {
                            if (deformity.equalsIgnoreCase("Grade1")) {
                                //No. of deformity case amoung new leprosy cases Grade 1 PB, Male S.C - 2525
                                saveData(NLEPDataElements.NLEP_DE025);
                            } else if (deformity.equalsIgnoreCase("Grade2")) {
                                //No. of deformity case amoung new leprosy cases Grade 2 PB, Male S.C - 2537
                                saveData(NLEPDataElements.NLEP_DE037);
                            }
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="PB Male SC Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of new leprosy cases RFT PB, Male S.C - 2549
                            saveData(NLEPDataElements.NLEP_DE049);
                        } else if (voidedReason != null) {
                            //No. of new leprosy cases deleted others PB, Male S.C - 2561
                            saveData(NLEPDataElements.NLEP_DE061);
                        }
                        //</editor-fold>
                    } //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="PB Male ST">
                    else if (category.equalsIgnoreCase("ST")) {
                        // <editor-fold defaultstate="collapsed" desc="PB Male ST - Adult/Child">
                        if (getAge() > 14) {
                            //No. of new leprosy cases ADULT PB, Male S.T - 2503
                            saveData(NLEPDataElements.NLEP_DE003);
                        } else {
                            //No. of new leprosy cases Child PB, Male S.T - 2515
                            saveData(NLEPDataElements.NLEP_DE015);
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Deformity PB Male ST">
                        if (deformity != null) {
                            if (deformity.equalsIgnoreCase("Grade1")) {
                                //No. of deformity case amoung new leprosy cases Grade 1 PB, Male S.T - 2527
                                saveData(NLEPDataElements.NLEP_DE027);
                            } else if (deformity.equalsIgnoreCase("Grade2")) {
                                //No. of deformity case amoung new leprosy cases Grade 2 PB, Male S.T - 2539
                                saveData(NLEPDataElements.NLEP_DE039);
                            }
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="PB Male ST Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of new leprosy cases RFT PB, Male S.T - 2551
                            saveData(NLEPDataElements.NLEP_DE051);
                        } else if (voidedReason != null) {
                            //No. of new leprosy cases deleted others PB, Male S.T - 2563
                            saveData(NLEPDataElements.NLEP_DE063);
                        }
                        //</editor-fold>
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="PB Male Total">
                    // <editor-fold defaultstate="collapsed" desc="PB Male Total - Adult/Child">
                    if (getAge() > 14) {
                        //No. of new leprosy cases ADULT PB, Male total - 2505
                        saveData(NLEPDataElements.NLEP_DE005);
                    } else {
                        //No. of new leprosy cases Child PB, Male total - 2517
                        saveData(NLEPDataElements.NLEP_DE017);
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Deformity PB Male Total">
                    if (deformity != null) {
                        if (deformity.equalsIgnoreCase("Grade1")) {
                            //No. of deformity case amoung new leprosy cases Grade 1 PB, Male total - 2529
                            saveData(NLEPDataElements.NLEP_DE029);
                        } else if (deformity.equalsIgnoreCase("Grade2")) {
                            //No. of deformity case amoung new leprosy cases Grade 2 PB, Male total - 2541
                            saveData(NLEPDataElements.NLEP_DE041);
                        }
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="PB Male Total Deleted/RFT">
                    if (dateRFT != null) {
                        //No. of new leprosy cases RFT PB, Male Total - 2553
                        saveData(NLEPDataElements.NLEP_DE053);
                    } else if (voidedReason != null) {
                        //No. of new leprosy cases deleted others PB, Male Total - 2565
                        saveData(NLEPDataElements.NLEP_DE065);
                    }
                    //</editor-fold>
                    //</editor-fold>
                } //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="PB Female">
                else if (gender.equalsIgnoreCase("F")) {
                    // <editor-fold defaultstate="collapsed" desc="PB Female SC">
                    if (category.equalsIgnoreCase("SC")) {
                        // <editor-fold defaultstate="collapsed" desc="PB Female SC - Adult/Child">
                        if (getAge() > 14) {
                            //No. of new leprosy cases ADULT PB, Female S.C - 2502
                            saveData(NLEPDataElements.NLEP_DE002);
                        } else {
                            //No. of new leprosy cases Child PB, Female S.C - 2514
                            saveData(NLEPDataElements.NLEP_DE014);
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Deformity PB Female SC">
                        if (deformity != null) {
                            if (deformity.equalsIgnoreCase("Grade1")) {
                                //No. of deformity case amoung new leprosy cases Grade 1 PB, Female S.C - 2526
                                saveData(NLEPDataElements.NLEP_DE026);
                            } else if (deformity.equalsIgnoreCase("Grade2")) {
                                //No. of deformity case amoung new leprosy cases Grade 2 PB, Female S.C - 2538
                                saveData(NLEPDataElements.NLEP_DE038);
                            }
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="PB Female SC Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of new leprosy cases RFT PB, Female S.C - 2550
                            saveData(NLEPDataElements.NLEP_DE050);
                        } else if (voidedReason != null) {
                            //No. of new leprosy cases deleted others PB, Female S.C - 2562
                            saveData(NLEPDataElements.NLEP_DE062);
                        }
                        //</editor-fold>
                    } //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="PB Female ST">
                    else if (category.equalsIgnoreCase("ST")) {
                        // <editor-fold defaultstate="collapsed" desc="PB Female ST - Adult/Child">
                        if (getAge() > 14) {
                            //No. of new leprosy cases ADULT PB, Female S.T - 2504
                            saveData(NLEPDataElements.NLEP_DE004);
                        } else {
                            //No. of new leprosy cases Child PB, Female S.T - 2516
                            saveData(NLEPDataElements.NLEP_DE016);
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Deformity PB Female ST">
                        if (deformity != null) {
                            if (deformity.equalsIgnoreCase("Grade1")) {
                                //No. of deformity case amoung new leprosy cases Grade 1 PB, Female S.T - 2528
                                saveData(NLEPDataElements.NLEP_DE028);
                            } else if (deformity.equalsIgnoreCase("Grade2")) {
                                //No. of deformity case amoung new leprosy cases Grade 2 PB, Female S.T - 2540
                                saveData(NLEPDataElements.NLEP_DE040);
                            }
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="PB Female ST Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of new leprosy cases RFT PB, Female S.T - 2552
                            saveData(NLEPDataElements.NLEP_DE052);
                        } else if (voidedReason != null) {
                            //No. of new leprosy cases deleted others PB, Female S.T - 2564
                            saveData(NLEPDataElements.NLEP_DE064);
                        }
                        //</editor-fold>
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="PB Female Total">
                    // <editor-fold defaultstate="collapsed" desc="PB Female Total - Adult/Child">
                    if (getAge() > 14) {
                        //No. of new leprosy cases ADULT PB, Female total - 2506
                        saveData(NLEPDataElements.NLEP_DE006);
                    } else {
                        //No. of new leprosy cases Child PB, Female total - 2518
                        saveData(NLEPDataElements.NLEP_DE018);
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Deformity PB Female Total">
                    if (deformity != null) {
                        if (deformity.equalsIgnoreCase("Grade1")) {
                            //No. of deformity case amoung new leprosy cases Grade 1 PB, Female total - 2530
                            saveData(NLEPDataElements.NLEP_DE030);
                        } else if (deformity.equalsIgnoreCase("Grade2")) {
                            //No. of deformity case amoung new leprosy cases Grade 2 PB, Female total - 2542
                            saveData(NLEPDataElements.NLEP_DE042);
                        }
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="PB Female Total Deleted/RFT">
                    if (dateRFT != null) {
                        //No. of new leprosy cases RFT PB, Female total - 2554
                        saveData(NLEPDataElements.NLEP_DE054);
                    } else if (voidedReason != null) {
                        //No. of new leprosy cases deleted others PB, Female total - 2566
                        saveData(NLEPDataElements.NLEP_DE066);
                    }
                    //</editor-fold>
                    //</editor-fold>
                }
            } //</editor-fold>
            //</editor-fold>
            // <editor-fold defaultstate="collapsed" desc="New MB">
            else if (diseaseType.equalsIgnoreCase("MB")) {
                // <editor-fold defaultstate="collapsed" desc="MB Male">
                if (gender.equalsIgnoreCase("M")) {
                    // <editor-fold defaultstate="collapsed" desc="MB Male SC">
                    if (category.equalsIgnoreCase("SC")) {
                        // <editor-fold defaultstate="collapsed" desc="MB Male SC Adult/Child">
                        if (getAge() > 14) {
                            //No. of new leprosy cases ADULT MB, Male S.C - 2507
                            saveData(NLEPDataElements.NLEP_DE007);
                        } else {
                            //No. of new leprosy cases Child MB, Male S.C - 2519
                            saveData(NLEPDataElements.NLEP_DE019);
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Deformity MB Male SC">
                        if (deformity != null) {
                            if (deformity.equalsIgnoreCase("Grade1")) {
                                //No. of deformity case amoung new leprosy cases Grade 1 MB, Male S.C - 2531
                                saveData(NLEPDataElements.NLEP_DE031);
                            } else if (deformity.equalsIgnoreCase("Grade2")) {
                                //No. of deformity case amoung new leprosy cases Grade 2 MB, Male S.C - 2543
                                saveData(NLEPDataElements.NLEP_DE043);
                            }
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="MB Male SC Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of new leprosy cases RFT MB, Male S.C - 2555
                            saveData(NLEPDataElements.NLEP_DE055);
                        } else if (voidedReason != null) {
                            //No. of new leprosy cases deleted others MB, Male S.C - 2567
                            saveData(NLEPDataElements.NLEP_DE067);
                        }
                        //</editor-fold>
                    } //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="MB Male ST">
                    else if (category.equalsIgnoreCase("ST")) {
                        // <editor-fold defaultstate="collapsed" desc="MB Male ST Adult/Child">
                        if (getAge() > 14) {
                            //No. of new leprosy cases ADULT MB, Male S.T - 2509
                            saveData(NLEPDataElements.NLEP_DE009);
                        } else {
                            //No. of new leprosy cases Child MB, Male S.T - 2521
                            saveData(NLEPDataElements.NLEP_DE021);
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Deformity MB Male ST">
                        if (deformity != null) {
                            if (deformity.equalsIgnoreCase("Grade1")) {
                                //No. of deformity case amoung new leprosy cases Grade 1 MB, Male S.T - 2533
                                saveData(NLEPDataElements.NLEP_DE033);
                            } else if (deformity.equalsIgnoreCase("Grade2")) {
                                //No. of deformity case amoung new leprosy cases Grade 2 MB, Male S.T - 2545
                                saveData(NLEPDataElements.NLEP_DE045);
                            }
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="MB Male ST Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of new leprosy cases RFT MB, Male S.T - 2557
                            saveData(NLEPDataElements.NLEP_DE057);
                        } else if (voidedReason != null) {
                            //No. of new leprosy cases deleted others MB, Male S.T - 2569
                            saveData(NLEPDataElements.NLEP_DE069);
                        }
                        //</editor-fold>
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="MB Male Total">
                    // <editor-fold defaultstate="collapsed" desc="MB Male Total Adult/Child">
                    if (getAge() > 14) {
                        //No. of new leprosy cases ADULT MB, Male total - 2511
                        saveData(NLEPDataElements.NLEP_DE011);
                    } else {
                        //No. of new leprosy cases Child MB, Male total - 2523
                        saveData(NLEPDataElements.NLEP_DE023);
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Deformity MB Male Total">
                    if (deformity != null) {
                        if (deformity.equalsIgnoreCase("Grade1")) {
                            //No. of deformity case amoung new leprosy cases Grade 1 MB, Male total - 2535
                            saveData(NLEPDataElements.NLEP_DE035);
                        } else if (deformity.equalsIgnoreCase("Grade2")) {
                            //No. of deformity case amoung new leprosy cases Grade 2 MB, Male total - 2547
                            saveData(NLEPDataElements.NLEP_DE047);
                        }
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="MB Male Total Deleted/RFT">
                    if (dateRFT != null) {
                        //No. of new leprosy cases RFT MB, Male Total - 2559
                        saveData(NLEPDataElements.NLEP_DE059);
                    } else if (voidedReason != null) {
                        //No. of new leprosy cases deleted others MB, Male Total - 2571
                        saveData(NLEPDataElements.NLEP_DE071);
                    }
                    //</editor-fold>
                    //</editor-fold>
                } //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="MB Female">
                else if (gender.equalsIgnoreCase("F")) {
                    // <editor-fold defaultstate="collapsed" desc="MB Female SC">
                    if (category.equalsIgnoreCase("SC")) {
                        // <editor-fold defaultstate="collapsed" desc="MB Female SC Adult/Child">
                        if (getAge() > 14) {
                            //No. of new leprosy cases ADULT MB, Female S.C - 2508
                            saveData(NLEPDataElements.NLEP_DE008);
                        } else {
                            //No. of new leprosy cases Child MB, Female S.C - 2520
                            saveData(NLEPDataElements.NLEP_DE020);
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Deformity MB Female SC">
                        if (deformity != null) {
                            if (deformity.equalsIgnoreCase("Grade1")) {
                                //No. of deformity case amoung new leprosy cases Grade 1 MB, Female S.C - 2532
                                saveData(NLEPDataElements.NLEP_DE032);
                            } else if (deformity.equalsIgnoreCase("Grade2")) {
                                //No. of deformity case amoung new leprosy cases Grade 2 MB, Female S.C - 2544
                                saveData(NLEPDataElements.NLEP_DE044);
                            }
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="MB Female SC Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of new leprosy cases RFT MB, Female S.C - 2556
                            saveData(NLEPDataElements.NLEP_DE056);
                        } else if (voidedReason != null) {
                            //No. of new leprosy cases deleted others MB, Female S.C - 2568
                            saveData(NLEPDataElements.NLEP_DE068);
                        }
                        //</editor-fold>
                    } //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="MB Female ST">
                    else if (category.equalsIgnoreCase("ST")) {
                        // <editor-fold defaultstate="collapsed" desc="MB Female ST Adult/Child">
                        if (getAge() > 14) {
                            //No. of new leprosy cases ADULT MB, Female S.T - 2510
                            saveData(NLEPDataElements.NLEP_DE010);
                        } else {
                            //No. of new leprosy cases Child MB, Female S.T - 2522
                            saveData(NLEPDataElements.NLEP_DE022);
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="Deformity MB Female ST">
                        if (deformity != null) {
                            if (deformity.equalsIgnoreCase("Grade1")) {
                                //No. of deformity case amoung new leprosy cases Grade 1 MB, Female S.T - 2534
                                saveData(NLEPDataElements.NLEP_DE034);
                            } else if (deformity.equalsIgnoreCase("Grade2")) {
                                //No. of deformity case amoung new leprosy cases Grade 2 MB, Female S.T - 2546
                                saveData(NLEPDataElements.NLEP_DE046);
                            }
                        }
                        //</editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="MB Female ST Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of new leprosy cases RFT MB, Female S.T - 2558
                            saveData(NLEPDataElements.NLEP_DE058);
                        } else if (voidedReason != null) {
                            //No. of new leprosy cases deleted others MB, Female S.T - 2570
                            saveData(NLEPDataElements.NLEP_DE070);
                        }
                        //</editor-fold>
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="MB Female Total">
                    // <editor-fold defaultstate="collapsed" desc="MB Female Total Adult/Child">
                    if (getAge() > 14) {
                        //No. of new leprosy cases ADULT MB, Female total - 2512
                        saveData(NLEPDataElements.NLEP_DE012);
                    } else {
                        //No. of new leprosy cases Child MB, Female total - 2524
                        saveData(NLEPDataElements.NLEP_DE024);
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Deformity MB Female Total">
                    if (deformity != null) {
                        if (deformity.equalsIgnoreCase("Grade1")) {
                            //No. of deformity case amoung new leprosy cases Grade 1 MB, Female total - 2536
                            saveData(NLEPDataElements.NLEP_DE036);
                        } else if (deformity.equalsIgnoreCase("Grade2")) {
                            //No. of deformity case amoung new leprosy cases Grade 2 MB, Female total - 2548
                            saveData(NLEPDataElements.NLEP_DE048);
                        }
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="MB Female total Deleted/RFT">
                    if (dateRFT != null) {
                        //No. of new leprosy cases RFT MB, Female total - 2560
                        saveData(NLEPDataElements.NLEP_DE060);
                    } else if (voidedReason != null) {
                        //No. of new leprosy cases deleted others MB, Female total - 2572
                        saveData(NLEPDataElements.NLEP_DE072);
                    }
                    //</editor-fold>
                    //</editor-fold>
                }
                //</editor-fold>
            }
            //</editor-fold>
        } //</editor-fold>
        // <editor-fold defaultstate="collapsed" desc="OTHER CASES">
        else {
            // <editor-fold defaultstate="collapsed" desc="Other PB">
            if (diseaseType.equalsIgnoreCase("PB")) {
                // <editor-fold defaultstate="collapsed" desc="Other PB Male">
                if (gender.equalsIgnoreCase("M")) {
                    // <editor-fold defaultstate="collapsed" desc="Other PB Male SC">
                    if (category.equalsIgnoreCase("SC")) {
                        if (caseDetection.equalsIgnoreCase("RELAPSE")) {
                            //No. of other cases recorded & put under treatment, Relapse PB, Male S.C - 2573
                            saveData(NLEPDataElements.NLEP_DE073);
                        } else if (caseDetection.equalsIgnoreCase("RE-ENTERED")) {
                            //No. of other cases recorded & put under treatment, Re-entered PB, Male S.C - 2585
                            saveData(NLEPDataElements.NLEP_DE085);
                        } else if (caseDetection.equalsIgnoreCase("REFERRED")) {
                            //No. of other cases recorded & put under treatment, Referred PB, Male S.C - 2597
                            saveData(NLEPDataElements.NLEP_DE097);
                        } else if (caseDetection.equalsIgnoreCase("RE-CLASSIFIED")) {
                            //No. of other cases recorded & put under treatment, Re-classified PB, Male S.C - 2609
                            saveData(NLEPDataElements.NLEP_DE109);
                        } else if (caseDetection.equalsIgnoreCase("OTHER-STATE")) {
                            //No. of other cases recorded & put under treatment, Other state PB, Male S.C - 2621
                            saveData(NLEPDataElements.NLEP_DE121);
                        }
                        // <editor-fold defaultstate="collapsed" desc="Other PB Male SC Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of other leprosy cases RFT PB, Male S.C - 2633
                            saveData(NLEPDataElements.NLEP_DE133);
                        } else if (voidedReason != null) {
                            //No. of other leprosy cases deleted others PB, Male S.C - 2645
                            saveData(NLEPDataElements.NLEP_DE145);
                        }
                        //</editor-fold>
                    } //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Other PB Male ST">
                    else if (category.equalsIgnoreCase("ST")) {
                        if (caseDetection.equalsIgnoreCase("RELAPSE")) {
                            //No. of other cases recorded & put under treatment, Relapse PB, Male S.T - 2575
                            saveData(NLEPDataElements.NLEP_DE075);
                        } else if (caseDetection.equalsIgnoreCase("RE-ENTERED")) {
                            //No. of other cases recorded & put under treatment, Reentered PB, Male S.T - 2587
                            saveData(NLEPDataElements.NLEP_DE087);
                        } else if (caseDetection.equalsIgnoreCase("REFERRED")) {
                            //No. of other cases recorded & put under treatment, Referred PB, Male S.T - 2599
                            saveData(NLEPDataElements.NLEP_DE099);
                        } else if (caseDetection.equalsIgnoreCase("RE-CLASSIFIED")) {
                            //No. of other cases recorded & put under treatment, Re-classified PB, Male S.T - 2611
                            saveData(NLEPDataElements.NLEP_DE111);
                        } else if (caseDetection.equalsIgnoreCase("OTHER-STATE")) {
                            //No. of other cases recorded & put under treatment, Other state PB, Male S.T - 2623
                            saveData(NLEPDataElements.NLEP_DE123);
                        }
                        // <editor-fold defaultstate="collapsed" desc="Other PB Male st Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of other leprosy cases RFT PB, Male S.T - 2635
                            saveData(NLEPDataElements.NLEP_DE135);
                        } else if (voidedReason != null) {
                            //No. of other leprosy cases deleted others PB, Male S.T - 2647
                            saveData(NLEPDataElements.NLEP_DE147);
                        }
                        //</editor-fold>
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Other PB Male Total">
                    if (caseDetection.equalsIgnoreCase("RELAPSE")) {
                        //No. of other cases recorded & put under treatment, Relapse PB, Male total - 2577
                        saveData(NLEPDataElements.NLEP_DE077);
                    } else if (caseDetection.equalsIgnoreCase("RE-ENTERED")) {
                        //No. of other cases recorded & put under treatment, Reenter PB, Male total - 2589
                        saveData(NLEPDataElements.NLEP_DE089);
                    } else if (caseDetection.equalsIgnoreCase("REFERRED")) {
                        //No. of other cases recorded & put under treatment, Referred PB, Male total - 2601
                        saveData(NLEPDataElements.NLEP_DE101);
                    } else if (caseDetection.equalsIgnoreCase("RE-CLASSIFIED")) {
                        //No. of other cases recorded & put under treatment, Reclassified PB, Male total - 2613
                        saveData(NLEPDataElements.NLEP_DE113);
                    } else if (caseDetection.equalsIgnoreCase("OTHER-STATE")) {
                        //No. of other cases recorded & put under treatment, Other state PB, Male total - 2625
                        saveData(NLEPDataElements.NLEP_DE125);
                    }
                    // <editor-fold defaultstate="collapsed" desc="Other PB Male total Deleted/RFT">
                    if (dateRFT != null) {
                        //No. of other leprosy cases RFT PB, Male total - 2637
                        saveData(NLEPDataElements.NLEP_DE137);
                    } else if (voidedReason != null) {
                        //No. of other leprosy cases deleted others PB, Male total - 2649
                        saveData(NLEPDataElements.NLEP_DE149);
                    }
                    //</editor-fold>
                    //</editor-fold>
                } //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Other PB Female">
                else if (gender.equalsIgnoreCase("F")) {
                    // <editor-fold defaultstate="collapsed" desc="Other PB Female SC">
                    if (category.equalsIgnoreCase("SC")) {
                        if (caseDetection.equalsIgnoreCase("RELAPSE")) {
                            //No. of other cases recorded & put under treatment, Relapse PB, Female S.C - 2574
                            saveData(NLEPDataElements.NLEP_DE074);
                        } else if (caseDetection.equalsIgnoreCase("RE-ENTERED")) {
                            //No. of other cases recorded & put under treatment, Reentered PB, Female S.C - 2586
                            saveData(NLEPDataElements.NLEP_DE086);
                        } else if (caseDetection.equalsIgnoreCase("REFERRED")) {
                            //No. of other cases recorded & put under treatment, Referred PB, Female S.C - 2598
                            saveData(NLEPDataElements.NLEP_DE098);
                        } else if (caseDetection.equalsIgnoreCase("RE-CLASSIFIED")) {
                            //No. of other cases recorded & put under treatment, Reclassified PB, Female S.C - 2610
                            saveData(NLEPDataElements.NLEP_DE110);
                        } else if (caseDetection.equalsIgnoreCase("OTHER-STATE")) {
                            //No. of other cases recorded & put under treatment, Other state PB, Female S.C - 2622
                            saveData(NLEPDataElements.NLEP_DE122);
                        }
                        // <editor-fold defaultstate="collapsed" desc="Other PB Female SC Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of other leprosy cases RFT PB, Female S.C - 2634
                            saveData(NLEPDataElements.NLEP_DE134);
                        } else if (voidedReason != null) {
                            //No. of other leprosy cases deleted others PB, Female S.C - 2669
                            saveData(NLEPDataElements.NLEP_DE170);
                        }
                        //</editor-fold>
                    } //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Other PB Female ST">
                    else if (category.equalsIgnoreCase("ST")) {
                        if (caseDetection.equalsIgnoreCase("RELAPSE")) {
                            //No. of other cases recorded & put under treatment, Relapse PB, Female S.T - 2576
                            saveData(NLEPDataElements.NLEP_DE076);
                        } else if (caseDetection.equalsIgnoreCase("RE-ENTERED")) {
                            //No. of other cases recorded & put under treatment, Reentered PB, Female S.T - 2588
                            saveData(NLEPDataElements.NLEP_DE088);
                        } else if (caseDetection.equalsIgnoreCase("REFERRED")) {
                            //No. of other cases recorded & put under treatment, Referred PB, Female S.T - 2600
                            saveData(NLEPDataElements.NLEP_DE100);
                        } else if (caseDetection.equalsIgnoreCase("RE-CLASSIFIED")) {
                            //No. of other cases recorded & put under treatment, Reclassified PB, Female S.T - 2612
                            saveData(NLEPDataElements.NLEP_DE112);
                        } else if (caseDetection.equalsIgnoreCase("OTHER-STATE")) {
                            //No. of other cases recorded & put under treatment, Other state PB, Female S.T - 2624
                            saveData(NLEPDataElements.NLEP_DE124);
                        }
                        // <editor-fold defaultstate="collapsed" desc="Other PB Female SC Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of other leprosy cases RFT PB, Female S.T - 2636
                            saveData(NLEPDataElements.NLEP_DE136);
                        } else if (voidedReason != null) {
                            //No. of other leprosy cases deleted others PB, Female S.T - 2648
                            saveData(NLEPDataElements.NLEP_DE148);
                        }
                        //</editor-fold>
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Other PB Female Total">
                    if (caseDetection.equalsIgnoreCase("RELAPSE")) {
                        //No. of other cases recorded & put under treatment, Relapse PB, Female total - 2578
                        saveData(NLEPDataElements.NLEP_DE078);
                    } else if (caseDetection.equalsIgnoreCase("RE-ENTERED")) {
                        //No. of other cases recorded & put under treatment, Reentered PB, Female total - 2590
                        saveData(NLEPDataElements.NLEP_DE090);
                    } else if (caseDetection.equalsIgnoreCase("REFERRED")) {
                        //No. of other cases recorded & put under treatment, Referred PB, Female total - 2602
                        saveData(NLEPDataElements.NLEP_DE102);
                    } else if (caseDetection.equalsIgnoreCase("RE-CLASSIFIED")) {
                        //No. of other cases recorded & put under treatment, Reclassified PB, Female total - 2614
                        saveData(NLEPDataElements.NLEP_DE114);
                    } else if (caseDetection.equalsIgnoreCase("OTHER-STATE")) {
                        //No. of other cases recorded & put under treatment, Other state PB, Female total - 2626
                        saveData(NLEPDataElements.NLEP_DE126);
                    }
                    // <editor-fold defaultstate="collapsed" desc="Other PB Female total Deleted/RFT">
                    if (dateRFT != null) {
                        //No. of other leprosy cases RFT PB, Female total - 2638
                        saveData(NLEPDataElements.NLEP_DE138);
                    } else if (voidedReason != null) {
                        //No. of other leprosy cases deleted others PB, Female total - 2650
                        saveData(NLEPDataElements.NLEP_DE150);
                    }
                    //</editor-fold>
                    //</editor-fold>
                }
                //</editor-fold>
            } //</editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Other MB">
            else if (diseaseType.equalsIgnoreCase("MB")) {
                // <editor-fold defaultstate="collapsed" desc="Other MB Male">
                if (gender.equalsIgnoreCase("M")) {
                    // <editor-fold defaultstate="collapsed" desc="Other MB Male SC">
                    if (category.equalsIgnoreCase("SC")) {
                        if (caseDetection.equalsIgnoreCase("RELAPSE")) {
                            //No. of other cases recorded & put under treatment, Relapse MB, Male S.C - 2579
                            saveData(NLEPDataElements.NLEP_DE079);
                        } else if (caseDetection.equalsIgnoreCase("RE-ENTERED")) {
                            //No. of other cases recorded & put under treatment, Reentered MB, Male S.C - 2591
                            saveData(NLEPDataElements.NLEP_DE091);
                        } else if (caseDetection.equalsIgnoreCase("REFERRED")) {
                            //No. of other cases recorded & put under treatment, Referred MB, Male S.C - 2603
                            saveData(NLEPDataElements.NLEP_DE103);
                        } else if (caseDetection.equalsIgnoreCase("RE-CLASSIFIED")) {
                            //No. of other cases recorded & put under treatment, Reclassified MB, Male S.C - 2615
                            saveData(NLEPDataElements.NLEP_DE115);
                        } else if (caseDetection.equalsIgnoreCase("OTHER-STATE")) {
                            //No. of other cases recorded & put under treatment, Other state MB, Male S.C - 2627
                            saveData(NLEPDataElements.NLEP_DE127);
                        }
                        // <editor-fold defaultstate="collapsed" desc="Other MB Male SC Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of other leprosy cases RFT MB, Male S.C - 2639
                            saveData(NLEPDataElements.NLEP_DE139);
                        } else if (voidedReason != null) {
                            //No. of other leprosy cases deleted others MB, Male S.C - 2651
                            saveData(NLEPDataElements.NLEP_DE151);
                        }
                        //</editor-fold>
                    } //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Other MB Male ST">
                    else if (category.equalsIgnoreCase("ST")) {
                        if (caseDetection.equalsIgnoreCase("RELAPSE")) {
                            //No. of other cases recorded & put under treatment, Relapse MB, Male S.T - 2581
                            saveData(NLEPDataElements.NLEP_DE081);
                        } else if (caseDetection.equalsIgnoreCase("RE-ENTERED")) {
                            //No. of other cases recorded & put under treatment, Reentered MB, Male S.T - 2593
                            saveData(NLEPDataElements.NLEP_DE093);
                        } else if (caseDetection.equalsIgnoreCase("REFERRED")) {
                            //No. of other cases recorded & put under treatment, Referred MB, Male S.T - 2605
                            saveData(NLEPDataElements.NLEP_DE105);
                        } else if (caseDetection.equalsIgnoreCase("RE-CLASSIFIED")) {
                            //No. of other cases recorded & put under treatment, Reclassified MB, Male S.T - 2617
                            saveData(NLEPDataElements.NLEP_DE117);
                        } else if (caseDetection.equalsIgnoreCase("OTHER-STATE")) {
                            //No. of other cases recorded & put under treatment, Other state MB, Male S.T - 2629
                            saveData(NLEPDataElements.NLEP_DE129);
                        }
                        // <editor-fold defaultstate="collapsed" desc="Other MB Male ST Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of other leprosy cases RFT MB, Male S.T - 2641
                            saveData(NLEPDataElements.NLEP_DE141);
                        } else if (voidedReason != null) {
                            //No. of other leprosy cases deleted others MB, Male S.T - 2653
                            saveData(NLEPDataElements.NLEP_DE153);
                        }
                        //</editor-fold>
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Other MB Male Total">
                    if (caseDetection.equalsIgnoreCase("RELAPSE")) {
                        //No. of other cases recorded & put under treatment, Relapse MB, Male total - 2583
                        saveData(NLEPDataElements.NLEP_DE083);
                    } else if (caseDetection.equalsIgnoreCase("RE-ENTERED")) {
                        //No. of other cases recorded & put under treatment, Reentered MB, Male total - 2595
                        saveData(NLEPDataElements.NLEP_DE095);
                    } else if (caseDetection.equalsIgnoreCase("REFERRED")) {
                        //No. of other cases recorded & put under treatment, Referred MB, Male total - 2607
                        saveData(NLEPDataElements.NLEP_DE107);
                    } else if (caseDetection.equalsIgnoreCase("RE-CLASSIFIED")) {
                        //No. of other cases recorded & put under treatment, Reclassified MB, Male total - 2619
                        saveData(NLEPDataElements.NLEP_DE119);
                    } else if (caseDetection.equalsIgnoreCase("OTHER-STATE")) {
                        //No. of other cases recorded & put under treatment, Other state MB, Male total - 2631
                        saveData(NLEPDataElements.NLEP_DE131);
                    }
                    // <editor-fold defaultstate="collapsed" desc="Other MB Male total Deleted/RFT">
                    if (dateRFT != null) {
                        //No. of other leprosy cases RFT MB, Male total - 2643
                        saveData(NLEPDataElements.NLEP_DE143);
                    } else if (voidedReason != null) {
                        //No. of other leprosy cases deleted others MB, Male total - 2655
                        saveData(NLEPDataElements.NLEP_DE155);
                    }
                    //</editor-fold>
                    //</editor-fold>
                } //</editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Other MB Female">
                else if (gender.equalsIgnoreCase("F")) {
                    // <editor-fold defaultstate="collapsed" desc="Other MB Female SC">
                    if (category.equalsIgnoreCase("SC")) {
                        if (caseDetection.equalsIgnoreCase("RELAPSE")) {
                            //No. of other cases recorded & put under treatment, Relapse MB, Female S.C - 2580
                            saveData(NLEPDataElements.NLEP_DE080);
                        } else if (caseDetection.equalsIgnoreCase("RE-ENTERED")) {
                            //No. of other cases recorded & put under treatment, Reentered MB, Female S.C - 2592
                            saveData(NLEPDataElements.NLEP_DE092);
                        } else if (caseDetection.equalsIgnoreCase("REFERRED")) {
                            //No. of other cases recorded & put under treatment, Referred MB, Female S.C - 2604
                            saveData(NLEPDataElements.NLEP_DE104);
                        } else if (caseDetection.equalsIgnoreCase("RE-CLASSIFIED")) {
                            //No. of other cases recorded & put under treatment, Reclassified MB, Female S.C - 2616
                            saveData(NLEPDataElements.NLEP_DE116);
                        } else if (caseDetection.equalsIgnoreCase("OTHER-STATE")) {
                            //No. of other cases recorded & put under treatment, Other state MB, Female S.C - 2628
                            saveData(NLEPDataElements.NLEP_DE128);
                        }
                        // <editor-fold defaultstate="collapsed" desc="Other MB Female SC Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of other leprosy cases RFT MB, Female S.C - 2640
                            saveData(NLEPDataElements.NLEP_DE140);
                        } else if (voidedReason != null) {
                            //No. of other leprosy cases deleted others MB, Female S.C - 2652
                            saveData(NLEPDataElements.NLEP_DE152);
                        }
                        //</editor-fold>
                    } //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Other PB Female ST">
                    else if (category.equalsIgnoreCase("ST")) {
                        if (caseDetection.equalsIgnoreCase("RELAPSE")) {
                            //No. of other cases recorded & put under treatment, Relapse MB, Female S.T - 2582
                            saveData(NLEPDataElements.NLEP_DE082);
                        } else if (caseDetection.equalsIgnoreCase("RE-ENTERED")) {
                            //No. of other cases recorded & put under treatment, Reentered MB, Female S.T - 2594
                            saveData(NLEPDataElements.NLEP_DE094);
                        } else if (caseDetection.equalsIgnoreCase("REFERRED")) {
                            //No. of other cases recorded & put under treatment, Referred MB, Female S.T - 2606
                            saveData(NLEPDataElements.NLEP_DE106);
                        } else if (caseDetection.equalsIgnoreCase("RE-CLASSIFIED")) {
                            //No. of other cases recorded & put under treatment, Reclassified MB, Female S.T - 2618
                            saveData(NLEPDataElements.NLEP_DE118);
                        } else if (caseDetection.equalsIgnoreCase("OTHER-STATE")) {
                            //No. of other cases recorded & put under treatment, Other state MB, Female S.T - 2630
                            saveData(NLEPDataElements.NLEP_DE130);
                        }
                        // <editor-fold defaultstate="collapsed" desc="Other PB Female ST Deleted/RFT">
                        if (dateRFT != null) {
                            //No. of other leprosy cases RFT MB, Female S.T - 2642
                            saveData(NLEPDataElements.NLEP_DE142);
                        } else if (voidedReason != null) {
                            //No. of other leprosy cases deleted others MB, Female S.T - 2654
                            saveData(NLEPDataElements.NLEP_DE154);
                        }
                        //</editor-fold>
                    }
                    //</editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="Other PB Female Total">
                    if (caseDetection.equalsIgnoreCase("RELAPSE")) {
                        //No. of other cases recorded & put under treatment, Relapse PB, Female total - 2578
                        saveData(NLEPDataElements.NLEP_DE078);
                    } else if (caseDetection.equalsIgnoreCase("RE-ENTERED")) {
                        //No. of other cases recorded & put under treatment, Reentered PB, Female total - 2590
                        saveData(NLEPDataElements.NLEP_DE090);
                    } else if (caseDetection.equalsIgnoreCase("REFERRED")) {
                        //No. of other cases recorded & put under treatment, Referred PB, Female total - 2602
                        saveData(NLEPDataElements.NLEP_DE102);
                    } else if (caseDetection.equalsIgnoreCase("RE-CLASSIFIED")) {
                        //No. of other cases recorded & put under treatment, Reclassified PB, Female total - 2614
                        saveData(NLEPDataElements.NLEP_DE114);
                    } else if (caseDetection.equalsIgnoreCase("OTHER-STATE")) {
                        //No. of other cases recorded & put under treatment, Other state PB, Female total - 2626
                        saveData(NLEPDataElements.NLEP_DE126);
                    }
                    // <editor-fold defaultstate="collapsed" desc="Other PB Female total Deleted/RFT">
                    if (dateRFT != null) {
                        //No. of other leprosy cases RFT PB, Female total - 2638
                        saveData(NLEPDataElements.NLEP_DE138);
                    } else if (voidedReason != null) {
                        //No. of other leprosy cases deleted others PB, Female total - 2650
                        saveData(NLEPDataElements.NLEP_DE150);
                    }
                    //</editor-fold>
                    //</editor-fold>
                }
                //</editor-fold>
            }
        }
        // </editor-fold>
        //</editor-fold>
        //</editor-fold>

        // <editor-fold defaultstate="collapsed" desc="PART2 - Populate Based on Reaction - Required for DPMR PHC/Block Report">
        if (reactionTime != null) {
            if (diseaseType.equalsIgnoreCase("PB")) {
                //No. of reaction cases recorded (PB) - 2657
                saveData(NLEPDataElements.NLEP_DE157);
                if (reactionMgmtAt.equalsIgnoreCase("CENTER")) {
                    //No.of reaction cases managed at PHC  (PB) - 2658
                    saveData(NLEPDataElements.NLEP_DE158);
                } else if (reactionMgmtAt.equalsIgnoreCase("REFERRED_TO_DIST_HOSPITAL")) {
                    //No.of reaction cases referred to Dist. Hosp./other instt(PB) - 2659
                    saveData(NLEPDataElements.NLEP_DE159);
                }
            } else if (diseaseType.equalsIgnoreCase("MB")) {
                //No. of reaction cases recorded (MB) - 2671
                saveData(NLEPDataElements.NLEP_DE171);
                if (reactionMgmtAt.equalsIgnoreCase("CENTER")) {
                    //No.of reaction cases managed at PHC (MB) - 2672
                    saveData(NLEPDataElements.NLEP_DE172);
                } else if (reactionMgmtAt.equalsIgnoreCase("REFERRED_TO_DIST_HOSPITAL")) {
                    //No.of reaction cases referred to Dist. Hosp./other instt. (MB) - 2673
                    saveData(NLEPDataElements.NLEP_DE173);
                }
            }
        }
        if (diseaseType.equalsIgnoreCase(
                "PB")) {
            if (caseDetection.equalsIgnoreCase("RELAPSE") && relapseSelect.equalsIgnoreCase("YES")) {
                //No. of relapse cases suspected and referred (PB) - 2660
                saveData(NLEPDataElements.NLEP_DE160);
            }

            if (disabilityMDT != null) {
                //No. of cases developed new disability after MDT (PB) - 2661
                saveData(NLEPDataElements.NLEP_DE161);
            }

            if (servicesGiven.equalsIgnoreCase("MCR_CHAPPAL")) {
                //No. of patients provided with footwear (PB) - 2662
                saveData(NLEPDataElements.NLEP_DE162);
            } else if (servicesGiven.equalsIgnoreCase("SELF_CARE_KIT")) {
                //No. of patients provided with self care kit (PB) - 2663
                saveData(NLEPDataElements.NLEP_DE163);
            }

            if (rcsReferred.equalsIgnoreCase("YES")) {
                //No. of patients referred for RCS (PB) - 2664
                saveData(NLEPDataElements.NLEP_DE164);
            }
        } else if (diseaseType.equalsIgnoreCase(
                "MB")) {
            if (caseDetection.equalsIgnoreCase("RELAPSE") && relapseSelect.equalsIgnoreCase("YES")) {
                //No. of relapse cases suspected and referred (MB) - 2674
                saveData(NLEPDataElements.NLEP_DE174);
            }

            if (disabilityMDT != null) {
                //No. of cases developed new disability after MDT (MB) - 2675
                saveData(NLEPDataElements.NLEP_DE175);
            }

            if (servicesGiven.equalsIgnoreCase("MCR_CHAPPAL")) {
                //No. of patients provided with footwear (MB) - 2676
                saveData(NLEPDataElements.NLEP_DE176);
            } else if (servicesGiven.equalsIgnoreCase("SELF_CARE_KIT")) {
                //No. of patients provided with self care kit (MB) - 2677
                saveData(NLEPDataElements.NLEP_DE177);
            }

            if (rcsReferred.equalsIgnoreCase("YES")) {
                //No. of patients referred for RCS (MB) - 2678
                saveData(NLEPDataElements.NLEP_DE178);
            }
        }
        //</editor-fold>
    }
    //-------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------
    private DataElementService dataElementService;

    public void setDataElementService(DataElementService dataElementService) {
        this.dataElementService = dataElementService;
    }
    private DataValueService dataValueService;

    public void setDataValueService(DataValueService dataValueService) {
        this.dataValueService = dataValueService;
    }
    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
            DataElementCategoryOptionComboService dataElementCategoryOptionComboService) {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) {
        this.organisationUnitService = organisationUnitService;
    }
    private PeriodService periodService;

    public void setPeriodService(PeriodService periodService) {
        this.periodService = periodService;
    }
    //-------------------------------------------------------------------------
    // Input & Output
    //-------------------------------------------------------------------------
    private int ouIDTB;

    public void setOuIDTB(int ouIDTB) {
        this.ouIDTB = ouIDTB;
    }
    private int parentouIDTB;

    public void setParentouIDTB(int parentouIDTB) {
        this.parentouIDTB = parentouIDTB;
    }
    private OrganisationUnit orgUnit;
    private Period period;
}
