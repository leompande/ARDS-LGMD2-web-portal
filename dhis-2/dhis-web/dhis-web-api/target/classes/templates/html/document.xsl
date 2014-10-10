<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:document">
    <div class="document">
      <h2> <xsl:value-of select="@name" /> </h2>
	  
	  <table>
		<tr>
			<td>Resource Data</td>
			<td><a href="{@href}/data">any</a></td>
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
          <td>URL</td>
          <td> <xsl:value-of select="d:url" /> </td>
        </tr>
        <tr>
          <td>External</td>
          <td> <xsl:value-of select="d:external" /> </td>
        </tr>
        <tr>
          <td>Content type</td>
          <td> <xsl:value-of select="d:contentType" /> </td>
        </tr>
      </table>
    </div>
  </xsl:template>

  <xsl:template match="d:documents" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Documents</h3>
      <table class="documents">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
