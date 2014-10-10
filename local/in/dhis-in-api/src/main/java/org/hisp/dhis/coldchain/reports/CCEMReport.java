package org.hisp.dhis.coldchain.reports;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ccemReport")
public class CCEMReport
{
    public static final String ORGUNITGROUP_DATAVALUE = "ORGUNITGROUP_DATAVALUE";
    public static final String ORGUNIT_EQUIPMENT_ROUTINE_DATAVALUE = "ORGUNIT_EQUIPMENT_ROUTINE_DATAVALUE";    

    // Facility Type
    public static final String TOTAL_POPULATION = "TOTAL_POPULATION";
    public static final String LIVE_BIRTHS = "LIVE_BIRTHS";
    public static final String MODE_OF_VACCINE = "MODE_OF_VACCINE";
    public static final String ELECTRICITY_AVAILABILITY_BY_FACILITY_TYPE = "ELECTRICITY_AVAILABILITY_BY_FACILITY_TYPE";
    public static final String KEROSENE_AVAILABILITY_BY_FACILITY_TYPE = "KEROSENE_AVAILABILITY_BY_FACILITY_TYPE";
    public static final String GAS_AVAILABILITY_BY_FACILITY_TYPE = "GAS_AVAILABILITY_BY_FACILITY_TYPE";
    public static final String ENERGY_AVAILABILITY_AT_FACILITIES = "ENERGY_AVAILABILITY_AT_FACILITIES";
    public static final String ELECTRICITY_AVAILABILITY = "ELECTRICITY_AVAILABILITY";

    // Cold Chain
    public static final String CATALOGTYPE_ATTRIBUTE_VALUE = "CATALOGTYPE_ATTRIBUTE_VALUE";
    public static final String CATALOGTYPE_ATTRIBUTE_VALUE_BY_WORKING_STATUS = "CATALOGTYPE_ATTRIBUTE_VALUE_BY_WORKING_STATUS";
    public static final String WORKING_STATUS_BY_REFRIGERATORS_FREEZER_MODEL = "WORKING_STATUS_BY_REFRIGERATORS_FREEZER_MODEL";
    public static final String REFRIGERATORS_FREEZER_BY_WORKING_STATUS = "REFRIGERATORS_FREEZER_BY_WORKING_STATUS";
    public static final String REFRIGERATORS_BY_WORKING_STATUS = "REFRIGERATORS_BY_WORKING_STATUS";
    public static final String CATALOGTYPE_ATTRIBUTE_VALUE_AGE_GROUP_PIE = "CATALOGTYPE_ATTRIBUTE_VALUE_AGE_GROUP_PIE";
    public static final String CATALOGTYPE_ATTRIBUTE_VALUE_AGE_GROUP_BAR = "CATALOGTYPE_ATTRIBUTE_VALUE_AGE_GROUP_BAR";
    public static final String CATALOGTYPE_ATTRIBUTE_VALUE_AGE_GROUP = "CATALOGTYPE_ATTRIBUTE_VALUE_AGE_GROUP";
    public static final String REFRIGERATOR_FREEZER_UTILIZATION_PIE = "REFRIGERATOR_FREEZER_UTILIZATION_PIE";
    public static final String REFRIGERATOR_FREEZER_UTILIZATION_BAR = "REFRIGERATOR_FREEZER_UTILIZATION_BAR";
    public static final String REFRIGERATOR_FREEZER_UTILIZATION = "REFRIGERATOR_FREEZER_UTILIZATION";
    public static final String DISTRIBUTION_REFRIGERATOR_FREEZER_MODELS = "DISTRIBUTION_REFRIGERATOR_FREEZER_MODELS";
    public static final String LINELIST_EQUIPMENT_NOT_WORKING_AND_REPAIR = "LINELIST_EQUIPMENT_NOT_WORKING_AND_REPAIR";
    public static final String LINELIST_EQUIPMENT_WORKING_AND_NOT_WORKING = "LINELIST_EQUIPMENT_WORKING_AND_NOT_WORKING";

