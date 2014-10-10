package org.hisp.dhis.sqlview;

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

/**
 * @author Dang Duy Hieu
 * @version $Id SqlCodeMapUtil.java Aug 16, 2010$
 */
public class SqlViewJoinLib
{
    public static final String COCN_JOIN_CS = "JOIN _categorystructure AS _cs ON _cocn.categoryoptioncomboid = _cs.categoryoptioncomboid \n";

    public static final String COCN_JOIN_DV = "JOIN datavalue AS dv ON _cocn.categoryoptioncomboid = dv.categoryoptioncomboid \n";

    public static final String CS_JOIN_COCN = "JOIN _categoryoptioncomboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

    public static final String DEGSS_JOIN_DV = "JOIN datavalue AS dv ON _degss.dataelementid = dv.dataelementid \n";

    public static final String DV_JOIN_COCN = "JOIN _categoryoptioncomboname AS _cocn ON dv.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

    public static final String DV_JOIN_DEGSS = "JOIN _dataelementgroupsetstructure AS _degss ON dv.dataelementid = _degss.dataelementid \n";

    public static final String DV_JOIN_OUS = "JOIN _orgunitstructure AS _ous ON dv.sourceid = _ous.organisationunitid \n";

    public static final String DV_JOIN_OUSTGSS = "JOIN _organisationunitgroupsetstructure AS _oustgss ON dv.sourceid = _oustgss.organisationunitid \n";

    public static final String OUS_JOIN_DV = "JOIN datavalue AS dv ON _ous.organisationunitid = dv.sourceid \n";

    public static final String OUGSS_JOIN_DV = "JOIN datavalue AS dv ON _ougss.organisationunitid = dv.sourceid \n";

    public static final String OUSTGSS_JOIN_DV = "JOIN datavalue AS dv ON _oustgss.organisationunitid = dv.sourceid \n";

    public static final String OUS_JOIN_OUGSS = "JOIN _orgunitgroupsetstructure AS _ougss ON _ous.organisationunitid = _ougss.organisationunitid \n";

    public static final String OUS_JOIN_OUSTGSS = "JOIN _organisationunitgroupsetstructure AS _oustgss ON _ous.organisationunitid = _oustgss.organisationunitid \n";

    public static final String OUGSS_JOIN_OUS = "JOIN _orgunitstructure AS _ous ON _ougss.organisationunitid = _ous.organisationunitid \n";

    public static final String OUGSS_JOIN_OUSTGSS = "JOIN _organisationunitgroupsetstructure AS _oustgss ON _ougss.organisationunitid = _oustgss.organisationunitid \n";

    public static final String OUSTGSS_JOIN_OUS = "JOIN _orgunitstructure AS _ous ON _oustgss.organisationunitid = _ous.organisationunitid \n";

    public static final String OUSTGSS_JOIN_OUGSS = "JOIN _orgunitgroupsetstructure AS _ougss ON _oustgss.organisationunitid = _ougss.organisationunitid \n";

    /**
     * COCN_JOIN_DEGSS presents to the relationship between two resourcetables
     * _CategoryOptionComboname and _DataElementGroupSetStructure through the
     * INNER JOIN between DataValue and _DataElementGroupSetStructure
     */
    public static final String COCN_JOIN_DEGSS = COCN_JOIN_DV + DV_JOIN_DEGSS;

    /**
     * DEGSS_JOIN_COCN presents to the relationship between two resourcetables
     * _dataelementgroupsetstructure and _CategoryOptionComboname through the
     * INNER JOIN between DataValue and _CategoryOptionComboname
     */
    public static final String DEGSS_JOIN_COCN = DEGSS_JOIN_DV + DV_JOIN_COCN;

    /**
     * COCN_JOIN_OUS presents to the relationship between two resourcetables
     * _CategoryOptionComboname and _OrgUnitStructure through the INNER JOIN
     * between DataValue and _OrgUnitStructure
     */
    public static final String COCN_JOIN_OUS = COCN_JOIN_DV + DV_JOIN_OUS;

    /**
     * COCN_JOIN_OUSTGSS presents to the relationship between two resourcetables
     * _CategoryOptionComboname and _OrganisationUnitGroupSetStructure through
     * the INNER JOIN between DataValue and _OrganisationUnitGroupSetStructure
     */
    public static final String COCN_JOIN_OUSTGSS = COCN_JOIN_DV + DV_JOIN_OUSTGSS;

    /**
     * OUSTGSS_JOIN_COCN presents to the relationship between two resourcetables
     * _OrganisationUnitGroupSetStructure and _CategoryOptionComboname through
     * the INNER JOIN between DataValue and _CategoryOptionComboname
     */
    public static final String OUSTGSS_JOIN_COCN = OUSTGSS_JOIN_DV + DV_JOIN_COCN;

    /**
     * OUGSS_JOIN_COCN presents to the relationship between two resourcetables
     * _OrgUnitGroupSetStructure and _CategoryOptionComboname through the INNER
     * JOIN between DataValue and _CategoryOptionComboname
     */
    public static final String OUGSS_JOIN_COCN = OUGSS_JOIN_DV + DV_JOIN_COCN;

    /**
     * OUS_JOIN_COCN presents to the relationship between two resourcetables
     * _OrgUnitStructure and _CategoryOptionComboname through the INNER JOIN
     * between DataValue and _CategoryOptionComboname
     */
    public static final String OUS_JOIN_COCN = OUS_JOIN_DV + DV_JOIN_COCN;

    /**
     * OUS_JOIN_DEGSS presents to the relationship between two resourcetables
     * _OrgUnitStructure and _DataElementGroupSetStructure through the INNER
     * JOIN between DataValue and _DataElementGroupSetStructure
     */
    public static final String OUS_JOIN_DEGSS = OUS_JOIN_DV + DV_JOIN_DEGSS;

    /**
     * OUGSS_JOIN_DEGSS presents to the relationship between two
     * resourcetables_OrgUnitGroupSetStructure and _DataElementGroupSetStructure
     * through the INNER JOIN between DataValue and
     * _DataElementGroupSetStructure
     */
    public static final String OUGSS_JOIN_DEGSS = OUGSS_JOIN_DV + DV_JOIN_DEGSS;

    /**
     * OUGSS_JOIN_DEGSS presents to the relationship between two
     * _OrganisationUnitGroupSetStructure and _DataElementGroupSetStructure
     * through the INNER JOIN between DataValue and
     * _DataElementGroupSetStructure
     */
    public static final String OUSTGSS_JOIN_DEGSS = OUSTGSS_JOIN_DV + DV_JOIN_DEGSS;

    /**
     * DEGSS_JOIN_OUS presents to the relationship between two resourcetables
     * _DataElementGroupSetStructure and _OrgUnitStructure through the INNER
     * JOIN between DataValue and _OrgUnitStructure
     */
    public static final String DEGSS_JOIN_OUS = DEGSS_JOIN_DV + DV_JOIN_OUS;

    /**
     * DEGSS_JOIN_OUSTGSS presents to the relationship between two
     * resourcetables _DataElementGroupSetStructure and
     * _OrganisationUnitGroupSetStructure through the INNER JOIN between
     * DataValue and _OrganisationUnitGroupSetStructure
     */
    public static final String DEGSS_JOIN_OUSTGSS = DEGSS_JOIN_DV + DV_JOIN_OUSTGSS;

}
