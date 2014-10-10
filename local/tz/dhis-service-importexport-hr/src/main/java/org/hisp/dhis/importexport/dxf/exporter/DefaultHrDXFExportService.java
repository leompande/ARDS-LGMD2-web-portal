package org.hisp.dhis.importexport.dxf.exporter;

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

import static org.hisp.dhis.importexport.ImportParams.ATTRIBUTE_NAMESPACE;
import static org.hisp.dhis.importexport.dxf.converter.DXFConverter.ATTRIBUTE_EXPORTED;
import static org.hisp.dhis.importexport.dxf.converter.DXFConverter.ATTRIBUTE_MINOR_VERSION;
import static org.hisp.dhis.importexport.dxf.converter.DXFConverter.DXFROOT;
import static org.hisp.dhis.importexport.dxf.converter.DXFConverter.MINOR_VERSION_12;
import static org.hisp.dhis.importexport.dxf.converter.DXFConverter.NAMESPACE_10;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.amplecode.staxwax.factory.XMLFactory;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hibernate.SessionFactory;
import org.hisp.dhis.hr.AttributeGroupService;
import org.hisp.dhis.hr.AttributeOptionGroupService;
import org.hisp.dhis.hr.AttributeOptionsService;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.DataTypeService;
import org.hisp.dhis.hr.DataValuesService;
import org.hisp.dhis.hr.HistoryService;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.hr.InputTypeService;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.hr.TrainingService;
import org.hisp.dhis.importexport.HrExportParams;
import org.hisp.dhis.importexport.HrExportPipeThread;
import org.hisp.dhis.importexport.HrExportService;
import org.hisp.dhis.importexport.dxf.converter.DataTypeConverter;
import org.hisp.dhis.importexport.dxf.converter.AttributeConverter;
import org.hisp.dhis.importexport.dxf.converter.AttributeAssociationConverter;
import org.hisp.dhis.importexport.dxf.converter.AttributeOptionsAssociationConverter;
import org.hisp.dhis.importexport.dxf.converter.AttributeGroupConverter;
import org.hisp.dhis.importexport.dxf.converter.AttributeOptionGroupConverter;
import org.hisp.dhis.importexport.dxf.converter.AttributeOptionsConverter;
import org.hisp.dhis.importexport.dxf.converter.DataValuesConverter;
import org.hisp.dhis.importexport.dxf.converter.HistoryConverter;
import org.hisp.dhis.importexport.dxf.converter.HrDataSetConverter;
import org.hisp.dhis.importexport.dxf.converter.HrDataSetMemberConverter;
import org.hisp.dhis.importexport.dxf.converter.InputTypeConverter;
import org.hisp.dhis.importexport.dxf.converter.PersonConverter;
import org.hisp.dhis.importexport.dxf.converter.TrainingConverter;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id: DefaultDXFHrExportService.java 5960 2008-10-17 14:07:50Z larshelg
 *          $
 */
