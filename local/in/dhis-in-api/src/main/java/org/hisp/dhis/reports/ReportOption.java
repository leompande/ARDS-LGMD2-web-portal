package org.hisp.dhis.reports;

public class ReportOption
{

    private String optionText;
    
    private String optionValue;

    
    public ReportOption()
    {
        
    }
    
    public ReportOption( String optionText, String optionValue )
    {
        this.optionText = optionText;
        this.optionValue = optionValue;
    }
    
    public String getOptionText()
    {
        return optionText;
    }

    public void setOptionText( String optionText )
    {
        this.optionText = optionText;
    }

    public String getOptionValue()
    {
        return optionValue;
    }

    public void setOptionValue( String optionValue )
    {
        this.optionValue = optionValue;
    }
    
}
