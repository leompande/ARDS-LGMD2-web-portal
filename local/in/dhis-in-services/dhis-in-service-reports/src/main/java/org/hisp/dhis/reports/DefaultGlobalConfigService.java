package org.hisp.dhis.reports;

import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <gaurav>,Date: 6/25/12, Time: 12:42 PM
 */

public class DefaultGlobalConfigService implements GlobalConfigService {


    //----------------------------------------------------------------------------------------------
    //                         INPUT-OUTPUT FOLDER PATHS
    //----------------------------------------------------------------------------------------------

    private static final String SETTINGS_XML = "globalsettings.xml";

    private static Map<String, String> globalValueMap = new HashMap<String, String>();

    //----------------------------------------------------------------------------------------------
    //                                    Dependencies
    //----------------------------------------------------------------------------------------------

    private DataElementService dataElementService;

    private DataElementCategoryService dataElementCategoryService;

    private ConfigurationService configurationService;

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }


    public void setDataElementService(DataElementService dataElementService) {
        this.dataElementService = dataElementService;
    }

    public void setDataElementCategoryService(DataElementCategoryService dataElementCategoryService) {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    //----------------------------------------------------------------------------------------------
    //                         GET LIST OF DECODE XML FILES
    //----------------------------------------------------------------------------------------------

    public File[] getFileNames() {

        String RAFOLDER = configurationService.getConfigurationByKey(Configuration_IN.KEY_REPORTFOLDER)
                .getValue();

        String RAFOLDER_PATH = System.getenv("DHIS2_HOME") + File.separator + RAFOLDER;

        File raFile = new File(RAFOLDER_PATH);

        return raFile.listFiles();
    }

    public static boolean isParsable2Int(String intString) {
        if (intString.trim().isEmpty()) {
            return false;
        }
        for (char c : intString.trim().toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }

        return true;
    }

    //----------------------------------------------------------------------------------------------
    //      Implementation: Replace local De-Code Values with auto-generated Global Values
    //----------------------------------------------------------------------------------------------

    public void updateDecodeFiles() {

        String OUTPUT_FOLDER = System.getenv("DHIS2_HOME") + File.separator + configurationService.getConfigurationByKey(Configuration_IN.KEY_REPORTFOLDER)
                .getValue() + "_new";

        Integer globalID = 1;

        File[] files = getFileNames();

        for (File file : files) {

            if (file.getName().contains("DECodes") && file.getName().endsWith(".xml")) {

                if (file.exists()) {
                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

                    try {
                        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                        Document doc = docBuilder.parse(file);


                        NodeList listOfDECodes = doc.getElementsByTagName("de-code");

                        int totalCodes = listOfDECodes.getLength();

                        for (int i = 0; i < totalCodes; i++) {

                            Element deCodeElement = (Element) listOfDECodes.item(i);
                            NodeList textCodeList = deCodeElement.getChildNodes();
                            String expression = (textCodeList.item(0).getNodeValue().trim());

                            String res;
                            Pattern p = Pattern.compile("\\[(.*?)\\]");
                            Matcher matcher = p.matcher(expression);
                            String gconfig;

                            while (matcher.find()) {

                                res = matcher.group(1);

                                if (!(globalValueMap.containsKey(res)))
                                {
                                        globalValueMap.put(res, globalID.toString());

                                        globalID++;
                                }

                                    gconfig = globalValueMap.get(res);

                                    expression = expression.replace("[" + res + "]", "[" + gconfig + "]");

                            }

                            res = expression;

                            textCodeList.item(0).setNodeValue(res);

                            TransformerFactory transformerFactory = TransformerFactory.newInstance();

                            Transformer transformer = null;

                            try {

                                transformer = transformerFactory.newTransformer();
                                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                            } catch (TransformerConfigurationException e) {
                                e.printStackTrace();
                            }
                            DOMSource source = new DOMSource(doc);

                            File newRAFolder = new File(OUTPUT_FOLDER);

                            if (newRAFolder.exists() == false) {
                                newRAFolder.mkdir();
                            }

                            File newOutFile = new File(OUTPUT_FOLDER + "/" + file.getName());

                            if (!newOutFile.exists()) {
                                boolean isCreated = newOutFile.createNewFile();

                                if (isCreated == false) {
                                    System.out.println("*ERROR: [FAILED TO CREATE UPDATED XML FILE: " + newOutFile.getName() + " ]");
                                }
                            }

                            StreamResult result = new StreamResult(newOutFile);

                            try {
                                transformer.transform(source, result);
                            } catch (TransformerException e) {
                                e.printStackTrace();
                            }

                        }

                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("*ERROR: [DECODES XML FILE NOT FOUND]");
                }
            }
        }

        writeGlobalSettings(globalValueMap);
    }

    //----------------------------------------------------------------------------------------------
    //                    Implementation: Create Global Settings XML
    //----------------------------------------------------------------------------------------------


    public void writeGlobalSettings(Map<String, String> globalValueMap) {

        String OUTPUT_FOLDER = System.getenv("DHIS2_HOME") + File.separator + configurationService.getConfigurationByKey(Configuration_IN.KEY_REPORTFOLDER)
                .getValue() + "_new";

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("globalSettings");
            doc.appendChild(rootElement);

            for (Map.Entry<String, String> mapElement : globalValueMap.entrySet()) {

                Element gConfig = doc.createElement("gconfig");
                rootElement.appendChild(gConfig);

                Attr dhisidAttr = doc.createAttribute("commonid");
                dhisidAttr.setValue(mapElement.getValue());
                gConfig.setAttributeNode(dhisidAttr);

                Attr dhisIDAttr = doc.createAttribute("dhisid");
                dhisIDAttr.setValue(mapElement.getKey());
                gConfig.setAttributeNode(dhisIDAttr);

                String replaceString = mapElement.getKey().trim();

                String optionComboIdStr = replaceString.substring(replaceString.indexOf('.') + 1, replaceString.length());

                replaceString = replaceString.substring(0, replaceString.indexOf('.'));

                int dataElementId;
                int optionComboId;

                if (isParsable2Int(replaceString) && isParsable2Int(optionComboIdStr)) {

                    dataElementId = Integer.parseInt(replaceString);
                    optionComboId = Integer.parseInt(optionComboIdStr);


                    if (dataElementService == null) {
                        System.out.println("*ERROR:[Data-element service is NULL]");
                    }

                    DataElement dataElement = dataElementService.getDataElement(dataElementId);
                    DataElementCategoryOptionCombo optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo(optionComboId);


                    if (dataElement != null && optionCombo != null) {

                        if (optionComboId == 1) {
                            Attr deNameAttr = doc.createAttribute("de-name");
                            String deName = dataElement.getName().replace('\"', ' ').replace('\'', ' ');
                            deNameAttr.setValue(deName);
                            gConfig.setAttributeNode(deNameAttr);
                        } else {
                            Attr deNameAttr = doc.createAttribute("de-name");
                            String optionName = optionCombo.getName().replace('(', ' ').replace(')', ' ');
                            String deName = dataElement.getName().replace('\"', ' ').replace('\'', ' ');
                            deNameAttr.setValue(deName + " [" + optionName + "]");
                            gConfig.setAttributeNode(deNameAttr);
                        }

                    } else {

                        System.out.println("\n* INFO [ DATA-ELEMENT OR OPTION_COMBO MISSING (" + dataElementId + ")]");

                        Attr deNameAttr = doc.createAttribute("de-name");
                        deNameAttr.setValue("NA" + "(NA)");
                        gConfig.setAttributeNode(deNameAttr);

                    }

                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                DOMSource source = new DOMSource(doc);

                StreamResult result = new StreamResult(new File(OUTPUT_FOLDER + "/" + SETTINGS_XML));
                transformer.transform(source, result);
            }

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

    }


}
