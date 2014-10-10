<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

<xsl:template match="d:dxf2/d:dataValueSets">

<p>The DataValueSet resource provides a way to POST data values into DHIS 2.</p>

<p>Identifiers can be found by browsing the API from the <a href="../api/resources">resources endpoint</a>.</p>

<p>Period values are given in ISO format. Some examples:</p>

<table>
  <tr><th>Interval</th><th>Format</th><th>Example</th><th>Description</th></tr>
  <tr><td>Day</td><td>yyyyMMdd</td><td>20040315</td><td>March 15 2004</td></tr>
  <tr><td>Week</td><td>yyyyWn</td><td>2004W10</td><td>Week 10 2004</td></tr>
  <tr><td>Month</td><td>yyyyMM</td><td>200403</td><td>March 2004</td></tr>
  <tr><td>Quarter</td><td>yyyyQn</td><td>2004Q1</td><td>January-March 2004</td></tr>
  <tr><td>Sixmonth</td><td>yyyySn</td><td>2004S1</td><td>Janary-June 2004</td></tr>
  <tr><td>Year</td><td>yyyy</td><td>2004</td><td>2004</td></tr>
</table>

<p>A complete example of a data value set:</p>

<pre style="font-size:12pt;">
<![CDATA[
<dataValueSet xmlns="http://dhis2.org/schema/dxf/2.0" period="periodISODate" orgUnit="orgUnitID" dataSet="dataSetID" completeDate="yyyy-MM-dd">
  <dataValues>
    <dataValue dataElement="dataElementID" categoryOptionCombo="categoryOptionComboID" value="1" />
    <dataValue dataElement="dataElementID" categoryOptionCombo="categoryOptionComboID" value="2" />
    <dataValue dataElement="dataElementID" categoryOptionCombo="categoryOptionComboID" value="3" />
  </dataValues>
</dataValueSet>
]]>
</pre>

</xsl:template>

</xsl:stylesheet>
