import javax.mail.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by jon-bassi on 2/4/16.
 */
public class ReadMessageThread implements Runnable
{

    private static final String SECRET_FILE = "secret.txt";

    private ReadMessageThread()
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
    }

    public static ReadMessageThread getInstance()
    {
        return ReadInstance.getInstance();
    }


    public void run()
    {
        while(true)
        {
            // System.out.println("***********************************\nReading\n***********************************");
            while (PollingThread.newMessages.peek() != null)
            {
                // 5 is the mimimum for at least one popup thread to be created
                if (Thread.activeCount() < Backend.MAX_THREADS)
                {
                    // create analysis thread here for each message
                    Message m = PollingThread.newMessages.poll();
                    new Thread(new MessageAnalysisThread(m)).start();
                }
                else
                {
                    System.out.println("blocked, current thread count: " + Thread.activeCount());
                    break;
                }
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
