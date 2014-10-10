package org.hisp.dhis.linelisting.llaggregation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultLinelistAggMapService
    implements LinelistAggMapService
{
    Log log = LogFactory.getLog( getClass() );
    
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private static final String ALIAS_GROUPTABLE = "g";

    private static final String MAP_CONSTANT_QUERYFROM = "queryFrom";

    private static final String MAP_CONSTANT_QUERYWHERE = "queryWhere";

    private static final String MAP_CONSTANT_GROUPTABLE = "aliasGroup";
    
    private static final String QUERY_WHERE = " WHERE ";
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LinelistAggMapStore linelistAggMapStore;

    public void setLinelistAggMapStore( LinelistAggMapStore linelistAggMapStore )
    {
        this.linelistAggMapStore = linelistAggMapStore;
    }

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    // -------------------------------------------------------------------------
    // LinelistAggregationMapping
    // -------------------------------------------------------------------------

    public void addLineListAggregationMapping( LinelistAggregationMapping llAggregationMapping )
    {
        linelistAggMapStore.addLineListAggregationMapping( llAggregationMapping );
    }

    public void deleteLinelistAggregationMapping( LinelistAggregationMapping llAggregationMapping )
    {
        linelistAggMapStore.deleteLinelistAggregationMapping( llAggregationMapping );
    }

    public void updateLinelistAggregationMapping( LinelistAggregationMapping llAggregationMapping )
    {
        linelistAggMapStore.updateLinelistAggregationMapping( llAggregationMapping );
    }

    public LinelistAggregationMapping getLinelistAggregationMappingByOptionCombo( DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo )
    {
        return linelistAggMapStore.getLinelistAggregationMappingByOptionCombo( dataElement, optionCombo );
    }
    
    public  int executeAggregationQuery( OrganisationUnit orgUnit, Period period, LinelistAggregationMapping mappingObject )
    {
        try 
        {
            String query =  buildQuery( orgUnit, period, scan( mappingObject.getExpression() ) );
            System.out.println("QUERY: "+query);
            return linelistAggMapStore.executeAggregationQuery( query );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Scan and parse the Aggeration Mapping Query
     * 
     * @param input
     * @return CaseAggregationQuery, this object contains the list Conditions of
     *         the mapping query and the main function of the query (SUM OR
     *         COUNT)
     * @throws Exception 
     */

    public AggregationQuery scan( String input ) throws Exception
    {
        input = StringUtils.deleteWhitespace( input );
        AggregationQuery query = new AggregationQuery();
        String[] arr = input.split( AggregationCondition.FUNCTION_IDENTIFIER );
        query.setFunction( arr[0] );
        char[] arrChar = arr[1].toCharArray();
        String tmp = "";
        int i = 0;
        AggregationCondition c = null;
        while ( i < arrChar.length )
        {
            tmp += arrChar[i];

            if ( AggregationCondition.SINGLE_CONDITION.equals( tmp ) || AggregationCondition.CONDITION.equals( tmp ) )
            {
                c = new AggregationCondition();
                c.setType( AggregationCondition.SINGLE_CONDITION.equals( tmp ) ? AggregationCondition.SINGLE_CONDITION
                    : AggregationCondition.CONDITION );
                tmp = "";
            }
            if ( AggregationCondition.OPEN_CONDITION == arrChar[i] )
            {
                i++;
                tmp = "";
                while ( i < arrChar.length  && AggregationCondition.CLOSE_CONDITION != arrChar[i]  )
                {
                    if ( AggregationCondition.OPEN_CONDITION == arrChar[i] )
                    {
                        throw new Exception("Missing close condition character: "+input);
                    }
                    tmp += arrChar[i];
                    i++;
                }
                if ( AggregationCondition.SINGLE_CONDITION.equals( c.getType() ) )
                {
                    c.setLeftExpression( tmp );
                }
                else if ( AggregationCondition.CONDITION.equals( c.getType() ) )
                {
                    c = parseCondition( c, tmp );
                }
                tmp = "";
                query.addCondition( c );
            }
            if ( "AND".equals( tmp ) || "OR".equals( tmp ) )
            {
                
                query.getConditions().get( query.getConditions().size() - 1 ).setNext( tmp );

                tmp = "";
            }
            i++;
        }
        
        // check if there is a single condition. then it should not have any more condition
        
        for( AggregationCondition con : query.getConditions() )
        {
            if( con.getType().equalsIgnoreCase( AggregationCondition.SINGLE_CONDITION ))
            {
                if( query.getConditions().size() > 1 )
                {
                    throw new Exception("If there is one single condition, then you can not have anymore condition");
                }
            }
        }

        return query;
    }
    
    /**
     * 
     * @param c
     * @param input
     * @return
     * @throws Exception 
     */

    private AggregationCondition parseCondition( AggregationCondition c, String input ) throws Exception
    {
        input = StringUtils.deleteWhitespace( input );
        String tmp = "";
        int i = 0;
        char[] arr = input.toCharArray();
        while ( i < arr.length )
        {
            if ( AggregationCondition.OPEN_EXPRESSION == arr[i] )
            {
                i++;
                while ( AggregationCondition.CLOSE_EXPRESSION != arr[i] && i < arr.length )
                {
                    if ( AggregationCondition.OPEN_EXPRESSION == arr[i] )
                    {
                        throw new Exception("Missing close expression character : "+input);
                    }
                    tmp += arr[i];
                    i++;
                }
                
                if ( c.getLeftExpression() == null )
                {
                    c.setLeftExpression( tmp );
                }
                else
                {
                    c.setRightExpression( tmp );
                }
                tmp = "";

            }
            else
            {
                c.setOperator( c.getOperator() + arr[i] );
            }
            i++;
        }
        return c;
    }

    /**
     * parse an Expression into a list of characters
     * 
     * @param input : expression string
     * @return : list of characters of the input expression
     * @throws Exception 
     */
    private List<String> parseExpression( String input ) throws Exception
    {
        if ( input == null )
            return null;

        List<String> list = new ArrayList<String>();

        input = StringUtils.deleteWhitespace( input );
        String tmp = "";
        int i = 0;
        char[] arr = input.toCharArray();
        while ( i < arr.length )
        {
            if ( AggregationCondition.OPEN_ARGUMENT == arr[i] )
            {
                i++;
                while ( AggregationCondition.CLOSE_ARGUMENT != arr[i] && i < arr.length )
                {
                    tmp += arr[i];
                    i++;
                }
                if( AggregationCondition.CLOSE_ARGUMENT != arr[i])
                {
                    throw new Exception("Missing close character : "+input);
                }
                list.add( tmp );
                tmp = "";
            }
            else if ( isOperator( arr[i] ) )
            {
                if ( list.size() == 0 )
                {
                    throw new Exception("Error parseExpression: " + input + "-- at char: " + arr[i]);
                }
                list.add( CharUtils.toString( arr[i] ) );
            }
//            else if ( new char(''))
//            {
//                // error unidentified character ....
//                throw new Exception("Error unidentified character while parsing expression :"+ input + "-- at char: " + arr[i]);
//            }
            i++;
        }

        return list;
    }

    /**
     * Generate sql query from AggregationQuery Object
     * 
     * @param organisationUnit
     * @param period
     * @param input : AggregationQuery
     * @return SQL query for aggregation.
     */
    public String buildQuery( OrganisationUnit organisationUnit, Period period, AggregationQuery input ) throws Exception
    {

        String function = "";
        if ( input.getFunction().equals( "COUNT" ) )
        {
            function = "COUNT(*)";
        }
        else if ( input.getFunction().equals( "SUM" ) )
        {
            function = "SUM(" + ALIAS_GROUPTABLE + ".value)";
        }
        else
        {
            throw new Exception("Error syntax. Unknow function: "+function);
        }

        String queryFrom = "Select " + function + " from ";

        String queryWhere = QUERY_WHERE;
        System.out.println( "listConditions: " + input.getConditions() );
        Map<String, String> map = new HashMap<String, String>();
        map.put( MAP_CONSTANT_QUERYWHERE, queryWhere );
        map.put( MAP_CONSTANT_QUERYFROM, queryFrom );
        AggregationCondition c = null;
        for ( int i = 0; i < input.getConditions().size(); i++ )
        {
            c = input.getConditions().get( i );
            map = buildQueryForExpression( c, map, i );
            if ( c.getNext() != null )
            {
                map.put( MAP_CONSTANT_QUERYWHERE, map.get( MAP_CONSTANT_QUERYWHERE ) + " " + c.getNext() + " " );
            }
        }
        queryFrom = map.get( MAP_CONSTANT_QUERYFROM );
        queryWhere = map.get( MAP_CONSTANT_QUERYWHERE );

        queryFrom = StringUtils.substringBeforeLast( queryFrom, "," );

        queryWhere = queryWhere.replace( "$ORGUNITID$", "" + organisationUnit.getId() );
        queryWhere = queryWhere.replace( "$PERIODID$", ""  +  period.getId() );

        queryWhere = StringUtils.substringBeforeLast( queryWhere, "AND" );
        return queryFrom + queryWhere;
    }

    /**
     * generate sql query for an expression
     * 
     * @param condition : AggregationCondition object
     * @param map: Map contains current WHERE query and FROM query
     * @param index : index of the parent for loop, using for create table alisa
     * @return a Map contains current WHERE and FROM query
     */

    private Map<String, String> buildQueryForExpression( AggregationCondition condition, Map<String, String> map,
        int index ) throws Exception
    {
       
        if ( map == null )
        {
            map = new HashMap<String, String>();
        }

        String[] arrExp = null;
        String[] arrIds = null;

        int groupId = 0;
        int dataElementId = 0;

        String tmpFromQuery = "";
        String tmpWhereQuery = " ";
        String curAliasGroupTable = null;
        String tableName = "";

        boolean singleExpression = false;

        List<String> listArg = parseExpression( condition.getLeftExpression() );
        System.out.println( "listArg: " + listArg );

        if ( listArg.size() > 1 )
        {
            // there are more than 1 arg in this expression -> using sub query
            tmpFromQuery = " ( Select $value$ from ";
        }
        else
        {
            singleExpression = true;
            tmpFromQuery = map.get( MAP_CONSTANT_QUERYFROM );
            tmpWhereQuery = map.get( MAP_CONSTANT_QUERYWHERE );
        }
        String arg = null;
        for ( int i = 0; i < listArg.size(); i++ )
        {
            arg = listArg.get( i );

            if ( isOperator( arg.charAt( 0 ) ) )
            {
                // if this is operator ( +, - , * , / ) then just append to the where clause
                map.put( MAP_CONSTANT_QUERYWHERE, map.get( MAP_CONSTANT_QUERYWHERE ) + arg );
            }
            else
            {
                // if this is an expression, then replace all needed parameters

                arrExp = StringUtils.split( arg, AggregationCondition.ARGUMENT_IDENTIFIER );
                
                if ( arrExp.length != 2 )
                {
                    throw new Exception("Invalid syntax at : "+arg);
                }
                else if ( AggregationCondition.ARGUMENT_DATALEMENT.equals( arrExp[0] ) )
                {
                    arrIds = StringUtils.split( arrExp[1], AggregationCondition.ARGUMENT_SPLITTER );

                    if ( arrIds.length != 2 )
                    {
                        throw new Exception( "Error syntax in expression" + arrExp[1] );
                    }
                    // get all parameters
                    groupId = NumberUtils.toInt( arrIds[0], 0 );
                    dataElementId = NumberUtils.toInt( arrIds[1], 0 );

                    // Get table name from group id
                    LineListGroup group = lineListService.getLineListGroup( groupId );

                    if ( group == null )
                    {
                        // ERROR : can not find group with id = groupId
                       throw new Exception("Can not find group with id: "+groupId);
                    }

                    tableName = group.getShortName();
//                    tableName="livebirth";
                    String columnName = "";
                    List<LineListElement> listDe = new ArrayList<LineListElement>(group.getLineListElements());
                    if( listDe == null || listDe.size() == 0 )
                    {
                        throw new Exception("Line listing group does not have any Line listing data element");
                    }
                    int k=0;
                    boolean found=false;
                    LineListElement element = null;
                    while ( k < listDe.size() &&  !found )
                    {
                        element = listDe.get(k);
                        if ( element.getId() == dataElementId )
                        {
                            columnName = element.getShortName();
                            found = true;
                        }
                        k++;
                    }
//
                    if ( StringUtils.isBlank( columnName ) )
                    {
                        // ERROR : can not find element with id = elementId
                        throw new Exception("Can not find element with id: "+dataElementId);
                    }

                    if ( condition.getRightExpression() != null && condition.getOperator() != null )
                    {
                        // build query for the right expression of current
                        // condition.

                        List<String> listRightArg = parseExpression( condition.getRightExpression() );

                        if ( listRightArg.size() != 1 )
                        {
                            // current not supported ... [DE.1.2.3] >
                            // [DE.1.2.3] + 100 ???
                            throw new Exception("Not supported two arguments in one side of an expression: "+condition.getRightExpression());
                        }
                        else
                        {
                            // build the where clause with all parameters
                            if ( !StringUtils.contains( tmpWhereQuery, QUERY_WHERE ))
                            {
                                tmpWhereQuery += QUERY_WHERE ;
                            }
                            
                            tmpWhereQuery += "$ALIAS_GROUPTABLE$." + columnName + " = ";
                            arrExp = StringUtils.split( listRightArg.get( 0 ),
                                AggregationCondition.ARGUMENT_IDENTIFIER );
                            if ( arrExp.length != 2 )
                            {
                                // error format . Correct : [VL:123]
                                throw new Exception("Invalid syntax at : "+ listRightArg.get( 0 ));
                            }
                            if ( "VL".equals( arrExp[0] ) )
                            {
                                tmpWhereQuery += arrExp[1];
                            }
                            else
                            {
                                continue;
                            }
                            tmpWhereQuery += " AND ";
                        }
                    }
                    if ( !singleExpression )
                    {
                        tmpWhereQuery += ")";
                    }
                }

                // -----------------------------------------------------------------------------------------
                // Replace alias
                // -----------------------------------------------------------------------------------------
                if ( singleExpression )
                {
                    // add patientdatavalue and programstageinstance alias to
                    // FROM clause, only once.

                    if ( map.get( MAP_CONSTANT_GROUPTABLE ) == null )
                    {
                        curAliasGroupTable = " " + ALIAS_GROUPTABLE;
                        map.put( MAP_CONSTANT_GROUPTABLE, curAliasGroupTable );
                        tmpFromQuery += tableName + " as " + curAliasGroupTable + ",";

                    }
                    else
                    {
                        curAliasGroupTable = map.get( MAP_CONSTANT_GROUPTABLE );
                    }

                }
                else if ( !singleExpression )
                {
                    // using subquery here , need to add some unique number to
                    // the alias ...
                    curAliasGroupTable = " " + ALIAS_GROUPTABLE + "_" + index;
                    map.put( MAP_CONSTANT_GROUPTABLE, curAliasGroupTable );
                    tmpFromQuery += tableName + " as " + curAliasGroupTable + ",";
                }

            }

            // Add org Unit and period condition
            if( !tmpWhereQuery.contains( "sourceid" ) )
            {
                tmpWhereQuery += " $ALIAS_GROUPTABLE$.sourceid = $ORGUNITID$  AND $ALIAS_GROUPTABLE$.periodid = $PERIODID$ ";
            }
            
            if ( curAliasGroupTable != null )
                tmpWhereQuery = tmpWhereQuery.replace( "$ALIAS_GROUPTABLE$", curAliasGroupTable );
        }

        map.put( MAP_CONSTANT_QUERYWHERE, tmpWhereQuery );
        map.put( MAP_CONSTANT_QUERYFROM, tmpFromQuery );

        return map;
    }

    private boolean isOperator( char input )
    {
        return '+' == input || '-' == input || '*' == input || '/' == input;
    }

    public static void main( String[] args )
    {
        String string = "SUM@ COND{ ([LE:7.57]) < ([VL:2500]) } AND SCOND {([LE:7.45])} ";
        String input = "COUNT@ COND{([LE:1.2]) = ([VL:123])}";
        DefaultLinelistAggMapService test = new DefaultLinelistAggMapService();
        AggregationQuery query = null;
        try
        {
            query = test.scan( input );
            OrganisationUnit org = new OrganisationUnit();
            org.setId( 1 );
            Period p = new Period();
            p.setId( 58 );
            System.out.println( test.buildQuery( org, p, query ) );
        }
        catch ( Exception e1 )
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    public void validateAggregateQuery( String arg0 )
        throws Exception
    {
        scan(arg0);
    }

}
