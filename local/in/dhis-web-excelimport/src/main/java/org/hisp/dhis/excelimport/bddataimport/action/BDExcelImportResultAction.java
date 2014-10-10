package org.hisp.dhis.excelimport.bddataimport.action;

import com.opensymphony.xwork2.Action;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.excelimport.util.ExcelImport_OUDeCode;
import org.hisp.dhis.excelimport.util.ReportService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.database.DatabaseInfo;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BDExcelImportResultAction implements Action {

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService(OrganisationUnitGroupService organisationUnitGroupService) {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    private DataSetService dataSetService;

    public void setDataSetService(DataSetService dataSetService) {
        this.dataSetService = dataSetService;
    }

    private DatabaseInfoProvider databaseInfoProvider;

    public void setDatabaseInfoProvider( DatabaseInfoProvider databaseInfoProvider )
    {
        this.databaseInfoProvider = databaseInfoProvider;
    }

    private I18nFormat format;

    public void setFormat(I18nFormat format) {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    private String checkTemplateName;

    public void setCheckTemplateName(String checkTemplateName) {
        this.checkTemplateName = checkTemplateName;
    }

    private String checkRangeForHeader;

    public void setCheckRangeForHeader(String checkRangeForHeader) {
        this.checkRangeForHeader = checkRangeForHeader;
    }

    private String checkRangeForData;

    public void setCheckRangeForData(String checkRangeForData) {
        this.checkRangeForData = checkRangeForData;
    }

    private String importSheetId;

    public void setImportSheetId(String importSheetId) {
        this.importSheetId = importSheetId;
    }

    private String message = "";

    public String getMessage() {
        return message;
    }

    private File output;

    public File getOutput() {
        return output;
    }

    private File upload;

    public File getUpload() {
        return upload;
    }

    public void setUpload(File upload) {
        this.upload = upload;
    }

    private String raFolderName;

    private boolean lockStatus;

    public boolean isLockStatus() {
        return lockStatus;
    }

    String selectedPeriodicity;

    public void setSelectedPeriodicity(String selectedPeriodicity) {
        this.selectedPeriodicity = selectedPeriodicity;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception {

        message += "\n<br><font color=blue>Importing StartTime : " + new Date() + "  - By " + currentUserService.getCurrentUsername() + "</font><br>";

        System.out.println(message);

        raFolderName = reportService.getRAFolderName();

        System.out.println("\n==========================================TESTING=============================================");

        System.out.println("TemplateName [" + checkTemplateName + "]");


        String excelTemplatePath = System.getenv("DHIS2_HOME") + File.separator + raFolderName + File.separator
                + "excelimport" + File.separator + "template" + File.separator + checkTemplateName;

        String outputReportPath = System.getenv("DHIS2_HOME") + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        Workbook excelImportFile = Workbook.getWorkbook(upload);

        WritableWorkbook writableExcelImportFile = Workbook.createWorkbook(new File(outputReportPath), excelImportFile);

        Workbook excelTemplateFile = Workbook.getWorkbook(new File(excelTemplatePath));

        if (validateReport(excelImportFile, excelTemplateFile)) {
            System.out.println("Uploaded ExcelSheet is matched with Template file.");
            importPortalData(writableExcelImportFile);

        } else {
            message = "The file you are trying to import is not the correct format";
        }

        try {

        } finally {
            excelImportFile.close();
            excelTemplateFile.close();
            writableExcelImportFile.close();
        }

        System.out.println("==========================================TESTING=============================================\n");

        System.out.println("Importing has been completed which is started by : " + currentUserService.getCurrentUsername() + " at " + new Date());
        message += "<br><br><font color=blue>Importing EndTime : " + new Date() + "  - By " + currentUserService.getCurrentUsername() + "</font>";

        return SUCCESS;
    }




    private void importPortalData(WritableWorkbook importWorkbook) throws Exception {

        List<ExcelImport_OUDeCode> excelImport_ouDeCodeList = new ArrayList<ExcelImport_OUDeCode>();

        final String excelImportFolderName = "excelimport";

        String path = System.getProperty("user.home") + File.separator + "dhis" + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator + importSheetId;

        try
        {
            String newPath = System.getenv("DHIS2_HOME");

            if (newPath != null) {
                path = newPath + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator + importSheetId;
            }
        }
        catch (NullPointerException npe)
        {
            System.out.println("DHIS2_HOME is not set");
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(path));

            if (doc == null)
            {
                System.out.println("There is no DECodes related XML file in the DHIS2 Home");
            }

            NodeList periodCells = doc.getElementsByTagName("period-info");

            Element periodCell = (Element) periodCells.item(0);

            Integer periodSheetNo = Integer.parseInt(periodCell.getAttribute("sheetno"));
            Integer periodRowNo = Integer.parseInt(periodCell.getAttribute("rowno"));
            Integer periodColNo = Integer.parseInt(periodCell.getAttribute("colno"));
            String periodFormat = periodCell.getAttribute("format");

            System.out.println("PERIOD-INFO ["+periodRowNo+","+periodColNo+","+periodSheetNo+", ("+periodFormat+")]");

            Sheet importFileSheet = importWorkbook.getSheet(periodSheetNo);
            String cellContent = importFileSheet.getCell(periodColNo, periodRowNo).getContents().trim();

            String periodSplit[] = cellContent.split(" ");

            String sDateString;

            String eDateString;

            Period selectedPeriod = null ;

            if(periodSplit.length<3)
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

                PeriodType periodType = periodService.getPeriodTypeByName( selectedPeriodicity );

                selectedPeriod = getSelectedPeriod( convertedStartDate, periodType );

                System.out.println("DATE_EXPRESSIONS ["+convertedStartDate+","+convertedEndDate+"]");
            }

            NodeList listOfDeCodes = doc.getElementsByTagName("cell-info");
            int totalDeCodes = listOfDeCodes.getLength();

            for (int s = 0; s < totalDeCodes; s++)
            {
                Element deCodeElement = (Element) listOfDeCodes.item(s);
                NodeList textDeCodeList = deCodeElement.getChildNodes();
                String deCodeExpression = ((Node) textDeCodeList.item(0)).getNodeValue().trim();
                Integer sheetNo = Integer.parseInt(deCodeElement.getAttribute("sheetno"));
                Integer rowNo = Integer.parseInt(deCodeElement.getAttribute("rowno"));
                Integer colNo = Integer.parseInt(deCodeElement.getAttribute("colno"));
                String type = deCodeElement.getAttribute("type");
                Integer ouCode = 0;
                if (type.equalsIgnoreCase("OU-DE"))
                {
                   ouCode = Integer.parseInt(deCodeElement.getAttribute("oucode"));
                }

                System.out.println("OU-DE-INFO ["+rowNo+","+colNo+","+sheetNo+",("+deCodeExpression+","+ouCode+")]");

                ExcelImport_OUDeCode excelImport_ouDeCode = new ExcelImport_OUDeCode(sheetNo,rowNo,colNo,deCodeExpression,ouCode);

                excelImport_ouDeCodeList.add(excelImport_ouDeCode);

            }

            String checkForEntry = new String();
            String InsertQuery = new String();
            String UpdateQuery = new String();

            DatabaseInfo dataBaseInfo = databaseInfoProvider.getDatabaseInfo();

            System.out.println("* INFO IMPORT LIST SIZE ["+excelImport_ouDeCodeList.size()+"]" );

            for (ExcelImport_OUDeCode excelImport_ouDeCode: excelImport_ouDeCodeList)
            {

                System.out.println("* INFO [EXCEL IMPORT VALUES :"+excelImport_ouDeCode.getExpression()+","+excelImport_ouDeCode.getOuCode()+"]");

                Integer dataElementId = 0 ;

                Integer optionComboId = 0 ;

                Integer ouCode = excelImport_ouDeCode.getOuCode();

                Integer dataCellRowNo = excelImport_ouDeCode.getRowno();

                Integer dataCellCollNo = excelImport_ouDeCode.getColno();

                int selectedPeriodID = selectedPeriod.getId();


                if(!excelImport_ouDeCode.getExpression().contains("."))
                {
                    System.out.println("* ERROR: WRONG DATAELEMENT EXPRESSION FORMAT ["+excelImport_ouDeCode.getExpression()+"]");
                    System.exit(1);
                }
                else
                {
                    System.out.println("* TEST PARSE-INT : "+(excelImport_ouDeCode.getExpression().substring(0,(excelImport_ouDeCode.getExpression().indexOf('.')))));
                    System.out.println("* TEST PARSE-INT : "+(excelImport_ouDeCode.getExpression().substring((excelImport_ouDeCode.getExpression().indexOf('.')+1),(excelImport_ouDeCode.getExpression().length()))));

                    dataElementId = Integer.parseInt(excelImport_ouDeCode.getExpression().substring(0,(excelImport_ouDeCode.getExpression().indexOf('.'))));
                    optionComboId = Integer.parseInt(excelImport_ouDeCode.getExpression().substring((excelImport_ouDeCode.getExpression().indexOf('.')+1),(excelImport_ouDeCode.getExpression().length())));
                }


                if ( dataBaseInfo.getType().equalsIgnoreCase( "postgresql" ) )
                {
                    checkForEntry = "SELECT COUNT(*) FROM datavalue WHERE dataelementid = "
                            + dataElementId + " AND categoryoptioncomboid = " + optionComboId + " AND periodid = "
                            + selectedPeriodID + " AND sourceid = " + ouCode;
                }
                else if ( dataBaseInfo.getType().equalsIgnoreCase( "mysql" ) )
                {

                    checkForEntry = "SELECT COUNT(*) FROM datavalue WHERE dataelementid = "
                            + dataElementId + " AND categoryoptioncomboid = " + optionComboId + " AND periodid = "
                            + selectedPeriodID + " AND sourceid = " + ouCode;
                }

                int preEntryCount = -1;

                preEntryCount = jdbcTemplate.queryForInt(checkForEntry);

                System.out.println("* QUERY CHECK: ["+checkForEntry+" >>> "+preEntryCount+"]");

                double dataValue = Double.parseDouble(importFileSheet.getCell(dataCellCollNo, dataCellRowNo).getContents().trim());;

                System.out.println("* CHECK EXCEL-READ : ["+dataValue+"]");

                int isInserted = 0;

                int isUpdated = 0;

                if(preEntryCount == 0)
                {
                    if ( dataBaseInfo.getType().equalsIgnoreCase( "postgresql" ) )
                    {
                        InsertQuery = "INSERT INTO datavalue (dataelementid,categoryoptioncomboid,periodid,sourceid) VALUES("
                                + dataElementId + "," + optionComboId + ","
                                + selectedPeriodID + "," + ouCode+")";
                    }
                    else if ( dataBaseInfo.getType().equalsIgnoreCase( "mysql" ) )
                    {

                        InsertQuery =  "INSERT INTO datavalue (dataelementid,categoryoptioncomboid,periodid,sourceid) VALUES("
                                + dataElementId + "," + optionComboId + ","
                                + selectedPeriodID + "," + ouCode+")";
                    }

                    isInserted = jdbcTemplate.update(InsertQuery);

                    if(isInserted==0)
                    {
                        System.out.println("* WARNING : INSERT Operation FAILED");
                    }
                    else
                    {
                        System.out.println("* INSERT QUERY CHECK: ["+InsertQuery+" >>> "+isInserted+"]");
                    }

                }
                else if(preEntryCount == 1)
                {
                    if ( dataBaseInfo.getType().equalsIgnoreCase( "postgresql" ) )
                    {
                        UpdateQuery = "UPDATE datavalue SET value="+dataValue+" WHERE dataelementid = "
                                + dataElementId + " AND categoryoptioncomboid = " + optionComboId + " AND periodid = "
                                + selectedPeriodID + " AND sourceid = " + ouCode;
                    }
                    else if ( dataBaseInfo.getType().equalsIgnoreCase( "mysql" ) )
                    {

                        UpdateQuery = "UPDATE datavalue SET value="+dataValue+" WHERE dataelementid = "
                                + dataElementId + " AND categoryoptioncomboid = " + optionComboId + " AND periodid = "
                                + selectedPeriodID + " AND sourceid = " + ouCode;;
                    }

                    isUpdated = jdbcTemplate.update(UpdateQuery);

                    if(isUpdated==0)
                    {
                        System.out.println("* WARNING : UPDATE Operation FAILED");
                    }
                    else
                    {
                        System.out.println("* UPADTE QUERY CHECK: ["+UpdateQuery+" >>> "+isUpdated+"]");
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


    public boolean validateReport(Workbook excelImportFile, Workbook excelTemplateFile) {

        boolean validator = true;

        final int sheetNumber = 0;

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

        System.out.println("* INFO: VALIDATING IMPORTED FILE ["+excelImportFile.getSheet(sheetNumber).getName()+"]"+"AGAINST TEMPLATE ["+excelTemplateFile.getSheet(sheetNumber).getName()+"]");

        Sheet importFileSheet = excelImportFile.getSheet(sheetNumber);
        Sheet templateFileSheet = excelTemplateFile.getSheet(sheetNumber);

        if (excelImportFile.getSheet(sheetNumber).getRows() == excelTemplateFile.getSheet(sheetNumber).getRows())
        {

            //-------------------------------- Checking Header Cells ---------------------------------------------

            for (int c = headerStartCol; c <= headerEndCol; c++) {
                for (int r = headerStartRow; r <= headerEndRow; r++) {

                    String cellContent = importFileSheet.getCell(c, r).getContents();
                    String templateContent = templateFileSheet.getCell(c, r).getContents();

                    if (templateContent.equalsIgnoreCase(cellContent) && cellContent.equalsIgnoreCase(templateContent)) {
                        continue;
                    }
                    else
                    {
                        System.out.println("["+cellContent+"|"+templateContent+"]");
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
                    }
                    else
                    {
                        System.out.println("["+cellContent+"|"+templateContent+"]");
                        validator = false;
                        break;
                    }
                }
            }
        }
        else
        {
            System.out.println("* FAILURE :Validation failed due to unequal count of rows.");
            validator = false;
        }

        return validator;
    }


    public Period getSelectedPeriod(Date startDate, PeriodType periodType) throws Exception {



        List<Period> periods = new ArrayList<Period>(periodService.getPeriodsByPeriodType(periodType));
        for (Period period : periods) {
            Date tempDate = period.getStartDate();
            if (tempDate.equals(startDate)) {
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

}
