<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >
  
  <xsl:template match="d:mapLegendSet">
    <div class="mapLegend">
      <h2>
        <xsl:value-of select="@name" />
      </h2>
      <table>
        <tr>
          <td>ID</td>
          <td> <xsl:value-of select="@id" /> </td>
        </tr>
        <tr>
          <td>Created</td>
          <td> <xsl:value-of select="@created" /> </td>
        </tr>
        <tr>
          <td>Last Updated</td>
          <td> <xsl:value-of select="@lastUpdated" /> </td>
        </tr>
        <tr>
          <td>Code</td>
          <td> <xsl:value-of select="@code" /> </td>
        </tr>
        <tr>
          <td>Symbolizer</td>
          <td> <xsl:value-of select="d:symbolizer" /> </td>
        </tr>
        <tr>
          <td>Type</td>
          <td> <xsl:value-of select="d:type" /> </td>
        </tr>
      </table>

      <xsl:apply-templates select="d:mapLegends" mode="short"/>

    </div>
  </xsl:template>
  
</xsl:stylesheet>
