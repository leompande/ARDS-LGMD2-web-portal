<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns="http://dhis2.org/schema/dxf/1.0" xmlns:msg="http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message">
    
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jun 8, 2010</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>SDMX Cross-Sectional Data to DXF</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:output method="xml" indent="yes"/>
     
    <!-- This parameter should contain the relative file path to the zip package -->
    <xsl:param name="zip_url"/>
    <!-- This parameter should contain the relative file path to the dxf metadata directory -->
    <xsl:param name="dxf_url"/>
    <!-- This parameter should contain the dxf minor version attribute -->
    <xsl:param name="dxf_minor_version">1.1</xsl:param>
    <!-- This parameter should contain the current date -->
    <xsl:param name="timestamp"/>
    
    <!--  Construct a "jar" protocol base-uri for finding streams in the zip file-->
    <xsl:variable name="base-uri" select="concat('jar:file:',$zip_url,'!')"/>
    <!--  Open the dxf metadata stream-->
    <xsl:variable name="dxf_meta" select="document($dxf_url)/dxf"/>
    <!--  Get comment from header -->
    <xsl:variable name="comment">
        <xsl:value-of select="//msg:Header/msg:Sender/msg:Name"/>
    </xsl:variable>
    
    <xsl:template match="/">
<!--        <xsl:element name="dxf" namespace="http://dhis2.org/schema/dxf/1.0">
            <xsl:attribute name="minorVersion">
                <xsl:value-of select="$dxf_minor_version"/>
                </xsl:attribute>-->
        <xsl:element name="dxf">

            <!--  Bring in *all* the metadata-->
            <!--  TODO: we'll trim this -->
            <xsl:copy-of select="$dxf_meta/*"/>

            <!--            Create period metadata for datavalues-->
            <periods>
                <xsl:apply-templates select="//*[local-name()='Group']" mode="create-period"/>
            </periods>
            <!--            Process the dataset-->
            <xsl:element name="dataValues" namespace="http://dhis2.org/schema/dxf/1.0">
                <xsl:apply-templates select="child::node()/child::node()[local-name()='DataSet']"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="*[local-name()='DataSet']">
        <!--        The namespace uri was formed from the category combo -->
        <!--        ... payback time ... find the categoryOptionCombo node in dxf metadata -->
        <xsl:variable name="catcombo" 
            select="substring-before(substring-after(namespace-uri(),'KF_'),':')"/>
        <!--  catoptcombos is the nodeset containing all categoryOptionCombos for the categorycombo-->
        <xsl:variable name="catoptcombos" 
            select="$dxf_meta/categoryOptionCombos/categoryOptionCombo[categoryCombo/id=$catcombo]"/>
<!--        <xsl:message>Dataset catoptcombos = <xsl:value-of select="count($catoptcombos)"/></xsl:message>-->
        <xsl:apply-templates select="child::node()[local-name()='Group']" mode="data">
            <xsl:with-param name="catoptcombos" select="$catoptcombos"/>
        </xsl:apply-templates>
    </xsl:template>
        
    <xsl:template match="*[local-name()='Group']" mode="data">
        <xsl:param name="catoptcombos"/> 
        <xsl:variable name="period" select="position()"/>
        <xsl:apply-templates select="child::node()[local-name()='Section']">
            <xsl:with-param name="period" select="$period"/>
            <xsl:with-param name="catoptcombos" select="$catoptcombos"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="*[local-name()='Section']">
        <xsl:param name="period"></xsl:param>
        <xsl:param name="catoptcombos"/> 
