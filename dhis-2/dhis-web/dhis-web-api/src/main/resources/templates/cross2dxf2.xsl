<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
                version="1.0">
  
    <xsl:output method="xml" indent="yes" />

    <xsl:template match="/">
        <xsl:for-each select="//*[local-name()='DataSet']">
            <d:dataValueSet period='{@TIME_PERIOD}' 
              orgUnitIdScheme='code' 
              dataElementIdScheme='code' 
              dataSet='{@datasetID}' 
              orgUnit='{@FACILITY}'>
                <xsl:apply-templates />
            </d:dataValueSet>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match='*[local-name()="OBS_VALUE"]'>
      <xsl:element name="d:dataValue" >
      <xsl:attribute name="dataElement"><xsl:value-of select="@DATAELEMENT"/></xsl:attribute>  
      <xsl:if test="@DISAGG">
        <xsl:attribute name="categoryOptionCombo"><xsl:value-of select="@DISAGG"/></xsl:attribute>
      </xsl:if>
      <xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>  
    </xsl:element>

    </xsl:template> 
</xsl:stylesheet>
