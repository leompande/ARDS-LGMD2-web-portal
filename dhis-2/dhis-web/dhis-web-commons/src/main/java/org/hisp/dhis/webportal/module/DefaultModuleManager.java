package org.hisp.dhis.webportal.module;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.Dispatcher;
import org.hisp.dhis.appmanager.App;
import org.hisp.dhis.appmanager.AppManager;
import org.hisp.dhis.security.ActionAccessResolver;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.PackageConfig;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DefaultModuleManager.java 4883 2008-04-12 13:12:54Z larshelg $
 */
public class DefaultModuleManager
    implements ModuleManager
{
    private static final Log LOG = LogFactory.getLog( DefaultModuleManager.class );

    private boolean modulesDetected = false;

    private Map<String, Module> modulesByName = new HashMap<String, Module>();

    private Map<String, Module> modulesByNamespace = new HashMap<String, Module>();

    private List<Module> menuModules = new ArrayList<Module>();
    
    private List<Module> maintenanceMenuModules = new ArrayList<Module>();
    
    private List<Module> serviceMenuModules = new ArrayList<Module>();

    private ThreadLocal<Module> currentModule = new ThreadLocal<Module>();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private AppManager appManager;
    
    private ActionAccessResolver actionAccessResolver;

    public void setActionAccessResolver( ActionAccessResolver actionAccessResolver )
    {
        this.actionAccessResolver = actionAccessResolver;
    }

    private Comparator<Module> moduleComparator;

    public void setModuleComparator( Comparator<Module> moduleComparator )
    {
        this.moduleComparator = moduleComparator;
    }

    private String defaultActionName;

    public void setDefaultActionName( String defaultActionName )
    {
        this.defaultActionName = defaultActionName;
    }

    private String maintenanceModuleIdentifier;

    public void setMaintenanceModuleIdentifier( String maintenanceModuleIdentifier )
    {
        this.maintenanceModuleIdentifier = maintenanceModuleIdentifier;
    }
    
    // -------------------------------------------------------------------------
    // ModuleManager
    // -------------------------------------------------------------------------

    public Module getModuleByName( String name )
    {
        detectModules();

        return modulesByName.get( name );
    }

    public Module getModuleByNamespace( String namespace )
    {
        detectModules();

        return modulesByNamespace.get( namespace );
    }
    
    public boolean moduleExists( String name )
    {
        return getModuleByName( name ) != null;
    }

    public List<Module> getMenuModules()
    {
        detectModules();

        return menuModules;
    }

    public List<Module> getAccessibleMenuModules()
    {
        detectModules();
        
        return getAccessibleModules( menuModules );
    }
    
    public List<Module> getAccessibleMenuModulesAndApps()
    {
        List<Module> modules = getAccessibleMenuModules();
        
        List<App> apps = appManager.getApps();
        
        for ( App app : apps )
        {   
            modules.add( Module.getModule( app ) );
        }
        
        return modules;
    }

    public List<Module> getMaintenanceMenuModules()
    {
        detectModules();
        
        return maintenanceMenuModules;
    }

    public List<Module> getAccessibleMaintenanceModules()
    {
        detectModules();
        
        return getAccessibleModules( maintenanceMenuModules );
    }
    
    public List<Module> getServiceMenuModules()
    {
        detectModules();
        
        return serviceMenuModules;
    }

    public List<Module> getAccessibleServiceModules()
    {
        detectModules();
        
        return getAccessibleModules( serviceMenuModules );
    }

    public List<Module> getAccessibleServiceModulesAndApps()
    {
        List<Module> modules = getAccessibleServiceModules();

        List<App> apps = appManager.getApps();
        
        for ( App app : apps )
        {   
            modules.add( Module.getModule( app ) );
        }
        
        return modules;
    }
    
    public Collection<Module> getAllModules()
    {
        detectModules();

        return modulesByName.values();
    }
    
    public Module getCurrentModule()
    {
        return currentModule.get();
    }

    public void setCurrentModule( Module module )
    {
        currentModule.set( module );
    }

    // -------------------------------------------------------------------------
    // Module detection
    // -------------------------------------------------------------------------

    private synchronized void detectModules()
    {
        if ( modulesDetected )
        {
            return;
        }

        for ( PackageConfig packageConfig : getPackageConfigs() )
        {
            String name = packageConfig.getName();
            String namespace = packageConfig.getNamespace();

            if ( packageConfig.getAllActionConfigs().size() == 0 )
            {
                LOG.debug( "Ignoring action package with no actions: " + name );

                continue;
            }

            if ( namespace == null || namespace.length() == 0 )
            {
                throw new RuntimeException( "Missing namespace in action package: " + name );
            }

            if ( modulesByName.containsKey( name ) )
            {
                throw new RuntimeException( "Two action packages have the same name: " + name );
            }

            if ( modulesByNamespace.containsKey( namespace ) )
            {
                Module module = modulesByNamespace.get( namespace );

                throw new RuntimeException( "These action packages have the same namespace: " + name + " and "
                    + module.getName() );
            }

            Module module = new Module( name, namespace );
            modulesByName.put( name, module );
            modulesByNamespace.put( namespace, module );

            if ( packageConfig.getActionConfigs().containsKey( defaultActionName ) )
            {
                module.setDefaultAction( ".." + namespace + "/" + defaultActionName + ".action" );

                menuModules.add( module );

                LOG.debug( "Has default action: " + name );
                
                if ( name.toLowerCase().contains( maintenanceModuleIdentifier ) )
                {
                    maintenanceMenuModules.add( module );
                }
                else
                {
                    serviceMenuModules.add( module );
                }
            }
            else
            {
                LOG.debug( "Doesn't have default action: " + name );
            }
        }

        Collections.sort( menuModules, moduleComparator );
        Collections.sort( maintenanceMenuModules, moduleComparator );
        Collections.sort( serviceMenuModules, moduleComparator );

        modulesDetected = true;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Collection<PackageConfig> getPackageConfigs()
    {
        Configuration configuration = Dispatcher.getInstance().getConfigurationManager().getConfiguration();

        Map<String, PackageConfig> packageConfigs = configuration.getPackageConfigs();

        return packageConfigs.values();
    }
    
    private List<Module> getAccessibleModules( List<Module> modules )
    {
        List<Module> list = new ArrayList<Module>();
        
        for ( Module module : modules )
        {
            if ( module != null && actionAccessResolver.hasAccess( module.getName(), defaultActionName ) )
            {
                list.add( module );
            }
        }
        
        return list;
    }
}
