/**
 * Created by jon-bassi on 2/3/16.
 */

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class PollingThread implements Runnable
{
    private static final String SECRET_FILE = "secret.txt";
    public static ArrayList<Message> newMessages = new ArrayList<>();
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

        final String user = secret.next();
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
                inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);
                Message messages[] = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

                newMessages.addAll(Arrays.asList(messages));
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
