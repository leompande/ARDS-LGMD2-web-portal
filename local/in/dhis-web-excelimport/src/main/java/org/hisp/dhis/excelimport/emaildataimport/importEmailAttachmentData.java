package org.hisp.dhis.excelimport.emaildataimport;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.hisp.dhis.excelimport.util.BDImportSheet;
import org.hisp.dhis.excelimport.util.ExcelImport_OUDeCode;
import org.hisp.dhis.excelimport.util.ReportService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.database.DatabaseInfo;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class importEmailAttachmentData {

    //------------------------------------------------------------------------------------------------------
    //                             EMAIL IMPORT SETTINGS
    //------------------------------------------------------------------------------------------------------

    public static final String DATABANK_STORE = System.getenv("DHIS2_HOME") + File.separator + "HISPDATABANK";

    public static final String FILTER_TEXT = "IMPORT";

    public static final String EMAIL_PROTOCOL = "imaps";

    public static final String HOST_SERVER =  "imap.gmail.com";

    public static final String USERNAME =  "hispdatabank";

    public static final String PASSWORD =  "Hispindia7";

    public static final String SERVER_FOLDER_NAME = "Inbox";


    //------------------------------------------------------------------------------------------------------
    //                                DEPENDENCIES
    //------------------------------------------------------------------------------------------------------

    private ReportService reportService;

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    private PeriodService periodService;

    public void setPeriodService(PeriodService periodService) {
        this.periodService = periodService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private DatabaseInfoProvider databaseInfoProvider;

    public void setDatabaseInfoProvider(DatabaseInfoProvider databaseInfoProvider) {
        this.databaseInfoProvider = databaseInfoProvider;
    }

    private String raFolderName;

    private List<BDImportSheet> excelImportSheetList;

    public static int importCount = 0;

    //------------------------------------------------------------------------------------------------------
    //                                IMPLEMENTATION
    //------------------------------------------------------------------------------------------------------


    public void saveFile(String filename, InputStream is) throws Exception, WriteException {

        File dataBankFolder = new File(DATABANK_STORE);

        if (!dataBankFolder.exists()) {
            dataBankFolder.mkdir();
        }

        String attachmentPath = DATABANK_STORE + File.separator + filename;

        File extractedFile = new File(attachmentPath);

        if (!extractedFile.exists()) {

            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(extractedFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] buf = new byte[4096];

            int bytesRead;

            try {
                while ((bytesRead = is.read(buf)) != -1) {
                    try {
                        fos.write(buf, 0, bytesRead);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        } else {
            System.out.println("* INFO   : Attachment already extracted. Skipping!!");
        }

        importData(extractedFile);

        extractedFile.delete();

    }

    public void getExcelImportSheetList(String reportListFileName) {

        String fileName = reportListFileName;

        String excelImportFolderName = "excelimport";

        String path = System.getProperty("user.home") + File.separator + "dhis" + raFolderName + File.separator + excelImportFolderName + File.separator + fileName;

        try
        {
            String newpath = System.getenv("DHIS2_HOME");

            if (newpath != null) {
                path = newpath + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator + fileName;
            }
        }
        catch (NullPointerException npe)
        {
            System.out.println("DHIS2_HOME is not set");
        }

        String xmlTemplateName;
        String displayName;
        String periodicity;
        String checkerTemplateName;
        String checkerRangeForHeader;
        String checkerRangeForData;

        int count = 0;

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(path));
            if (doc == null) {
                System.out.println("XML File Not Found at DHIS HOME");
                return;
            }

            NodeList listOfReports = doc.getElementsByTagName("BDImportSheet");
            int totalReports = listOfReports.getLength();
            for (int s = 0; s < totalReports; s++) {
                Node reportNode = listOfReports.item(s);
                if (reportNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element reportElement = (Element) reportNode;

                    NodeList nodeList = reportElement.getElementsByTagName("xmlTemplateName");
                    Element element = (Element) nodeList.item(0);
                    nodeList = element.getChildNodes();
                    xmlTemplateName = ((Node) nodeList.item(0)).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName("displayName");
                    element = (Element) nodeList.item(0);
                    nodeList = element.getChildNodes();
                    displayName = ((Node) nodeList.item(0)).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName("periodType");
                    element = (Element) nodeList.item(0);
                    nodeList = element.getChildNodes();
                    periodicity = ((Node) nodeList.item(0)).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName("checkerTemplateName");
                    element = (Element) nodeList.item(0);
                    nodeList = element.getChildNodes();
                    checkerTemplateName = ((Node) nodeList.item(0)).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName("checkerRangeForHeader");
                    element = (Element) nodeList.item(0);
                    nodeList = element.getChildNodes();
                    checkerRangeForHeader = ((Node) nodeList.item(0)).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName("checkerRangeForData");
                    element = (Element) nodeList.item(0);
                    nodeList = element.getChildNodes();
                    checkerRangeForData = ((Node) nodeList.item(0)).getNodeValue().trim();

                    BDImportSheet bdImportSheet = new BDImportSheet(xmlTemplateName, displayName, periodicity, checkerTemplateName, checkerRangeForHeader, checkerRangeForData);

                    excelImportSheetList.add(count, bdImportSheet);

                    count++;
                }
            }
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    private void importPortalData(WritableWorkbook importWorkbook, String templateName) throws Exception {

        for (BDImportSheet importSheet : excelImportSheetList) {

            if (importSheet.getCheckTemplateName().equals(templateName)) {

                List<ExcelImport_OUDeCode> excelImport_ouDeCodeList = new ArrayList<ExcelImport_OUDeCode>();

                String importSheetId = importSheet.getXmlTemplateName();

                String selectedPeriodicity = importSheet.getPeriodicity();

                final String excelImportFolderName = "excelimport";

                String path = System.getProperty("user.home") + File.separator + "dhis" + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator + importSheetId;

                try {
                    String newPath = System.getenv("DHIS2_HOME");

                    if (newPath != null) {

                        path = newPath + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator + importSheetId;
                    }
                } catch (NullPointerException npe) {
                    System.out.println("DHIS2_HOME is not set");
                }

                try {
                    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                    Document doc = docBuilder.parse(new File(path));

                    if (doc == null) {
                        System.out.println("There is no DECodes related XML file in the DHIS2 Home");
                    }

                    NodeList periodCells = doc.getElementsByTagName("period-info");

                    Element periodCell = (Element) periodCells.item(0);

                    Integer periodSheetNo = Integer.parseInt(periodCell.getAttribute("sheetno"));
                    Integer periodRowNo = Integer.parseInt(periodCell.getAttribute("rowno"));
                    Integer periodColNo = Integer.parseInt(periodCell.getAttribute("colno"));
                    String periodFormat = periodCell.getAttribute("format");

                    Sheet importFileSheet = importWorkbook.getSheet(periodSheetNo);

                    String cellContent = importFileSheet.getCell(periodColNo, periodRowNo).getContents().trim();

                    String periodSplit[] = cellContent.split(" ");

                    String sDateString;

                    String eDateString;

                    Period selectedPeriod = null;

                    if (periodSplit.length < 3)
                    {
                        System.out.println("* ERROR: Wrong DATE FORMAT");
                        System.exit(1);
                    }
                    else
                    {
                        sDateString = periodSplit[1].trim();
                        eDateString = periodSplit[3].trim();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");

                        Date convertedStartDate = dateFormat.parse(sDateString);
                        Date convertedEndDate = dateFormat.parse(eDateString);

                        PeriodType periodType = periodService.getPeriodTypeByName(selectedPeriodicity);

                        selectedPeriod = getSelectedPeriod(convertedStartDate, periodType);
                    }

                    NodeList listOfDeCodes = doc.getElementsByTagName("cell-info");
                    int totalDeCodes = listOfDeCodes.getLength();

                    for (int s = 0; s < totalDeCodes; s++) {
                        Element deCodeElement = (Element) listOfDeCodes.item(s);
                        NodeList textDeCodeList = deCodeElement.getChildNodes();
                        String deCodeExpression = ((Node) textDeCodeList.item(0)).getNodeValue().trim();
                        Integer sheetNo = Integer.parseInt(deCodeElement.getAttribute("sheetno"));
                        Integer rowNo = Integer.parseInt(deCodeElement.getAttribute("rowno"));
                        Integer colNo = Integer.parseInt(deCodeElement.getAttribute("colno"));
                        String type = deCodeElement.getAttribute("type");
                        Integer ouCode = 0;
                        if (type.equalsIgnoreCase("OU-DE")) {
                            ouCode = Integer.parseInt(deCodeElement.getAttribute("oucode"));
                        }

                        ExcelImport_OUDeCode excelImport_ouDeCode = new ExcelImport_OUDeCode(sheetNo, rowNo, colNo, deCodeExpression, ouCode);

                        excelImport_ouDeCodeList.add(excelImport_ouDeCode);

                    }

                    String checkForEntry = new String();
                    String InsertQuery = new String();
                    String UpdateQuery = new String();

                    DatabaseInfo dataBaseInfo = databaseInfoProvider.getDatabaseInfo();

                    System.out.println("* INFO IMPORT LIST SIZE [" + excelImport_ouDeCodeList.size() + "]");

                    for (ExcelImport_OUDeCode excelImport_ouDeCode : excelImport_ouDeCodeList) {

                        Integer dataElementId = 0;

                        Integer optionComboId = 0;

                        Integer ouCode = excelImport_ouDeCode.getOuCode();

                        Integer dataCellRowNo = excelImport_ouDeCode.getRowno();

                        Integer dataCellCollNo = excelImport_ouDeCode.getColno();

                        int selectedPeriodID = selectedPeriod.getId();


                        if (!excelImport_ouDeCode.getExpression().contains("."))
                        {
                            System.out.println("* ERROR: WRONG DATAELEMENT EXPRESSION FORMAT [" + excelImport_ouDeCode.getExpression() + "]");
                            System.exit(1);
                        }
                        else
                        {
                            dataElementId = Integer.parseInt(excelImport_ouDeCode.getExpression().substring(0, (excelImport_ouDeCode.getExpression().indexOf('.'))));
                            optionComboId = Integer.parseInt(excelImport_ouDeCode.getExpression().substring((excelImport_ouDeCode.getExpression().indexOf('.') + 1), (excelImport_ouDeCode.getExpression().length())));
                        }


                        if (dataBaseInfo.getType().equalsIgnoreCase("postgresql")) {
                            checkForEntry = "SELECT COUNT(*) FROM datavalue WHERE dataelementid = "
                                    + dataElementId + " AND categoryoptioncomboid = " + optionComboId + " AND periodid = "
                                    + selectedPeriodID + " AND sourceid = " + ouCode;
                        } else if (dataBaseInfo.getType().equalsIgnoreCase("mysql")) {

                            checkForEntry = "SELECT COUNT(*) FROM datavalue WHERE dataelementid = "
                                    + dataElementId + " AND categoryoptioncomboid = " + optionComboId + " AND periodid = "
                                    + selectedPeriodID + " AND sourceid = " + ouCode;
                        }

                        int preEntryCount = -1;

                        preEntryCount = jdbcTemplate.queryForInt(checkForEntry);


                        double dataValue = Double.parseDouble(importFileSheet.getCell(dataCellCollNo, dataCellRowNo).getContents().trim());

                        int isInserted = 0;

                        int isUpdated = 0;

                        if (preEntryCount == 0) {
                            if (dataBaseInfo.getType().equalsIgnoreCase("postgresql")) {
                                InsertQuery = "INSERT INTO datavalue (dataelementid,categoryoptioncomboid,periodid,sourceid) VALUES("
                                        + dataElementId + "," + optionComboId + ","
                                        + selectedPeriodID + "," + ouCode + ")";
                            } else if (dataBaseInfo.getType().equalsIgnoreCase("mysql")) {

                                InsertQuery = "INSERT INTO datavalue (dataelementid,categoryoptioncomboid,periodid,sourceid) VALUES("
                                        + dataElementId + "," + optionComboId + ","
                                        + selectedPeriodID + "," + ouCode + ")";
                            }

                            isInserted = jdbcTemplate.update(InsertQuery);

                            if (isInserted == 0) {
                                System.out.println("* WARNING : INSERT Operation FAILED");
                            }

                        }
                        else if (preEntryCount == 1) {
                            if (dataBaseInfo.getType().equalsIgnoreCase("postgresql")) {
                                UpdateQuery = "UPDATE datavalue SET value=" + dataValue + " WHERE dataelementid = "
                                        + dataElementId + " AND categoryoptioncomboid = " + optionComboId + " AND periodid = "
                                        + selectedPeriodID + " AND sourceid = " + ouCode;
                            } else if (dataBaseInfo.getType().equalsIgnoreCase("mysql")) {

                                UpdateQuery = "UPDATE datavalue SET value=" + dataValue + " WHERE dataelementid = "
                                        + dataElementId + " AND categoryoptioncomboid = " + optionComboId + " AND periodid = "
                                        + selectedPeriodID + " AND sourceid = " + ouCode;

                            }

                            isUpdated = jdbcTemplate.update(UpdateQuery);

                            if (isUpdated == 0) {
                                System.out.println("* WARNING : UPDATE Operation FAILED");
                            }
                        }


                    }

                } catch (SAXParseException err) {
                    System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
                    System.out.println(" " + err.getMessage());
                } catch (SAXException e) {
                    Exception x = e.getException();
                    ((x == null) ? e : x).printStackTrace();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

    }


    public boolean validateReport(Workbook excelImportFile, Workbook excelTemplateFile, String templateName) {

        boolean validator = true;

        final int sheetNumber = 0;

        for (BDImportSheet importSheet : excelImportSheetList) {

            if (importSheet.getCheckTemplateName().equals(templateName)) {

                String checkRangeForHeader = importSheet.getCheckRangeForHeader();

                String checkRangeForData = importSheet.getCheckRangeForHeader();

                String headerParts[] = checkRangeForHeader.split("-");
                int headerStartRow = Integer.parseInt(headerParts[0].split(",")[0]);
                int headerEndRow = Integer.parseInt(headerParts[1].split(",")[0]);
                int headerStartCol = Integer.parseInt(headerParts[0].split(",")[1]);
                int headerEndCol = Integer.parseInt(headerParts[1].split(",")[1]);

                String dataParts[] = checkRangeForData.split("-");
                int dataStartRow = Integer.parseInt(dataParts[0].split(",")[0]);
                int dataEndRow = Integer.parseInt(dataParts[1].split(",")[0]);
                int dataStartCol = Integer.parseInt(dataParts[0].split(",")[1]);
                int dataEndCol = Integer.parseInt(dataParts[1].split(",")[1]);

                System.out.println("* INFO: VALIDATING IMPORTED FILE [" + excelImportFile.getSheet(sheetNumber).getName() + "]" + "AGAINST TEMPLATE [" + excelTemplateFile.getSheet(sheetNumber).getName() + "]");

                Sheet importFileSheet = excelImportFile.getSheet(sheetNumber);
                Sheet templateFileSheet = excelTemplateFile.getSheet(sheetNumber);

                if (excelImportFile.getSheet(sheetNumber).getRows() == excelTemplateFile.getSheet(sheetNumber).getRows()) {

                    //-------------------------------- Checking Header Cells ---------------------------------------------

                    for (int c = headerStartCol; c <= headerEndCol; c++) {
                        for (int r = headerStartRow; r <= headerEndRow; r++) {

                            String cellContent = importFileSheet.getCell(c, r).getContents();
                            String templateContent = templateFileSheet.getCell(c, r).getContents();

                            if (templateContent.equalsIgnoreCase(cellContent) && cellContent.equalsIgnoreCase(templateContent)) {
                                continue;
                            } else {
                                System.out.println("[" + cellContent + "|" + templateContent + "]");
                                validator = false;
                                break;
                            }
                        }
                    }

                    //--------------------------------- Checking Data Cells ----------------------------------------------

                    for (int c = dataStartCol; c <= dataEndCol; c++) {
                        for (int r = dataStartRow; r <= dataEndRow; r++) {
                            String cellContent = importFileSheet.getCell(c, r).getContents();
                            String templateContent = templateFileSheet.getCell(c, r).getContents();

                            if (templateContent.equalsIgnoreCase(cellContent) && cellContent.equalsIgnoreCase(templateContent)) {
                                continue;
                            } else {
                                System.out.println("[" + cellContent + "|" + templateContent + "]");
                                validator = false;
                                break;
                            }
                        }
                    }
                } else {
                    System.out.println("* FAILURE :Validation failed due to unequal count of rows.");
                    validator = false;
                }

            }

        }

        return validator;
    }


    public Period getSelectedPeriod(Date startDate, PeriodType periodType) throws Exception {


        List<Period> periods = new ArrayList<Period>(periodService.getPeriodsByPeriodType(periodType));

        for (Period period : periods)
        {
            Date tempDate = period.getStartDate();
            if (tempDate.equals(startDate))
            {
                return period;
            }
        }

        Period period = periodType.createPeriod(startDate);
        period = reloadPeriodForceAdd(period);

        return period;
    }

    private final Period reloadPeriod(Period period) {
        return periodService.getPeriod(period.getStartDate(), period.getEndDate(), period.getPeriodType());
    }

    private final Period reloadPeriodForceAdd(Period period) {
        Period storedPeriod = reloadPeriod(period);

        if (storedPeriod == null) {

            periodService.addPeriod(period);

            return period;
        }

        return storedPeriod;
    }

    public void importData(File fileToImport) throws Exception, IOException, WriteException {

        raFolderName = reportService.getRAFolderName();

        excelImportSheetList = new ArrayList<BDImportSheet>();

        getExcelImportSheetList("BDDataImportSheetList.xml");


        System.out.println("\n========================================== EMAIL IMPORT TESTING=============================================");

        System.out.println("* INFO: TemplateName [" + fileToImport.getName() + "]");


        String excelTemplatePath = System.getenv("DHIS2_HOME") + File.separator + raFolderName + File.separator
                + "excelimport" + File.separator + "template" + File.separator + fileToImport.getName();

        String outputReportPath = System.getenv("DHIS2_HOME") + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";


        Workbook excelImportFile = Workbook.getWorkbook(fileToImport);

        WritableWorkbook writableExcelImportFile = Workbook.createWorkbook(new File(outputReportPath), excelImportFile);

        Workbook excelTemplateFile = Workbook.getWorkbook(new File(excelTemplatePath));

        if (validateReport(excelImportFile, excelTemplateFile, fileToImport.getName())) {

            System.out.println("* INFO: Uploaded ExcelSheet is matched with Template file.");

            importPortalData(writableExcelImportFile, fileToImport.getName());

        } else {

            System.out.println("* WARNING: The file you are trying to import is not the correct format");
        }

        try {

        } finally {
            excelImportFile.close();
            excelTemplateFile.close();
            writableExcelImportFile.close();
        }

        System.out.println("==========================================DONE-TESTING=============================================\n");

    }


    public void extractXLSToImport() {


        Properties props = System.getProperties();


        props.setProperty("mail.store.protocol", "imaps");

        try {

            Session session = Session.getDefaultInstance(props, null);

            Store store = session.getStore(EMAIL_PROTOCOL);

            store.connect(HOST_SERVER,USERNAME,PASSWORD);

            System.out.println("* INFO: EMAIL IMPORT SERVICE STARTED FOR STORE ["+store+"] AT " + new Date().toString());

            Folder inbox = store.getFolder(SERVER_FOLDER_NAME);

            inbox.open(Folder.READ_WRITE);

            Message messages[] = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));;

            int j = 0;

            for (Message message : messages) {

                Multipart multipart = (Multipart) message.getContent();

                inbox.setFlags(new Message[]{message}, new Flags(Flags.Flag.SEEN), true);

                for (int i = 0, n = multipart.getCount(); i < n; i++) {

                    Part part = multipart.getBodyPart(i);

                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) && message.getSubject().contains(FILTER_TEXT) && part.getFileName().endsWith(".xls"))  {
                        ++j;

                        System.out.println("\n--------------------------(" + j + ")---------------------------");
                        System.out.println("* SUBJECT: " + message.getSubject() + "[1]");
                        System.out.println("* FROM   : " + ((InternetAddress)message.getFrom()[0]).getAddress());
                        System.out.println("* DATE   : " + message.getReceivedDate().toString());
                        System.out.println("* INFO   : Attachment File Found [" + part.getFileName() + "]");
                        System.out.println("* INFO   : File Details [" + part.getContentType() + " , [" + (int) ((((float) part.getSize()) / 1024.0) + 0.5) + "] KB ]");

                        try
                        {
                            saveFile(part.getFileName(), part.getInputStream());

                            ++importCount;

                        }

                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }


            System.out.println("* INFO: EMAIL OPERATION FINISHED WITH TOTAL [ "+importCount+" ] ATTACHMENT IMPORTS.");
            System.out.println();

            inbox.close(true);

            store.close();


        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(2);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

}
