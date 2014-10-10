package org.hisp.dhis.ihrissyncmanager.scheduledtasks;

import org.hisp.dhis.hierarchy.HierarchyViolationException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Date;

/**
 * Gaurav<gaurav08021@gmail.com>, 9/13/12 [1:21 PM]
 */

public class SyncIHRISOrgTreeService implements Runnable{


    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private OrganisationUnitService organisationUnitService;

    private CurrentUserService currentUserService;

    public void setCurrentUserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) {
        this.organisationUnitService = organisationUnitService;
    }


    public void createOrgUnitMaps() {


        System.out.println("* INFO: CREATING MAPPING TABLES");

        int countTables = 0;

        if (jdbcTemplate.queryForList("SHOW tables LIKE \"country_mapping\"").size() == 0) {
            jdbcTemplate.update("CREATE TABLE country_mapping (" +
                    "id int AUTO_INCREMENT, " +
                    "ihrisid varchar(20) NOT NULL, " +
                    "dhisid int NOT NULL, " +
                    "status char, " +
                    "PRIMARY KEY (id)" +
                    ")");
        }

        if (jdbcTemplate.queryForList("SHOW tables LIKE \"region_mapping\"").size() == 0) {
            jdbcTemplate.update("CREATE TABLE region_mapping (" +
                    "id int AUTO_INCREMENT, " +
                    "ihrisid varchar(20) NOT NULL, " +
                    "dhisid int NOT NULL, " +
                    "status char, " +
                    "PRIMARY KEY (id)" +
                    ")");
        }

        if (jdbcTemplate.queryForList("SHOW tables LIKE \"district_mapping\"").size() == 0) {
            jdbcTemplate.update("CREATE TABLE district_mapping (" +
                    "id int AUTO_INCREMENT, " +
                    "ihrisid varchar(20) NOT NULL, " +
                    "dhisid int NOT NULL, " +
                    "status char, " +
                    "PRIMARY KEY (id)" +
                    ")");
        }


        if (jdbcTemplate.queryForList("SHOW tables LIKE \"county_mapping\"").size() == 0) {
            jdbcTemplate.update("CREATE TABLE county_mapping (" +
                    "id int AUTO_INCREMENT, " +
                    "ihrisid varchar(20) NOT NULL, " +
                    "dhisid int NOT NULL, " +
                    "status char, " +
                    "PRIMARY KEY (id)" +
                    ")");


        }
    }

    public void replicateIhrisOrgTree() {

        System.out.println("\n\n* INFO: Stating the iHRIS tree replication process.\n\n");

        createOrgUnitMaps();

        String countryQuery = "SELECT name,id FROM hippo_country";

        SqlRowSet countryRowSet = jdbcTemplate.queryForRowSet(countryQuery);

        while (countryRowSet.next()) {

            String countryName = countryRowSet.getString(1);

            OrganisationUnit country = organisationUnitService.getOrganisationUnitByName(countryName);

            if (country == null) {
                OrganisationUnit newIHRISOrgUnit = new OrganisationUnit();

                newIHRISOrgUnit.setName(countryName);

                newIHRISOrgUnit.setShortName(countryName);

                newIHRISOrgUnit.setActive(true);

                organisationUnitService.addOrganisationUnit(newIHRISOrgUnit);

                int newIHRISOrgUnitId = newIHRISOrgUnit.getId();

                jdbcTemplate.update("INSERT INTO country_mapping (ihrisid, dhisid, status) VALUES (" + "\"" + countryRowSet.getString(2) + "\", " + "\"" + newIHRISOrgUnitId + "\", " + " \'S\' )");
            }

            String regionQuery = "SELECT name,id FROM hippo_region where country=\"" + countryRowSet.getString(2) + "\"";

            SqlRowSet regionRowSet = jdbcTemplate.queryForRowSet(regionQuery);

            while (regionRowSet.next()) {

                String regionName = regionRowSet.getString(1);

                OrganisationUnit region = organisationUnitService.getOrganisationUnitByName(regionName);

                OrganisationUnit regionParent = organisationUnitService.getOrganisationUnitByName(countryName);

                if (region == null) {
                    OrganisationUnit newIHRISOrgUnit = new OrganisationUnit();

                    newIHRISOrgUnit.setName(regionName);

                    newIHRISOrgUnit.setShortName(regionName);

                    newIHRISOrgUnit.setParent(regionParent);

                    newIHRISOrgUnit.setActive(true);

                    organisationUnitService.addOrganisationUnit(newIHRISOrgUnit);

                    int newIHRISOrgUnitId = newIHRISOrgUnit.getId();

                    jdbcTemplate.update("INSERT INTO region_mapping (ihrisid, dhisid, status) VALUES (" + "\"" + regionRowSet.getString(2) + "\", " + "\"" + newIHRISOrgUnitId + "\", " + " \'S\' )");
                }

                String districtQuery = "SELECT name,id FROM hippo_district where region=\"" + regionRowSet.getString(2) + "\"";

                SqlRowSet districtRowSet = jdbcTemplate.queryForRowSet(districtQuery);

                while (districtRowSet.next()) {

                    String districtName = districtRowSet.getString(1);

                    OrganisationUnit district = organisationUnitService.getOrganisationUnitByName(districtName);

                    OrganisationUnit districtParent = organisationUnitService.getOrganisationUnitByName(regionName);

                    if (district == null) {
                        OrganisationUnit newIHRISOrgUnit = new OrganisationUnit();

                        newIHRISOrgUnit.setName(districtName);

                        newIHRISOrgUnit.setShortName(districtName);

                        newIHRISOrgUnit.setParent(districtParent);

                        newIHRISOrgUnit.setActive(true);

                        organisationUnitService.addOrganisationUnit(newIHRISOrgUnit);

                        int newIHRISOrgUnitId = newIHRISOrgUnit.getId();

                        jdbcTemplate.update("INSERT INTO district_mapping (ihrisid, dhisid, status) VALUES (" + "\"" + districtRowSet.getString(2) + "\", " + "\"" + newIHRISOrgUnitId + "\", " + " \'S\' )");
                    }

                    String countyQuery = "SELECT name,id FROM hippo_county where district=\"" + districtRowSet.getString(2) + "\"";

                    SqlRowSet countyRowSet = jdbcTemplate.queryForRowSet(countyQuery);

                    while (countyRowSet.next()) {

                        String countyName = countyRowSet.getString(1);

                        OrganisationUnit county = organisationUnitService.getOrganisationUnitByName(countyName);

                        OrganisationUnit countyParent = organisationUnitService.getOrganisationUnitByName(districtName);

                        if (county == null) {
                            OrganisationUnit newIHRISOrgUnit = new OrganisationUnit();

                            newIHRISOrgUnit.setName(countyName);

                            newIHRISOrgUnit.setShortName(countyName);

                            newIHRISOrgUnit.setParent(countyParent);

                            newIHRISOrgUnit.setActive(true);

                            organisationUnitService.addOrganisationUnit(newIHRISOrgUnit);

                            int newIHRISOrgUnitId = newIHRISOrgUnit.getId();

                            jdbcTemplate.update("INSERT INTO county_mapping (ihrisid, dhisid, status) VALUES (" + "\"" + countyRowSet.getString(2) + "\", " + "\"" + newIHRISOrgUnitId + "\", " + " \'S\' )");
                        }
                    }

                }
            }
        }

    }


    public void deleteIhrisOrgTreeFromDhis() {

        System.out.println("\n\n* INFO: Stating the iHRIS tree truncation process.\n\n");

        String delCounties = "SELECT dhisid FROM county_mapping";

        SqlRowSet delCountySet = jdbcTemplate.queryForRowSet(delCounties);

        while (delCountySet.next())
        {
            int dhisID = delCountySet.getInt(1);

            OrganisationUnit organisationUnitToDelete = organisationUnitService.getOrganisationUnit(dhisID);

            if (organisationUnitToDelete != null) {
                try {
                    organisationUnitService.deleteOrganisationUnit(organisationUnitToDelete);
                } catch (HierarchyViolationException e) {
                    System.out.println("* ERROR :HierarchyViolationException while deleting [ " + organisationUnitToDelete.getName() + "]");
                }
            }
        }

        jdbcTemplate.update("DROP TABLE county_mapping");

        String delDistricts = "SELECT dhisid FROM district_mapping";

        SqlRowSet delDistrictSet = jdbcTemplate.queryForRowSet(delDistricts);

        while (delDistrictSet.next()) {
            int dhisID = delDistrictSet.getInt(1);

            OrganisationUnit organisationUnitToDelete = organisationUnitService.getOrganisationUnit(dhisID);

            if (organisationUnitToDelete != null) {
                try {
                    organisationUnitService.deleteOrganisationUnit(organisationUnitToDelete);
                } catch (HierarchyViolationException e) {
                    System.out.println("* ERROR :HierarchyViolationException while deleting [ " + organisationUnitToDelete.getName() + "]");
                }
            }
        }

        jdbcTemplate.update("DROP TABLE district_mapping");

        String delRegion = "SELECT dhisid FROM region_mapping";

        SqlRowSet delRegionSet = jdbcTemplate.queryForRowSet(delRegion);

        while (delRegionSet.next()) {
            int dhisID = delRegionSet.getInt(1);

            OrganisationUnit organisationUnitToDelete = organisationUnitService.getOrganisationUnit(dhisID);

            if (organisationUnitToDelete != null) {
                try {
                    organisationUnitService.deleteOrganisationUnit(organisationUnitToDelete);
                } catch (HierarchyViolationException e) {
                    System.out.println("* ERROR :HierarchyViolationException while deleting [ " + organisationUnitToDelete.getName() + "]");
                }
            }
        }

        jdbcTemplate.update("DROP TABLE region_mapping");

        String delCountry = "SELECT dhisid FROM country_mapping";

        SqlRowSet delCountrySet = jdbcTemplate.queryForRowSet(delCountry);

        while (delCountrySet.next()) {
            int dhisID = delCountrySet.getInt(1);

            OrganisationUnit organisationUnitToDelete = organisationUnitService.getOrganisationUnit(dhisID);

            if (organisationUnitToDelete != null) {
                try {
                    organisationUnitService.deleteOrganisationUnit(organisationUnitToDelete);
                } catch (HierarchyViolationException e) {
                    System.out.println("* ERROR :HierarchyViolationException while deleting [ " + organisationUnitToDelete.getName() + "]");
                }
            }
        }

        jdbcTemplate.update("DROP TABLE country_mapping");

        replicateIhrisOrgTree();

    }


    public void checkForIhrisOrgTreeUpdate() {


        System.out.println("\n* NOTICE: IHRIS ORGANISATION UNIT SYNC PROCESS STARTED @[" + new Date() + "]");


        if(jdbcTemplate.queryForList("SHOW tables LIKE \"%_mapping\"").size() < 4)
        {
            System.out.println("* NOTE: Mapping tables FOUND.");
            replicateIhrisOrgTree();
        }

        else
        {

            String checkCounty = "SELECT dhisid,ihrisid FROM county_mapping";

            SqlRowSet checkCountySet = jdbcTemplate.queryForRowSet(checkCounty);

            while (checkCountySet.next()) {

                int dhisID = checkCountySet.getInt(1);

                String ihrisID = checkCountySet.getString(2);

                String getIhrisDetailsQuery = "SELECT name,district FROM hippo_county WHERE id=" + "\"" + ihrisID + "\"";

                SqlRowSet ihrisDetailSet = jdbcTemplate.queryForRowSet(getIhrisDetailsQuery);

                String ihrisOUName = null;

                String ihrisParentName = null;

                while (ihrisDetailSet.next()) {
                    ihrisOUName = ihrisDetailSet.getString(1).trim();

                    String ihrisOUParentQuery = "SELECT name FROM hippo_district WHERE id=" + "\"" + ihrisDetailSet.getString(2) + "\"";

                    SqlRowSet ihrisParentDetailSet = jdbcTemplate.queryForRowSet(ihrisOUParentQuery);

                    while (ihrisParentDetailSet.next())
                    {
                        ihrisParentName = ihrisParentDetailSet.getString(1);
                    }
                }

                OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(dhisID);

                if(organisationUnit==null || ihrisOUName==null || ihrisParentName ==null)
                {
                    System.out.println("* INFO: REFERENCED ORG. UNIT MISSING");
                    deleteIhrisOrgTreeFromDhis();
                    return;
                }

                String dhisOrgUnitName = organisationUnit.getName();

                OrganisationUnit organisationUnitParent = organisationUnit.getParent();

                String parentName;

                if(organisationUnitParent!=null){

                    parentName = organisationUnitParent.getName();

                }
                else{

                    System.out.println("* INFO: REFERENCED ORG. UNIT PARENT MISSING");
                    deleteIhrisOrgTreeFromDhis();
                    return;

                }

                if (!((dhisOrgUnitName.trim()).equalsIgnoreCase(ihrisOUName.trim()) && (parentName.trim()).equalsIgnoreCase(ihrisParentName.trim()))) {

                    System.out.println("* INFO: MIS-MATCH FOUND IN ["+dhisOrgUnitName+"("+ihrisOUName+")"+" ,"+parentName+"("+ihrisParentName+")"+"]");
                    deleteIhrisOrgTreeFromDhis();
                    return;
                }


            }


            String checkDistrict = "SELECT dhisid,ihrisid FROM district_mapping";

            SqlRowSet checkDistrictSet = jdbcTemplate.queryForRowSet(checkDistrict);

            while (checkDistrictSet.next()) {

                int dhisID = checkDistrictSet.getInt(1);

                String ihrisID = checkDistrictSet.getString(2);

                String getIhrisDetailsQuery = "SELECT name,region FROM hippo_district WHERE id=" + "\"" + ihrisID + "\"";

                SqlRowSet ihrisDetailSet = jdbcTemplate.queryForRowSet(getIhrisDetailsQuery);

                String ihrisOUName = null;

                String ihrisParentName = null;

                while (ihrisDetailSet.next()) {
                    ihrisOUName = ihrisDetailSet.getString(1).trim();

                    String ihrisOUParentQuery = "SELECT name FROM hippo_region WHERE id=" + "\"" + ihrisDetailSet.getString(2) + "\"";

                    SqlRowSet ihrisParentDetailSet = jdbcTemplate.queryForRowSet(ihrisOUParentQuery);

                    while (ihrisParentDetailSet.next()) {
                        ihrisParentName = ihrisParentDetailSet.getString(1);
                    }
                }

                OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(dhisID);

                if(organisationUnit==null || ihrisOUName==null || ihrisParentName ==null)
                {
                    System.out.println("* INFO: REFERENCED ORG. UNIT MISSING");
                    deleteIhrisOrgTreeFromDhis();
                    return;
                }

                String dhisOrgUnitName = organisationUnit.getName();

                OrganisationUnit organisationUnitParent = organisationUnit.getParent();

                String parentName;

                if(organisationUnitParent!=null){

                    parentName = organisationUnitParent.getName();

                }
                else{

                    System.out.println("* INFO: REFERENCED ORG. UNIT PARENT MISSING");
                    deleteIhrisOrgTreeFromDhis();
                    return;

                }

                if (!((dhisOrgUnitName.trim()).equalsIgnoreCase(ihrisOUName.trim()) && (parentName.trim()).equalsIgnoreCase(ihrisParentName.trim()))) {

                    System.out.println("* INFO: MIS-MATCH FOUND IN ["+dhisOrgUnitName+"("+ihrisOUName+")"+" ,"+parentName+"("+ihrisParentName+")"+"]");
                    deleteIhrisOrgTreeFromDhis();
                    return;
                }
            }


            String checkRegion = "SELECT dhisid,ihrisid FROM region_mapping";

            SqlRowSet checkRegionSet = jdbcTemplate.queryForRowSet(checkRegion);

            while (checkRegionSet.next()) {

                int dhisID = checkRegionSet.getInt(1);

                String ihrisID = checkRegionSet.getString(2);

                String getIhrisDetailsQuery = "SELECT name,country FROM hippo_region WHERE id=" + "\"" + ihrisID + "\"";

                SqlRowSet ihrisDetailSet = jdbcTemplate.queryForRowSet(getIhrisDetailsQuery);

                String ihrisOUName = null;

                String ihrisParentName = null;

                while (ihrisDetailSet.next()) {
                    ihrisOUName = ihrisDetailSet.getString(1).trim();

                    String ihrisOUParentQuery = "SELECT name FROM hippo_country WHERE id=" + "\"" + ihrisDetailSet.getString(2) + "\"";

                    SqlRowSet ihrisParentDetailSet = jdbcTemplate.queryForRowSet(ihrisOUParentQuery);

                    while (ihrisParentDetailSet.next()) {
                        ihrisParentName = ihrisParentDetailSet.getString(1);
                    }
                }

                OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(dhisID);

                if(organisationUnit==null || ihrisOUName==null || ihrisParentName ==null)
                {
                    System.out.println("* INFO: REFERENCED ORG. UNIT MISSING");
                    deleteIhrisOrgTreeFromDhis();
                    return;
                }

                String dhisOrgUnitName = organisationUnit.getName();

                OrganisationUnit organisationUnitParent = organisationUnit.getParent();

                String parentName;

                if(organisationUnitParent!=null){

                   parentName = organisationUnitParent.getName();

                }
                else{

                    System.out.println("* INFO: REFERENCED ORG. UNIT PARENT MISSING");
                    deleteIhrisOrgTreeFromDhis();
                    return;

                }

                if (!((dhisOrgUnitName.trim()).equalsIgnoreCase(ihrisOUName.trim()) && (parentName.trim()).equalsIgnoreCase(ihrisParentName.trim()))) {

                    System.out.println("* INFO: MIS-MATCH FOUND IN ["+dhisOrgUnitName+"("+ihrisOUName+")"+" ,"+parentName+"("+ihrisParentName+")"+"]");
                    deleteIhrisOrgTreeFromDhis();
                    return;
                }
            }


            String checkCountry = "SELECT dhisid,ihrisid FROM country_mapping";

            SqlRowSet checkCountrySet = jdbcTemplate.queryForRowSet(checkCountry);

            while (checkCountrySet.next()) {

                int dhisID = checkCountrySet.getInt(1);

                String ihrisID = checkCountrySet.getString(2);

                String getIhrisDetailsQuery = "SELECT name FROM hippo_country WHERE id=" + "\"" + ihrisID + "\"";

                SqlRowSet ihrisDetailSet = jdbcTemplate.queryForRowSet(getIhrisDetailsQuery);

                String ihrisOUName = null;

                while (ihrisDetailSet.next()) {
                    ihrisOUName = ihrisDetailSet.getString(1).trim();
                }

                OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(dhisID);

                if(organisationUnit==null || ihrisOUName==null)
                {
                    System.out.println("* INFO: REFERENCED ORG. UNIT MISSING");
                    deleteIhrisOrgTreeFromDhis();
                    return;
                }

                String dhisOrgUnitName = organisationUnit.getName();

                if (!((dhisOrgUnitName.trim()).equalsIgnoreCase(ihrisOUName.trim()))) {

                    System.out.println("* INFO: MIS-MATCH FOUND IN ["+dhisOrgUnitName+"("+ihrisOUName+")"+"]");
                    deleteIhrisOrgTreeFromDhis();
                    return;
                }
            }


        }

        System.out.println("* NOTICE: IHRIS ORGANISATION UNIT SYNC PROCESS FINISHED @[" + new Date() + "]\n");
    }


    @Override
    @Transactional
    public void run()
    {
        checkForIhrisOrgTreeUpdate();
    }
}
