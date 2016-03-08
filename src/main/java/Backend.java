import javax.mail.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by jon-bassi on 2/3/16.
 */
public class Backend {

    public static final int MAX_THREADS = 10;
    private static final String SECRET_FILE = "secret.txt";

    public static void main(String[] args) {
        Scanner secret = null;
        try {
            secret = new Scanner(new File(SECRET_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (secret == null) {
            System.out.println("Error, secret file not found");
            return;
        }

        String user = secret.next();
        String pass = secret.next();
        secret.close();

        Properties properties = System.getProperties();
        properties.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(properties, null);
        Store store = null;
        try {
            store = session.getStore("imaps");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }


        while (true) {
            //open inbox, view all unread messages
            //detect if each message is a valid cortex app message
            //if so, spawn new thread to handle it
            try {
                store.connect("imap.gmail.com", user, pass);
                Folder inbox = store.getFolder("Inbox");
                inbox.open(Folder.READ_WRITE);

                List<Message> potentialCortexMessages = new ArrayList<>();

                Message[] messages = inbox.getMessages();
                for (Message m : messages) {
                    boolean addMessage = true;
                    Flags allFlags = m.getFlags();
                    Flags.Flag[] flags = allFlags.getSystemFlags();
                    for (Flags.Flag f : flags) {
                        if (f == Flags.Flag.SEEN)
                            addMessage = false;
                    }
                    if (addMessage)
                        potentialCortexMessages.add(m);

                }

                System.out.println("You have " + potentialCortexMessages.size() + " new messages");

                List<MessageResponseThread> threads = new ArrayList<>();
                for (Message m : potentialCortexMessages) {
                    threads.add(new MessageResponseThread(m, user, pass));
                }

                for (MessageResponseThread thread : threads) {
                    System.out.println("Thread started!");
                    thread.start();
                }

                for (MessageResponseThread thread : threads) {
                    thread.join();
                }
                for (Message m : messages) {
                    m.setFlag(Flags.Flag.DELETED, true);
                }
                inbox.close(true);
                store.close();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            } finally {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
