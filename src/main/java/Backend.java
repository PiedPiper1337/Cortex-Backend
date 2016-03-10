import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by jon-bassi on 2/3/16.
 */
public class Backend {
    //    private static final String SECRET_FILE = "outlooksecret.txt";
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

        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props, null);
        Store store = null;
        try {
            store = session.getStore("imaps");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        }


        while (true) {
            //open inbox, view all unread messages
            //detect if each message is a valid cortex app message
            //if so, spawn new thread to handle it
            try {

//                store.connect("outlook.office365.com", user, pass);
                store.connect("imap.gmail.com", user, pass);
                Folder inbox = store.getFolder("Inbox");
                inbox.open(Folder.READ_WRITE);


                Message messages[] = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

                System.out.println("You have " + messages.length + " new messages");

                List<MessageResponseThread> threads = new ArrayList<>();
                for (Message m : messages) {
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

                String workingDir = System.getProperty("user.dir");
                File file = new File(workingDir);
                File[] currentDirectoryFiles = file.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".random");
                    }
                });
                if (currentDirectoryFiles != null) {
                    for (File currentFile : currentDirectoryFiles) {
                        currentFile.delete();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
