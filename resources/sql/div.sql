
-- Delete all data values for category combo

delete from datavalue where categoryoptioncomboid in (
select cc.categoryoptioncomboid from categoryoptioncombo cc
join categorycombos_optioncombos co
on (cc.categoryoptioncomboid=co.categoryoptioncomboid)
where categorycomboid=12414 );

-- Data elements and frequency with average agg operator (higher than yearly negative for data mart performance)

select d.dataelementid, d.name as dataelement, pt.name as periodtype from dataelement d 
join datasetmembers dsm on d.dataelementid=dsm.dataelementid 
join dataset ds on dsm.datasetid=ds.datasetid 
join periodtype pt on ds.periodtypeid = pt.periodtypeid 
where d.aggregationtype = 'average'
order by pt.name;

-- Data elements with aggregation levels

select d.dataelementid, d.name, dal.aggregationlevel from dataelementaggregationlevels dal 
join dataelement d on dal.dataelementid=d.dataelementid 
order by name, aggregationlevel;

-- Data elements with less than 100 data values

select de.dataelementid, de.name, (select count(*) from datavalue dv where de.dataelementid=dv.dataelementid) as count 
from dataelement de
where (select count(*) from datavalue dv where de.dataelementid=dv.dataelementid) < 100
order by count;

-- Number of data elements with less than 100 data values

select count(*) from dataelement de
where (select count(*) from datavalue dv where de.dataelementid=dv.dataelementid) < 100;

-- Duplicate codes

select code, count(code) as count
from dataelement
group by code
order by count desc;

-- Exploded category option combo view

select cc.categorycomboid, cc.name as categorycomboname, cn.* from _categoryoptioncomboname cn
join categorycombos_optioncombos co using(categoryoptioncomboid)
join categorycombo cc using(categorycomboid)
order by categorycomboname, categoryoptioncomboname;

-- Groups orgunits into groups based on the text match in the where clause for the orgunit group with the given id

insert into orgunitgroupmembers(orgunitgroupid,organisationunitid)
select 22755 as orgunitgroupid,ou.organisationunitid as organisationunitid from organisationunit ou 
where lower(name) like '%dispensary%'
and not exists (
select orgunitgroupid from orgunitgroupmembers om 
where ou.organisationunitid=om.organisationunitid
and om.orgunitgroupid=22755);

-- Facility overview

select distinct ous.idlevel5 as internalid, ou.uid, ou.code, ou.name, ougs.type, ougs.ownership,
ou2.name as province, ou3.name as county, ou4.name as district, ou.coordinates as longitide_latitude
from _orgunitstructure ous
left join organisationunit ou on ous.organisationunitid=ou.organisationunitid
left join organisationunit ou2 on ous.idlevel2=ou2.organisationunitid
left join organisationunit ou3 on ous.idlevel3=ou3.organisationunitid
left join organisationunit ou4 on ous.idlevel4=ou4.organisationunitid
left join _organisationunitgroupsetstructure ougs on ous.organisationunitid=ougs.organisationunitid
where ous.level=5
order by province, county, district, ou.name;

-- Compare user roles (lists what is in the first role but not in the second)

select authority from userroleauthorities where userroleid=33706 and authority not in (select authority from userroleauthorities where userroleid=21504);

-- User overview (Postgres only)

select u.username, u.lastlogin, u.selfregistered, ui.surname, ui.firstname, ui.email, ui.phonenumber, ui.jobtitle, (
  select array_to_string( array(
    select name from userrole ur
    join userrolemembers urm using(userroleid)
    where urm.userid=u.userid), ', ' )
  )  as userroles, (
  select array_to_string( array(
    select name from organisationunit ou
    join usermembership um using(organisationunitid)
    where um.userinfoid=ui.userinfoid), ', ' )
  ) as orgunits
from users u 
join userinfo ui on u.userid=ui.userinfoid
order by u.username;

-- Explore report tables

select rt.name, rt.paramleafparentorganisationunit as leaf, 
rt.paramgrandparentorganisationunit as grand, rt.paramparentorganisationunit as parent,
(select count(*) from reporttable_dataelements where reporttableid=rt.reporttableid) as de,
(select count(*) from reporttable_datasets where reporttableid=rt.reporttableid) as ds,
(select count(*) from reporttable_indicators where reporttableid=rt.reporttableid) as in,
(select count(*) from reporttable_organisationunits where reporttableid=rt.reporttableid) as ou, 
(select count(*) from reporttable_orgunitgroups where reporttableid=rt.reporttableid) as oug,
(select count(*) from reporttable_periods where reporttableid=rt.reporttableid) as pe
from reporttable rt;

