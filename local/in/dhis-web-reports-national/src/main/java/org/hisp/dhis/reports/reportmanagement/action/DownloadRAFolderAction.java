package org.hisp.dhis.reports.reportmanagement.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * <gaurav>,Date: 6/29/12, Time: 10:35 AM
 */
public class DownloadRAFolderAction implements Action {


    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ConfigurationService configurationService;

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    // -------------------------------------------------------------------------
    // Input and Output Parameters
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream() {
        return inputStream;
    }

    private String fileName;

    public String getFileName() {
        return fileName;
    }

    private String selectedButton;

    public void setSelectedButton(String selectedButton) {
        this.selectedButton = selectedButton;
    }

    private String statusMessage;

    public String getStatusMessage() {
        return statusMessage;
    }


    @Override
    public String execute() throws Exception {


        String raFolderName = configurationService.getConfigurationByKey(Configuration_IN.KEY_REPORTFOLDER).getValue();

        String raPath = configurationService.backupFolder(System.getenv("DHIS2_HOME") + File.separator + raFolderName + "_new");

        if (raPath.equalsIgnoreCase("INPUT")) {
            statusMessage = "Problem while taking backup for reports folder, please see the log";
        } else {
            fileName = raFolderName + ".zip";

            inputStream = new BufferedInputStream(new FileInputStream(raPath), 1024);

            return "download";
        }
        return SUCCESS;
    }
}
