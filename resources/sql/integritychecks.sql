
-- Get name of datasets for a dataelement

select ds.name from dataset ds
join datasetmembers dm on (ds.datasetid=dm.datasetid) 
join dataelement de on (dm.dataelementid=de.dataelementid)
where de.name = 'Adverse Events Following Immunization';

-- Get dataelements not part of any dataset

select dataelementid, name from dataelement where dataelementid not in (
select dataelementid from datasetmembers)
and domaintype='aggregate'
order by name;

-- Get category combo with no data elements

select cc.categorycomboid, cc.name from categorycombo cc where cc.categorycomboid not in (
select distinct categorycomboid from dataelement);

-- Get dataelement name and category combo for a section

select de.name as dataelementname, cc.name as categorycomboname from dataelement de
join categorycombo cc on(de.categorycomboid=cc.categorycomboid)
join sectiondataelements sd on(de.dataelementid=sd.dataelementid)
join section sc on(sd.sectionid=sc.sectionid)
where sc.name = 'OPD Diagnoses';

-- Get data elements and number of data values sorted ascending

select distinct d.dataelementid, d.name, count(v.*) as cnt from datavalue v 
join dataelement d on(v.dataelementid=d.dataelementid) 
group by d.dataelementid, d.name 
order by cnt asc;

-- Get dataset memberships for data elements with more than one membership

select de.name, ds.name from dataelement de
join datasetmembers dm on(de.dataelementid=dm.dataelementid)
join dataset ds on(dm.datasetid=ds.datasetid)
where de.dataelementid in (
  select de.dataelementid from dataelement de
  join datasetmembers ds on (de.dataelementid=ds.dataelementid)
  group by de.dataelementid
  having(count(de.dataelementid) > 1) )
order by de.name;

-- Get dataelements which are members of a section but not the section's dataset

select de.name as dataelementname, sc.name as sectionname, ds.name as datasetname from sectiondataelements sd
join dataelement de on(sd.dataelementid=de.dataelementid)
join section sc on (sd.sectionid=sc.sectionid)
join dataset ds on (sc.datasetid=ds.datasetid)
where sd.dataelementid not in (
  select dm.dataelementid from datasetmembers dm
  join dataset ds on(dm.datasetid=ds.datasetid)
  where sc.datasetid=ds.datasetid)
order by ds.name, de.name;

-- Get categories with category memberships

select co.name, c.name from dataelementcategory c 
join categories_categoryoptions using(categoryid) 
join dataelementcategoryoption co using(categoryoptionid) order by co.name, c.name;

-- Get orgunit groups which an orgunit is member of

select * from orgunitgroup g
join orgunitgroupmembers m using(orgunitgroupid)
join organisationunit o using (organisationunitid)
where o.name = 'Mandera District Hospital';

-- Get reports which uses report table

select * from report r
join reportreporttables rr using(reportid)
join reporttable t using(reporttableid)
where t.name='Indicators';

-- Show collection frequency of data elements with average aggregation operator

select distinct de.name, periodtype.name 
from dataelement de 
join datasetmembers using (dataelementid) 
join dataset using (datasetid) 
join periodtype using(periodtypeid) 
where de.aggregationtype = 'average';

-- Recreate indexes on aggregated tables

drop index aggregateddatavalue_index;
drop index aggregatedindicatorvalue_index;
drop index aggregateddatasetcompleteness_index;
create index aggregateddatavalue_index on aggregateddatavalue (dataelementid, periodid, organisationunitid, categoryoptioncomboid, value);
create index aggregatedindicatorvalue_index on aggregatedindicatorvalue (indicatorid, periodid, organisationunitid, value);
create index aggregateddatasetcompleteness_index on aggregateddatasetcompleteness  (datasetid, periodid, organisationunitid, value);

-- Get category option combos without category options

select * from categoryoptioncombo where categoryoptioncomboid not in (select distinct categoryoptioncomboid from categoryoptioncombos_categoryoptions);

-- Get category option combos without category combo

select * from categoryoptioncombo where categoryoptioncomboid not in (select distinct categoryoptioncomboid from categorycombos_optioncombos);

-- Get category options without category option combos

select * from dataelementcategoryoption where categoryoptionid not in (select distinct categoryoptionid from categoryoptioncombos_categoryoptions);

-- Get catetegory options without categories

select * from dataelementcategoryoption where categoryoptionid not in (select distinct categoryoptionid from categories_categoryoptions);

-- Get categories without category options

select * from dataelementcategory where categoryid not in (select distinct categoryid from categories_categoryoptions);

-- Get categories without category combos (not an error but could be removed)

select * from dataelementcategory where categoryid not in (select distinct categoryid from categorycombos_categories);

-- Get category combos without categories

select * from categorycombo where categorycomboid not in (select distinct categorycomboid from categorycombos_categories);

-- Get category options with count of memberships in categories

select cc.categoryoptionid, co.name, (
select count(categoryoptionid) from categories_categoryoptions where categoryoptionid=cc.categoryoptionid )
as categorycount from categories_categoryoptions as cc join dataelementcategoryoption co on(cc.categoryoptionid=co.categoryoptionid) order by categorycount desc;

-- Get category option combos without data values (not an error)

select * from categoryoptioncombo where categoryoptioncomboid not in (select distinct categoryoptioncomboid from datavalue);

-- Category combo and option combo overview

select c.name as categorycombo, n.categoryoptioncomboname, n.categoryoptioncomboid  from _categoryoptioncomboname n 
join categorycombos_optioncombos co on (n.categoryoptioncomboid=co.categoryoptioncomboid)
join categorycombo c on (co.categorycomboid=c.categorycomboid)
order by c.name, n.categoryoptioncomboname;

-- Get category combinations without data elements

select * from categorycombo where categorycomboid not in (select distinct categorycomboid from dataelement);

