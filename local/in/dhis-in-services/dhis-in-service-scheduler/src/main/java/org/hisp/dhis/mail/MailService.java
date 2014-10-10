package org.hisp.dhis.mail;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailService
{

// To - Do
// Need to get this info from properties file.
    public static final String MAIL_SERVER = "smtp.gmail.com";

    public static final String USERNAME = "abc@gmail.com";

    public static final String PASSWORD = "xyz";

    public String sendEmail()
    {

        String stusMessage = "Mail sent Successfully";

        try
        {
            String fromAddress = "abc@gmail.com";
            String toAddress = "def@gmail.com";
            String subject = "This is a test Message";
            String message = "Hello Hows u?";

            Properties properties = System.getProperties();
            properties.put( "mail.smtps.host", MAIL_SERVER );
            properties.put( "mail.smtps.auth", "true" );

            Session session = Session.getInstance( properties );
            MimeMessage msg = new MimeMessage( session );

            msg.setFrom( new InternetAddress( fromAddress ) );
            msg.addRecipients( Message.RecipientType.TO, toAddress );
            msg.setSubject( subject );
            msg.setText( message );

            Transport tr = session.getTransport( "smtps" );
            tr.connect( MAIL_SERVER, USERNAME, PASSWORD );
            tr.sendMessage( msg, msg.getAllRecipients() );
            tr.close();
        }
        catch ( AddressException ex )
        {
            System.out.println( ex.getMessage() );
            stusMessage = "Mail couldn't sent becuase " + ex.getMessage();
        }
        catch ( MessagingException ex )
        {
            System.out.println( ex.getMessage() );
            stusMessage = "Mail couldn't sent becuase " + ex.getMessage();
        }

        return stusMessage;
    }

    public String sendEmailWithAttachment()
    {
        String stusMessage = "Mail sent Successfully";

        try
        {
            String fromAddress = "abc@gmail.com";
            String toAddress = "def@gmail.com";
            String subject = "This is a test Mail with attachment";
            // String message = "Hello Hows u?";

            String fileAttachment = "c:/samp.xls";

            // Get system properties
            Properties properties = System.getProperties();
            properties.put( "mail.smtps.host", MAIL_SERVER );
            properties.put( "mail.smtps.auth", "true" );

            // Get session
            Session session = Session.getInstance( properties );

            // Define message
            MimeMessage message = new MimeMessage( session );

            message.setFrom( new InternetAddress( fromAddress ) );
            message.addRecipient( Message.RecipientType.TO, new InternetAddress( toAddress ) );
            message.setSubject( subject );

            // create the message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            // fill message
            messageBodyPart.setText( "Hi with attachment" );

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart( messageBodyPart );

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource( fileAttachment );
            messageBodyPart.setDataHandler( new DataHandler( source ) );
            messageBodyPart.setFileName( fileAttachment );
            multipart.addBodyPart( messageBodyPart );

            // Put parts in message
            message.setContent( multipart );

            // Send the message
            //Transport.send( message );
            Transport tr = session.getTransport( "smtps" );
            tr.connect( MAIL_SERVER, USERNAME, PASSWORD );
            tr.sendMessage( message, message.getAllRecipients() );
            tr.close();

        }
        catch ( AddressException ex )
        {
            System.out.println( ex.getMessage() );
            stusMessage = "Mail couldn't sent becuase " + ex.getMessage();
        }
        catch ( MessagingException ex )
        {
            System.out.println( ex.getMessage() );
            stusMessage = "Mail couldn't sent becuase " + ex.getMessage();
        }

        return stusMessage;
    }
}
