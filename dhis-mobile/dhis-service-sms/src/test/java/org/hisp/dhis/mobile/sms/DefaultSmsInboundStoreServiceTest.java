package org.hisp.dhis.mobile.sms;


import org.hisp.dhis.mobile.sms.api.SmsInboundStoreService;
import java.util.Calendar;
import org.hisp.dhis.DhisSpringTest;
import java.util.Collection;
import java.util.Date;
import org.hisp.dhis.mobile.sms.api.SmsInbound;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Saptarshi
 */
public class DefaultSmsInboundStoreServiceTest extends DhisSpringTest
{

    private SmsInboundStoreService smsInboundStoreService;

    private SmsInbound testSms;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------
    @Override
    public void setUpTest()
        throws Exception
    {
        smsInboundStoreService = (SmsInboundStoreService) getBean( SmsInboundStoreService.ID );
        testSms = new SmsInbound();
        testSms.setEncoding( 'U' );
        testSms.setGatewayId( "modem1" );
        testSms.setMessageDate( new Date() );
        testSms.setOriginalReceiveDate( new Date() );
        testSms.setOriginalRefNo( "1" );
        testSms.setProcess( 0 );
        testSms.setReceiveDate( new Date() );
        testSms.setText( "I AM A DISCO DANCER" );
        testSms.setType( 'I' );
        testSms.setOriginator( "+919867192752" );
    }

    /**
     * Test of saveSms method, of class HibernateSmsInboundStore.
     */
    @Test
    public void testSaveSms()
    {
        System.out.println( "testSaveSms" );
        smsInboundStoreService.saveSms( testSms );

        for ( SmsInbound savedSms : smsInboundStoreService.getAllReceivedSms() )
        {
            assertEquals( testSms, savedSms );
        }
    }

    /**
     * Test of getSmsByDate method, of class HibernateSmsInboundStore.
     */
    @Test
    public void testGetSmsByDate() throws Exception
    {
        System.out.println( "testSaveSms" );
        Calendar today = Calendar.getInstance();
        Calendar prevDay = ( (Calendar) today.clone() );
        prevDay.add( Calendar.DAY_OF_YEAR, -1 );
        Calendar nextDay = ( (Calendar) today.clone() );
        nextDay.add( Calendar.DAY_OF_YEAR, 1 );
        System.out.println( "testGetSmsByDate" );
        Date startDate = prevDay.getTime();
        Date endDate = nextDay.getTime();
        smsInboundStoreService.saveSms( testSms );
        Collection<SmsInbound> smsByDate = smsInboundStoreService.getSmsByDate( startDate, endDate );
        for ( SmsInbound sms : smsByDate )
        {
            assertEquals( sms, testSms );
        }
    }

    /**
     * Test of getSmsByRecipient method, of class HibernateSmsInboundStore.
     */
    @Test
    public void testGetSmsByRecipient()
    {
        System.out.println( "testGetSmsByRecipient" );
        String originator = "+919867192752";
        smsInboundStoreService.saveSms( testSms );
        Collection<SmsInbound> result = smsInboundStoreService.getSmsByOriginator( originator );
        for ( SmsInbound sms : result )
        {
            assertEquals( sms, testSms );
        }
    }

    /**
     * Test of getSmsByProcess method, of class HibernateSmsInboundStore.
     */
    @Test
    public void testGetSmsByProcess()
    {
        System.out.println( "testGetSmsByProcess" );
        smsInboundStoreService.saveSms( testSms );
        Collection<SmsInbound> result = smsInboundStoreService.getSmsByProcess( 0 );
        for ( SmsInbound sms : result )
        {
            assertEquals( sms, testSms );
        }
    }

    /**
     * Test of getAllReceiveSMS method, of class HibernateSmsInboundStore.
     */
    public void testGetAllReceiveSMS()
    {
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of updateSms method, of class HibernateSmsInboundStore.
     */
    public void testUpdateSms()
    {
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of getSmsCount method, of class HibernateSmsInboundStore.
     */
    public void testGetSmsCount()
    {
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }
}
