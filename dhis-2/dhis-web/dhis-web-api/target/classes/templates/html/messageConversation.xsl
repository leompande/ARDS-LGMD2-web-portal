<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:messageConversation">
    <div class="messageConversation">
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
      </table>

      <xsl:apply-templates select="d:lastSender|d:messages" />

    </div>
  </xsl:template>

  <xsl:template match="d:lastSender">
    <h3>Last Sender</h3>
    <table border="1" class="lastSender">
      <xsl:apply-templates select="." mode="row"/>
    </table>
  </xsl:template>

  <xsl:template match="d:messages">
    <h3>Messages</h3>
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="d:message">
    <table>
      <tr>
        <td style="width: 80px;">Text</td>
        <td style="width: 800px;"> <xsl:value-of select="@name" /> </td>
      </tr>
    </table>
    <br/>
  </xsl:template>

</xsl:stylesheet>
