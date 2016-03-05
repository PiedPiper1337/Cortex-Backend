import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by jon-bassi on 2/3/16.
 */
public class Backend
{

    public static final int MAX_THREADS = 10;
    public static String user;
    public static String pass;
    private static final String SECRET_FILE = "secret.txt";

    public static void main(String[] args)
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

        user = secret.next();
        pass = secret.next();
        secret.close();

        new Thread(PollingThread.getInstance()).start();

        new Thread(ReadMessageThread.getInstance()).start();
    }
}
