<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >

  <xsl:template match="d:dataSet">
    <div class="dataSet">
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
          <td>Period Type</td>
          <td> <xsl:value-of select="d:periodType" /> </td>
        </tr>
        <tr>
          <td>Expiry Days</td>
          <td> <xsl:value-of select="d:expiryDays" /> </td>
        </tr>
        <tr>
          <td>Version</td>
          <td> <xsl:value-of select="d:version" /> </td>
        </tr>
        <tr>
          <td>Mobile</td>
          <td> <xsl:value-of select="d:mobile" /> </td>
        </tr>

      </table>

      <xsl:apply-templates select="d:dataElements|d:indicators|d:organisationUnits" mode="short" />

    </div>
  </xsl:template>
  
  <xsl:template match="d:dataSets" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>DataSets</h3>
      <table class="dataSets">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
