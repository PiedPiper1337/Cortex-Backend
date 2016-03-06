import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

            String messageContent = message.getContent().toString();
            if (messageContent.length() > 140)
                messageContent = messageContent.substring(0,141);
            messageContent = messageContent.replaceAll("[^a-zA-Z0-9: ]","");

            String[] query = messageContent.split(":");


            for (String s : query)
                System.out.println(s);

            String output = query[1] + ":";
            if (query[0].equals("QUES"))
            {
                System.out.println("GOOGLE QUESTION");
                Process p = Runtime.getRuntime().exec("python google_search.py " + query[2]);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String s = "";

                long t = System.currentTimeMillis();
                long end = t + 7500;
                while (System.currentTimeMillis() < end)
                {
                    if ((s = stdInput.readLine()) != null)
                    {
                        output += s + " ";
                        System.out.println(output);
                    }
                }
            }
            else if (query[0].equals("WIKI"))
            {
                System.out.println("WIKI QUESTION");
                Process p = Runtime.getRuntime().exec("python wiki_summarizer.py " + query[2]);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String s = "";

                long t = System.currentTimeMillis();
                long end = t + 7500;
                while (System.currentTimeMillis() < end)
                {
                    if ((s = stdInput.readLine()) != null)
                    {
                        output += s + " ";
                        System.out.println(output);
                    }
                }
            }
            else
            {
                output = query[1] + ":cortex could not complete your search, please try again with a different question";
            }

            if (output.length() == 0)
                output = query[1] + ":cortex could not complete your search, please try again with a different question";

            if (output.length() > 20)
            {
                if (output.substring(0, 20).contains("Traceback"))
                {
                    output = query[1] + ":cortex could not complete your search, please try again with a different question";
                }
                if (output.length() > 1000)
                {
                    output = output.substring(0,1001);
                }
            }



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

                System.out.println("sending");

                Message message = new MimeMessage(sendSession);
                message.setFrom(new InternetAddress("cortex@cortex.io"));
                message.setRecipient(Message.RecipientType.BCC, from[0]);
                message.setSubject("Thank you for using cortex");
                message.setText(output);
                System.out.println(output);
                Transport.send(message);

                PollingThread.toDelete.add(message);

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
        }
    }
}
