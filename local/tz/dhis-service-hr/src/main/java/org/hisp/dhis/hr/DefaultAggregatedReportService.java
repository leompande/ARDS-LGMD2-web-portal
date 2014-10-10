package org.hisp.dhis.hr;

import static org.hisp.dhis.chart.Chart.TYPE_LINE;

import java.awt.Color;
import java.awt.Font;
import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.springframework.transaction.annotation.Transactional;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */
@Transactional
public class DefaultAggregatedReportService 
	implements AggregatedReportService{
	
	private static final Font titleFont = new Font( "Tahoma", Font.BOLD, 14 );

    private static final Font subTitleFont = new Font( "Tahoma", Font.PLAIN, 12 );

    private static final String TREND_PREFIX = "Trend - ";

    private static final String TITLE_SEPARATOR = " - ";

    private static final String DEFAULT_TITLE_PIVOT_CHART = "Pivot Chart";

    private static final Color[] colors = { Color.decode( "#d54a4a" ), Color.decode( "#2e4e83" ),
        Color.decode( "#75e077" ), Color.decode( "#e3e274" ), Color.decode( "#e58c6d" ), Color.decode( "#df6ff3" ),
        Color.decode( "#88878e" ), Color.decode( "#6ff3e8" ), Color.decode( "#6fc3f3" ), Color.decode( "#aaf36f" ),
        Color.decode( "#9d6ff3" ), Color.decode( "#474747" ) };    

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
   
    private PersonService personService;

    public void setPersonService( PersonService personService )
    {
        this.personService = personService;
    }
    
    private AttributeOptionsService attributeOptionsService;

    public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
    {
        this.attributeOptionsService = attributeOptionsService;
    }
    
    // -------------------------------------------------------------------------
    // AggregatedReportService implementation
    // -------------------------------------------------------------------------
    
    /**
     * Returns a bar renderer.
     */
    private BarRenderer getBarRenderer()
    {
        BarRenderer renderer = new BarRenderer();

        renderer.setMaximumBarWidth( 0.07 );

        for ( int i = 0; i < colors.length; i++ )
        {
            renderer.setSeriesPaint( i, colors[i] );
            renderer.setShadowVisible( false );
        }

        return renderer;
    }

    /**
     * Returns a line and shape renderer.
     */
    private LineAndShapeRenderer getLineRenderer()
    {
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();

        for ( int i = 0; i < colors.length; i++ )
        {
            renderer.setSeriesPaint( i, colors[i] );
        }

        return renderer;
    }
    
    
    private CategoryDataset createDataset() {
    	  double[][] data = new double[][]{
    	  {210, 300, 320, 265, 299, 200},
    	  {200, 304, 201, 201, 340, 300},
    	  };
    	  return DatasetUtilities.createCategoryDataset(
    	  "Team ", "Match", data);
    	  }
    
    /**
     * Returns a JFreeChart of type defined in the chart argument.
     */
    /*
    public JFreeChart getJFreeChart()
    {
        
    	DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    	  dataset.setValue(2, "Marks", "Rahul");
    	  dataset.setValue(7, "Marks", "Vinod");
    	  dataset.setValue(4, "Marks", "Deepak");
    	  dataset.setValue(9, "Marks", "Prashant");
    	  dataset.setValue(6, "Marks", "Chandan");
    	  
    	  JFreeChart chart = ChartFactory.createBarChart
    	  ("BarChart using JFreeChart","Student", "Marks", dataset, 
    	   PlotOrientation.VERTICAL, false,true, false);
    	  chart.setBackgroundPaint(Color.yellow);
    	  chart.getTitle().setPaint(Color.blue); 
    	  CategoryPlot p = chart.getCategoryPlot(); 
    	  p.setRangeGridlinePaint(Color.red); 
    	
    	return chart;
    }
    */
    
    public JFreeChart getJFreeChart()
    {
    	final BarRenderer barRenderer = getBarRenderer();
        final LineAndShapeRenderer lineRenderer = getLineRenderer();

        // ---------------------------------------------------------------------
        // Plot
        // ---------------------------------------------------------------------

        CategoryPlot plot = null;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	  	  dataset.setValue(2, "Marks", "Rahul");
	  	  dataset.setValue(7, "Marks", "Vinod");
	  	  dataset.setValue(4, "Marks", "Deepak");
	  	  dataset.setValue(9, "Marks", "Prashant");
	  	  dataset.setValue(6, "Marks", "Chandan");

      
        plot = new CategoryPlot( dataset, new CategoryAxis(), new NumberAxis(), barRenderer );
       

        JFreeChart jFreeChart = new JFreeChart( "BarChart using JFreeChart", titleFont, plot, true );

        //if ( subTitle )
        //{
        //    jFreeChart.addSubtitle( getSubTitle( chart, chart.getFormat() ) );
        //}

        // ---------------------------------------------------------------------
        // Plot orientation
        // ---------------------------------------------------------------------

        plot.setOrientation( PlotOrientation.HORIZONTAL );
        plot.setDatasetRenderingOrder( DatasetRenderingOrder.FORWARD );

        // ---------------------------------------------------------------------
        // Category label positions
        // ---------------------------------------------------------------------

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions( CategoryLabelPositions.STANDARD );

        // ---------------------------------------------------------------------
        // Color & antialias
        // ---------------------------------------------------------------------

        jFreeChart.setBackgroundPaint( Color.WHITE );
        jFreeChart.setAntiAlias( true );

        return jFreeChart;
    }
    
    public JFreeChart getJFreeChart(HrDataSet dataSet, Attribute attribute, OrganisationUnit unit, boolean selectedUnitOnly)
    {
    	final BarRenderer barRenderer = getBarRenderer();
    	
    	String title = attribute.getCaption() + " Aggregate Report for " + unit.getName();
	    
	    if(!selectedUnitOnly)title = title + " with lower Level"; 
    	
    	Collection<AggregateOperands> aggregateOperands = personService.getAggregatedPersonByAttributeDatasetandOrganisation(dataSet, attribute, unit, selectedUnitOnly);
    	
    	// ---------------------------------------------------------------------
        // Plot
        // ---------------------------------------------------------------------

        CategoryPlot plot = null;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for(AggregateOperands aggregateOperand : aggregateOperands ){
        	
        	Person person = new Person();
        	
        	if( attribute.getCaption().equalsIgnoreCase(person.getNationalityColumn()) || attribute.getCaption().equalsIgnoreCase(person.getGenderColumn()) ){
        		
        		dataset.setValue(aggregateOperand.gettotal(), attribute.getCaption(), aggregateOperand.getAttributeOptionsValue());
	        	
        	}else{
        	
	        	AttributeOptions attributeOptions = attributeOptionsService.getAttributeOptions( Integer.parseInt(aggregateOperand.getAttributeOptionsValue()) );
	        	dataset.setValue(aggregateOperand.gettotal(), attribute.getCaption(), attributeOptions.getValue());
        	
        	}
        }
      
        plot = new CategoryPlot( dataset, new CategoryAxis(), new NumberAxis(), barRenderer );
       
        
        JFreeChart jFreeChart = new JFreeChart( title, titleFont, plot, true );
        
     // ---------------------------------------------------------------------
        // Plot orientation
        // ---------------------------------------------------------------------

        plot.setOrientation( PlotOrientation.VERTICAL );
        plot.setDatasetRenderingOrder( DatasetRenderingOrder.FORWARD );

        // ---------------------------------------------------------------------
        // Category label positions
        // ---------------------------------------------------------------------

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions( CategoryLabelPositions.UP_45 );

        // ---------------------------------------------------------------------
        // Color & antialias
        // ---------------------------------------------------------------------

        jFreeChart.setBackgroundPaint( Color.WHITE );
        jFreeChart.setAntiAlias( true );
    	
    	return jFreeChart;
    }

}
