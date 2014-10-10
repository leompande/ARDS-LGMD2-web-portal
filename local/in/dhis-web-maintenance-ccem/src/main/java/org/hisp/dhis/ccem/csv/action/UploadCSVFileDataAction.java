package org.hisp.dhis.ccem.csv.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import com.opensymphony.xwork2.Action;

public class UploadCSVFileDataAction implements Action {

	private File importData;

	public void setImportData(File importData) {
		this.importData = importData;
	}	

	public String execute() {
		try {
			
				//System.out.println(importData);
				InputStream in = new FileInputStream(importData);
				
				CSVReader csvReader = new CSVReader(new FileReader(importData), ',');
				
				//String[] header = csvReader.readNext();
				
				List allRows = new ArrayList<String>();
				String[] row = null;
				
				//allRows = csvReader.readAll();
				while ((row = csvReader.readNext()) != null)
				{
					allRows.add(row);
					//System.out.println( row );
				}
				importCSVData(allRows);
		} 
		catch (Exception e) {			
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public String importCSVData( List csvRows ) throws Exception
	{
		String nodeHeader = "NodeID";
		int nodeId = -1;
		
		for ( Object obj : csvRows)
        {           
            String[] oneRow = (String[]) obj;            
            for(int i=0;i<=7;i++)
            {
	           if(oneRow[i] != null && oneRow.length >= i && nodeHeader.equalsIgnoreCase(oneRow[i]))
	            {
	            	nodeId = i;
	            }	            
            }            
            System.out.println(oneRow[0]);
        }
		return null;
	}
}
