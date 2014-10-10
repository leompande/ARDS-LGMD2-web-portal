package org.hisp.dhis.survey;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

import static junit.framework.Assert.*;

public class SurveyStoreTest
    extends DhisSpringTest
{
    private SurveyStore surveyStore;
    
    private Survey surveyA;
    private Survey surveyB;
    
    @Override
    public void setUpTest()
    {
        surveyStore = (SurveyStore) getBean( SurveyStore.ID );
        
        surveyA = new Survey( "SurveyA", "SurveyA" );
        surveyB = new Survey( "SurveyB", "SurveyB" );
    }
    
    @Test
    public void add()
    {
        int idA = surveyStore.addSurvey( surveyA );
        int idB = surveyStore.addSurvey( surveyB );
       
        assertEquals( surveyA, surveyStore.getSurvey( idA ) );
        assertEquals( surveyB, surveyStore.getSurvey( idB ) );
    }
}