<!--        <xsl:message>Period: <xsl:value-of select="$period"/></xsl:message>-->
        <xsl:apply-templates select="child::node()[local-name()='OBS_VALUE']">
            <xsl:with-param name="period" select="$period"/>
            <xsl:with-param name="catoptcombos" select="$catoptcombos"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="*[local-name()='OBS_VALUE']">
        <xsl:param name="period"/>
        <xsl:param name="catoptcombos"/> 
        
        <xsl:element name="dataValue">
            <xsl:attribute name="dataElement"><xsl:value-of select="@DATAELEMENT"/></xsl:attribute>
            <xsl:attribute name="period"><xsl:value-of select="$period"/></xsl:attribute>
            <xsl:attribute name="source"><xsl:value-of select="@FACILITY"/></xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
            <xsl:attribute name="timeStamp">2010-01-01</xsl:attribute>
            <xsl:attribute name="comment"><xsl:value-of select="$comment"/></xsl:attribute>
            
            <!--  Find all categoryoption attributes-->
            <xsl:variable name="keys" 
                select="@*[not(local-name()='DATAELEMENT' or local-name()='FACILITY' or local-name()='value' or local-name()='VALUE_TYPE')]"/> 
<!--            <xsl:for-each select="$keys">
                <xsl:message><xsl:value-of select="local-name()"/>: <xsl:value-of select="."/></xsl:message>
            </xsl:for-each>-->
            <xsl:attribute name="categoryOptionCombo"><xsl:call-template name="find-catoptcombo">
                <xsl:with-param name="keys" select="$keys"/>
                <xsl:with-param name = "last-key" select="count($keys)"/>
                <xsl:with-param name="catoptcombos" select="$catoptcombos"></xsl:with-param>
            </xsl:call-template></xsl:attribute>
            
            
        </xsl:element>
    </xsl:template>
    
    <xsl:template name="find-catoptcombo">
        <xsl:param name="keys"/>
        <xsl:param name="last-key"/>
        <xsl:param name="catoptcombos"/>
        
<!--        <xsl:message>key: <xsl:value-of select="$keys[$last-key]"/></xsl:message>
        <xsl:message>number of last key: <xsl:value-of select="$last-key"/></xsl:message>-->
<!--        <xsl:message><xsl:value-of select="count($catoptcombos)"/></xsl:message>-->
        
        <xsl:variable name="sub-catoptcombos" 
            select="$catoptcombos[categoryOptions/categoryOption/id=$keys[$last-key]]"></xsl:variable>
        
        <xsl:if test="$last-key=0">
            <xsl:value-of select="$catoptcombos/id"/> 
        </xsl:if>
        
        <xsl:if test="$last-key>0">
            <xsl:call-template name="find-catoptcombo">
                <xsl:with-param name="keys" select="$keys"/>
                <xsl:with-param name="last-key" select="$last-key - 1"></xsl:with-param>
                <xsl:with-param name="catoptcombos" select="$sub-catoptcombos"></xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="*[local-name()='Group']" mode="create-period">
<!--        Create period id based on the position() of the Group-->
        <xsl:variable name="period" select="@TIME_PERIOD"/>
        <xsl:variable name="start"><xsl:value-of select="$period"/>-01</xsl:variable>
        <xsl:variable name="end"><xsl:value-of select="$period"/>-<xsl:call-template name="last-day-of-month">
            <xsl:with-param name="period" select="$period"/>
        </xsl:call-template></xsl:variable>
        <period>
            <id><xsl:value-of select="position()"/></id>
            <!--            TODO: This periodType needs to be made correct-->
            <periodType>Monthly</periodType>
            <startDate><xsl:value-of select="$start"/></startDate>
            <endDate><xsl:value-of select="$end"/></endDate>
        </period>
        <xsl:message>Created period: <xsl:value-of select="$start"/>, <xsl:value-of select="$end"/></xsl:message>
    </xsl:template>

    <!--    Find last day of month
        (Algorithm from 'XSLT CookBook, 2nd Edition')-->
    <xsl:template name="last-day-of-month">
        <xsl:param name="period"/>
        <xsl:variable name="month" select="substring-after($period,'-')"/>
        <xsl:variable name="year" select="substring-before($period,'-')"/>
        <xsl:message>Month: <xsl:value-of select="$month"/></xsl:message>
        <xsl:message>Year: <xsl:value-of select="$year"/></xsl:message>
        
        <xsl:choose>
            <xsl:when
                test="$month = 2 and
                not($year mod 4) and
                ($year mod 100 or not($year mod 400) ) ">
                <xsl:value-of select="29"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of
                    select="substring('312831303130313130313031' ,
                    2 * $month - 1, 2) "
                />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    
</xsl:stylesheet>
