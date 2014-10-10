<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:template match="d:user">
    <div class="user">
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
          <td>First Name</td>
          <td> <xsl:value-of select="d:firstName" /> </td>
        </tr>
        <tr>
          <td>Surname</td>
          <td> <xsl:value-of select="d:surname" /> </td>
        </tr>
        <tr>
          <td>E-Mail</td>
          <td> <xsl:value-of select="d:email" /> </td>
        </tr>
        <tr>
          <td>PhoneNumber</td>
          <td> <xsl:value-of select="d:phoneNumber" /> </td>
        </tr>
      </table>

      <xsl:apply-templates select="d:userCredentials|d:organisationUnits" mode="short" />
    </div>
  </xsl:template>

  <xsl:template match="d:users" mode="short">
    <xsl:if test="count(child::*) > 0">
      <h3>Users</h3>
      <table class="users">
        <xsl:apply-templates select="child::*" mode="row"/>
      </table>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
