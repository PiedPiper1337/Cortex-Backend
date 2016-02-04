import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by jon-bassi on 2/3/16.
 */
public class PollingThread implements Runnable
{

    private Session session;

    private Folder inbox;

    public PollingThread(String secretFile) throws MessagingException
    {
        Scanner secret = null;
        try
        {
            secret = new Scanner(new File(secretFile));
        }
        catch (IOException e)
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


        Properties reapProps = System.getProperties();
        reapProps.setProperty("mail.store.protocol", "imaps");
        session = Session.getDefaultInstance(reapProps, null);

        Store store = session.getStore("imaps");

        store.connect("imap.gmail.com", user, pass);
        inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        Message messages[] = inbox.search(new FlagTerm(new Flags(
                Flags.Flag.SEEN), false));
        //messages[0].setFlag(Flags.Flag.SEEN, true);
        Address[] returnAddress = messages[0].getFrom();
        System.out.println("No. of Unread Messages : " + messages.length);
        System.out.println(returnAddress[0]);

        Properties sendProps = new Properties();
        sendProps.put("mail.smtp.auth", "true");
        sendProps.put("mail.smtp.starttls.enable", "true");
        sendProps.put("mail.smtp.host", "smtp.gmail.com");
        sendProps.put("mail.smtp.port", "587");

        Session sendSession = Session.getInstance(sendProps, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(user, pass);
            }
        });

        try {

            Message message = new MimeMessage(sendSession);
            message.setFrom(new InternetAddress("someone@gmail.com"));
            message.setRecipient(Message.RecipientType.BCC, returnAddress[0]);
            message.setSubject("Testing");
            message.setText("I just replied to you using java");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }



    public void run()
    {


        // for now check email every 30s, may change to longer or shorter
        try
        {
            Thread.sleep(30000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            //nothing yet... check if still connected?
        }

    }
}
