package org.hisp.dhis.dataanalyser.util;

public class MotionChart 
{

	private String location;
	
	private String date;
	
	private Double xaxisValue;
	
	private Double yaxisValue;
	
	private Double zaxisValue;
	
	//---------------------------------------------------------------
	// Constructor
	//---------------------------------------------------------------
	public MotionChart()
	{
		
	}
	
	public MotionChart( String location, String date, Double xaxisValue, Double yaxisValue, Double zaxisValue )	
	{
		this.location = location;
		this.date = date;
		this.xaxisValue = xaxisValue;
		this.yaxisValue = yaxisValue;
		this.zaxisValue = zaxisValue;
	}
	
	//---------------------------------------------------------------
	// Getter & Setter
	//---------------------------------------------------------------

	public String getLocation() 
	{
		return location;
	}

	public void setLocation(String location) 
	{
		this.location = location;
	}

	public String getDate() 
	{
		return date;
	}

	public void setDate(String date) 
	{
		this.date = date;
	}

	public Double getXaxisValue() 
	{
		return xaxisValue;
	}

	public void setXaxisValue(Double xaxisValue) 
	{
		this.xaxisValue = xaxisValue;
	}

	public Double getYaxisValue() 
	{
		return yaxisValue;
	}

	public void setYaxisValue(Double yaxisValue) 
	{
		this.yaxisValue = yaxisValue;
	}

	public Double getZaxisValue() 
	{
		return zaxisValue;
	}

	public void setZaxisValue(Double zaxisValue) 
	{
		this.zaxisValue = zaxisValue;
	}
	
	
}