public class DefaultHrDXFExportService
    implements HrExportService
{
    private static final String ZIP_ENTRY_NAME = "Export.xml";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    
    // ----------------------------------
    // Setters for Hr service classes
    // ----------------------------------
    
    private AttributeService attributeService;
    
    public void setAttributeService( AttributeService attributeService )
    {
    	this.attributeService = attributeService;
    }
    
    private AttributeOptionsService attributeOptionsService;
    
    public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
    {
    	this.attributeOptionsService = attributeOptionsService;
    }
    
    private HrDataSetService hrDataSetService;
    
    public void setHrDataSetService( HrDataSetService hrDataSetService )
    {
    	this.hrDataSetService = hrDataSetService;
    }
    
    private PersonService personService;
    
    public void setPersonService( PersonService personService )
    {
    	this.personService = personService;
    }
    
    private DataValuesService dataValuesService;
    
    public void setDataValuesService( DataValuesService dataValuesService )
    {
    	this.dataValuesService = dataValuesService;
    }
    
    private HistoryService historyService;
    
    public void setHistoryService( HistoryService historyService )
    {
    	this.historyService = historyService;
    }
    
    private TrainingService trainingService;
    
    public void setTrainingService( TrainingService trainingService )
    {
    	this.trainingService = trainingService;
    }
    
    private AttributeGroupService attributeGroupService;
    
    public void setAttributeGroupService( AttributeGroupService attributeGroupService )
    {
    	this.attributeGroupService = attributeGroupService;
    }
    
    private AttributeOptionGroupService attributeOptionGroupService;
    
    public void setAttributeOptionGroupService( AttributeOptionGroupService attributeOptionGroupService )
    {
    	this.attributeOptionGroupService = attributeOptionGroupService;
    }
    
    private InputTypeService inputTypeService;
    
    public void setInputTypeService( InputTypeService inputTypeService )
    {
    	this.inputTypeService = inputTypeService;
    }
    
    private DataTypeService dataTypeService;
    
    public void setDataTypeService( DataTypeService dataTypeService )
    {
    	this.dataTypeService = dataTypeService;
    }

    // -------------------------------------------------------------------------
    // HrExportService implementation
    // -------------------------------------------------------------------------

    public InputStream exportData( HrExportParams params )
    {
        try
        {
            // -----------------------------------------------------------------
            // Pipes are input/output pairs. Data written on the output stream
            // shows up on the input stream at the other end of the pipe.
            // -----------------------------------------------------------------

            PipedOutputStream out = new PipedOutputStream();

            PipedInputStream in = new PipedInputStream( out );

            ZipOutputStream zipOut = new ZipOutputStream( out );

            zipOut.putNextEntry( new ZipEntry( ZIP_ENTRY_NAME ) );

            XMLWriter writer = XMLFactory.getPlainXMLWriter( zipOut );

            // -----------------------------------------------------------------
            // Writes to one end of the pipe
            // -----------------------------------------------------------------

            String[] rootProperties = { ATTRIBUTE_NAMESPACE, NAMESPACE_10, ATTRIBUTE_MINOR_VERSION, MINOR_VERSION_12,
                ATTRIBUTE_EXPORTED, DateUtils.getMediumDateString() };

            HrExportPipeThread thread = new HrExportPipeThread( sessionFactory );

            thread.setZipOutputStream( zipOut );
            thread.setParams( params );
            thread.setWriter( writer );
            thread.setRootName( DXFROOT );
            thread.setRootProperties( rootProperties );

            if ( params.isHrDomain() )
            {
            // ---------------------------------------------------------------
            // HR XMLCovenverters
            // ---------------------------------------------------------------
            thread.registerXMLHrConverter( new DataTypeConverter( dataTypeService) );
            thread.registerXMLHrConverter( new InputTypeConverter( inputTypeService) );
            thread.registerXMLHrConverter( new AttributeConverter( attributeService) );
            thread.registerXMLHrConverter( new AttributeOptionsConverter( attributeOptionsService ) );
            thread.registerXMLHrConverter( new AttributeGroupConverter( attributeGroupService ) );
            thread.registerXMLHrConverter( new AttributeAssociationConverter( attributeGroupService ) );
            thread.registerXMLHrConverter( new AttributeOptionGroupConverter( attributeOptionGroupService ) );
            thread.registerXMLHrConverter( new AttributeOptionsAssociationConverter( attributeOptionGroupService ) );
            thread.registerXMLHrConverter( new HrDataSetConverter( hrDataSetService ) );
            thread.registerXMLHrConverter( new HrDataSetMemberConverter( hrDataSetService, attributeService ) );
            
            thread.registerXMLHrConverter( new PersonConverter( personService) );
            thread.registerXMLHrConverter( new HistoryConverter( historyService ) );
            thread.registerXMLHrConverter( new TrainingConverter( trainingService ) );
            thread.registerXMLHrConverter( new DataValuesConverter( dataValuesService ) );
            // ------------------------------------------------------------------
            }

            thread.start();

            // -----------------------------------------------------------------
            // Reads at the other end of the pipe
            // -----------------------------------------------------------------

            InputStream bis = new BufferedInputStream( in );

            return bis;
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Error occured during export to stream", ex );
        }
    }
}
