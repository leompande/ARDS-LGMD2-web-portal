package org.hisp.dhis.reports.reportmanagement.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.reports.GlobalConfigService;

import java.util.Date;

/**
 * <gaurav>,Date: 6/28/12, Time: 11:36 AM
 */
public class GenerateGlobalConfigAction implements Action {

    private GlobalConfigService globalConfigService;

    public void setGlobalConfigService(GlobalConfigService globalConfigService) {
        this.globalConfigService = globalConfigService;
    }

    @Override
    public String execute() throws Exception {

        System.out.println( "\n" +"--------------------------------------------------------------------------\n"+
                                  "     Global Config Action Start Time is : " + new Date()+
                                "\n--------------------------------------------------------------------------\n" );

        globalConfigService.updateDecodeFiles();

        System.out.println( "\n" +"----------------------------------------------------------------------------\n"+
                                  "     Global Config Action End Time is : " + new Date()+
                                "\n----------------------------------------------------------------------------\n" );

        return SUCCESS;
    }
}
