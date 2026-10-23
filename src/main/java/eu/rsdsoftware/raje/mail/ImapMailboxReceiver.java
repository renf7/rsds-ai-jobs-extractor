package eu.rsdsoftware.raje.mail;

import eu.rsdsoftware.raje.config.MailboxProperties;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.UIDFolder;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ImapMailboxReceiver {

    private static final Logger logger = LoggerFactory.getLogger(ImapMailboxReceiver.class);

    private final MailboxProperties mailboxProperties;

    public ImapMailboxReceiver(MailboxProperties mailboxProperties) {
        this.mailboxProperties = mailboxProperties;
    }

    @Scheduled(fixedDelayString = "${raje.mailboxes.poll-interval:PT1M}")
    public void pollMailboxes() {
        if (!mailboxProperties.isEnabled()) {
            return;
        }

        for (MailboxProperties.Account account : mailboxProperties.getAccounts()) {
            receive(account);
        }
    }

    private void receive(MailboxProperties.Account account) {
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");
        properties.setProperty("mail.imaps.connectiontimeout", "10000");
        properties.setProperty("mail.imaps.timeout", "10000");
        Session session = Session.getInstance(properties);

        try (Store store = session.getStore("imaps")) {
            store.connect(account.getHost(), account.getPort(), account.getUsername(), account.getPassword());
            Folder folder = store.getFolder(account.getFolder());
            if (folder == null || !folder.exists()) {
                logger.error("Configured mailbox folder '{}' is unavailable for account '{}'",
                        account.getFolder(), account.getName());
                return;
            }

            try (folder) {
                folder.open(Folder.READ_ONLY);
                int messageCount = folder.getMessageCount();
                long newestUid = folder instanceof UIDFolder uidFolder && messageCount > 0
                        ? uidFolder.getUID(folder.getMessage(messageCount))
                        : -1;
                logger.info("Mailbox '{}' is connected in read-only mode; {} messages available, newest UID {}",
                        account.getName(), messageCount, newestUid);
            }
        } catch (MessagingException exception) {
            logger.error("Could not receive messages from configured mailbox '{}'", account.getName(), exception);
        }
    }
}
