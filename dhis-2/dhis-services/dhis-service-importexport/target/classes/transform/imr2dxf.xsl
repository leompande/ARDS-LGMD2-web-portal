<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gm="http://www.SDMX.org/resources/SDMXML/schemas/v2_0/genericmetadata"    
    version="1.0">

    <xsl:output indent="yes" method="xml"/>

    <xsl:key name="indicator-types"
        match="/Indicators/Indicator[not (DataType = preceding::Indicator/DataType)]" use="DataType"/>

    <!--Construct tree of unique indicator types   -->
    <xsl:template name="indicator-types">
        <xsl:element name="indicatorTypes">
            <xsl:for-each
                select="/Indicators/Indicator[not (DataType = preceding::Indicator/DataType)]">
                <xsl:element name="indicatorType">
                    <xsl:element name="name">
                        <xsl:value-of select="DataType"/>
                    </xsl:element>
                    <xsl:element name="id">
                        <xsl:value-of select="IndicatorId"/>
                    </xsl:element>
                    <!--            This will have to be edited manually -->
                    <xsl:element name="factor">1</xsl:element>
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template name="indicators">
        <xsl:element name="indicators">
            <xsl:for-each select="//gm:AttributeValueSet">
                <xsl:message>New indicator</xsl:message>
                <indicator>
                    <id>
                        <xsl:value-of select="position()"/>
                    </id>
                    <uuid/>
                    <name>
                        <xsl:value-of select="gm:ReportedAttribute[@conceptID='SHORT_NAME']/gm:Value"/>
                    </name>
                    <alternativeName/>
                    <!--ShortName is a pain ... imr has a few of value "short name" ... dhis requires uniquess :-( -->
                    <shortName>
                    <!--    <xsl:if test="contains(Shortname,'short name')">
                            <xsl:value-of select="substring(Name,1,25)"/>
                        </xsl:if>
                        <xsl:if test="not(contains(Shortname,'short name'))">
                            <xsl:value-of select="substring(ShortName,1,25)"/>
                        </xsl:if>-->

                        <!--            Bugger it - random strings ...-->
                        <xsl:value-of select="gm:ReportedAttribute[@conceptID='SHORT_NAME']/gm:Value"/>
                    </shortName>
                    <code/>
                    <description>
                        <xsl:value-of select="gm:ReportedAttribute[@conceptID='_DESCRIPTION']/gm:Value"/>
                        <!--            Rationale: <xsl:value-of select="Rationale"/>
            Preferred Data sources: <xsl:value-of select="PreferredDataSources"/>-->
                    </description>
                    <xsl:variable name="DataType" select="DataType"/>
                    <annualized/>
                    <indicatorType>1
<!--                        <xsl:value-of select="key('indicator-types',$DataType)/IndicatorId"/>-->
                    </indicatorType>
                </indicator>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template match="/">
        <xsl:element name="dxf">
            <xsl:call-template name="indicator-types"/>
            <xsl:call-template name="indicators"/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
