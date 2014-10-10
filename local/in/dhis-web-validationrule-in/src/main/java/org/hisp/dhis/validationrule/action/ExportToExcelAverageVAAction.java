package org.hisp.dhis.validationrule.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
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
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class ExportToExcelAverageVAAction  extends ActionSupport
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

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

        Hashtable<OrganisationUnit,List<Integer>> validationAverageResult = (Hashtable<OrganisationUnit,List<Integer>>) session.getAttribute( "validationAverageResult" );
        List<Period> selPeriodList = (ArrayList<Period>) session.getAttribute( "selPeriodList" );
        List<OrganisationUnit> selOrgUnitList = (ArrayList<OrganisationUnit>) session.getAttribute( "selOrgUnitList" );
            
        if(validationAverageResult == null || selPeriodList == null || selOrgUnitList == null)
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
        wCellformat4.setAlignment( Alignment.CENTRE );
        wCellformat4.setBackground( Colour.WHITE );
        wCellformat4.setWrap( true );

        WritableCellFormat wCellformat5 = new WritableCellFormat();                            
        wCellformat5.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat5.setAlignment( Alignment.CENTRE );
        wCellformat5.setBackground( Colour.BROWN );
        wCellformat5.setWrap( true );

        WritableCellFormat wCellformat6 = new WritableCellFormat();                            
        wCellformat6.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat6.setAlignment( Alignment.CENTRE );
        wCellformat6.setBackground( Colour.LIGHT_GREEN );
        wCellformat6.setWrap( true );

        WritableCellFormat wCellformat7 = new WritableCellFormat();                            
        wCellformat7.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat7.setAlignment( Alignment.CENTRE );
        wCellformat7.setBackground( Colour.RED );
        wCellformat7.setWrap( true );

        WritableCellFormat wCellformat8 = new WritableCellFormat();                            
        wCellformat8.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat8.setAlignment( Alignment.CENTRE );
        wCellformat8.setBackground( Colour.AQUA );
        wCellformat8.setWrap( true );
        
        String tempStr;

        int count = 0;
        while(count < organisationUnitService.getNumberOfOrganisationalLevels())
        {
            tempStr = "Level"+(count+1);
            sheet0.addCell( new Label( tempCol1, tempRow1, tempStr, wCellformat2) );
            count++;
            tempCol1++;
        }
        
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
        
        Iterator<OrganisationUnit> it1 = selOrgUnitList.iterator();
        while(it1.hasNext())
        {            
            OrganisationUnit ou = (OrganisationUnit) it1.next();
            tempCol1 = organisationUnitService.getLevelOfOrganisationUnit( ou )-1;
            
            sheet0.addCell( new Label( tempCol1, tempRow1, ou.getShortName(), wCellformat4) );            
            
            tempCol1 = organisationUnitService.getNumberOfOrganisationalLevels();
                                                
            List<Integer> resultList = validationAverageResult.get( ou );
            Iterator<Integer> it2 = resultList.iterator();            
            while(it2.hasNext())
            {                
                Integer result = (Integer) it2.next();
                tempStr = String.valueOf( result );
                if(result.intValue() < 0)
                    sheet0.addCell( new Label( tempCol1, tempRow1, tempStr, wCellformat4) );
                else if(result.intValue() == 0)
                    sheet0.addCell( new Label( tempCol1, tempRow1, tempStr, wCellformat5) );
                else if(result.intValue() > 75)
                    sheet0.addCell( new Label( tempCol1, tempRow1, tempStr, wCellformat6) );
                else if(result.intValue() > 40 && result.intValue() <= 75)
                    sheet0.addCell( new Label( tempCol1, tempRow1, tempStr, wCellformat7) );
                else
                    sheet0.addCell( new Label( tempCol1, tempRow1, tempStr, wCellformat8) );

                
                sheet0.mergeCells( tempCol1, tempRow1, tempCol1+1, tempRow1 );
                tempCol1 += 2;
                
            }// validation Rule loop
            
            tempRow1++;
        }// orgunit loop
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "validationAnalysis.xls";
                
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );


        return SUCCESS;
    }

            
}
