<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xl="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
    xmlns:ext="xalan://org.hisp.dhis.importexport.zip.ExcelExtension">

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Apr 14, 2010</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>This transform processes sheet2 of a pre-formatted excel document and produces dxf
                for the orgunits</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:output method="xml" indent="yes"/>

     <!-- This parameter should contain the relative file path to the actual excel document-->
    <xsl:param name="zip_url"/>
    <!--  Construct a "jar" protocol base-uri for finding streams in the excel file-->
    <!--  (remember its really just a zip file) -->
    <xsl:variable name="base-uri" select="concat('jar:file:',$zip_url,'!')"/>
    
    <!--  Most important - we need to be able to reference the shared strings-->
    <!--  Build the uri then read in with the document() function-->
    <xsl:variable name="sstrings-uri" select="concat($base-uri,'/xl/sharedStrings.xml')"/>
    <xsl:variable name="sstrings" select="document($sstrings-uri)"/>
    
    <!--  Get a handle on all the orgunit rows -->
    <!--  ie. rows after row 2 with a value in first cell -->
    <xsl:variable name="orgunit-rows" select="/xl:worksheet/xl:sheetData/xl:row[@r>2 and xl:c/xl:v]"/>

    <!--  Utility template to substitute strings from shared strings-->
    <xsl:template name="sstring">
        <xsl:param name="index"/>
        <xsl:value-of select="$sstrings/xl:sst/xl:si[$index + 1]/xl:t"/>
    </xsl:template>

    <!--  This is the main entry point  -->
    <!--  Because we are only processing sheet2 we just  -->
    <!--  generate the orgunits then the associations-->
    <xsl:template match="/">
        <xsl:message>Processing <xsl:value-of select="$zip_url"/></xsl:message>
        <xsl:element name="dxf">
            <xsl:call-template name="organisationUnits"/>
            <xsl:call-template name="organisationUnitAssociations"/>
        </xsl:element>
    </xsl:template>



    <xsl:template name="organisationUnits">
        <xsl:element name="organisationUnits">
            <xsl:message>Listing orgunits</xsl:message>
            <xsl:for-each select="$orgunit-rows">
                <xsl:variable name="id" select="xl:c[substring(@r,1,1)='A']"/>
                <xsl:variable name="uuid" select="xl:c[substring(@r,1,1)='B']"/>
                <xsl:variable name="name" select="xl:c[substring(@r,1,1)='C']"/>
                <xsl:variable name="shortname" select="xl:c[substring(@r,1,1)='E']"/>
                <xsl:variable name="code" select="xl:c[substring(@r,1,1)='F']"/>
                <xsl:variable name="openingdate" select="xl:c[substring(@r,1,1)='G']"/>
                <xsl:variable name="closedate" select="xl:c[substring(@r,1,1)='H']"/>
                <xsl:variable name="active" select="xl:c[substring(@r,1,1)='I']"/>
                <xsl:variable name="comment" select="xl:c[substring(@r,1,1)='J']"/>
                <xsl:variable name="geocode" select="xl:c[substring(@r,1,1)='K']"/>
                <xsl:variable name="latitude" select="xl:c[substring(@r,1,1)='L']"/>
                <xsl:variable name="longitude" select="xl:c[substring(@r,1,1)='M']"/>
                <xsl:variable name="url" select="xl:c[substring(@r,1,1)='N']"/>
                <xsl:variable name="type" select="xl:c[substring(@r,1,1)='O']"/>
                <xsl:variable name="polygon" select="xl:c[substring(@r,1,1)='P']"/>
                <xsl:variable name="lastupdated" select="xl:c[substring(@r,1,1)='Q']"/>

                <xsl:element name="organisationUnit">
                    <xsl:element name="id">
                        <xsl:value-of select="$id"/>
                    </xsl:element>

                    <xsl:element name="uuid">
                        <xsl:call-template name="sstring">
                            <xsl:with-param name="index" select="$uuid"/>
                        </xsl:call-template>
                    </xsl:element>

                    <xsl:element name="name">
                        <xsl:call-template name="sstring">
                            <xsl:with-param name="index" select="$name"/>
                        </xsl:call-template>
                    </xsl:element>

                    <xsl:element name="shortName">
                        <xsl:call-template name="sstring">
                            <xsl:with-param name="index" select="$shortname"/>
                        </xsl:call-template>
                    </xsl:element>

                    <xsl:element name="code">
                        <xsl:call-template name="sstring">
                            <xsl:with-param name="index" select="$code"/>
                        </xsl:call-template>
                    </xsl:element>

                    <xsl:element name="openingDate">
                        <xsl:value-of select="ext:date($openingdate)"/>
                    </xsl:element>

                    <xsl:element name="closedDate">
                        <xsl:value-of select="ext:date($closedate)"/>
                    </xsl:element>

                    <xsl:element name="active">
                        <xsl:value-of select="$active"/>
                    </xsl:element>

                    <xsl:element name="comment">
                        <xsl:call-template name="sstring">
                            <xsl:with-param name="index" select="$comment"/>
                        </xsl:call-template>
                    </xsl:element>

                    <xsl:element name="geoCode">
                        <xsl:call-template name="sstring">
                            <xsl:with-param name="index" select="$geocode"/>
                        </xsl:call-template>
                    </xsl:element>

                </xsl:element>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template name="organisationUnitAssociations">
        <xsl:message>Building orgunit associations</xsl:message>
        <xsl:element name="organisationUnitRelationships">
            <xsl:for-each select="$orgunit-rows">
                <xsl:variable name="parent" select="xl:c[substring(@r,1,1)='D']"/>
                <xsl:if test="$parent">
                    <xsl:element name="organisationUnitRelationship">
                        <xsl:element name="parent">
                            <xsl:value-of select="$parent"/>
                        </xsl:element>
                        <xsl:element name="child">
                            <xsl:value-of select="xl:c[substring(@r,1,1)='A']"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:if>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
