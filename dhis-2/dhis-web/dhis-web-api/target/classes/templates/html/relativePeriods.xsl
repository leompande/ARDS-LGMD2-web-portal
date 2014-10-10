<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
                version="1.0">

  <xsl:template match="d:relativePeriods">
    <div class="relativePeriods">
      <h2>Relative Periods</h2>

	  <table>
      <tr>
        <td>lastSixMonth</td>
        <td> <xsl:value-of select="d:lastSixMonth"/> </td>
      </tr>
      <tr>
        <td>monthsThisYear</td>
        <td> <xsl:value-of select="d:monthsThisYear"/> </td>
      </tr>
      <tr>
        <td>quartersThisYear</td>
        <td> <xsl:value-of select="d:quartersThisYear"/> </td>
      </tr>
      <tr>
        <td>thisYear</td>
        <td> <xsl:value-of select="d:thisYear"/> </td>
      </tr>
      <tr>
        <td>monthsLastYear</td>
        <td> <xsl:value-of select="d:monthsLastYear"/> </td>
      </tr>
      <tr>
        <td>quartersLastYear</td>
        <td> <xsl:value-of select="d:quartersLastYear"/> </td>
      </tr>
      <tr>
        <td>lastYear</td>
        <td> <xsl:value-of select="d:lastYear"/> </td>
      </tr>
      <tr>
        <td>last5Years</td>
        <td> <xsl:value-of select="d:last5Years"/> </td>
      </tr>
      <tr>
        <td>last12Months</td>
        <td> <xsl:value-of select="d:last12Months"/> </td>
      </tr>
	  <tr>
        <td>last3Months</td>
        <td> <xsl:value-of select="d:last3Months"/> </td>
      </tr>
      <tr>
        <td>last6BiMonths</td>
        <td> <xsl:value-of select="d:last6BiMonths"/> </td>
      </tr>
      <tr>
        <td>last4Quarters</td>
        <td> <xsl:value-of select="d:last4Quarters"/> </td>
      </tr>
      <tr>
        <td>last2SixMonths</td>
        <td> <xsl:value-of select="d:last2SixMonths"/> </td>
      </tr>
      <tr>
        <td>lastMonth</td>
        <td> <xsl:value-of select="d:lastMonth"/> </td>
      </tr>
      <tr>
        <td>lastBimonth</td>
        <td> <xsl:value-of select="d:lastBimonth"/> </td>
      </tr>
      <tr>
        <td>lastQuarter</td>
        <td> <xsl:value-of select="d:lastQuarter"/> </td>
      </tr>
    </table>

    </div>
  </xsl:template>

</xsl:stylesheet>
