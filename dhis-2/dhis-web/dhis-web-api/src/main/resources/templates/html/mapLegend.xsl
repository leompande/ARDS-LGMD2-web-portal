<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >

  <xsl:template match="d:mapLegend">
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
          <td>Image</td>
          <td> <xsl:value-of select="d:image" /> </td>
        </tr>
        <tr>
          <td>Start Value</td>
          <td> <xsl:value-of select="d:startValue" /> </td>
        </tr>
        <tr>
          <td>End Value</td>
          <td> <xsl:value-of select="d:endValue" /> </td>
        </tr>
      </table>

    </div>
  </xsl:template>

  <xsl:template match="d:mapLegends" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>MapLegends</h3>
      <table class="mapLegends">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
