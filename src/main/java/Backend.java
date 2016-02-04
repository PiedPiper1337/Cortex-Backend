/**
 * Created by jon-bassi on 2/3/16.
 */
public class Backend
{

    public static final int MAX_THREADS = 10;

    public static void main(String[] args)
    {
        new Thread(PollingThread.getInstance()).start();

        new Thread(ReadMessageThread.getInstance()).start();
    }
}
