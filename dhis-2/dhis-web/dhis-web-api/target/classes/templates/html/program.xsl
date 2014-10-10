<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:program">
    <div class="program">
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
          <td>version</td>
          <td> <xsl:value-of select="d:version" /> </td>
        </tr>
        <tr>
          <td>dateOfEnrollmentDescription</td>
          <td> <xsl:value-of select="d:dateOfEnrollmentDescription" /> </td>
        </tr>
        <tr>
          <td>dateOfIncidentDescription</td>
          <td> <xsl:value-of select="d:dateOfIncidentDescription" /> </td>
        </tr>
        <tr>
          <td>Type</td>
          <td> <xsl:value-of select="d:type" /> </td>
        </tr>
        <tr>
          <td>displayProvidedOtherFacility</td>
          <td> <xsl:value-of select="d:displayProvidedOtherFacility" /> </td>
        </tr>
        <tr>
          <td>displayIncidentDate</td>
          <td> <xsl:value-of select="d:displayIncidentDate" /> </td>
        </tr>
        <tr>
          <td>generatedByEnrollmentDate</td>
          <td> <xsl:value-of select="d:generatedByEnrollmentDate" /> </td>
        </tr>
        <tr>
          <td>ignoreOverdueEvents</td>
          <td> <xsl:value-of select="d:ignoreOverdueEvents" /> </td>
        </tr>
        <tr>
          <td>blockEntryForm</td>
          <td> <xsl:value-of select="d:blockEntryForm" /> </td>
        </tr>
        <tr>
          <td>onlyEnrollOnce</td>
          <td> <xsl:value-of select="d:onlyEnrollOnce" /> </td>
        </tr>
        <tr>
          <td>remindCompleted</td>
          <td> <xsl:value-of select="d:remindCompleted" /> </td>
        </tr>
        <tr>
          <td>disableRegistrationFields</td>
          <td> <xsl:value-of select="d:disableRegistrationFields" /> </td>
        </tr>
        <tr>
          <td>displayOnAllOrgunit</td>
          <td> <xsl:value-of select="d:displayOnAllOrgunit" /> </td>
        </tr>
        <tr>
          <td>singleEvent</td>
          <td> <xsl:value-of select="d:singleEvent" /> </td>
        </tr>
        <tr>
          <td>registration</td>
          <td> <xsl:value-of select="d:registration" /> </td>
        </tr>
      </table>

      <xsl:apply-templates select="d:programStages|d:userRoles" mode="short" />
      <xsl:apply-templates select="d:organisationUnits" mode="short" />
    </div>
  </xsl:template>

  <xsl:template match="d:program" mode="short">
    <h3>Program</h3>
    <table>
      <xsl:apply-templates select="." mode="row"/>
    </table>
  </xsl:template>

  <xsl:template match="d:programs" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Programs</h3>
      <table class="programs">
        <xsl:apply-templates mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
