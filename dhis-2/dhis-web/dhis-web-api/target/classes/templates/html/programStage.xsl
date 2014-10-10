<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:programStage">
    <div class="programStage">
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
          <td>description</td>
          <td> <xsl:value-of select="d:description" /> </td>
        </tr>
        <tr>
          <td>minDaysFromStart</td>
          <td> <xsl:value-of select="d:minDaysFromStart" /> </td>
        </tr>
        <tr>
          <td>irregular</td>
          <td> <xsl:value-of select="d:irregular" /> </td>
        </tr>
        <tr>
          <td>reportDateDescription</td>
          <td> <xsl:value-of select="d:reportDateDescription" /> </td>
        </tr>
        <tr>
          <td>autoGenerateEvent</td>
          <td> <xsl:value-of select="d:autoGenerateEvent" /> </td>
        </tr>
        <tr>
          <td>validCompleteOnly</td>
          <td> <xsl:value-of select="d:validCompleteOnly" /> </td>
        </tr>
        <tr>
          <td>validCompleteOnly</td>
          <td> <xsl:value-of select="d:validCompleteOnly" /> </td>
        </tr>
        <tr>
          <td>displayGenerateEventBox</td>
          <td> <xsl:value-of select="d:displayGenerateEventBox" /> </td>
        </tr>
        <tr>
          <td>dataEntryType</td>
          <td> <xsl:value-of select="d:dataEntryType" /> </td>
        </tr>
        <tr>
          <td>defaultTemplateMessage</td>
          <td> <xsl:value-of select="d:defaultTemplateMessage" /> </td>
        </tr>
      </table>

      <xsl:apply-templates select="d:program|d:programStageDataElements" mode="short" />
    </div>
  </xsl:template>

  <xsl:template match="d:programStages" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>ProgramStages</h3>
      <table class="programStages">
        <xsl:apply-templates mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

  <xsl:template match="d:programStageDataElements" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>ProgramStage DataElements</h3>
        <xsl:for-each select="d:programStageDataElement">
          <table>
            <tr>
              <td>Compulsory</td>
              <td colspan="4"> <xsl:value-of select="d:compulsory" /> </td>
            </tr>
            <tr>
              <td>allowProvidedElsewhere</td>
              <td colspan="4"> <xsl:value-of select="d:allowProvidedElsewhere" /> </td>
            </tr>
            <tr>
              <td>displayInReports</td>
              <td colspan="4"> <xsl:value-of select="d:displayInReports" /> </td>
            </tr>
            <xsl:call-template name="local-de" />
          </table>
          <br />
        </xsl:for-each>
    </xsl:if>
  </xsl:template>

  <xsl:template name="local-de">
    <tr>
      <td> <xsl:value-of select="d:dataElement/@name"/> </td>
      <td> <a href="../dataElements/{d:dataElement/@id}">html</a> </td>
      <td> <a href="../dataElements/{d:dataElement/@id}.xml">xml</a> </td>
      <td> <a href="../dataElements/{d:dataElement/@id}.json">json</a> </td>
      <td> <a href="../dataElements/{d:dataElement/@id}.jsonp">jsonp</a> </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