-- Turn longitude/latitude around for organisationunit coordinates (adjust the like clause)

update organisationunit set coordinates=regexp_replace(coordinates,'\[(.+?\..+?),(.+?\..+?)\]','[\2,\1]')
where coordinates like '[0%'
and featuretype='Point';

-- Fetch longitude/latitude from organisationunit

select name, coordinates, 
cast( regexp_replace( coordinates, '\[(-?\d+\.?\d*)[,](-?\d+\.?\d*)\]', '\1' ) as double precision ) as longitude,
cast( regexp_replace( coordinates, '\[(-?\d+\.?\d*)[,](-?\d+\.?\d*)\]', '\2' ) as double precision ) as latitude 
from organisationunit
where featuretype='Point';

-- Nullify coordinates with longitude outside range (adjust where clause values)

update organisationunit set coordinates=null
where featuretype='Point'
and (
  cast(substring(coordinates from '\[(.+?\..+?),.+?\..+?\]') as double precision) < 32
  or cast(substring(coordinates from '\[(.+?\..+?),.+?\..+?\]') as double precision) > 43
);

-- Identify empty groups

select 'Data element group' as type, o.name as name
from dataelementgroup o
where not exists (
  select * from dataelementgroupmembers
  where dataelementgroupid=o.dataelementgroupid)
union all
select 'Indicator group' as type, o.name as name
from indicatorgroup o
where not exists (
  select * from indicatorgroupmembers
  where indicatorgroupid=o.indicatorgroupid)
union all
select 'Organisation unit group' as type, o.name as name
from orgunitgroup o
where not exists (
  select * from orgunitgroupmembers
  where orgunitgroupid=o.orgunitgroupid)
order by type,name;

-- Display overview of data elements and related category option combos

select de.uid as deuid, de.name as dename, coc.uid as cocuid, con.categoryoptioncomboname
from dataelement de
join categorycombos_optioncombos cc using(categorycomboid)
join categoryoptioncombo coc using(categoryoptioncomboid)
join _categoryoptioncomboname con using(categoryoptioncomboid);

-- Display category option combo identifier and name

select cc.categoryoptioncomboid as id, uid, categoryoptioncomboname as name, code
from categoryoptioncombo cc
join _categoryoptioncomboname cn
on (cc.categoryoptioncomboid=cn.categoryoptioncomboid);

-- (Write) Populate dashboards for all users (7666 is userinfoid for target dashboard, replace with preferred id)

insert into usersetting (userinfoid, name, value)
select userinfoid, 'dashboardConfig', (
  select value
  from usersetting
  where userinfoid=7666
  and name='dashboardConfig') as value
from userinfo
where userinfoid not in (
  select userinfoid
  from usersetting
  where name='dashboardConfig')
  
-- (Write) Reset password to "district" for account with given username

update users set password='48e8f1207baef1ef7fe478a57d19f2e5' where username='admin';

-- (Write) Generate random coordinates based on org unit location for events

update programstageinstance psi
set longitude = (
  select ( cast( regexp_replace( coordinates, '\[(-?\d+\.?\d*)[,](-?\d+\.?\d*)\]', '\1' ) as double precision ) + ( random() / 10 ) )
  from organisationunit ou
  where psi.organisationunitid=ou.organisationunitid ),
latitude = (
  select ( cast( regexp_replace( coordinates, '\[(-?\d+\.?\d*)[,](-?\d+\.?\d*)\]', '\2' ) as double precision ) + ( random() / 10 ) )
  from organisationunit ou
  where psi.organisationunitid=ou.organisationunitid );

-- (Write) Move startdate and enddate in period to next year

update period set 
startdate = (startdate + interval '1 year')::date,
enddate = (enddate + interval '1 year')::date
where extract(year from startdate) = 2013;

-- (Write) Move programstageinstance to next year

update programstageinstance set 
duedate = (duedate + interval '1 year'),
executiondate = (executiondate + interval '1 year'),
created = (created + interval '1 year'),
lastupdated = (lastupdated + interval '1 year');

-- (Write) Insert random org unit codes

create function setrandomcode() returns integer AS $$
declare ou integer;
begin
for ou in select organisationunitid from _orgunitstructure where level=6 loop
  execute 'update organisationunit set code=(select substring(cast(random() as text),5,6)) where organisationunitid=' || ou;
end loop;
return 1;
end;
$$ language plpgsql;

select setrandomcode();
