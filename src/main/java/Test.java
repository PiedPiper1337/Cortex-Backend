import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by brianzhao on 3/7/16.
 */
public class Test {
    public static void main(String[] args) throws Exception {
//        System.out.println(executeCommand(new String[]{"python", "google.py", "hello", "world"}));
        System.out.println(executeCommand(new String[]{"python", "python/wiki.py", "cortex"}));
    }

    public static String executeCommand(String[] command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));


            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            System.out.println(output.toString());

            output = new StringBuffer();


            System.out.println(p.getErrorStream().toString());
            line = "";
            reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }


//            System.out.println(p.getErrorStream().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

}
