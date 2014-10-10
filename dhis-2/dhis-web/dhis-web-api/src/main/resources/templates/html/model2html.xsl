<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
                exclude-result-prefixes="d"
    >

  <xsl:output method="html" />

  <!-- html page level settings -->
  <xsl:include href="html-wrapper.xsl" />

  <!-- for list views -->
  <xsl:include href="list.xsl" />

  <!-- for rendering elements -->
  <xsl:include href="resource.xsl" />
  <xsl:include href="relativePeriods.xsl" />
  <xsl:include href="map.xsl" />
  <xsl:include href="mapView.xsl" />
  <xsl:include href="mapLegend.xsl" />
  <xsl:include href="mapLegendSet.xsl" />
  <xsl:include href="mapLayer.xsl" />
  <xsl:include href="chart.xsl" />
  <xsl:include href="constant.xsl" />
  <xsl:include href="category.xsl" />
  <xsl:include href="categoryOption.xsl" />
  <xsl:include href="categoryCombo.xsl" />
  <xsl:include href="categoryOptionCombo.xsl" />
  <xsl:include href="dataElement.xsl" />
  <xsl:include href="dataElementGroup.xsl" />
  <xsl:include href="dataElementGroupSet.xsl" />
  <xsl:include href="document.xsl" />
  <xsl:include href="indicator.xsl" />
  <xsl:include href="indicatorType.xsl" />
  <xsl:include href="indicatorGroup.xsl" />
  <xsl:include href="indicatorGroupSet.xsl" />
  <xsl:include href="organisationUnit.xsl" />
  <xsl:include href="organisationUnitLevel.xsl" />
  <xsl:include href="organisationUnitGroup.xsl" />
  <xsl:include href="organisationUnitGroupSet.xsl" />
  <xsl:include href="dataSet.xsl" />
  <xsl:include href="attributeType.xsl" />
  <xsl:include href="report.xsl" />
  <xsl:include href="reportTable.xsl" />
  <xsl:include href="validationRule.xsl" />
  <xsl:include href="validationRuleGroup.xsl" />
  <xsl:include href="sqlView.xsl" />

  <xsl:include href="user.xsl" />
  <xsl:include href="userGroup.xsl" />
  <xsl:include href="userAuthorityGroup.xsl" />
  <xsl:include href="userCredentials.xsl" />
  <xsl:include href="messageConversations.xsl" />
  <xsl:include href="messageConversation.xsl" />
  <xsl:include href="interpretation.xsl" />
  <xsl:include href="dataValueSets.xsl" />
  <xsl:include href="optionSets.xsl" />
  <xsl:include href="program.xsl" />
  <xsl:include href="programStage.xsl" />
  <xsl:include href="event.xsl" />
  <xsl:include href="dashboard.xsl" />

  <!--@author Ovidiu Rosu <rosu.ovi@gmail.com>-->
  <xsl:include href="filter.xsl" />

  <!-- Config elements -->

  <xsl:include href="config/sms.xsl" />

</xsl:stylesheet>
