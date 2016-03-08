import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by brianzhao on 3/4/16.
 */
public class MessageResponseThread extends Thread {
    public static final String CORTEX_FAILURE = "cortex could not complete your search, please try again with a different question";
    private Message message;
    private String user;
    private String password;


    public MessageResponseThread(Message message, String user, String password) {
        this.message = message;
        this.user = user;
        this.password = password;
    }

    @Override
    public void run() {
        try {
            if (shouldReply(message)) {
                System.out.println("should reply!");
                String messageText = (String) message.getContent();
                System.out.println("message text was: "+ messageText);
                replyToMessage(messageText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
   * This method checks for content-type
   * based on which, it processes and
   * fetches the content of the message
   */
    private boolean shouldReply(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            System.out.println("message is plaintext");
            String messageText = (String) message.getContent();
            messageText = messageText.replaceAll("\\s+", " ");
            if (verifyString(messageText)) {
                System.out.println("message was verified");
                return true;
            }
        }
        return false;
    }

    private void replyToMessage(String messageText) {
        int indexOfFirstColon = messageText.indexOf(":");
        int indexOfSecondColon = messageText.indexOf(":", indexOfFirstColon + 1);
        String type = messageText.substring(0, indexOfFirstColon);
        String hexEncodedPrimaryKey = messageText.substring(indexOfFirstColon + 1, indexOfSecondColon);
        String actualMessage = messageText.substring(indexOfSecondColon + 1);
        String result = null;
        switch (type) {
            case "WIKI":
                result = lookUpWiki(actualMessage);
                break;
            case "QUES":
                result = lookUpGoogle(actualMessage);
                break;
            default:
                result = CORTEX_FAILURE;
                break;
        }
//        System.out.println(result);
        int headerLength = type.length() + hexEncodedPrimaryKey.length() + 4 + 6; //assume 3 digits number of messages to send
        int charactersPerMessage = 140 - headerLength;
        ArrayList<String> replies = new ArrayList<>();
        String firstPartOfHeader = type + ":" + hexEncodedPrimaryKey + ":";
        int numMessages = (int) Math.ceil((result.length() * 1.0)/charactersPerMessage);
        String totalNumMessages = convertToThreeLetterString(numMessages);

        int currentMessageNumber = 1;
        int characterPosition = 0;
        while (characterPosition < result.length()) {
            String header = firstPartOfHeader + convertToThreeLetterString(currentMessageNumber) + "/"
                    + totalNumMessages + ":";
            int finalIndex = ((characterPosition + charactersPerMessage) > result.length()) ? result.length() : (characterPosition + charactersPerMessage);
            String body = result.substring(characterPosition, finalIndex);
            replies.add(header + body);
            characterPosition += charactersPerMessage;
            currentMessageNumber++;
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
                return new PasswordAuthentication(user, password);
            }
        });

        for (String replyText : replies) {
            try {
                Address[] from = message.getFrom();
                Message reply = new MimeMessage(sendSession);
                reply.setFrom(new InternetAddress("cortex@cortex.io"));
                reply.setRecipient(Message.RecipientType.BCC, from[0]);
                reply.setSubject("Thank you for using cortex");
                reply.setText(replyText);
                Transport.send(reply);
//                System.out.println(reply);
                Thread.sleep(300);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private String convertToThreeLetterString(int num) {
        String result = String.valueOf(num);
        int lengthDifference = 3 - result.length();
        for (int i = 0; i < lengthDifference; i++) {
            result = " " + result;
        }
        return result;
    }



    private String lookUpGoogle(String actualMessage) {
        return performPython("google_search.py", actualMessage);
    }

    private String lookUpWiki(String actualMessage) {
        return performPython("wiki.py", actualMessage);
    }

    private String performPython(String pythonProgram, String arg) {
        String output = null;
        try {
            Random random = new Random();
            int timeToSleep = random.nextInt(2000);
            Thread.sleep(timeToSleep);
            Process p = Runtime.getRuntime().exec(new String[]{"python", "python/"+pythonProgram, arg});
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder result = new StringBuilder();
            String s;
            long t = System.currentTimeMillis();
            long end = t + 6000;

            while (System.currentTimeMillis() < end) {
                if ((s = stdInput.readLine()) != null) {
                    result.append(s).append(" ");
                }
                Thread.sleep(200);
            }
            output = result.toString();
            output = output.trim();
            System.out.println(output);


            if (output.length() > 0) {
                if (output.length() > 3000) {
                    output = output.substring(0, 3000);
                }
            } else {
                System.out.println("length wasn't right");
                output = CORTEX_FAILURE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            output = CORTEX_FAILURE;
        }
        return output;
    }

    private boolean verifyString(String input) {
        //if its too long
        if (input.length() > 150) {
            System.out.println("message was too long");
            return false;
        }

        //if it doesn't fit our pattern
        Pattern messagePattern = Pattern.compile("(QUES|WIKI):[a-fA-F0-9]+:.*");
        Matcher matcher = messagePattern.matcher(input);
        if (!matcher.matches()) {
            System.out.println("message didn't match regex");
            System.out.println(input);
            return false;
        }

        //if the question portion has any punctuation
        int indexOfFirstColon = input.indexOf(":");
        int indexOfSecondColon = input.indexOf(":", indexOfFirstColon + 1);
        String actualMessage = input.substring(indexOfSecondColon + 1);

        if (actualMessage.matches(".*[^\\w ].*")) {
            System.out.println("rest of message had non word characters");
            System.out.println(actualMessage);
            return false;
        }
        return true;
    }
//
//    /*
//    * This method would print FROM,TO and SUBJECT of the message
//    */
//    public static void writeEnvelope(Message m) throws Exception {
//        System.out.println("New message!");
//        System.out.println("---------------------------");
//        Address[] a;
//
//        // FROM
//        if ((a = m.getFrom()) != null) {
//            for (int j = 0; j < a.length; j++)
//                System.out.println("FROM: " + a[j].toString());
//        }
//
//        // TO
//        if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
//            for (int j = 0; j < a.length; j++)
//                System.out.println("TO: " + a[j].toString());
//        }
//
//        // SUBJECT
//        if (m.getSubject() != null)
//            System.out.println("SUBJECT: " + m.getSubject());
//
//    }

}