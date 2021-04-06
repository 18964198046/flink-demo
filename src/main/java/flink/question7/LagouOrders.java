package flink.question7;

import flink.question1.WordCount;
import jdk.jfr.DataAmount;
import lombok.Data;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.calcite.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;

import java.math.BigDecimal;
import java.util.List;

public class LagouOrders {

    public static void main(String[] args) throws Exception {

        String inPath = args[0];
        String outPath = args[1];

        ExecutionEnvironment executionEnvironment = ExecutionEnvironment.getExecutionEnvironment();

        DataSet<String> text = executionEnvironment.readTextFile(inPath);
        DataSet<String> dataSet = text.flatMap(new LineSplitter());
        dataSet.writeAsCsv(outPath, "\n", " ").setParallelism(1);

        executionEnvironment.execute("Lagou Orders Splitter Process");

    }

    static class LineSplitter implements FlatMapFunction<String, String> {

        private static ObjectMapper objectMapper = new ObjectMapper();

        public void flatMap(String line, Collector<String> collector) throws Exception {
            LagouOrder lagouOrder = objectMapper.readValue(line, LagouOrder.class);
            for (LagouProduct lagouProduct : lagouOrder.getProducts()) {
                LagouDruidOrder lagouDruidOrder = new LagouDruidOrder(lagouOrder, lagouProduct);
                collector.collect(objectMapper.writeValueAsString(lagouDruidOrder));
            }
        }
    }

    @Data
    static class LagouOrder {
        private long ts;
        private String orderId;
        private String userId;
        private int orderStatusId;
        private int payModeId;
        private String payMode;
        private String payment;
        private List<LagouProduct> products;
    }

    @Data
    static class LagouProduct {
        private String productId;
        private String productName;
        private float price;
        private int productNum;
        private String categoryid;
        private String catname1;
        private String catname2;
        private String catname3;
    }

    @Data
    static class LagouDruidOrder {
        private long ts;
        private String orderId;
        private String userId;
        private int orderStatusId;
        private int payModeId;
        private String payMode;
        private String payment;
        private String productId;
        private String productName;
        private float price;
        private int productNum;
        private String categoryid;
        private String catname1;
        private String catname2;
        private String catname3;

        public LagouDruidOrder(LagouOrder lagouOrder, LagouProduct lagouProduct) {
            this.ts = lagouOrder.getTs();
            this.orderId = lagouOrder.getOrderId();
            this.userId = lagouOrder.getUserId();
            this.orderStatusId = lagouOrder.getOrderStatusId();
            this.payModeId = lagouOrder.getPayModeId();
            this.payMode = lagouOrder.getPayMode();
            this.payment = lagouOrder.getPayment();
            this.productId = lagouProduct.getProductId();
            this.productName = lagouProduct.getProductName();
            this.price = lagouProduct.getPrice();
            this.productNum = lagouProduct.getProductNum();
            this.categoryid = lagouProduct.getCategoryid();
            this.catname1 = lagouProduct.getCatname1();
            this.catname2 = lagouProduct.getCatname2();
            this.catname3 = lagouProduct.getCatname3();
        }

    }

}
