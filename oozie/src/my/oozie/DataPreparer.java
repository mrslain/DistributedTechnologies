package my.oozie;

import java.io.*;
import java.util.Scanner;


public class DataPreparer {
    public static void main(String[] args) throws IOException {
        args = new String[2];
        args[1] = "data/input";
        args[0] = "data/rus_web_2002_300K-sentences.txt";

        int lineCount = 300;
        Scanner in = new Scanner(new File(args[0]));
        FileWriter out = null;
        int i = 300, j = 0;

        try {
            while (in.hasNextLine()) {
                if (i == lineCount) {
                    if (out != null) {
                        out.flush();
                    }
                    File file = new File(args[1]+"/"+"data_part" + j + ".txt");
                    if(file.exists())
                        file.delete();
                    file.createNewFile();
                    out = new FileWriter(file);
                    j++;
                    i = 0;
                }
                i++;
                out.write(in.nextLine());
                out.write("\r\n");
            }
        }
        catch (Exception e)
        {
            out.close();
        }
        finally {
            out.close();
            in.close();
        }
    }
}
