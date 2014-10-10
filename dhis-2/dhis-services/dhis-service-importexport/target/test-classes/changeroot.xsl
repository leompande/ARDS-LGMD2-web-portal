<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Feb 15, 2010</xd:p>
            <xd:p><xd:b>Author:</xd:b> bobj</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>

<xsl:template match="change">
    <dxf>
        <xsl:apply-templates/>
    </dxf>
</xsl:template>
    
<!--    Identity-->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
<xsl:template match="/">
    <xsl:message>Hello World</xsl:message>
    <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>
