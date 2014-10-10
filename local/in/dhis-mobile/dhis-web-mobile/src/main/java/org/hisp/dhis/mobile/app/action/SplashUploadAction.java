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
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;

public class SplashUploadAction extends ActionSupport {

    private File file;
    private String contentType;
    private String filename;
    private String[] splashImg;

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

    public void setUpload(File file) {
        this.file = file;
    }

    public void setUploadContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setUploadFileName(String filename) {
        this.filename = filename;
    }

    @Override
    public String execute() {
        try {
            String webappPath = ServletActionContext.getServletContext().getRealPath("/");
            String imgPath = webappPath + "/dhis-web-mobile/javame_src/src/main/resources/splash";
            File imgFolder = new File(imgPath);
            FileUtils.copyFile(file, new File(imgFolder, String.valueOf(imgFolder.listFiles().length + 1) + ".png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return SUCCESS;
    }
}
