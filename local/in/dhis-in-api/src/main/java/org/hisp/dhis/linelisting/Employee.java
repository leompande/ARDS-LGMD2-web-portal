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

import java.util.Date;

/**
 * @author Mithilesh Kumar Thakur
 * 
 * @version Employee.java Oct 15, 2010 1:42:16 PM
 */

public class Employee
{

    public static final Integer LPR_PERIOD = 40;

    /**
     * The unique identifier for Employee
     */
    private String pdsCode;

    /**
     * Name of the employee, required
     */
    private String name;

    /**
     * Date of Birth
     */
    private Date dateOfBirth;

    /**
     * LPR Date
     */
    private Date lprDate;

    /**
     * Gender
     */
    private String sex;

    /**
     * Date of Join to Government Service.
     */
    private Date joinDateToGovtService;

    /**
     * Residential Address
     */
    private String resAddress;

    /**
     * Mobile Number
     */
    private String contactNumber;

    /**
     * Emergency Contact Number
     */
    private String emergencyContactNumber;

    /**
     * isTransferred
     */
    private Boolean isTransferred = false;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Employee()
    {
    }

    public Employee( String pdsCode, String name, Date dateOfBirth, Date lprDate, String sex,
        Date joinDateToGovtService, String resAddress, String contactNumber, String emergencyContactNumber )
    {
        this.pdsCode = pdsCode;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.lprDate = lprDate;
        this.sex = sex;
        this.joinDateToGovtService = joinDateToGovtService;
        this.resAddress = resAddress;
        this.contactNumber = contactNumber;
        this.emergencyContactNumber = emergencyContactNumber;
    }

    public Employee( String pdsCode, String name, Date dateOfBirth, Date lprDate, String sex,
        Date joinDateToGovtService, String resAddress, String contactNumber, String emergencyContactNumber,
        Boolean isTransferred )
    {
        this.pdsCode = pdsCode;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.lprDate = lprDate;
        this.sex = sex;
        this.joinDateToGovtService = joinDateToGovtService;
        this.resAddress = resAddress;
        this.contactNumber = contactNumber;
        this.emergencyContactNumber = emergencyContactNumber;
        this.isTransferred = isTransferred;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return pdsCode.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof Employee) )
        {
            return false;
        }

        final Employee other = (Employee) o;

        return pdsCode.equals( other.getPdsCode() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getPdsCode()
    {
        return pdsCode;
    }

    public void setPdsCode( String pdsCode )
    {
        this.pdsCode = pdsCode;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Date getDateOfBirth()
    {
        return dateOfBirth;
    }

    public void setDateOfBirth( Date dateOfBirth )
    {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getLprDate()
    {
        return lprDate;
    }

    public void setLprDate( Date lprDate )
    {
        this.lprDate = lprDate;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex( String sex )
    {
        this.sex = sex;
    }

    public Date getJoinDateToGovtService()
    {
        return joinDateToGovtService;
    }

    public void setJoinDateToGovtService( Date joinDateToGovtService )
    {
        this.joinDateToGovtService = joinDateToGovtService;
    }

    public String getResAddress()
    {
        return resAddress;
    }

    public void setResAddress( String resAddress )
    {
        this.resAddress = resAddress;
    }

    public String getContactNumber()
    {
        return contactNumber;
    }

    public void setContactNumber( String contactNumber )
    {
        this.contactNumber = contactNumber;
    }

    public String getEmergencyContactNumber()
    {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber( String emergencyContactNumber )
    {
        this.emergencyContactNumber = emergencyContactNumber;
    }

    public Boolean getIsTransferred()
    {
        return isTransferred;
    }

    public void setIsTransferred( Boolean isTransferred )
    {
        this.isTransferred = isTransferred;
    }

}
