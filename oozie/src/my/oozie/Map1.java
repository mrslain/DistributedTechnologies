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

public class Map1 implements Mapper<LongWritable, Text, Text, IntWritable> {

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {

        Pattern p = Pattern.compile("[a-zA-Zа-яА-Я]+|[^a-zA-Zа-яА-Я\\s]");
        String line = value.toString();
        Scanner scanner = new Scanner(line);

        IntWritable one = new IntWritable(1);
        Text word = new Text();

        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            Matcher m = p.matcher(s);
            boolean isFirstResult = true;
            while (m.find())
            {
                String match = m.group();
                if(!isFirstResult) {
                    word.set(match);
                    output.collect(word, one);
                }
                isFirstResult = false;
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
