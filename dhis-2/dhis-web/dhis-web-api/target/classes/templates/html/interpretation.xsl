<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >
  
  <xsl:template match="d:interpretation">
    <div class="interpretation">
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
          <td>Text</td>
          <td> <xsl:value-of select="d:text" /> </td>
        </tr>
        <tr>
          <td>Created</td>
          <td> <xsl:value-of select="d:created" /> </td>
        </tr>
      </table>

      <xsl:apply-templates select="d:user|d:comments" mode="short" />
    </div>
  </xsl:template>

  <xsl:template match="d:comments" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Comments</h3>
      <xsl:apply-templates select="child::*" mode="short"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="d:comment" mode="short">
    <table class="comments">
      <tr>
        <td>Created</td>
        <td> <xsl:value-of select="d:created" /> </td>
      </tr>
      <tr>
        <td>Text</td>
        <td> <xsl:value-of select="d:text" /> </td>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>
