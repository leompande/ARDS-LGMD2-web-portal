package org.hisp.dhis.hr;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.hisp.dhis.hr.AttributeOptionsService;

import org.amplecode.quick.mapper.RowMapper;

public class AggregatedReportMapper  
implements RowMapper<AggregateOperands>
{
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------
	
		
	@Override
	public AggregateOperands mapRow( ResultSet resultSet )
	    throws SQLException
	{
		final AggregateOperands operand = new AggregateOperands(
	    		resultSet.getString( 1 ),
	        resultSet.getInt( 2 ));
	    
	    return operand;
	}
}
