/**
 * Created by jon-bassi on 2/3/16.
 */

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PollingThread implements Runnable
{
    private static final String SECRET_FILE = "secret.txt";
    public static ConcurrentLinkedQueue<Message> newMessages = new ConcurrentLinkedQueue<>();
    private static  String user;
    private static Properties readProperties;
    private static Session session;
    private static Store store;
    private static Folder inbox;

    private PollingThread()
    {
        Scanner secret = null;
        try
        {
            secret = new Scanner(new File(SECRET_FILE));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        if (secret == null)
        {
            System.out.println("Error, secret file not found");
            return;
        }

        user = secret.next();
        final String pass = secret.next();
        secret.close();


        try
        {
            readProperties = System.getProperties();
            readProperties.setProperty("mail.store.protocol", "imaps");
            session = Session.getDefaultInstance(readProperties, null);
            session.setDebug(true);

            store = session.getStore("imaps");
            store.connect("imap.gmail.com", user, pass);

            inbox = store.getFolder("INBOX");
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
                Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                for (Message m : messages)
                {
                    newMessages.add(m);
                    m.setFlag(Flags.Flag.SEEN, true);
                }

                System.out.println("You have " + newMessages.size() + " new messages");

            } catch (MessagingException e)
            {
                e.printStackTrace();
            }

            try
            {
                Thread.sleep(30000);
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
