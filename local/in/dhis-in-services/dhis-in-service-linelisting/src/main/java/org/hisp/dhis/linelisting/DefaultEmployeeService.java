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
package org.hisp.dhis.linelisting;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DefaultEmployeeService.java Oct 15, 2010 1:53:20 PM
 */
@Transactional
public class DefaultEmployeeService implements EmployeeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EmployeeStore employeeStore;
    
    public void setEmployeeStore( EmployeeStore employeeStore )
    {
        this.employeeStore = employeeStore;
    }
    
    // -------------------------------------------------------------------------
    // Employee
    // -------------------------------------------------------------------------
    
    public void addEmployee( Employee employee )
    {
        employeeStore.addEmployee( employee );
    }

    public void deleteEmployee( Employee employee )
    {
        employeeStore.deleteEmployee( employee );
    }

    public void updateEmployee( Employee employee )
    {
        employeeStore.updateEmployee( employee );
    }

    public Employee getEmployeeByPDSCode( String pdsCode )
    {
        return employeeStore.getEmployeeByPDSCode( pdsCode );
    }

    public Collection<Employee> getAllEmployee()
    {
        return employeeStore.getAllEmployee();
    }
    
    public Collection<Employee> getEmployeeByisTransferred( Boolean isTransferred )
    {
        return employeeStore.getEmployeeByisTransferred( isTransferred );
    }
    
    public int getEmployeeCount()
    {
        return employeeStore.getEmployeeCount();
    }
    
    public Collection<Employee> getEmployeesBetween( int first, int max )
    {
        return employeeStore.getEmployeesBetween( first, max );
    }
}
