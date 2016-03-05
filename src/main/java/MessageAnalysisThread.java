import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by jon-bassi on 2/4/16.
 */
public class MessageAnalysisThread implements Runnable
{
    private Message message;

    public MessageAnalysisThread(Message message)
    {
        this.message = message;
    }

    public void run()
    {
        try
        {
            // google search and summarize here
            System.out.println(Arrays.asList(message.getFrom()));
            System.out.println(message.getSubject());
            System.out.println(message.getContent().toString());

            String query = message.getContent().toString();



            Properties sendProps = new Properties();
            sendProps.put("mail.smtp.auth", "true");
            sendProps.put("mail.smtp.starttls.enable", "true");
            sendProps.put("mail.smtp.host", "smtp.gmail.com");
            sendProps.put("mail.smtp.port", "587");

            Session sendSession = Session.getInstance(sendProps, new javax.mail.Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(Backend.user, Backend.pass);
                }
            });

            try {
                Address[] from = message.getFrom();

                Message message = new MimeMessage(sendSession);
                message.setFrom(new InternetAddress("cortex@gmail.com"));
                message.setRecipient(Message.RecipientType.BCC, from[0]);
                message.setSubject("Thank you for using cortex");
                message.setText("Thank you for using cortex, we are currently developing our backend" +
                                        ", it will be finished soon!");

                Transport.send(message);

                PollingThread.toDelete.add(message);

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
        catch (MessagingException | IOException e)
        {
            e.printStackTrace();
        }
    }
}
