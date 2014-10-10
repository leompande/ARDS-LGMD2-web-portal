<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:userCredentials" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>UserCredentials</h3>
      <table class="userCredentials">
        <tr>
          <td>Username</td>
          <td> <xsl:value-of select="d:username" /> </td>
        </tr>
      </table>

      <xsl:apply-templates select="d:userAuthorityGroups" mode="short" />
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
