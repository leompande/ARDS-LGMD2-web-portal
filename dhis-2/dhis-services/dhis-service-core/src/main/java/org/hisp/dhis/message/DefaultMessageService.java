package org.hisp.dhis.message;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.system.velocity.VelocityManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
@Transactional
public class DefaultMessageService
    implements MessageService
{
    private static final Log log = LogFactory.getLog( DefaultMessageService.class );

    private static final String COMPLETE_SUBJECT = "Form registered as complete";

    private static final String COMPLETE_TEMPLATE = "completeness_message";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MessageConversationStore messageConversationStore;

    public void setMessageConversationStore( MessageConversationStore messageConversationStore )
    {
        this.messageConversationStore = messageConversationStore;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private List<MessageSender> messageSenders;

    @Autowired
    public void setMessageSenders( List<MessageSender> messageSenders )
    {
        this.messageSenders = messageSenders;

        log.info( "Found the following message senders: " + messageSenders );
    }
    
    // -------------------------------------------------------------------------
    // MessageService implementation
    // -------------------------------------------------------------------------

    public int sendMessage( String subject, String text, String metaData, Set<User> users )
    {
        return sendMessage( subject, text, metaData, users, null, false, false );
    }

    public int sendMessage( String subject, String text, String metaData, Set<User> users_,
        User sender, boolean includeFeedbackRecipients, boolean forceNotifications )
    {
        Set<User> users = new HashSet<User>( users_ );

        // ---------------------------------------------------------------------
        // Add feedback recipients to users if they are not there
        // ---------------------------------------------------------------------

        if ( includeFeedbackRecipients )
        {
            UserGroup userGroup = configurationService.getConfiguration().getFeedbackRecipients();

            if ( userGroup != null && userGroup.getMembers().size() > 0 )
            {
                users.addAll( userGroup.getMembers() );
            }
        }

        if ( sender == null )
        {
            sender = currentUserService.getCurrentUser();
            
            if ( sender != null )
            {
                users.add( sender );
            }
        }
        else
        {
            users.add( sender );
        }

        // ---------------------------------------------------------------------
        // Instantiate message, content and user messages
        // ---------------------------------------------------------------------

        MessageConversation conversation = new MessageConversation( subject, sender );

        conversation.addMessage( new Message( text, metaData, sender ) );

        for ( User user : users )
        {
            boolean read = user != null && user.equals( sender );

            conversation.addUserMessage( new UserMessage( user, read ) );
        }

        int id = saveMessageConversation( conversation );

        users.remove( sender );
        
        invokeMessageSenders( subject, text, sender, users, forceNotifications );

        return id;
    }

    public int sendFeedback( String subject, String text, String metaData )
    {
        return sendMessage( subject, text, metaData, new HashSet<User>(), null, true, false );
    }

    public void sendReply( MessageConversation conversation, String text, String metaData )
    {
        User sender = currentUserService.getCurrentUser();

        Message message = new Message( text, metaData, sender );

        conversation.markReplied( sender, message );

        updateMessageConversation( conversation );

        invokeMessageSenders( conversation.getSubject(), text, sender, new HashSet<User>( conversation.getUsers() ), false );
    }

    public int sendCompletenessMessage( CompleteDataSetRegistration registration )
    {
        DataSet dataSet = registration.getDataSet();

        if ( dataSet == null )
        {
            return 0;
        }

        UserGroup userGroup = dataSet.getNotificationRecipients();

        User sender = currentUserService.getCurrentUser();

        Set<User> recipients = new HashSet<User>();

        if ( userGroup != null )
        {
            recipients.addAll( new HashSet<User>( userGroup.getMembers() ) );
        }

        if ( dataSet.isNotifyCompletingUser() )
        {
            recipients.add( sender );
        }

        if ( recipients.isEmpty() )
        {
            return 0;
        }

        String text = new VelocityManager().render( registration, COMPLETE_TEMPLATE );

        MessageConversation conversation = new MessageConversation( COMPLETE_SUBJECT, sender );

        conversation.addMessage( new Message( text, null, sender ) );

        for ( User user : recipients )
        {
            conversation.addUserMessage( new UserMessage( user ) );
        }

        if ( !conversation.getUserMessages().isEmpty() )
        {
            int id = saveMessageConversation( conversation );
            
            invokeMessageSenders( COMPLETE_SUBJECT, text, sender, new HashSet<User>( conversation.getUsers() ), false );

            return id;
        }

        return 0;
    }

    public int saveMessageConversation( MessageConversation conversation )
    {
        return messageConversationStore.save( conversation );
    }

    public void updateMessageConversation( MessageConversation conversation )
    {
        messageConversationStore.update( conversation );
    }

    public MessageConversation getMessageConversation( int id )
    {
        return messageConversationStore.get( id );
    }

    public MessageConversation getMessageConversation( String uid )
    {
        return messageConversationStore.getByUid( uid );
    }

    public long getUnreadMessageConversationCount()
    {
        return messageConversationStore.getUnreadUserMessageConversationCount( currentUserService.getCurrentUser() );
    }

    public long getUnreadMessageConversationCount( User user )
    {
        return messageConversationStore.getUnreadUserMessageConversationCount( user );
    }

    public List<MessageConversation> getMessageConversations( int first, int max )
    {
        return messageConversationStore.getMessageConversations( currentUserService.getCurrentUser(), false, false,
            first, max );
    }

    public List<MessageConversation> getMessageConversations( boolean followUpOnly, boolean unreadOnly, int first,
        int max )
    {
        return messageConversationStore.getMessageConversations( currentUserService.getCurrentUser(), followUpOnly,
            unreadOnly, first, max );
    }

    public int getMessageConversationCount()
    {
        return messageConversationStore.getMessageConversationCount( currentUserService.getCurrentUser(), false, false );
    }

    public int getMessageConversationCount( boolean followUpOnly, boolean unreadOnly )
    {
        return messageConversationStore.getMessageConversationCount( currentUserService.getCurrentUser(), followUpOnly,
            unreadOnly );
    }

    public List<MessageConversation> getAllMessageConversations()
    {
        return messageConversationStore.getMessageConversations( null, false, false, null, null );
    }

    public void deleteMessages( User user )
    {
        messageConversationStore.deleteMessages( user );
        messageConversationStore.deleteUserMessages( user );
        messageConversationStore.removeUserFromMessageConversations( user );
    }

    public List<UserMessage> getLastRecipients( int first, int max )
    {
        return messageConversationStore.getLastRecipients( currentUserService.getCurrentUser(), first, max );
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void invokeMessageSenders( String subject, String text, User sender, Set<User> users, boolean forceSend )
    {
        for ( MessageSender messageSender : messageSenders )
        {
            log.debug( "Invoking message sender: " + messageSender.getClass().getSimpleName() );
            
            messageSender.sendMessage( subject, text, sender, new HashSet<User>( users ), forceSend );
        }
    }
}
