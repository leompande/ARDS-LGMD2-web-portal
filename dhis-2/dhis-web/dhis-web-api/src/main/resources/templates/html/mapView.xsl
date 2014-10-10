<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml" xmlns:d="http://dhis2.org/schema/dxf/2.0">

  <xsl:template match="d:mapView">
    <div class="mapView">
      <h2><xsl:value-of select="@name"/></h2>
      
      <table>
        <tr>
            <td>Resource Data</td>
            <td><a href="{@href}/data">png</a></td>
        </tr>
      </table><br/>
      
      <table>
        <tr>
            <td>Dynamic Data</td>
            <td><a href="data">png</a></td>
        </tr>
        <tr>
            <td>in</td>
            <td>indicator uid (req)</td>
        </tr>
        <tr>
            <td>ou</td>
            <td>organisation unit uid (req)</td>
        </tr>
        <tr>
            <td>level</td>
            <td>organisation unit level (opt)</td>
        </tr>
      </table><br/>
      
      <a href="{@href}/data"><img src="{@href}/data" style="border: 1px solid; padding: 5px;" /></a>

      <h3>Details</h3>

      <table>
        <xsl:for-each select="attribute::*">
          <tr><td><xsl:value-of select="local-name()"/></td><td><xsl:value-of select="."/></td></tr>
        </xsl:for-each>
        <xsl:for-each select="child::*">
          <tr><td><xsl:value-of select="local-name()"/></td><td>
            <xsl:choose>
              <xsl:when test="@name"><a href="{@href}"><xsl:value-of select="@name"/></a>
              </xsl:when>
              <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
            </xsl:choose>
          </td></tr>
        </xsl:for-each>
      </table>
    </div>
  </xsl:template>

  <xsl:template match="d:mapView" mode="row">
    <tr>
      <td><xsl:value-of select="@name"/></td>
      <td>
        <xsl:element name="a">
          <xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute>
          <xsl:text>html</xsl:text>
        </xsl:element>
      </td>
      <td>
        <xsl:element name="a">
          <xsl:attribute name="href"><xsl:value-of select="concat(@href,'.png')"/></xsl:attribute>
          <xsl:text>png</xsl:text>
        </xsl:element>
      </td>
      <td>
        <xsl:element name="a">
          <xsl:attribute name="href"><xsl:value-of select="concat(@href,'.xml')"/></xsl:attribute>
          <xsl:text>xml</xsl:text>
        </xsl:element>
      </td>
      <td>
        <xsl:element name="a">
          <xsl:attribute name="href"><xsl:value-of select="concat(@href,'.json')"/></xsl:attribute>
          <xsl:text>json</xsl:text>
        </xsl:element>
      </td>
      <td>
        <xsl:element name="a">
          <xsl:attribute name="href"><xsl:value-of select="concat(@href,'.jsonp')"/></xsl:attribute>
          <xsl:text>jsonp</xsl:text>
        </xsl:element>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