    // Storage Capacity
    public static final String VACCINE_STORAGE_CAPACITY = "VACCINE_STORAGE_CAPACITY";
    public static final String VACCINE_STORAGE_CAPACITY_BAR = "VACCINE_STORAGE_CAPACITY_BAR";
    public static final String VACCINE_STORAGE_CAPACITY_FOR_LESS_THAN_20 = "VACCINE_STORAGE_CAPACITY_FOR_LESS_THAN_20";
    public static final String VACCINE_STORAGE_CAPACITY_FOR_LESS_THAN_20_BAR = "VACCINE_STORAGE_CAPACITY_FOR_LESS_THAN_20_BAR";
    public static final String STORAGE_CAPACITY_SHORTAGES_4C_BY_AREA = "STORAGE_CAPACITY_SHORTAGES_4C_BY_AREA";
    public static final String STORAGE_CAPACITY_SHORTAGES_20C_BY_AREA = "STORAGE_CAPACITY_SHORTAGES_20C_BY_AREA";

    // Energy for Cooling    
    public static final String EQUIPMENT_BY_AVAILABILITY_OF_ELECTRICITY ="EQUIPMENT_BY_AVAILABILITY_OF_ELECTRICITY";
    public static final String SUMMARY_OF_ABSORPTION_REFRIGERATORS_EXISTING_IN_fACILITIES ="SUMMARY_OF_ABSORPTION_REFRIGERATORS_EXISTING_IN_fACILITIES";
    public static final String ALL_ENERGY_AVAILABILITY_AT_FACILITIES = "ALL_ENERGY_AVAILABILITY_AT_FACILITIES";
    
    //Cold Room
    public static final String COLD_ROOM_BY_MODEL_AND_WORKING_STATUS = "COLD_ROOM_BY_MODEL_AND_WORKING_STATUS";
    public static final String LISTING_OF_COLD_ROOM_FACILITIES_AND_WORKING_STATUS = "LISTING_OF_COLD_ROOM_FACILITIES_AND_WORKING_STATUS";
    public static final String COLD_ROOM_QUALITY_ATTRIBUTES = "COLD_ROOM_QUALITY_ATTRIBUTES";
    
    //Cold boxes and vaccine carries
    public static final String COLD_BOX_AND_VACCINE_CARRIERS_BY_MODEL_AND_WORKING_STATUS_BAR = "COLD_BOX_AND_VACCINE_CARRIERS_BY_MODEL_AND_WORKING_STATUS_BAR";
    public static final String COLD_BOX_AND_VACCINE_CARRIERS_BY_MODEL_AND_WORKING_STATUS = "COLD_BOX_AND_VACCINE_CARRIERS_BY_MODEL_AND_WORKING_STATUS";
    public static final String QUANTITY_OF_COLD_BOXES_OR_CARRIERS = "QUANTITY_OF_COLD_BOXES_OR_CARRIERS";
    
    public static final String LAST_YEAR = "LAST_YEAR";
    public static final String CURRENT_YEAR = "CURRENT_YEAR";
    public static final String LAST_6_MONTHS = "LAST_6_MONTHS";
    public static final String LAST_3_MONTHS = "LAST_3_MONTHS";

    private String reportId;

    private String reportName;

    private String xmlTemplateName;

    private String outputType;

    private String reportType;

    private String periodRequire;

    private String categoryType;

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getReportId()
    {
        return reportId;
    }
    public void setReportId( String reportId )
    {
        this.reportId = reportId;
    }
    public String getReportName()
    {
        return reportName;
    }
    public void setReportName( String reportName )
    {
        this.reportName = reportName;
    }
    public String getXmlTemplateName()
    {
        return xmlTemplateName;
    }
    public void setXmlTemplateName( String xmlTemplateName )
    {
        this.xmlTemplateName = xmlTemplateName;
    }
    public String getOutputType()
    {
        return outputType;
    }
    public void setOutputType( String outputType )
    {
        this.outputType = outputType;
    }
    public String getReportType()
    {
        return reportType;
    }
    public void setReportType( String reportType )
    {
        this.reportType = reportType;
    }
    public String getPeriodRequire()
    {
        return periodRequire;
    }
    public void setPeriodRequire( String periodRequire )
    {
        this.periodRequire = periodRequire;
    }
	public String getCategoryType() {
		return categoryType;
	}
	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

}
