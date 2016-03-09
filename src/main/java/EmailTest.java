import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by brianzhao on 3/8/16.
 */
public class EmailTest {
    public static void main(String[] args) {
        Properties sendProps = new Properties();
        sendProps.put("mail.smtp.auth", "true");
        sendProps.put("mail.smtp.starttls.enable", "true");
        sendProps.put("mail.smtp.host", "smtp.gmail.com");
        sendProps.put("mail.smtp.port", "587");

        Session sendSession = Session.getInstance(sendProps, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("cortexmobileapp@gmail.com", "Im8dee8a");
            }
        });

        try {
            Message reply = new MimeMessage(sendSession);
            reply.setRecipient(Message.RecipientType.BCC, new InternetAddress("9093250777@txt.att.net"));
            reply.setText("i love python");
            Transport.send(reply);
            Thread.sleep(800);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
