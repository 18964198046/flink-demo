package flink.question4;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;


public class FlinkState {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStreamSource<String> data = env.socketTextStream("linux121", 7777);

        KeyedStream<String, String> keyed = data.keyBy(value -> value);

        SingleOutputStreamOperator<Tuple2<String, Long>> mapped = keyed.map(new RichMapFunction<String, Tuple2<String, Long>>() {

            private transient ValueState<Tuple1<Long>> state;

            @Override
            public Tuple2<String, Long> map(String value) throws Exception {
                String[] split = value.split(",");
                state.update(new Tuple1(state.value().f0 + 1));
                return new Tuple2(split[0], Long.valueOf(split[1]));
            }

            @Override
            public void open(Configuration parameters) throws Exception {
                ValueStateDescriptor<Tuple1<Long>> descriptor = new ValueStateDescriptor(
                        "collect", TypeInformation.of(new TypeHint<Tuple1<Long>>() {}));
                state = getRuntimeContext().getState(descriptor);
            }

        });

        SingleOutputStreamOperator<Tuple2<String, Long>> watermarks = mapped.assignTimestampsAndWatermarks(
                WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(10000l)));

        SingleOutputStreamOperator<String> res = watermarks.keyBy(0)
            .window(TumblingEventTimeWindows.of(Time.seconds(3)))
            .apply(new WindowFunction<Tuple2<String, Long>, String, Tuple, TimeWindow>() {
                @Override
                public void apply(Tuple tuple, TimeWindow timeWindow, Iterable<Tuple2<String, Long>> iterable, Collector<String> collector) throws Exception {
                    String key = tuple.toString();
                    ArrayList<Long> list = new ArrayList<>();
                    Iterator<Tuple2<String, Long>> it = iterable.iterator();
                    while (it.hasNext()) {
                        Tuple2<String, Long> next = it.next();
                        list.add(next.f1);
                    }
                    Collections.sort(list);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    String result = key + "," + list.size() + ","
                            + sdf.format(list.get(0)) + ","
                            + sdf.format(list.get(list.size() - 1)) + ","
                            + sdf.format(timeWindow.getStart()) + ","
                            + sdf.format(timeWindow.getEnd());
                    collector.collect(result);
                }
            });

        env.execute();

    }



}
