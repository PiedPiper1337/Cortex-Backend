import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by brianzhao on 3/7/16.
 */
public class Test {
    public static void main(String[] args) throws Exception {
//        System.out.println(executeCommand(new String[]{"python", "google.py", "hello", "world"}));
//        System.out.println(executeCommand(new String[]{"python", "python/wiki.py", "cortex"}));
        String messageBody = "alsdjfals;dkjf;alskdfj MSG: C1b: 2/18:hi";
//        if (messageBody.matches(".*[a-fA-F0-9]:[ 0-9]+/[ 0-9]+:.*")) {
//            System.out.println("hi");
//        }


        Pattern p = Pattern.compile("C[a-fA-F0-9]+:[ 0-9]+/[ 0-9]+:.*");
        Matcher m = p.matcher(messageBody);


        int index = -1;
        if (m.find()){
            index = m.start();
            System.out.println(index);
//            System.out.println("alsdjfals;dkjf;alskdfj MSG: ".length());
            System.out.println(messageBody.substring(index));
        }

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
