<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:categoryCombo">
    <div class="categoryCombo">
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
      </table>

      <xsl:apply-templates select="d:categoryOptionCombos|d:categories" mode="short"/>
    </div>
  </xsl:template>

  <xsl:template match="d:categoryCombo" mode="short">
    <h3>CategoryCombo</h3>
    <table border="1" class="categoryCombo">
      <xsl:apply-templates select="." mode="row"/>
    </table>
  </xsl:template>
  
  <xsl:template match="d:categories" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Categories</h3>
      <table border="1" class="categories">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
