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
package org.hisp.dhis.linelisting.llaggregation;

/**
 * @author Viet Nguyen
 *
 * @version $Id$
 */
public class AggregationCondition
{
    //---------------------------------------------------------------------------
    // Constants
    //---------------------------------------------------------------------------
    
    public static final char OPEN_CONDITION = '{';

    public static final char CLOSE_CONDITION = '}';

    public static final char OPEN_EXPRESSION = '(';

    public static final char CLOSE_EXPRESSION = ')';

    public static final char OPEN_ARGUMENT = '[';

    public static final char CLOSE_ARGUMENT = ']';

    public static final String CONDITION = "COND";

    public static final String SINGLE_CONDITION = "SCOND";

    public static final char ARGUMENT_IDENTIFIER = ':';

    public static final char ARGUMENT_SPLITTER = '.';

    public static final String ARGUMENT_DATALEMENT = "LE";

    public static final String FUNCTION_IDENTIFIER = "@";
    
    //---------------------------------------------------------------------------
    // Variables
    //---------------------------------------------------------------------------
    
    /**
     * Value : SCOND | COND  ( Single condition | Condition )
     */
    private String type;

    /**
     * Value : AND | OR 
     * Value : NULL if this is the last condition in the query
     */
    private String next;

    /**
     * Left side expression of the condition
     * Format : [DE:ProgramStageId.DataElementId.OptionComboId]    
     *          [CP:CasePropertiesName] 
     *          [CA:CaseAttributeId]
     */
    private String leftExpression;

    /**
     * Right side expression of the condition
     * Format : like the left side.
     */
    private String rightExpression;
    
    /**
     * Current supported : > , >= , < , <=
     */
    private String operator = "";

    //---------------------------------------------------------------------------
    // Getters / Setters
    //---------------------------------------------------------------------------
    
    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getNext()
    {
        return next;
    }

    public void setNext( String next )
    {
        this.next = next;
    }

    public String getLeftExpression()
    {
        return leftExpression;
    }

    public void setLeftExpression( String leftExpression )
    {
        this.leftExpression = leftExpression;
    }

    public String getRightExpression()
    {
        return rightExpression;
    }

    public void setRightExpression( String rightExpression )
    {
        this.rightExpression = rightExpression;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator( String operator )
    {
        this.operator = operator;
    }
    
}
