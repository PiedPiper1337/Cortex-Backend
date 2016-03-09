import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

//https://stackoverflow.com/questions/5285378/how-to-read-email-of-outlook-with-javamail
//https://support.office.com/en-us/article/Outlook-settings-for-POP-and-IMAP-access-for-Office-365-for-business-or-Microsoft-Exchange-accounts-7fc677eb-2491-4cbc-8153-8e7113525f6c

/**
 * Created by brianzhao on 3/8/16.
 */
public class EmailTest {
    private static final String SECRET_FILE = "outlooksecret.txt";

    public static void main(String[] args) {

        try {
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
            Store store = session.getStore("imaps");
            store.connect("outlook.office365.com", user, pass);

            Folder inbox = store.getFolder("Inbox");
            System.out.println("No of Unread Messages : " + inbox.getUnreadMessageCount());
            inbox.open(Folder.READ_ONLY);

            /*  Get the messages which is unread in the Inbox*/
            Message messages[] = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
