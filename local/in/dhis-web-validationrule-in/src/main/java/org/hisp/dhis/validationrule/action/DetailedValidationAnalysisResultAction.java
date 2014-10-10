package org.hisp.dhis.validationrule.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.amplecode.quick.StatementManager;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitShortNameComparator;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.util.comparator.PeriodStartDateComparator;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.hisp.dhis.validation.ValidationRuleService;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

//import com.opensymphony.webwork.ServletActionContext;
//import com.opensymphony.xwork2.ActionContext;
//import com.opensymphony.xwork2.ActionSupport;

public class DetailedValidationAnalysisResultAction extends ActionSupport
{
    private static final String NULL_REPLACEMENT = "0";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;
    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private DataElementService dataElementService;
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private AggregationService aggregationService;
    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private DataElementCategoryService dataElementCategoryService;
    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
   
    private ValidationRuleService validationRuleService;
    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }
        
    private I18nFormat format;
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private SelectionTreeManager selectionTreeManager;
    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    private OrganisationUnitService organisationUnitService;
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private PeriodService periodService;
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
       
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------    

    private String detailOption;  
    public void setDetailOption( String detailOption )
    {
        this.detailOption = detailOption;
    }

    private String vRule;
    public void setVRule( String rule )
    {
        vRule = rule;
    }

    private String avgOption; 
    public void setAvgOption( String avgOption )
    {
        this.avgOption = avgOption;
    }

    private String selOrgUnit;  
    public void setSelOrgUnit( String selOrgUnit )
    {
        this.selOrgUnit = selOrgUnit;
    }

    private String startDate;
    public String getStartDate()
    {
        return startDate;
    }
    
    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;
    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }
    
    private String includeChildren;
    public void setIncludeChildren( String includeChildren )
    {
        this.includeChildren = includeChildren;
    }  
    

    private List<OrganisationUnit> selOrgUnitList;
    public List<OrganisationUnit> getSelOrgUnitList()
    {
        return selOrgUnitList;
    }

    private List<ValidationRule> validationRuleList;
    public List<ValidationRule> getValidationRuleList()
    {
        return validationRuleList;
    }

    private List<Period> selPeriodList;    
    public List<Period> getSelPeriodList()
    {
        return selPeriodList;
    }

    private Map<OrganisationUnit, Map<ValidationRule,List<String>>> orgUnitResultMap;
    public Map<OrganisationUnit, Map<ValidationRule, List<String>>> getOrgUnitResultMap()
    {
        return orgUnitResultMap;
    }

    private Map<OrganisationUnit, Map<ValidationRule,List<String>>> orgUnitColorMap;
    public Map<OrganisationUnit, Map<ValidationRule, List<String>>> getOrgUnitColorMap()
    {
        return orgUnitColorMap;
    }
    
    private int validationRuleGroupId;
    public void setValidationRuleGroupId( int validationRuleGroupId )
    {
        this.validationRuleGroupId = validationRuleGroupId;
    }
    public int getValidationRuleGroupId(  )
    {
        return validationRuleGroupId;
    }
    
    private String statusMessage;
    
    public String getStatusMessage()
    {
        return statusMessage;
    }
    
    
    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------
    
    @SuppressWarnings( "unchecked" )    
    public String execute()
    {
        statementManager.initialise();
        
        // OrgansationUnit Info
        
        selOrgUnitList = new ArrayList<OrganisationUnit>();
        
        List<OrganisationUnit> sources = new ArrayList<OrganisationUnit>();
                        
        if(detailOption != null && detailOption.equalsIgnoreCase( "yes" ))
        {
            sources.add( organisationUnitService.getOrganisationUnit( Integer.parseInt( selOrgUnit ) ) );
            includeChildren = "immChild";
        }
        else if(avgOption != null && avgOption.equalsIgnoreCase( "yes" ))
        {
            sources.add( organisationUnitService.getOrganisationUnit( Integer.parseInt( selOrgUnit ) ) );
            includeChildren = "false";
        }
        else
        {
            sources = new ArrayList<OrganisationUnit>(selectionTreeManager.getSelectedOrganisationUnits());
        }
            
        Collections.sort( sources, new OrganisationUnitShortNameComparator() );

        if ( includeChildren.equalsIgnoreCase( "true" ) )
        {                                        
            for ( OrganisationUnit source : sources )
            {
                selOrgUnitList.addAll( getChildOrgUnitTree( source ) );
            }
        }
        else if( includeChildren.equalsIgnoreCase( "false" ) )
        {
            selOrgUnitList.addAll( sources );
        }
        else
        {
            for ( OrganisationUnit source : sources )
            {
                selOrgUnitList.add( source );
                List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( source.getChildren() );
                Collections.sort( organisationUnits, new OrganisationUnitShortNameComparator() );
                selOrgUnitList.addAll( organisationUnits );
            }            
        }
        
        // ValidationRule Info
        validationRuleList = new ArrayList<ValidationRule>();      
        ValidationRuleGroup vrg = new ValidationRuleGroup();     
        if(detailOption == null)
        {
            if( (validationRuleGroupId < 0 ) )
            {
                validationRuleList = new ArrayList<ValidationRule>( validationRuleService.getAllValidationRules());
            }
            else
            {
                //System.out.println("validationRuleGroupId == "+validationRuleGroupId);
                vrg = validationRuleService.getValidationRuleGroup( validationRuleGroupId );
                validationRuleList.addAll( (Collection<? extends ValidationRule>) vrg.getMembers() );
                
                if(validationRuleList == null || validationRuleList.size() <=0 )
                {
                    statusMessage = "There is no ValidationRules available in ValidationGroup : "+vrg.getName();
                    
                    return INPUT;
                }
            }       
        }           
        else
        {
            ValidationRule vr = validationRuleService.getValidationRule( Integer.parseInt( vRule ) );
            validationRuleList.add( vr );
            //validationRuleList = new ArrayList<ValidationRule>( validationRuleService.getAllValidationRules());
        }
        
        // Period Info
        Date sDate = format.parseDate( startDate );            
        Date eDate = format.parseDate( endDate );            

        selPeriodList = new ArrayList<Period>();
        List<Period> periodList = new ArrayList<Period>(periodService.getIntersectingPeriods( sDate, eDate ));
        Iterator<Period> it1 = periodList.iterator();
        while(it1.hasNext())
        {
            Period p1 = (Period) it1.next();
            if(p1.getPeriodType().getName().equalsIgnoreCase( "monthly" ))
            {
                selPeriodList.add( p1 );
            }                
        }
        Collections.sort( selPeriodList, new PeriodStartDateComparator() );
        
        orgUnitResultMap = new HashMap<OrganisationUnit, Map<ValidationRule, List<String>>>();
        orgUnitColorMap = new HashMap<OrganisationUnit, Map<ValidationRule, List<String>>>();
        
        Iterator sourceIterator = selOrgUnitList.iterator();
        while(sourceIterator.hasNext())
        {
            OrganisationUnit orgUnit = (OrganisationUnit) sourceIterator.next();
            
            Map<ValidationRule, List<String>> vrResultMap = new HashMap<ValidationRule, List<String>>();
            Map<ValidationRule, List<String>> vrColorMap = new HashMap<ValidationRule, List<String>>();
            
            Iterator validationIterator = validationRuleList.iterator();
            while(validationIterator.hasNext())            
            {
                ValidationRule valRule = (ValidationRule) validationIterator.next();
                Expression leftExpression = valRule.getLeftSide();
                Expression rightExpression = valRule.getRightSide();

                List<String> tempVRResultList = new ArrayList<String>();
                List<String> tempVRColorList = new ArrayList<String>();
                Iterator periodIterator = selPeriodList.iterator();            
                while(periodIterator.hasNext())
                {
                    Period period = (Period) periodIterator.next();
                    
                    Double leftSide = getResultValue( leftExpression.getExpression(), period.getStartDate(), period.getEndDate(), orgUnit);
                    Double rightSide = getResultValue( rightExpression.getExpression(), period.getStartDate(), period.getEndDate(), orgUnit);
                    
                    if( leftSide == null) leftSide = 0.0;
                    if( rightSide == null ) rightSide = 0.0;

                    String tempString = ""+leftSide;
                    //System.out.println("*************************************************");
                    //System.out.println(valRule.getName()+" ---- "+valRule.getOperator());
                    if(valRule.getOperator().equalsIgnoreCase( ValidationRule.OPERATOR_EQUAL ))
                    {
                        if(leftSide.doubleValue() == rightSide.doubleValue())
                        {
                            tempVRColorList.add( "green" );
                            //System.out.print("green green : ");
                        }
                        else
                        {
                            tempVRColorList.add( "red" );
                            //System.out.print("red red : ");
                        }
                                                
                        tempString += "==";
                    }                       
                    else if(valRule.getOperator().equalsIgnoreCase( ValidationRule.OPERATOR_NOT_EQUAL ))
                    {
                        if(leftSide.doubleValue() != rightSide.doubleValue())
                        {
                            tempVRColorList.add( "green" );
                            //System.out.print("green : ");
                        }
                        else
                        {
                            tempVRColorList.add( "red" );
                            //System.out.print("red : ");
                        }
                        tempString += "!=";
                    }
                    else if(valRule.getOperator().equalsIgnoreCase( ValidationRule.OPERATOR_GREATER ))
                    {
                        if(leftSide.doubleValue() > rightSide.doubleValue())
                        {
                            tempVRColorList.add( "green" );
                            //System.out.print("green : ");
                        }
                        else
                        {
                            tempVRColorList.add( "red" );
                            //System.out.print("red : ");
                        }
                        tempString += ">";

                    }
                    else if(valRule.getOperator().equalsIgnoreCase( ValidationRule.OPERATOR_GREATER_EQUAL ))
                    {
                        if(leftSide.doubleValue() >= rightSide.doubleValue())
                        {
                            tempVRColorList.add( "green" );
                            //System.out.print("green : ");
                        }
                        else
                        {
                            tempVRColorList.add( "red" );
                            //System.out.print("red : ");
                        }
                        tempString += ">=";

                    }
                    else if(valRule.getOperator().equalsIgnoreCase( ValidationRule.OPERATOR_LESSER ))
                    {
                        if(leftSide.doubleValue() < rightSide.doubleValue())
                        {
                            tempVRColorList.add( "green" );
                            //System.out.print("green : ");
                        }
                        else
                        {
                            tempVRColorList.add( "red" );
                            //System.out.print("red : ");
                        }
                        tempString += "<";
                    }
                    else if(valRule.getOperator().equalsIgnoreCase( ValidationRule.OPERATOR_LESSER_EQUAL ))
                    {
                        if(leftSide.doubleValue() <= rightSide.doubleValue())
                        {
                            tempVRColorList.add( "green" );
                            //System.out.print("green : ");
                        }
                        else
                        {
                            tempVRColorList.add( "red" );
                            //System.out.print("red : ");
                        }
                        tempString += "<=";
                    }
                    
                    //System.out.println("Operator : "+valRule.getOperator());
                    tempString += rightSide;
                    //System.out.println(tempString + " : " );
                    tempVRResultList.add( tempString );

                }// Period While end
                vrResultMap.put( valRule, tempVRResultList );
                vrColorMap.put( valRule, tempVRColorList );
                                
            }// ValidationRule While end
            
            orgUnitResultMap.put( orgUnit, vrResultMap );
            orgUnitColorMap.put( orgUnit, vrColorMap);
        }// Source While end

        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );
        HttpSession session = req.getSession();
        
        session.setAttribute( "orgUnitResultMap", orgUnitResultMap );
        session.setAttribute( "orgUnitColorMap", orgUnitColorMap );
        session.setAttribute( "selPeriodList", selPeriodList );
        session.setAttribute( "selOrgUnitList", selOrgUnitList );
        session.setAttribute( "validationRuleList", validationRuleList );
        
        statementManager.destroy();

        if(detailOption != null && detailOption.equalsIgnoreCase( "yes" ))
            return "detForOneVR";
        
        return SUCCESS;        
    }
    
    
    
    
    private double getResultValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit)
    {           
        try
        {               
            int deFlag1 = 0;
            int deFlag2 = 0;
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );
            
            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();            
            
            while ( matcher.find() )
            {
                String replaceString = matcher.group();
                
                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf('.')+1, replaceString.length() );
                
                replaceString = replaceString.substring( 0, replaceString.indexOf('.') );
                
                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );                

                
                DataElement dataElement = dataElementService.getDataElement( dataElementId );                
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( optionComboId );

                if(dataElement == null || optionCombo == null)
                {
                    replaceString = NULL_REPLACEMENT;
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }
                if(dataElement.getType().equalsIgnoreCase( "int" ))
                {                
                    Double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo, startDate, endDate, organisationUnit );                
                    //System.out.println(aggregatedValue+" ---- "+dataElement.getName());                
                    if ( aggregatedValue == null )
                    {
                        replaceString = NULL_REPLACEMENT;
                    }
                    else
                    {
                        replaceString = String.valueOf( aggregatedValue );
                        deFlag2 = 1;
                    }
                }
                else
                {                    
                    replaceString = NULL_REPLACEMENT;

                }
                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );

            String resultValue = "";
            if(deFlag1 == 0)
            {
                double d = 0.0;
                try
                {
                    //System.out.println("Expression : "+buffer.toString());
                    if(buffer.toString().contains( "/0.0" ) || buffer.toString().contains( "/0" ) || buffer.toString().contains( "/((0.0" ) || buffer.toString().contains( "/((0" ))
                        d = 0.0;
                    else
                        d = 0;
                        d = MathUtils.calculateExpression(buffer.toString());
                }
                catch(ArithmeticException e)
                {
                    d = 0.0;
                    System.out.println("Divide By Zero ");                    
                }
                catch(Exception e)
                {
                    d = 0.0;
                    System.out.println("Divide By Zero ");
                }
                if(d == -1) d = 0.0;
                else 
                {
                    d = Math.round( d * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );                    
                    resultValue = ""+ (int)d;
                }
                
                if(deFlag2 == 0)
                {
                    resultValue = " ";
                }
            }
            else
            {
               resultValue = buffer.toString(); 
            }
            
            double finalResult = 0.0;
            try
            {
                finalResult = Double.parseDouble(resultValue);
            }
            catch(Exception e)
            {
                finalResult = 0.0;
            }
            
            return finalResult;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    
    // Returns the OrgUnitTree for which Root is the orgUnit
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>(orgUnit.getChildren());
        Collections.sort( children, new OrganisationUnitShortNameComparator() );

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end

}
