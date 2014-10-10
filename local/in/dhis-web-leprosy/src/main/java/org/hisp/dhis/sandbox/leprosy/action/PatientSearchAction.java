package org.hisp.dhis.sandbox.leprosy.action;

import com.opensymphony.xwork2.Action;
import java.util.List;
import java.util.Properties;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;

public class PatientSearchAction implements Action {

    public String execute()
            throws Exception {
        Context.startup("jdbc:mysql://localhost:3306/openmrs_leprosy?autoReconnect=true", "root", "toohot97", new Properties());
        try {
            Context.openSession();
            Context.authenticate("admin", "test");
            List<Patient> patients = Context.getPatientService().getPatientsByName("Joh");
            for (int i = 0; i < patients.size(); i++) {
                Patient p = patients.get(i);
                System.out.println(i + ".) Name = " + p.getGivenName() + " " + p.getMiddleName() + " " + p.getFamilyName());
            }
            ProgramWorkflowService pws = Context.getProgramWorkflowService();
            Program program = pws.getProgram(5);
            System.out.println("NLEP>>>>>>> Program No 5 = "+program.getName());
        } finally {
            Context.closeSession();
        }
        return SUCCESS;
    }
}
