<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:d="http://dhis2.org/schema/dxf/2.0"
    >

<xsl:template match="d:messageConversations">

<h3> <xsl:value-of select="local-name()" /> </h3>

<h4>Starting a new conversation</h4>
<p>This is done through a POST to <code>/api/messageConversations</code> using the format below.</p>

<pre style="font-size: 12pt;">
<![CDATA[
<message xmlns="http://dhis2.org/schema/dxf/2.0">
  <subject>This is the subject</subject>
  <text>This is the text</text>
  <users>
    <user id="user1ID" />
    <user id="user2ID" />
    <user id="user3ID" />
  </users>
</message>
]]>
</pre>

<p>Example using curl:<br/>
<code>curl -d @input.xml "http://localhost:8080/api/messageConversations" -H "Content-Type:application/xml" -u admin:district -X POST -v</code>
</p>

<h4>Replying to a conversation</h4>

<p> This is done with a POST to <code>/api/messageConversations/{conversation-id}</code>. The request body will be the reply text.</p>

<p>Example using curl:<br/>
<code>curl -d "This is my reply" "http://localhost:8080/api/messageConversations/adfAd134GTh" -H "Content-Type:text/plain" -u admin:district -X POST -v</code>
</p>

<h4>Writing a feedback</h4>

<p>This is done with a POST to <code>/api/messageConversations/feedback</code></p>

<p>Example using curl:<br/>
<code>curl -d "This is my feedback" "http://localhost:8080/api/messageConversations/feedback?subject={message-subject}" -H "Content-Type:text/plain" -u admin:district -X POST -v</code>
</p>
    
<table>
  <xsl:apply-templates select="child::*" mode="row" />
</table>

</xsl:template>

</xsl:stylesheet>
