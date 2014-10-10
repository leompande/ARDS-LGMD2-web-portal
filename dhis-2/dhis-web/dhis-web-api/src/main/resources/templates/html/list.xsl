<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:include href="identifiable-row.xsl" />

  <!-- match all plural elements -->
  <xsl:template match="d:metaData">
    <xsl:apply-templates select="d:pager"/>
    <xsl:apply-templates select="d:*[local-name()!='pager']"/>
  </xsl:template>

  <xsl:template match="d:resources|d:maps|d:mapViews|d:charts|d:categoryCombos|d:categories|
      d:categoryOptions|d:categoryOptionCombos|d:dataElements|d:indicators|
      d:organisationUnits|d:dataElementGroups|d:dataElementGroupSets|d:dataSets|
      d:documents|d:indicatorGroups|d:indicatorGroupSets|d:organisationUnitGroups|
      d:organisationUnitGroupSets|d:indicatorTypes|d:attributeTypes|d:reports|d:constants|
      d:sqlViews|d:validationRules|d:validationRuleGroups|d:users|d:userGroups|d:userAuthorityGroups|
      d:reportTables|d:mapLegends|d:mapLegendSets|d:mapLayers|d:optionSets|d:interpretations|
      d:sections|d:userRoles|d:organisationUnitLevels|d:programs|d:programStages|d:dimensions|d:dashboards">

    <h3>
      <xsl:value-of select="local-name()" />
    </h3>

    <table>
      <xsl:apply-templates select="child::*" mode="row" />
    </table>

  </xsl:template>

  <xsl:template match="d:pager">
      <table>
        <tr>
          <td>Page
            <xsl:choose>
              <xsl:when test="d:page">
                <xsl:value-of select="d:page" />
              </xsl:when>
              <xsl:otherwise>1</xsl:otherwise>
            </xsl:choose>

            <xsl:text> / </xsl:text>

            <xsl:choose>
              <xsl:when test="d:page">
                <xsl:value-of select="d:pageCount" />
              </xsl:when>
              <xsl:otherwise>1</xsl:otherwise>
            </xsl:choose>

          </td>

          <xsl:if test="d:prevPage">
            <td>
              <xsl:element name="a">
                <xsl:attribute name="href">
                  <xsl:value-of select="d:prevPage" />
                </xsl:attribute>
                <xsl:text>Previous Page</xsl:text>
              </xsl:element>
            </td>
          </xsl:if>

          <xsl:if test="d:nextPage">
            <td>
              <xsl:element name="a">
                <xsl:attribute name="href">
                  <xsl:value-of select="d:nextPage" />
                </xsl:attribute>
                <xsl:text>Next Page</xsl:text>
              </xsl:element>
            </td>
          </xsl:if>
        </tr>
      </table>
  </xsl:template>

</xsl:stylesheet>
