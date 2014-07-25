package my.oozie;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Map3 implements Mapper<LongWritable, Text, Text, IntWritable> {

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        IntWritable one = new IntWritable(1);
        Text word = new Text();

        StringBuilder[] memory = new StringBuilder[]{new StringBuilder(""), new StringBuilder(""), new StringBuilder("")};
        Pattern p = Pattern.compile("[a-zA-Zа-яА-Я]+|[^a-zA-Zа-яА-Я\\s]");

        String line = value.toString();
        Scanner scanner = new Scanner(line);

        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            Matcher m = p.matcher(s);
            int cnt = 0;
            boolean isFirstResult = true;
            while (m.find())
            {
                String match = m.group();
                memory[2] = new StringBuilder(match);
                if(cnt > 0) (memory[1].append(" ")).append(match);
                if(cnt > 1) {
                    (memory[0].append(" ")).append(match);
                    if(!isFirstResult) {
                        word.set(memory[0].toString());
                        output.collect(word, one);
                    }
                    isFirstResult = false;
                }
                memory[0] = memory[1];
                memory[1] = memory[2];
                cnt++;
            }
        }
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void configure(JobConf entries) {
    }
}
