<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml" xmlns:d="http://dhis2.org/schema/dxf/2.0">

  <xsl:template match="d:event">
    <div class="event">
      <table>
        <tr>
          <td>ID</td>
          <td> <xsl:value-of select="@event" /> </td>
        </tr>
        <tr>
          <td>Program</td>
          <td> <xsl:value-of select="@program" /> </td>
        </tr>
        <tr>
          <td>Program Stage</td>
          <td> <xsl:value-of select="@programStage" /> </td>
        </tr>
        <tr>
          <td>Organisation Unit</td>
          <td> <xsl:value-of select="@orgUnit" /> </td>
        </tr>
        <tr>
          <td>Event Date</td>
          <td> <xsl:value-of select="@eventDate" /> </td>
        </tr>
        <tr>
          <td>Completed</td>
          <td> <xsl:value-of select="@completed" /> </td>
        </tr>
        <tr>
          <td>Stored By</td>
          <td> <xsl:value-of select="@storedBy" /> </td>
        </tr>
      </table>

    </div>
  </xsl:template>

</xsl:stylesheet>
