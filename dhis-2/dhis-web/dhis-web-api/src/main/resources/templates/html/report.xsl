<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >

  <xsl:template match="d:report">
  <div class="report">
    <h2> <xsl:value-of select="@name" /> </h2>
	  
	  <table>
		<tr>
			<td>Resource Data</td>
			<td><a href="{@href}/data">pdf</a></td>
			<td><a href="{@href}/data.xls">xls</a></td>
		</tr>
		<tr>
			<td>ou</td>
			<td colspan="2">organisation unit uid (opt)</td>
		</tr>
		<tr>
			<td>pe</td>
			<td colspan="2">period yyyy-MM-dd (opt)</td>
		</tr>
	  </table><br/>
	  
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
        <td>UsingOrgUnitGroupSets</td>
        <td> <xsl:value-of select="d:usingOrgUnitGroupSets" /> </td>
      </tr>
    </table>

    <xsl:apply-templates select="d:reportTable" mode="short"/>
  </div>
  </xsl:template>

  <xsl:template match="d:reports" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Reports</h3>
      <table class="reports">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="d:reportTable" mode="short">
    <xsl:if test="@name">
      <h3>ReportTable</h3>
      <table class="reportTable">
        <xsl:apply-templates select="." mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
