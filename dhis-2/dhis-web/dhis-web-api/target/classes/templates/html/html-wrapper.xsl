<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

  <xsl:output method="html" doctype-system="about:legacy-compat"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>DHIS Web-API</title>
        <style type="text/css">
        html {
          font-family: sans-serif;
          font-size: 12pt;
        }
        table {
          border-collapse: collapse;
		}
		table, th, td {
		  border: 1px solid #c0c0c0;
          padding: 3px;
        }
		h1, h2, h3 {
          text-transform: capitalize;
        }
		</style>
      </head>

      <body>
        <xsl:apply-templates />
      </body>

    </html>
  </xsl:template>

</xsl:stylesheet>
