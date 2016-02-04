import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;

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
        }
        catch (MessagingException | IOException e)
        {
            e.printStackTrace();
        }
    }
}
