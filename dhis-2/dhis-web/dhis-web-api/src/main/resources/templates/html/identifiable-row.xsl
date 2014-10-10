<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml" xmlns:d="http://dhis2.org/schema/dxf/2.0">

  <xsl:template match="*" mode="row">
    <xsl:if test="@name">
      <tr>
        <td> <xsl:value-of select="@name"/> </td>
        <td> <a href="{@href}">html</a> </td>
        <td> <a href="{@href}.xml">xml</a> </td>
        <td> <a href="{@href}.json">json</a> </td>
        <td> <a href="{@href}.jsonp">jsonp</a> </td>
        <td> <a href="{@href}.pdf">pdf</a> </td>
        <td> <a href="{@href}.xls">xls</a> </td>
        <td> <a href="{@href}.csv">csv</a> </td>
      </tr>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
