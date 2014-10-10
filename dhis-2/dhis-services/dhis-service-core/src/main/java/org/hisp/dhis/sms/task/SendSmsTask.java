package org.hisp.dhis.sms.task;

import java.util.List;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.sms.SmsSender;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;

public class SendSmsTask
    implements Runnable
{
    @Autowired
    private SmsSender smsSender;

    @Autowired
    private Notifier notifier;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String smsSubject;

    private String text;

    private User currentUser;

    private List<User> recipientsList;

    private String message = "success";

    private TaskId taskId;

    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    @Override
    public void run()
    {
        notifier.notify( taskId, "Sending SMS" );
        message = smsSender.sendMessage( smsSubject, text, currentUser, recipientsList, false );
        notifier.notify( taskId, "All Message Sent" );
    }

    public String getSmsSubject()
    {
        return smsSubject;
    }

    public void setSmsSubject( String smsSubject )
    {
        this.smsSubject = smsSubject;
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    public User getCurrentUser()
    {
        return currentUser;
    }

    public void setCurrentUser( User currentUser )
    {
        this.currentUser = currentUser;
    }

    public List<User> getRecipientsList()
    {
        return recipientsList;
    }

    public void setRecipientsList( List<User> recipientsList )
    {
        this.recipientsList = recipientsList;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public TaskId getTaskId()
    {
        return taskId;
    }

    public void setTaskId( TaskId taskId )
    {
        this.taskId = taskId;
    }

    public I18n getI18n()
    {
        return i18n;
    }

}
