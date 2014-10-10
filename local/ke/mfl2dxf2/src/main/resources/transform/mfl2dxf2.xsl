<?xml version="1.0" encoding="UTF-8"?>

<stylesheet version="1.0" xmlns="http://www.w3.org/1999/XSL/Transform" xmlns:mfl="http://ehealth.or.ke/schema"
    xmlns:dxf2="http://dhis2.org/schema/dxf/2.0">

  <template match="mfl:Facilities">

    <element name="dxf2:metaData">
      <element name="dxf2:organisationUnits">
        <apply-templates />
      </element>
    </element>
  </template>

  <template match="mfl:Facility">
    <element name="dxf2:organisationUnit">
      <attribute name="name">
        <value-of select="mfl:OfficialName" />
      </attribute>
      <attribute name="shortName">
        <value-of select="substring(mfl:OfficialName, 0, 49)" />
      </attribute>
      <attribute name="code">
        <value-of select="mfl:Code" />
      </attribute>

      <element name="dxf2:parent">
        <attribute name="code">
          <value-of select="mfl:District" />
        </attribute>
      </element>

      <element name="dxf2:active">
        <value-of select="mfl:Active" />
      </element>

      <if test="mfl:LocationDescription">
        <element name="dxf2:description">
          <value-of select="mfl:LocationDescription" />
        </element>
      </if>

      <if test="mfl:Latitude and mfl:Longitude">
        <element name="dxf2:featureType">
          <text>Point</text>
        </element>

        <element name="dxf2:coordinates">
          <text>[</text>
          <value-of select="mfl:Longitude" />
          <text>,</text>
          <value-of select="mfl:Latitude" />
          <text>]</text>
        </element>
      </if>

      <if test="string-length(mfl:OfficialEmail) > 0">
        <element name="dxf2:email">
          <value-of select="mfl:OfficialEmail" />
        </element>
      </if>

      <choose>
        <when test="string-length(mfl:OfficialLandline) > 0">
          <element name="dxf2:phoneNumber">
            <value-of select="mfl:OfficalLandline" />
          </element>
        </when>
        <when test="string-length(mfl:OfficialMobile) > 0">
          <element name="dxf2:phoneNumber">
            <value-of select="mfl:OfficialMobile" />
          </element>
        </when>
      </choose>

      <if
          test="string-length(mfl:AddressBox) > 0 and string-length(mfl:AddressTown) > 0 and string-length(mfl:AddressPostCode) > 0">
        <element name="dxf2:address">
          <value-of select="mfl:AddressBox" />
          <text>, </text>
          <value-of select="mfl:AddressPostCode" />
          <text> </text>
          <value-of select="mfl:AddressTown" />
        </element>
      </if>

    </element>
  </template>

</stylesheet>
