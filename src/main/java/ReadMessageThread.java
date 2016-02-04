import javax.mail.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by jon-bassi on 2/4/16.
 */
public class ReadMessageThread implements Runnable
{

    private static final String SECRET_FILE = "secret.txt";
    private static Properties sendProperties;
    private static Properties writeProperties;
    private static Session sendSession;
    private static Session writeSession;
    private static Store store;
    private static Folder inbox;

    public ReadMessageThread()
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




        /**

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
         **/
    }

    public static ReadMessageThread getInstance()
    {
        return ReadInstance.getInstance();
    }


    public void run()
    {
        while(true)
        {
            try
            {
                // set up session for read/write on messages
//            writeProperties = System.getProperties();
//            writeProperties.setProperty("mail.store.protocol", "imaps");
//            writeSession = Session.getDefaultInstance(writeProperties, null);
//
//            store = writeSession.getStore("imaps");
//            store.connect("imap.gmail.com", user, pass);
//
//            inbox = store.getFolder("INBOX");
//            inbox.open(Folder.READ_WRITE);

                System.out.println("***********************************\nReading\n***********************************");
                while (PollingThread.newMessages.peek() != null)
                {
                    Message m = PollingThread.newMessages.poll();
                    System.out.println(Arrays.asList(m.getFrom()));
                    System.out.println(m.getSubject());
                    System.out.println(m.getContent().toString());
                }

            }
            catch (MessagingException | IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                Thread.sleep(15000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static class ReadInstance
    {
        private static final ReadMessageThread INSTANCE = new ReadMessageThread();

        public static ReadMessageThread getInstance()
        {
            return ReadInstance.INSTANCE;
        }
    }
}