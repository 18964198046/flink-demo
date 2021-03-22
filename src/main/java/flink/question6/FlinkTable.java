package flink.question6;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import static org.apache.flink.table.api.Expressions.$;

public class FlinkTable {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        DataStreamSource<Tuple2<String, Integer>> data = env.addSource(new SourceFunction<Tuple2<String, Integer>>() {

            @Override
            public void run(SourceContext<Tuple2<String, Integer>> ctx) throws Exception {
                while (true) {
                    ctx.collect(new Tuple2<>("name", 10));
                    Thread.sleep(1000);
                }
            }

            @Override
            public void cancel() {

            }

        });

        Table table = tEnv.fromDataStream(data, $("name"), $("age"));
        Table name = table.select($("name"));

        DataStream<Tuple2<Boolean, Row>> result = tEnv.toRetractStream(name, Row.class);

        result.print();
        env.execute();

    }

}
