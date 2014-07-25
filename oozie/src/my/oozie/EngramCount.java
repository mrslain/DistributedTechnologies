package my.oozie;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.fs.Path;

public class EngramCount {
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


