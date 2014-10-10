<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jan 12, 2010</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p>Unit test - transform dataA.xml</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:param name="name"/>
    
    <xsl:template match="dataElement">
        <de><xsl:value-of select="child::name"/></de>
    </xsl:template>
    
    
    <xsl:template match="/">
        <xsl:message>Test transform</xsl:message>
        <xsl:element name="result">
            <xsl:attribute name="name">
                <xsl:value-of select="$name"/>
            </xsl:attribute>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
