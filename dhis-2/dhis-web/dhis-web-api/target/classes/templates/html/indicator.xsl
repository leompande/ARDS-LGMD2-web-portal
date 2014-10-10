<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >
  
  <xsl:template match="d:indicator">
    <div class="indicator">
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
          <td>Short Name</td>
          <td> <xsl:value-of select="d:shortName" /> </td>
        </tr>
        <tr>
          <td>Description</td>
          <td> <xsl:value-of select="d:description" /> </td>
        </tr>
        <tr>
          <td>Numerator</td>
          <td> <xsl:value-of select="d:numerator" /> </td>
        </tr>
        <tr>
          <td>Numerator Description</td>
          <td> <xsl:value-of select="d:numeratorDescription" /> </td>
        </tr>
        <tr>
          <td>Denominator</td>
          <td> <xsl:value-of select="d:denominator" /> </td>
        </tr>
        <tr>
          <td>Denominator Description</td>
          <td> <xsl:value-of select="d:denominatorDescription" /> </td>
        </tr>
        <tr>
          <td>Annualized</td>
          <td> <xsl:value-of select="d:annualized" /> </td>
        </tr>
      </table>

      <xsl:apply-templates select="d:indicatorGroups|d:dataSets" mode="short"/>

    </div>
  </xsl:template>
  
  <xsl:template match="d:indicators" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Indicators</h3>
      <table class="indicators">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>
