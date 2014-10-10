<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >
  
  <xsl:template match="d:mapLayer">
    <div class="mapLayer">
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
          <td>Type</td>
          <td> <xsl:value-of select="d:type" /> </td>
        </tr>
        <tr>
          <td>URL</td>
          <td> <xsl:value-of select="d:url" /> </td>
        </tr>
        <tr>
          <td>Layers</td>
          <td> <xsl:value-of select="d:layers" /> </td>
        </tr>
        <tr>
          <td>Time</td>
          <td> <xsl:value-of select="d:time" /> </td>
        </tr>
        <tr>
          <td>Fill Color</td>
          <td> <xsl:value-of select="d:fillColor" /> </td>
        </tr>
        <tr>
          <td>Fill Opacity</td>
          <td> <xsl:value-of select="d:fillOpacity" /> </td>
        </tr>
        <tr>
          <td>Stroke Color</td>
          <td> <xsl:value-of select="d:strokeColor" /> </td>
        </tr>
        <tr>
          <td>Stroke Width</td>
          <td> <xsl:value-of select="d:strokeWidth" /> </td>
        </tr>
      </table>

    </div>
  </xsl:template>

</xsl:stylesheet>
