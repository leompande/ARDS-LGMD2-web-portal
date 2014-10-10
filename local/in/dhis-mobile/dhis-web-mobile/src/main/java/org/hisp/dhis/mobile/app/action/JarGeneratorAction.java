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
import com.opensymphony.xwork2.Action;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;

public class JarGeneratorAction implements Action {

    private String mvnBin;
    private String splash;
    private String selectDataSet;
    private String dataElements;

    public void setDataElements(String dataElements) {
        this.dataElements = dataElements;
    }

    public void setSelectDataSet(String selectDataSet) {
        this.selectDataSet = selectDataSet;
    }

    public String getMvnBin() {
        return mvnBin;
    }

    public void setMvnBin(String mvnBin) {
        this.mvnBin = mvnBin;
    }

    public String getSplash() {
        return splash;
    }

    public void setSplash(String splash) {
        this.splash = splash;
    }

    private static String readFileAsString(File file) throws java.io.IOException {
        byte[] buffer = new byte[(int) file.length()];
        BufferedInputStream f = new BufferedInputStream(new FileInputStream(file));
        f.read(buffer);
        f.close();
        return new String(buffer);
    }

    public void replaceStringInFile(File dir, String fileName, String match, String replacingString) {
        try {
            File srcFile = new File(dir, fileName);
            File destFile = new File(dir, "temp");
            if (srcFile.exists()) {
                String str = readFileAsString(srcFile);
                str = str.replaceFirst(match, replacingString);
                FileWriter fw = new FileWriter(destFile);
                fw.write(str);
                fw.close();
            }
            FileUtils.copyFile(destFile, srcFile);
            destFile.delete();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String execute() throws Exception {
        String webappPath = ServletActionContext.getServletContext().getRealPath("/");
        String javameSrc = webappPath + "/dhis-web-mobile/javame_src/src/main/java/org/hisp/dhis/mobile";
        File dir = new File(javameSrc);

        //For splash screen
        replaceStringInFile(dir, "DHISMobile.java", "\\w*.png", splash);

        //For dataset
        replaceStringInFile(dir, "FormsListPage.java", "formNames\\[].*;", "formNames[] = {\"" + selectDataSet + "\"};");

        //For dataElements
        replaceStringInFile(dir, "DHISMobile.java", "dataElements =.*\\},\\{", "dataElements = {{" + dataElements + "},{");

        //For language
        //replaceStringInFile(dir, "DHISMobile.java", "", language);

        //For patient-program stage
        //replaceStringInFile(dir, "DHISMobile.java", "", patient_program);

        ProcessBuilder pb = new ProcessBuilder(mvnBin, "install", "-f", ServletActionContext.getServletContext().getRealPath("/") + "/dhis-web-mobile/javame_src/pom.xml");
        pb.redirectErrorStream(true);
        Process p = pb.start();
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String str;
        while ((str = br.readLine()) != null) {
            System.out.println(str);
        }
        return SUCCESS;
    }
}
