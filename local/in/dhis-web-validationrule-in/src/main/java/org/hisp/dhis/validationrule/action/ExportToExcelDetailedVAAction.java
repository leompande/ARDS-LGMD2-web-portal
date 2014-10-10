package org.hisp.dhis.validationrule.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.validation.ValidationRule;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class ExportToExcelDetailedVAAction extends ActionSupport
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------
    
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    /*
    private String contentType;

    public String getContentType()
    {
        return contentType;
    }
    */

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    /*
    private int bufferSize;

    public int getBufferSize()
    {
        return bufferSize;
    }
    */
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {   
        int tempCol1 = 0;
        int tempRow1 = 1;
        
        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );                        
        HttpSession session = req.getSession();

        Map<OrganisationUnit, Map<ValidationRule,List<String>>> orgUnitResultMap = (Map<OrganisationUnit, Map<ValidationRule,List<String>>>) session.getAttribute( "orgUnitResultMap" );
        Map<OrganisationUnit, Map<ValidationRule,List<String>>> orgUnitColorMap = (Map<OrganisationUnit, Map<ValidationRule,List<String>>>) session.getAttribute( "orgUnitColorMap" );
        List<Period> selPeriodList = (ArrayList<Period>) session.getAttribute( "selPeriodList" );
        List<ValidationRule> validationRuleList = (ArrayList<ValidationRule>) session.getAttribute( "validationRuleList" );
        List<OrganisationUnit> selOrgUnitList = (ArrayList<OrganisationUnit>) session.getAttribute( "selOrgUnitList" );
            
        if(orgUnitResultMap == null || orgUnitColorMap == null)
        {
            System.out.println("Session Variables are null"); 
            return SUCCESS;
        }
        
        String outputReportPath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "db"
        + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File(outputReportPath) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "ValidationOutput", 0 );


        WritableFont wFont1 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
        wFont1.setColour( Colour.RED );

        WritableFont wFont2 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
        wFont2.setColour( Colour.GREEN );

        WritableFont wFont3 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);        

        WritableCellFormat wCellformat1 = new WritableCellFormat(wFont1);                            
        wCellformat1.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat1.setAlignment( Alignment.CENTRE );
        wCellformat1.setWrap( true );
        
        WritableCellFormat wCellformat3 = new WritableCellFormat(wFont2);                            
        wCellformat3.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat3.setAlignment( Alignment.CENTRE );
        wCellformat3.setWrap( true );


        WritableCellFormat wCellformat2 = new WritableCellFormat(wFont3);                            
        wCellformat2.setBorder( Border.ALL, BorderLineStyle.THIN );        
        wCellformat2.setBackground( Colour.GRAY_25 );                
        wCellformat2.setWrap( true );

        WritableCellFormat wCellformat4 = new WritableCellFormat();                            
        wCellformat4.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat1.setAlignment( Alignment.CENTRE );
        wCellformat4.setWrap( true );

        String tempStr;

        Iterator<OrganisationUnit> it1 = selOrgUnitList.iterator();
        while(it1.hasNext())
        {
            tempCol1 = 0;
            OrganisationUnit ou = (OrganisationUnit) it1.next();
            
            sheet0.addCell( new Label( tempCol1, tempRow1, ou.getShortName(), wCellformat2) );
            sheet0.mergeCells( tempCol1, tempRow1, tempCol1+5, tempRow1 );
            tempCol1 += 6;
            
            Iterator<Period> it4 = selPeriodList.iterator();
            while(it4.hasNext())
            {
                Period p = (Period) it4.next();
                tempStr = p.getStartDate()+ " To " + p.getEndDate();
                sheet0.addCell( new Label( tempCol1, tempRow1, tempStr, wCellformat2) );
                sheet0.mergeCells( tempCol1, tempRow1, tempCol1+1, tempRow1 );
                tempCol1 += 2;
            }
            
            tempRow1++;            
            
            Map<ValidationRule,List<String>> vrResultMap = orgUnitResultMap.get( ou );
            Map<ValidationRule,List<String>> vrColorMap = orgUnitColorMap.get( ou );            
            Iterator<ValidationRule> it2 = validationRuleList.iterator();            
            while(it2.hasNext())
            {
                tempCol1 = 0;
                ValidationRule vr = (ValidationRule) it2.next();
                List<String> vrResultList = vrResultMap.get( vr );
                List<String> vrColorList = vrColorMap.get( vr );

                if(vrColorList.contains( "red" ))
                {
                    sheet0.addCell( new Label( tempCol1, tempRow1, vr.getName(), wCellformat2) );
                    sheet0.mergeCells( tempCol1, tempRow1, tempCol1+5, tempRow1 );
                    tempCol1 += 6;
                                
                    int count1 = 0;
                    Iterator<String> it3 = vrResultList.iterator();                
                    while(it3.hasNext())
                    {
                        tempStr = (String) it3.next();
                        String tempColor = vrColorList.get( count1 );                        
                        if(tempColor.equalsIgnoreCase( "red" ))
                        {
                            sheet0.addCell( new Label( tempCol1, tempRow1, tempStr, wCellformat1) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( tempCol1, tempRow1, " ", wCellformat4) );
                        }
                        sheet0.mergeCells( tempCol1, tempRow1, tempCol1+1, tempRow1 );
                        tempCol1 += 2;
                        count1++;
                    }// period values loop
                    tempRow1++;
                }    
            }// validation Rule loop
            
            tempRow1 += 2;
        }// orgunit loop
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "validationAnalysis.xls";
                
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );


        return SUCCESS;
    }
            
}
