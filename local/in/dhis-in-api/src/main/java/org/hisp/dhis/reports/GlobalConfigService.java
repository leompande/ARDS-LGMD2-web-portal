package org.hisp.dhis.reports;

import java.io.File;
import java.util.Map;

/**
 * <gaurav>,Date: 6/26/12, Time: 2:25 PM
 */
public interface GlobalConfigService {

    String ID = GlobalConfigService.class.getName();

    public File[] getFileNames();

    public void updateDecodeFiles();

    public void writeGlobalSettings(Map<String, String> globalValueMap);

}
