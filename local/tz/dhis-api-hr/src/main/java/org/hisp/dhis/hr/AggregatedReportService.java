package org.hisp.dhis.hr;

import java.util.Collection;

import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.jfree.chart.JFreeChart;

public interface AggregatedReportService {
	
	String ID = AggregatedReportService.class.getName();

	/**
     * Returns the data of employees (depending
     * on the selectedUnitOnly argument) mapped to a Attribute operand identifier.
     * 
     * @param dataSet the DataSet containing the Attribute to include in the data.
     * @param unit the OrganisationUnit.
     * @param selectedUnitOnly whether to return the data for the select unit only or with its lower levels.
     * @return a map.
     */
	//Collection<Person> getEmployeeData( HrDataSet dataSet, OrganisationUnit unit, boolean selectedUnitOnly );
	JFreeChart getJFreeChart();
	
	JFreeChart getJFreeChart( HrDataSet dataSet, Attribute attribute, OrganisationUnit unit, boolean selectedUnitOnly);
}
