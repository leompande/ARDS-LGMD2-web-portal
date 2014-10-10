package org.hisp.dhis.api.controller;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.hisp.dhis.api.controller.exception.NotFoundException;
import org.hisp.dhis.api.controller.exception.NotFoundForQueryException;
import org.hisp.dhis.api.utils.WebUtils;
import org.hisp.dhis.common.Access;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.PagerUtils;
import org.hisp.dhis.common.SharingUtils;
import org.hisp.dhis.dxf2.metadata.ExchangeClasses;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractCrudController<T extends IdentifiableObject>
{
    //--------------------------------------------------------------------------
    // Dependencies
    //--------------------------------------------------------------------------

    @Autowired
    protected IdentifiableObjectManager manager;

    @Autowired
    protected CurrentUserService currentUserService;

    //--------------------------------------------------------------------------
    // GET
    //--------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public String getObjectList( @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        WebMetaData metaData = new WebMetaData();
        List<T> entityList = getEntityList( metaData, options );

        ReflectionUtils.invokeSetterMethod( ExchangeClasses.getAllExportMap().get( getEntityClass() ), metaData, entityList );

        if ( options.getViewClass( "basic" ).equals( "basic" ) )
        {
            handleLinksAndAccess( options, metaData, entityList, false );
        }
        else
        {
            handleLinksAndAccess( options, metaData, entityList, true );
        }

        postProcessEntities( entityList );
        postProcessEntities( entityList, options, parameters );

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() ) + "List";
    }

    @RequestMapping(value = "/query/{query}", method = RequestMethod.GET)
    public String query( @PathVariable String query, @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        WebMetaData metaData = new WebMetaData();
        List<T> entityList = queryForEntityList( metaData, options, query );

        ReflectionUtils.invokeSetterMethod( ExchangeClasses.getAllExportMap().get( getEntityClass() ), metaData, entityList );

        String viewClass = options.getViewClass( "basic" );

        if ( viewClass.equals( "basic" ) || viewClass.equals( "sharingBasic" ) )
        {
            handleLinksAndAccess( options, metaData, entityList, false );
        }
        else
        {
            handleLinksAndAccess( options, metaData, entityList, true );
        }

        postProcessEntities( entityList );
        postProcessEntities( entityList, options, parameters );

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", viewClass );

        return StringUtils.uncapitalize( getEntitySimpleName() ) + "List";
    }

    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public String getObject( @PathVariable("uid") String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        T entity = getEntity( uid );

        if ( entity == null )
        {
            throw new NotFoundException( uid );
        }

        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( entity );
        }

        if ( SharingUtils.isSupported( getEntityClass() ) )
        {
            addAccessProperties( entity );
        }

        postProcessEntity( entity );
        postProcessEntity( entity, options, parameters );

        model.addAttribute( "model", entity );
        model.addAttribute( "viewClass", options.getViewClass( "detailed" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }

    @RequestMapping(value = "/search/{query}", method = RequestMethod.GET)
    public String search( @PathVariable String query, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        T entity = searchForEntity( getEntityClass(), query );

        if ( entity == null )
        {
            throw new NotFoundForQueryException( query );
        }

        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( entity );
        }

        postProcessEntity( entity );
        postProcessEntity( entity, options, parameters );

        model.addAttribute( "model", entity );
        model.addAttribute( "viewClass", "detailed" );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }

    //--------------------------------------------------------------------------
    // POST
    //--------------------------------------------------------------------------

    @RequestMapping(method = RequestMethod.POST, consumes = { "application/xml", "text/xml" })
    public void postXmlObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    //--------------------------------------------------------------------------
    // PUT
    //--------------------------------------------------------------------------

    @RequestMapping(value = "/{uid}", method = RequestMethod.PUT, consumes = { "application/xml", "text/xml" })
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void putXmlObject( HttpServletResponse response, HttpServletRequest request, @PathVariable("uid") String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    @RequestMapping(value = "/{uid}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void putJsonObject( HttpServletResponse response, HttpServletRequest request, @PathVariable("uid") String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    //--------------------------------------------------------------------------
    // DELETE
    //--------------------------------------------------------------------------

    @RequestMapping(value = "/{uid}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteObject( HttpServletResponse response, HttpServletRequest request, @PathVariable("uid") String uid ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.DELETE.toString() );
    }

    //--------------------------------------------------------------------------
    // Hooks
    //--------------------------------------------------------------------------


    /**
     * Override to process entities after it has been retrieved from
     * storage and before it is returned to the view. Entities is null-safe.
     */
    protected void postProcessEntities( List<T> entityList, WebOptions options, Map<String, String> parameters )
    {

    }

    /**
     * Override to process entities after it has been retrieved from
     * storage and before it is returned to the view. Entities is null-safe.
     */
    protected void postProcessEntities( List<T> entityList )
    {

    }

    /**
     * Override to process a single entity after it has been retrieved from
     * storage and before it is returned to the view. Entity is null-safe.
     */
    protected void postProcessEntity( T entity ) throws Exception
    {
    }

    /**
     * Override to process a single entity after it has been retrieved from
     * storage and before it is returned to the view. Entity is null-safe.
     */
    protected void postProcessEntity( T entity, WebOptions options, Map<String, String> parameters ) throws Exception
    {
    }

    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------

    protected T searchForEntity( Class<T> clazz, String query )
    {
        return manager.search( clazz, query );
    }

    protected List<T> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<T> entityList;

        Date lastUpdated = options.getLastUpdated();

        if ( lastUpdated != null )
        {
            entityList = new ArrayList<T>( manager.getByLastUpdatedSorted( getEntityClass(), lastUpdated ) );
        }
        else if ( options.hasPaging() )
        {
            int count = manager.getCount( getEntityClass() );

            Pager pager = new Pager( options.getPage(), count, options.getPageSize() );
            metaData.setPager( pager );

            entityList = new ArrayList<T>( manager.getBetween( getEntityClass(), pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            entityList = new ArrayList<T>( manager.getAllSorted( getEntityClass() ) );
        }

        return entityList;
    }

    protected List<T> queryForEntityList( WebMetaData metaData, WebOptions options, String query )
    {
        List<T> entityList = queryForList( getEntityClass(), query );

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), entityList.size(), options.getPageSize() );
            metaData.setPager( pager );

            entityList = PagerUtils.pageCollection( entityList, pager );
        }

        return entityList;
    }

    protected List<T> queryForList( Class<T> clazz, String query )
    {
        return new ArrayList<T>( manager.filter( getEntityClass(), query ) );
    }

    protected T getEntity( String uid )
    {
        return manager.getNoAcl( getEntityClass(), uid ); //TODO consider ACL
    }

    protected void addAccessProperties( T object )
    {
        Access access = new Access();
        access.setManage( SharingUtils.canManage( currentUserService.getCurrentUser(), object ) );
        access.setExternalize( SharingUtils.canExternalize( currentUserService.getCurrentUser(), object ) );
        access.setWrite( SharingUtils.canWrite( currentUserService.getCurrentUser(), object ) );
        access.setRead( SharingUtils.canRead( currentUserService.getCurrentUser(), object ) );
        access.setUpdate( SharingUtils.canUpdate( currentUserService.getCurrentUser(), object ) );
        access.setDelete( SharingUtils.canDelete( currentUserService.getCurrentUser(), object ) );

        ((BaseIdentifiableObject) object).setAccess( access );
    }

    protected void handleLinksAndAccess( WebOptions options, WebMetaData metaData, List<T> entityList, boolean deep )
    {
        if ( options != null && options.hasLinks() )
        {
            WebUtils.generateLinks( metaData, deep );
        }

        if ( !JacksonUtils.isSharingView( options.getViewClass( "basic" ) ) )
        {
            return;
        }

        if ( entityList != null && SharingUtils.isSupported( getEntityClass() ) )
        {
            for ( T object : entityList )
            {
                addAccessProperties( object );
            }
        }
    }

    //--------------------------------------------------------------------------
    // Reflection helpers
    //--------------------------------------------------------------------------

    private Class<T> entityClass;

    private String entityName;

    private String entitySimpleName;

    @SuppressWarnings( "unchecked" )
    protected Class<T> getEntityClass()
    {
        if ( entityClass == null )
        {
            Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
            entityClass = (Class<T>) actualTypeArguments[0];
        }

        return entityClass;
    }

    protected String getEntityName()
    {
        if ( entityName == null )
        {
            entityName = getEntityClass().getName();
        }

        return entityName;
    }

    protected String getEntitySimpleName()
    {
        if ( entitySimpleName == null )
        {
            entitySimpleName = getEntityClass().getSimpleName();
        }

        return entitySimpleName;
    }

    @SuppressWarnings("unchecked")
    protected T getEntityInstance()
    {
        try
        {
            return (T) Class.forName( getEntityName() ).newInstance();
        }
        catch ( InstantiationException ex )
        {
            throw new RuntimeException( ex );
        }
        catch ( IllegalAccessException ex )
        {
            throw new RuntimeException( ex );
        }
        catch ( ClassNotFoundException ex )
        {
            throw new RuntimeException( ex );
        }
    }
}
