import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.fs.Path;


import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EngramCount {

    public static class Map3
            extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {

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
    }

    public static class Map2
            extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {

            StringBuilder[] memory = new StringBuilder[]{new StringBuilder(""), new StringBuilder("")};
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
                    memory[1] = new StringBuilder(match);
                    if(cnt > 0) {
                        (memory[0].append(" ")).append(match);
                        if(!isFirstResult) {
                            word.set(memory[0].toString());
                            output.collect(word, one);
                        }
                        isFirstResult = false;
                    }
                    memory[0] = memory[1];
                    cnt++;
                }
            }
        }
    }

    public static class Map1
            extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {

            Pattern p = Pattern.compile("[a-zA-Zа-яА-Я]+|[^a-zA-Zа-яА-Я\\s]");
            String line = value.toString();
            Scanner scanner = new Scanner(line);

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
        }


    public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();
            }
            output.collect(key, new IntWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf(EngramCount.class);
        conf.setJobName("ngramCount");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        boolean wasSet = false;
        if(args[0].equals("1")) {
            conf.setMapperClass(Map1.class);
            wasSet = true;
        }
        if(args[0].equals("2")) {
            conf.setMapperClass(Map2.class);
            wasSet = true;
        }
        if(args[0].equals("3")) {
            conf.setMapperClass(Map3.class);
            wasSet = true;
        }
        if(!wasSet)
            throw new Exception("args[0] must be equals to 1, 2 or 3");

        conf.setCombinerClass(Reduce.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[1]));
        FileOutputFormat.setOutputPath(conf, new Path(args[2]));

        JobClient.runJob(conf);
    }
}
