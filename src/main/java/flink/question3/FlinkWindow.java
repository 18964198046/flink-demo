package flink.question3;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class FlinkWindow {

    public static void main(String[] args) throws Exception {

        String ip = args[0];
        int port = Integer.parseInt(args[1]);

        StreamExecutionEnvironment streamExecutionEnvironment =
                StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> textStream =
                streamExecutionEnvironment.socketTextStream(ip, port, "\n");

        SingleOutputStreamOperator<Tuple2<String, Long>>
                tuple2SingleOutputStreamOperator = textStream.flatMap(new LineSplitter());

        SingleOutputStreamOperator<Tuple2<String, Long>> word =
                tuple2SingleOutputStreamOperator.keyBy("word")
                .timeWindow(Time.seconds(2), Time.seconds(1))
                .sum(1);

        word.print();

        streamExecutionEnvironment.execute("word count stream process");
    }

    static class LineSplitter implements FlatMapFunction<String, Tuple2<String, Long>> {

        public void flatMap(String line, Collector<Tuple2<String, Long>> collector) throws Exception {
            for (String word : line.split(" ")) {
                collector.collect(new Tuple2<String, Long>(word, 1l));
            }
        }

    }

}
