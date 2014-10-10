<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0">

  <xsl:template match="d:smsConfiguration">
    <div class="config">
      <h2>Sms configuration</h2>

      <table border="1">
        <tr>
          <td>Enabled</td>
          <td>
            <xsl:value-of select="d:enabled" />
          </td>
        </tr>
        <tr>
          <td>Polling interval</td>
          <td>
            <xsl:value-of select="d:pollingInterval" />
          </td>
        </tr>
        <tr>
          <td>Long number</td>
          <td>
            <xsl:value-of select="d:longNumber" />
          </td>
        </tr>
      </table>

      <h3>Gateways configured</h3>
      <xsl:if test="d:gateways">
        <xsl:for-each select="d:gateways/*">
          <h4>
            <xsl:value-of select="local-name(.)" />
          </h4>
          <table border="1">
            <xsl:for-each select="child::*">
              <tr>
                <td>
                  <xsl:value-of select="local-name(.)" />
                </td>
                <td>
                  <xsl:value-of select="." />
                </td>
              </tr>
            </xsl:for-each>
          </table>
        </xsl:for-each>
      </xsl:if>
    </div>
  </xsl:template>


</xsl:stylesheet>
