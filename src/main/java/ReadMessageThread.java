/**
 * Created by jon-bassi on 2/4/16.
 */
public class ReadMessageThread
{
    public ReadMessageThread()
    {
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
}
