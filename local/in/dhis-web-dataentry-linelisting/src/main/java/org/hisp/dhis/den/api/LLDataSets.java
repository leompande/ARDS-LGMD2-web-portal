package org.hisp.dhis.den.api;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class LLDataSets
{
    public static final String LL_BIRTHS = "Line listing Births";
    public static final String LL_DEATHS = "Line listing Deaths";
    public static final String LL_MATERNAL_DEATHS = "Line listing Maternal Deaths";
    public static final String LL_UU_IDSP_EVENTS = "Line listing Unusual IDSP events- FormS";
    public static final String LL_UU_IDSP_EVENTSP = "Line listing Unusual IDSP events- FormP";
    public static final String LL_DEATHS_IDSP = "Line listing Deaths IDSP";
    public static final String LL_IDSP_LAB = "Line Listing IDSP Lab";
    public static final String LL_COLD_CHAIN = "Line listing Cold Chain";

    //----------------------------------------------------------------
    // LineListing Cold Chain
    //----------------------------------------------------------------
    public static final int LLCC_EQUIPMENT = 5786;
    public static final int LLCC_MACHINE = 5787;
    public static final int LLCC_MACHINE_WORKING = 5788;
    public static final int LLCC_BREAKDOWN_DATE = 5789;
    public static final int LLCC_INTIMATION_DATE = 5790;
    public static final int LLCC_REPAIR_DATE = 5791;
    public static final int LLCC_REMARKS = 5792;
    
    //----------------------------------------------------------------
    // LineListing IDSP LAB
    //----------------------------------------------------------------
    public static final int LLIDSPL_PATIENT_NAME = 1053;
    public static final int LLIDSPL_AGE = 1055;
    public static final int LLIDSPL_SEX = 1054;
    public static final int LLIDSPL_ADDRESS = 1056;
    public static final int LLIDSPL_TEST = 1057;
    public static final int LLIDSPL_LAB_DIAGNOSIS = 1058;
    public static final int LLIDSPL_OUTCOME = 3120;
    
    //----------------------------------------------------------------
    // LineListing Death IDSP
    //----------------------------------------------------------------
    public static final int LLDIDSP_CHILD_NAME = 1048;
    public static final int LLDIDSP_VILLAGE_NAME = 1049;
    public static final int LLDIDSP_SEX = 1050;
    public static final int LLDIDSP_AGE_CATEGORY = 1051;
    public static final int LLDIDSP_DEATH_CAUSE = 1052;

    //----------------------------------------------------------------
    // LineListing Unusual IDSP Event DataElements FORM-P
    //----------------------------------------------------------------    
    public static final int LLUUIDSPEP_EVENT_REPORTED = 1044;
    public static final int LLUUIDSPEP_DATE_OF_EVENT = 1045;    
    public static final int LLUUIDSPEP_WAS_INVESTIGATED = 1046;
    public static final int LLUUIDSPEP_ACTION_TAKEN = 1047;

    //----------------------------------------------------------------
    // LineListing Unusual IDSP Event DataElements
    //----------------------------------------------------------------
    public static final int LLUUIDSPE_SC_NAME = 1040;
    public static final int LLUUIDSPE_DATE_OF_EVENT = 1041;
    public static final int LLUUIDSPE_DEATAILS = 1042;
    public static final int LLUUIDSPE_WAS_INVESTIGATED = 1043;
    
    //----------------------------------------------------------------
    // LineListing Birth DataElements
    //----------------------------------------------------------------
    public static final int LLB_CHILD_NAME = 1020;
    public static final int LLB_VILLAGE_NAME = 1021;
    public static final int LLB_SEX = 1022;
    public static final int LLB_DOB = 1023;
    public static final int LLB_WIEGH = 1024;
    public static final int LLB_BREASTFED = 1025;
    
    public static final int LLB_BIRTHS = 22;
    public static final int LLB_BIRTHS_MALE = 24;
    public static final int LLB_BIRTHS_FEMALE = 23;
    public static final int LLB_WEIGHED_MALE = 515;
    public static final int LLB_WEIGHED_FEMALE = 516;
    public static final int LLB_WEIGHED_LESS1800_MALE = 519;
    public static final int LLB_WEIGHED_LESS1800_FEMALE = 520;
    public static final int LLB_WEIGHED_LESS2500_MALE = 517;
    public static final int LLB_WEIGHED_LESS2500_FEMALE = 518;
    public static final int LLB_BREASTFED_MALE = 521;
    public static final int LLB_BREASTFED_FEMALE = 522;
    
    //----------------------------------------------------------------
    // LineListing Death DataElements
    //----------------------------------------------------------------
    public static final int LLD_CHILD_NAME = 1027;
    public static final int LLD_VILLAGE_NAME = 1028;
    public static final int LLD_SEX = 1029;
    public static final int LLD_AGE_CATEGORY = 1030;
    public static final int LLD_DEATH_CAUSE = 1031;
    
    public static final int LLD_DEATH_OVER05Y = 552;
    public static final int LLD_DEATH_OVER05Y_MALE = 553;
    public static final int LLD_DEATH_OVER05Y_FEMALE = 554;
    public static final int LLD_DEATH_OVER15Y_MALE = 1195;
    public static final int LLD_DEATH_OVER15Y_FEMALE = 1196;
    public static final int LLD_DEATH_OVER55Y_MALE = 1197;
    public static final int LLD_DEATH_OVER55Y_FEMALE = 1198;
    public static final int LLD_DEATH_BELOW5Y = 555;
    public static final int LLD_DEATH_BELOW5Y_MALE = 556;
    public static final int LLD_DEATH_BELOW5Y_FEMALE = 557;
    public static final int LLD_DEATH_BELOW1Y_MALE = 558;
    public static final int LLD_DEATH_BELOW1Y_FEMALE = 559;
    public static final int LLD_DEATH_BELOW1M_MALE = 560;
    public static final int LLD_DEATH_BELOW1M_FEMALE = 561;
    public static final int LLD_DEATH_BELOW1W_MALE = 562;
    public static final int LLD_DEATH_BELOW1W_FEMALE = 563;
    public static final int LLD_DEATH_BELOW1D_MALE = 564;
    public static final int LLD_DEATH_BELOW1D_FEMALE = 565;
            
    // 1121 : Birth Asphyxia under one  month
    public static final int LLD_CAUSE_DE1 = 1121;           
    // 1122 : Sepsis under one  month
    public static final int LLD_CAUSE_DE2 = 1122;      
    // 1123 : Low Birth Weight under one  month
    public static final int LLD_CAUSE_DE3 = 1123;
    // 1124 : Immunization reactions under one  month
    public static final int LLD_CAUSE_DE4 = 1124;
    // 1125 : Others under one  month
    public static final int LLD_CAUSE_DE5 = 1125;
    // 1126 : Not known under one  month
    public static final int LLD_CAUSE_DE6 = 1126;
    // 1127 : Pneumonia 1 month to 5 year
    public static final int LLD_CAUSE_DE7 = 1127;
    // 1128 : Diarrhoeal disease 1 month to 5 year
    public static final int LLD_CAUSE_DE8 = 1128;
    // 1129 : Measles 1 month to 5 year
    public static final int LLD_CAUSE_DE9 = 1129;
    // 1130 : Other Fever related 1 month to 5 year
    public static final int LLD_CAUSE_DE10 = 1130;
    // 1131 : Others 1 month to 5 year
    public static final int LLD_CAUSE_DE11 = 1131;
    // 1132 : Not known 1 month to 5 year
    public static final int LLD_CAUSE_DE12 = 1132;
       
    // 1133 : Diarrhoeal disease 5-14 years
    public static final int LLD_CAUSE_DE13 = 1133;
    // 1134 : Tuberculosis 5-14 years
    public static final int LLD_CAUSE_DE14 = 1134;
    // 1135 : Malaria 5-14 years
    public static final int LLD_CAUSE_DE15 = 1135;
    // 1136 : HIV/AIDS 5-14 years
    public static final int LLD_CAUSE_DE16 = 1136;
    // 1137 : Other Fever related 5-14 years
    public static final int LLD_CAUSE_DE17 = 1137;
    // 1138 : Pregnancy related death( maternal mortality) 15-55 years
    public static final int LLD_CAUSE_DE18 = 1138;
    // 1139 : Sterilisation related deaths 15-55 years
    public static final int LLD_CAUSE_DE19 = 1139;
    // 1140 : Accidents or injuries 5-14 years
    public static final int LLD_CAUSE_DE20 = 1140;
    // 1141 : Suicides 5-14 years
    public static final int LLD_CAUSE_DE21 = 1141;
    // 1142 : Animal Bites or stings 5-14 years
    public static final int LLD_CAUSE_DE22 = 1142;
    // 1143 : Other known Acute disease (any  known cause- sick for less than 3 weeks- no fever) 5-14 years
    public static final int LLD_CAUSE_DE23 = 1143;
    // 1144 : Other known Chronic disease( sick for more than 3 weeks, no fever) 5-14 years
    public static final int LLD_CAUSE_DE24 = 1144;
    // 1145 : Cause Not Known, 5-14 years
    public static final int LLD_CAUSE_DE25 = 1145;
    // 1146 : Respiratory Infections and Disease – other than tuberculosis 5-14 years
    public static final int LLD_CAUSE_DE26 = 1146;
    // 1147 : Heart disease and hypertension 5-14 years
    public static final int LLD_CAUSE_DE27 = 1147;
    // 1148 : Stroke and Neurological disease 5-14 years
    public static final int LLD_CAUSE_DE28 = 1148;
    
    // 1199 : Malaria 15-55 years
    public static final int LLD_CAUSE_DE29 = 1199;
    // 1200 : Malaria Over 55 years
    public static final int LLD_CAUSE_DE30 = 1200;
    // 1201 : Tuberculosis 15-55 years
    public static final int LLD_CAUSE_DE31 = 1201;
    // 1202 : Tuberculosis Over 55 years
    public static final int LLD_CAUSE_DE32 = 1202;
    // 1203 : Malaria Below 5 years
    public static final int LLD_CAUSE_DE33 = 1203;
    // 1204 : Tuberculosis Below 5 years
    public static final int LLD_CAUSE_DE34 = 1204;
   
    // 1205 : Diarrhoeal disease 15-55 years
    public static final int LLD_CAUSE_DE35 = 1205;
    // 1206 : HIV/AIDS 15-55 years
    public static final int LLD_CAUSE_DE36 = 1206;
    // 1207 : Other Fever related 15-55 years
    public static final int LLD_CAUSE_DE37 = 1207;
    // 1208 : Accidents or injuries 15-55 years
    public static final int LLD_CAUSE_DE40 = 1208;
    // 1209 : Suicides 15-55 years
    public static final int LLD_CAUSE_DE41 = 1209;
    // 1210 : Animal Bites or stings 15-55 years
    public static final int LLD_CAUSE_DE42 = 1210;
    // 1211 : Other known Acute disease (any  known cause- sick for less than 3 weeks- no fever) 15-55 years
    public static final int LLD_CAUSE_DE43 = 1211;
    // 1212 : Other known Chronic disease( sick for more than 3 weeks, no fever) 15-55 years
    public static final int LLD_CAUSE_DE44 = 1212;
    // 1213 : Cause Not Known, 15-55 years
    public static final int LLD_CAUSE_DE45 = 1213;
    // 1214 : Respiratory Infections and Disease – other than tuberculosis 15-55 years
    public static final int LLD_CAUSE_DE46 = 1214;
    // 1215 : Heart disease and hypertension 15-55 years
    public static final int LLD_CAUSE_DE47 = 1215;
    // 1216 : Stroke and Neurological disease 15-55 years
    public static final int LLD_CAUSE_DE48 = 1216;
    
    // 1217 : Diarrhoeal disease over 55 years
    public static final int LLD_CAUSE_DE49 = 1217;
    // 1218 : HIV/AIDS over 55 years
    public static final int LLD_CAUSE_DE50 = 1218;
    // 1219 : Other Fever related over 55 years
    public static final int LLD_CAUSE_DE51 = 1219;
    // 1220 : Accidents or injuries over 55 years
    public static final int LLD_CAUSE_DE54 = 1220;
    // 1221 : Suicides over 55 years
    public static final int LLD_CAUSE_DE55 = 1221;
    // 1222 : Animal Bites or stings over 55 years
    public static final int LLD_CAUSE_DE56 = 1222;
    // 1223 : Other known Acute disease (any  known cause- sick for less than 3 weeks- no fever) over 55 years
    public static final int LLD_CAUSE_DE57 = 1223;
    // 1224 : Other known Chronic disease( sick for more than 3 weeks, no fever) over 55 years
    public static final int LLD_CAUSE_DE58 = 1224;
    // 1225 : Cause Not Known, over 55 years
    public static final int LLD_CAUSE_DE59 = 1225;
    // 1226 : Respiratory Infections and Disease – other than tuberculosis over 55 years
    public static final int LLD_CAUSE_DE60 = 1226;
    // 1227 : Heart disease and hypertension over 55 years
    public static final int LLD_CAUSE_DE61 = 1227;
    // 1228 : Stroke and Neurological disease over 55 years
    public static final int LLD_CAUSE_DE62 = 1228;

    // 1229 : Immunization reactions 1 month to 5 years
    public static final int LLD_CAUSE_DE63 = 1229;
        
    // 1230 : Birth Asphyxia under one  day
    public static final int LLD_CAUSE_DE64 = 1230;           
    // 1231 : Sepsis under one  day
    public static final int LLD_CAUSE_DE65 = 1231;      
    // 1232 : Low Birth Weight under one  day
    public static final int LLD_CAUSE_DE66 = 1232;
    // 1233 : Immunization reactions under one  day
    public static final int LLD_CAUSE_DE67 = 1233;
    // 1234 : Others under one  day
    public static final int LLD_CAUSE_DE68 = 1234;
    // 1235 : Not known under one  day
    public static final int LLD_CAUSE_DE69 = 1235;
   
    // 1236 : Birth Asphyxia under one  week
    public static final int LLD_CAUSE_DE70 = 1236;           
    // 1237 : Sepsis under one  week
    public static final int LLD_CAUSE_DE71 = 1237;      
    // 1238 : Low Birth Weight under one  week
    public static final int LLD_CAUSE_DE72 = 1238;
    // 1239 : Immunization reactions under one  week
    public static final int LLD_CAUSE_DE73 = 1239;
    // 1240 : Others under one  week
    public static final int LLD_CAUSE_DE74 = 1240;
    // 1241 : Not known under one  week
    public static final int LLD_CAUSE_DE75 = 1241;
   
    // 1242 : Pneumonia 1 month to 1 year
    public static final int LLD_CAUSE_DE76 = 1242;
    // 1243 : Diarrhoeal disease 1 month to 1 year
    public static final int LLD_CAUSE_DE77 = 1243;
    // 1244 : Measles 1 month to 5 year
    public static final int LLD_CAUSE_DE78 = 1244;
    // 1245 : Tuberculosis 1 month to 1 year
    public static final int LLD_CAUSE_DE79 = 1245;
    // 1246 : Malaria 1 month to 1 year
    public static final int LLD_CAUSE_DE80 = 1246;
    // 1247 : Immunization reactions 1 month to 1 year
    public static final int LLD_CAUSE_DE81 = 1247;
    // 1248 : Other Fever related  1 month to 1 year
    public static final int LLD_CAUSE_DE82 = 1248;
    // 1249 : Others 1 month to 1 year
    public static final int LLD_CAUSE_DE83 = 1249;
    // 1250 : Not known 1 month to 1 year
    public static final int LLD_CAUSE_DE84 = 1250;   
    
    public static final int LLD_OPTIONCOMBO_DEFAULT = 1;
    public static final int LLD_CAUSE_OPTIONCOMBO_MALE = 8;
    public static final int LLD_CAUSE_OPTIONCOMBO_FEMALE = 7;
       
    public static final String LLD_ASPHYXIA = "ASPHYXIA";
    public static final String LLD_SEPSIS = "SEPSIS";
    public static final String LLD_LOW_BIRTH_WEIGH = "LOWBIRTHWEIGH";   
    public static final String LLD_IMMREAC = "IMMREAC";
    public static final String LLD_PNEUMONIA = "PNEUMONIA";
    public static final String LLD_DIADIS = "DIADIS";
    public static final String LLD_MEASLES = "MEASLES";
    public static final String LLD_TUBER = "TUBER";
    public static final String LLD_MALARIA = "MALARIA";
    public static final String LLD_HIVAIDS = "HIVAIDS";
    public static final String LLD_OFR = "OFR";
    public static final String LLD_PRD = "PRD";
    public static final String LLD_SRD = "SRD";
    public static final String LLD_AI = "AI";
    public static final String LLD_SUICIDES = "SUICIDES";
    public static final String LLD_ABS = "ABS";
    public static final String LLD_RID = "RID";
    public static final String LLD_HDH = "HDH";
    public static final String LLD_SND = "SND";
    public static final String LLD_OKAD = "OKAD";
    public static final String LLD_OKCD = "OKCD";
    public static final String LLD_OTHERS = "OTHERS";                                          
    public static final String LLD_NOT_KNOWN = "NK";
       
    //-------------------------------------------------------------------
    // Line listing Maternal Death
    //-------------------------------------------------------------------
    public static final int LLMD_MOTHER_NAME = 1032;
    public static final int LLMD_VILLAGE_NAME = 1033;
    public static final int LLMD_AGE_AT_DEATH = 1034;
    public static final int LLMD_DURATION_OF_PREGNANCY = 1035;
    public static final int LLMD_DELIVERY_AT = 1036;
    public static final int LLMD_NATURE_OF_ASSISTANCE = 1037;
    public static final int LLMD_DEATH_CAUSE = 1038;
    public static final int LLMD_AUDITED = 1039;
    
    public static final int LLMD_DURING_PREGNANCY = 523;
    public static final int LLMD_DURING_FIRST_TRIM = 524;
    public static final int LLMD_DURING_SECOND_TRIM = 525;
    public static final int LLMD_DURING_THIRD_TRIM = 526;    
    public static final int LLMD_DURING_DELIVERY = 527;
    public static final int LLMD_AFTER_DEL_WITHIN_42DAYS = 528;
    
    public static final int LLMD_AGE_BELOW16 = 529;
    public static final int LLMD_AGE_16TO19 = 530;
    public static final int LLMD_AGE_19TO35 = 531;
    public static final int LLMD_AGE_ABOVE35 = 532;
    
    public static final int LLMD_AT_HOME = 533;
    public static final int LLMD_AT_SC = 534;
    public static final int LLMD_AT_PHC = 535;
    public static final int LLMD_AT_CHC = 536;
    public static final int LLMD_AT_MC = 537;
    public static final int LLMD_AT_PVTINST = 5726; // This data element should be created for state specific for line listing maternal death delivery at PVT INST
    
    public static final int LLMD_BY_UNTRAINED = 538;
    public static final int LLMD_BY_TRAINED = 539;
    public static final int LLMD_BY_ANM = 540;
    public static final int LLMD_BY_NURSE = 541;
    public static final int LLMD_BY_DOCTOR = 542;
    
    public static final int LLMD_CAUSE_ABORTION = 543;
    public static final int LLMD_CAUSE_OPL = 544;
    public static final int LLMD_CAUSE_FITS = 545;
    public static final int LLMD_CAUSE_SH = 546;
    public static final int LLMD_CAUSE_BBCD = 547;
    public static final int LLMD_CAUSE_BACD = 548;
    public static final int LLMD_CAUSE_HFBD = 549;
    public static final int LLMD_CAUSE_HFAD = 550;
    public static final int LLMD_CAUSE_NK = 551;
    public static final int LLMD_CAUSE_MDNK = 5725;// This dataelement should be created for state specific for linelisting maternal death Other Causes (including cause not known)
}
