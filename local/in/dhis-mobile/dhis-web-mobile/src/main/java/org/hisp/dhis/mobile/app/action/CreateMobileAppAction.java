package org.hisp.dhis.mobile.app.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.io.File;
import java.util.Collection;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

public class CreateMobileAppAction implements Action {

    private String mvnPath;
    private String mvnStatus;
    private String[] splashImg;
    private String[] availableDatasets;
    private String mobileAppFilename;
    
    public String getMobileAppFilename() {
        String webappPath = ServletActionContext.getServletContext().getRealPath("/");
       // String webappPath = ActionContext.getContext().getRealPath("/");
        String appPath = webappPath + "/dhis-web-mobile/javame_src/target";
        File appFile = new File(appPath, "dhis-javame-1.0.0-me.jar");
        if (appFile.exists()) {
            mobileAppFilename = appFile.getName();
        }
        return mobileAppFilename;
    }

    public String[] getSplashImg() {
        String webappPath = ServletActionContext.getServletContext().getRealPath("/");
        String imgPath = webappPath + "/dhis-web-mobile/javame_src/src/main/resources/splash";
        File imgFolder = new File(imgPath);
        File[] images;
        if (imgFolder.exists()) {
            if (imgFolder.isDirectory()) {
                images = imgFolder.listFiles();
                String[] imageNames = new String[images.length];
                for (int i = 0; i < images.length; i++) {
                    if (images[i].getName().contains(".png")) {
                        imageNames[i] = images[i].getName();
                    }
                }
                splashImg = imageNames;
            }
        }
        return splashImg;
    }

    public String getMvnPath() {
        String PATH = System.getenv("PATH");
        String[] locations;
        if (getOSName().equals("win")) {
            locations = PATH.split(";");
        } else {
            locations = PATH.split(":");
        }
        for (String location : locations) {
            File folder = new File(location);
            String filePath = scanPath(getOSName(), folder);
            if (!filePath.equals("")) {
                mvnPath = filePath;
                break;
            }
        }
        return mvnPath;
    }

    public void setMvnPath(String path) {
        File mvnFolder = new File(path);
        String filePath = scanPath(getOSName(), mvnFolder);
        if (!filePath.equals("")) {
            this.mvnPath = filePath;
        } else {
            mvnStatus = "Could not find mvn.bat at the location you entered";
        }
    }

    public String getMvnStatus() {
        return mvnStatus;
    }

    public void setMvnStatus(String status) {
        this.mvnStatus = status;
    }

    private String getOSName() {
        String osName;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            osName = "win";
        } else {
            osName = "nix";
        }
        return osName;
    }

    private String scanPath(String osName, File folder) {
        String filePath = new String();
        if (folder.exists()) {
            if (folder.isDirectory()) {
                File[] files = folder.listFiles();
                for (File file : files) {
                    if (osName.equals("win")) {
                        if (file.getName().equals("mvn.bat")) {
                            filePath = file.getAbsolutePath();
                            break;
                        }
                    } else {
                        if (osName.equals("nix")) {
                            if (file.getName().equals("mvn")) {
                                filePath = file.getAbsolutePath();
                                break;
                            }
                        }
                    }
                }
            }
        }
        return filePath;
    }
    private DataSetService dataSetService;

    public void setDataSetService(DataSetService dataSetService) {
        this.dataSetService = dataSetService;
    }
    private PeriodService periodService;

    public void setPeriodService(PeriodService periodService) {
        this.periodService = periodService;
    }

    public String[] getAvailableDatasets() {
        int i = 0;
        Collection<DataSet> allDataSets = dataSetService.getAllDataSets();
        availableDatasets = new String[allDataSets.size()];
        for (DataSet dataSet : allDataSets) {
            availableDatasets[i] = dataSet.getName();
            i++;
        }
        return availableDatasets;
    }

    @Override
    public String execute()
            throws Exception {
        //SMSService service = new SMSService();
        //service.testSMSService();
        return SUCCESS;
    }
}
