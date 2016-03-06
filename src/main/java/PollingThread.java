/**
 * Created by jon-bassi on 2/3/16.
 */

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PollingThread implements Runnable
{
    public static ConcurrentLinkedQueue<Message> newMessages = new ConcurrentLinkedQueue<Message>();
    public static ConcurrentLinkedQueue<Message> toDelete = new ConcurrentLinkedQueue<Message>();
    private static Properties readProperties;
    private static Session session;
    private static Store store;
    private static Folder inbox;

    private PollingThread()
    {
        String user = Backend.user;
        String pass = Backend.pass;

        try
        {
            readProperties = System.getProperties();
            readProperties.setProperty("mail.store.protocol", "imaps");
            session = Session.getDefaultInstance(readProperties, null);
            session.setDebug(true);

            store = session.getStore("imaps");
            store.connect("imap.gmail.com", user, pass);
            inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_WRITE);
        } catch (MessagingException e)
        {
            e.printStackTrace();
        }
    }

    public static PollingThread getInstance()
    {
        return PollingInstance.getInstance();
    }

    /**
     * create stream to handle debug output or collect output in case of error, flush stream every 5 minutes?
     */
    public void run()
    {
        while(true)
        {
            try
            {
//                inbox = store.getFolder("Inbox");
//                inbox.open(Folder.READ_WRITE);
                Message[] messages = inbox.getMessages();
                for (Message m : messages)
                {
                    boolean addMessage = true;
                    Flags allFlags = m.getFlags();
                    Flags.Flag[] flags = allFlags.getSystemFlags();
                    for (Flags.Flag f : flags)
                    {
                        if (f == Flags.Flag.SEEN)
                            addMessage = false;
                    }
                    if (addMessage)
                        newMessages.add(m);
                    m.setFlag(Flags.Flag.SEEN, true);
                }

                System.out.println("You have " + newMessages.size() + " new messages");


                // delete messages from queue
                Flags deleted = new Flags(Flags.Flag.DELETED);
                while (toDelete.peek() != null)
                {
                    Message m = toDelete.poll();
                    m.setFlag(Flags.Flag.DELETED, true);
                }

                inbox.expunge();
//                inbox.close(true);

            } catch (MessagingException e)
            {
                e.printStackTrace();
            }

            try
            {
                Thread.sleep(15000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static class PollingInstance
    {
        private static final PollingThread INSTANCE = new PollingThread();

        public static PollingThread getInstance()
        {
            return PollingInstance.INSTANCE;
        }
    }

}
