package org.hisp.dhis.coldchain.reports;

import java.util.List;
import java.util.Map;

public interface CCEMReportManager
{
    String getOrgunitIdsByComma( List<Integer> selOrgUnitList, List<Integer> orgunitGroupList );
    
    Map<String,String> getCCEMSettings();
    
    List<CCEMReportDesign> getCCEMReportDesign( String designXMLFile );
    
    CCEMReport getCCEMReportByReportId( String selReportId );
    
    Map<String, Integer> getCatalogTypeAttributeValue( String orgUnitIdsByComma, Integer inventoryTypeId, Integer catalogTypeAttributeId );
    
    Map<String, Integer> getCatalogTypeAttributeValueByAge( String orgUnitIdsByComma, Integer inventoryTypeId, Integer catalogTypeAttributeId, Integer yearInvTypeAttId, Integer ageStart, Integer ageEnd );
    
    List<String> getDistinctDataElementValue( Integer dataelementID, Integer optComboId, Integer periodId );
    
    List<Integer> getOrgunitIds( List<Integer> selOrgUnitList, Integer orgUnitGroupId );
    
    Map<String, Integer> getDataValueCountforDataElements( String dataElementIdsByComma, String optComboIdsByComma, Integer periodId, String orgUnitIdsBycomma );
    
    Integer getPeriodId( String startDate, String periodType );
    
    Map<String, Integer> getFacilityWiseEquipmentRoutineData( String orgUnitIdsByComma, String periodIdsByComma, String dataElementIdsByComma, String optComboIdsByComma );
    
    Map<Integer, Double> getCatalogDataSumByEquipmentData( String orgUnitIdsByComma, Integer inventoryTypeId, Integer catalogTypeAttributeId, Integer inventoryTypeAttributeId, String equipmentValue );
    
    Map<Integer, Double> getSumOfEquipmentDatabyInventoryType( String orgUnitIdsByComma, Integer inventoryTypeId, Integer inventoryTypeAttributeId, Double factor );
    
    Map<String, String> getOrgUnitGroupAttribDataForRequirement( String orgUnitGroupIdsByComma, String orgUnitGroupAttribIds );
    
    Map<String, String> getDataElementDataForCatalogOptionsForRequirement( String orgUnitIdsByComma, String catalogOption_DataelementIds, Integer periodId );
    
    Map<String, String> getCatalogDataForRequirement( Integer vsReqCatalogTypeId, Integer vsReqStorageTempId, String vsReqStorageTemp, Integer vsReqNationalSupplyId, String vsReqNationalSupply, String vsReqCatalogAttribIds );
    
    List<Integer> getCatalogIdsForRequirement( Integer vsReqCatalogTypeId, Integer vsReqStorageTempId, String vsReqStorageTemp, Integer vsReqNationalSupplyId, String vsReqNationalSupply );
    
    Map<Integer, String> getOrgunitAndOrgUnitGroupMap( String orgUnitGroupIdsByComma, String orgUnitIdsByComma );
    
    String getMinMaxAvgValues(String orgunitid, String periodid , Integer dataElementid, Integer optionCombo);
    
    String getMinMaxAvgValuesForLiveBirths( String orgunitid, String periodid, Integer dataElementid, Integer optionCombo, Double liveBirthsPerThousand );
    
    Integer getGrandTotalValue(String orgunitid, String periodid , Integer dataElementid);
    
    Map<String,Integer> getCatalogDatavalueId( String orgUnitIdsByComma, Integer inventoryTypeId, Integer catalogTypeAttributeId );
    
    List<String> getModelName(Integer inventoryTypeId, Integer catalogTypeAttributeId , String orgUnitIds);
    
    String getEquipmentValue(String catalogTypeAttributeValue,Integer catalogid, String euipmentValue, String orgUnitIdsByComma, Integer inventoryTypeId);
    
    Map<String,Integer> getModelNameAndCount(Integer catalogTypeAttributeId , Integer inventoryTypeId, String equipmentValue, String orgUnitIdsByComma);
    
    Integer getDataValue( String dataelementId, String dataValue, String orgUnitByIds ,String periodId);
    
    Integer getCountByOrgUnitGroup( Integer rootOrgUnitId, Integer orgUnitGroupId );
    
    Map<String,Integer> getDataValueAndCount(String dataelementId, String orgUnitByIds,String periodId);
    
    List<String> getEquipmentValueAndData( Integer catalogTypeAttributeId, String orgUnitIdsByComma,
        Integer inventoryTypeId );
    
    List<String> equipmentCatalogies( String orgUnitIdsByComma, Integer inventoryTypeId );
    
    Map<String, String> equipmentCatalogyValues( String orgUnitIdsByComma, Integer inventoryTypeId,
        Integer inventoryTypeAttributeId );
    
    Map<String, String> equipmentOrgUnit( String orgUnitIdsByComma, Integer inventoryTypeId );
    
    Map<String, String> getEquipmentNameWithOrgUnit( Integer inventoryTypeId, Integer catalogTypeAttributeId,
        String orgUnitIds );
    
    Integer getTotalFacilitiesWithOrgUnit( String orgUnitIdsById );
    
    Map<String, String> getTotalColdRoomValue( Integer inventorytypeid, String orgUnitIdByComma,
        String inventoryTypeAttributeId, String equipmentValue );
    
    Map<String, Integer> getModelNameAndCountForColdBox( Integer catalogTypeAttributeId,
        Integer inventoryTypeId, String workingStatus, String orgUnitIdsByComma );
    
    String getModelNameAndCountForQuantityOfColdbox( Integer inventoryTypeId, String catalogValue,
        String orgUnitIdsByComma );
    
    Map<String, Double> getSumOfEquipmentAndCatalogValue( Integer inventoryTypeId,
        Integer inventoryTypeAttributeId, Integer catalogTypeAttributeId, String orgUnitIdsByComma );
    
    Map<String, Map<String,Integer>> getModelName_EquipmentUtilization_Count( Integer inventoryTypeId, Integer catalogTypeAttributeId, Integer inventoryTypeAttributeId, String orgUnitIdsByComma );
    
    Map<String, Integer> getModelName_Count( Integer inventoryTypeId, Integer catalogTypeAttributeId, String orgUnitIds );
	
	List<String> getDataValueFacility( Integer dataElementId, String dataValue, String orgUnitIdByComma, String periodIds );
	
    Integer getCatalogDataValueCount( Integer inventoryTypeId, Integer catalogTypeAttributeId, String catogDataValue, String orgUnitIds );
    
    Map<String, Integer> getEquipmentValue_Count( Integer inventoryTypeId, Integer inventoryTypeAttributeId, String orgUnitIds );
    
    Integer getEquipmentInstanceCount( Integer inventoryTypeId, String orgUnitIds );
    
    Map<String, Map<String,Integer>> getEquipmentType_ElectricityAvailability_Count( Integer inventoryTypeId, Integer catalogTypeAttributeId, Integer dataElementId, String periodId, String orgUnitIdsByComma );
    
    Map<String, Integer> getSumOfColdBoxByOrgUnitGroup( Integer coldBoxInventoryTypeId, Integer qtyPresent_qtyWorking_inventoryTypeAttributeId, Integer modelNameCatalogTypeAttributeId, String orgUnitIdsByComma, Integer orgUnitGroupId );
}
