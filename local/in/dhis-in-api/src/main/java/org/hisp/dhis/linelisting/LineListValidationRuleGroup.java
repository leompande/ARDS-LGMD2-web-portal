package org.hisp.dhis.linelisting;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class LineListValidationRuleGroup {

    //private int id;
    /**
     * The identifier for this LineListing Element / Entry Element
     */
    private LineListElement llelement;

    /**
     * The identifier for this LineListing Group
     */
    private LineListGroup llgroup;

    private LineListValidationRule llvalidationrule;
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public LineListValidationRuleGroup()
    {
    }

    public LineListValidationRuleGroup(LineListElement llelement,LineListGroup llgroup, LineListValidationRule llvalidationrule)
    {
        this.llelement = llelement;
        this.llgroup = llgroup;
        this.llvalidationrule = llvalidationrule;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LineListValidationRuleGroup other = (LineListValidationRuleGroup) obj;

        return llgroup.equals( other.getLlgroup() ) && llelement.equals( other.getLlelement() )
            && llvalidationrule.equals( other.getLlvalidationrule() );
        //return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = result * prime + getLlelement().hashCode();
        result = result * prime + getLlgroup().hashCode();
        result = result * prime + getLlvalidationrule().hashCode();

        return result;
    }


    // -------------------------------------------------------------------------
    // Setter and Getter
    // -------------------------------------------------------------------------


    /**
     * @return the id
     */
    //public int getId() {
    //    return id;
    //}

    /**
     * @param id the id to set
     */
    //public void setId(int id) {
    //    this.id = id;
    //}

    /**
     * @return the llelement
     */
    public LineListElement getLlelement() {
        return llelement;
    }

    /**
     * @param llelement the llelement to set
     */
    public void setLlelement(LineListElement llelement) {
        this.llelement = llelement;
    }

    /**
     * @return the llgroup
     */
    public LineListGroup getLlgroup() {
        return llgroup;
    }

    /**
     * @param llgroup the llgroup to set
     */
    public void setLlgroup(LineListGroup llgroup) {
        this.llgroup = llgroup;
    }

    /**
     * @return the llvalidationrule
     */
    public LineListValidationRule getLlvalidationrule() {
        return llvalidationrule;
    }

    /**
     * @param llvalidationrule the llvalidationrule to set
     */
    public void setLlvalidationrule(LineListValidationRule llvalidationrule) {
        this.llvalidationrule = llvalidationrule;
    }
    
}