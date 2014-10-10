<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:attributeType">
    <div class="attributeType">
      <h2> <xsl:value-of select="@name" /> </h2>

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
          <td>Mandatory</td>
          <td> <xsl:value-of select="d:mandatory" /> </td>
        </tr>
        <tr>
          <td>Value Type</td>
          <td> <xsl:value-of select="d:valueType" /> </td>
        </tr>
        <tr>
          <td>Applicable for DataElements</td>
          <td> <xsl:value-of select="d:dataElementAttribute" /> </td>
        </tr>
        <tr>
          <td>Applicable for Indicators</td>
          <td> <xsl:value-of select="d:indicatorAttribute" /> </td>
        </tr>
        <tr>
          <td>Applicable for OrganisationUnits</td>
          <td> <xsl:value-of select="d:organisationUnitAttribute" /> </td>
        </tr>
        <tr>
          <td>Applicable for Users</td>
          <td> <xsl:value-of select="d:userAttribute" /> </td>
        </tr>
      </table>

    </div>
  </xsl:template>

</xsl:stylesheet>
