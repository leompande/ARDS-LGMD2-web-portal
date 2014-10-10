
package org.hisp.dhis.linelisting;

/*
 * Copyright (c) 2004-2009, University of Oslo
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
 *
 * @author Administrator
 */
public class LineListValidationRule {

    /**
     * The unique identifier for validation rule.
     */
    private int id;

    /**
     * Name of validation rule.. Required and unique.
     */
    private String name;

    /**
     * Description of the validation rule..
     */
    private String description;

    /**
     * Operator used in the validation rule.
     */
    private String operator;

    /**
     * leftside of the operator in the validation rule
     */
    private String leftside;

     /**
     * rightside of the operator in the validation rule.
     */

    private String rightside;

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public LineListValidationRule()
    {
    }

    public LineListValidationRule(String name)
    {
        this.name = name;
    }

    public LineListValidationRule(String name, String operator, String leftside, String rightside)
    {
        this.name = name;
        this.operator = operator;
        this.leftside = leftside;
        this.rightside = rightside;
    }

    public LineListValidationRule(String name, String description, String operator, String leftside, String rightside)
    {
        this.name = name;
        this.description = description;
        this.operator = operator;
        this.leftside = leftside;
        this.rightside = rightside;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
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

        if ( !(o instanceof LineListValidationRule) )
        {
            return false;
        }

        final LineListValidationRule other = (LineListValidationRule) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Setter and Getter
    // -------------------------------------------------------------------------

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * @return the leftside
     */
    public String getLeftside() {
        return leftside;
    }

    /**
     * @param leftside the leftside to set
     */
    public void setLeftside(String leftside) {
        this.leftside = leftside;
    }

    /**
     * @return the rightside
     */
    public String getRightside() {
        return rightside;
    }

    /**
     * @param rightside the rightside to set
     */
    public void setRightside(String rightside) {
        this.rightside = rightside;
    }


    
}
