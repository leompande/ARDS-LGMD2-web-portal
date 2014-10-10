<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:d="http://dhis2.org/schema/dxf/2.0"
  >

  <xsl:template match="d:validationRule">
    <div class="validationRule">
      <h2>
        <xsl:value-of select="@name" />
      </h2>
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
          <td>Description</td>
          <td> <xsl:value-of select="d:description" /> </td>
        </tr>
        <tr>
          <td>Operator</td>
          <td> <xsl:value-of select="d:operator" /> </td>
        </tr>
        <tr>
          <td>Type</td>
          <td> <xsl:value-of select="d:type" /> </td>
        </tr>
      </table>

      <xsl:apply-templates select="d:leftSide|d:rightSide|d:validationRuleGroups" mode="short" />
    </div>
  </xsl:template>

  <xsl:template match="d:leftSide" mode="short">
    <h3>LeftSide Expression</h3>
    <table class="expression">
      <tr>
        <td>Description</td>
        <td> <xsl:value-of select="d:description" /> </td>
      </tr>
      <tr>
        <td>Expression</td>
        <td> <xsl:value-of select="d:expression" /> </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="d:rightSide" mode="short">
    <h3>RightSide Expression</h3>
    <table class="expression">
      <tr>
        <td>Description</td>
        <td> <xsl:value-of select="d:description" /> </td>
      </tr>
      <tr>
        <td>Expression</td>
        <td> <xsl:value-of select="d:expression" /> </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="d:validationRules" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>ValidationRules</h3>
      <table class="validationRules">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
