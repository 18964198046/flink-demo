package elasticsearch;

import org.apache.flink.calcite.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class ElasticSearchDemo {

    RestHighLevelClient client;

    //准备一个highlevel client
    @Before
    public void init() {
        final RestHighLevelClient highLevelClient = new RestHighLevelClient(
            RestClient.builder(
                new HttpHost("linux121", 9200, "http"),
                new HttpHost("linux122", 9200, "http"),
                new HttpHost("linux123", 9200, "http")
            )
        );
        System.out.println(highLevelClient.cluster().toString());
        client = highLevelClient;
    }

    @After
    public void destory() {
        if (client != null) {
            try {
                client.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void getAllDoc() throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        SearchRequest searchRequest = new SearchRequest("book");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        if (searchResponse.status() == RestStatus.OK) {
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            for (SearchHit searchHit : searchHits) {
                Map<String, Object> map = searchHit.getSourceAsMap();
                System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map));
            }
        }

    }

    @Test
    public void getTermQuery() throws IOException {

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "offline");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(termQueryBuilder);

        SearchRequest searchRequest = new SearchRequest("es_test");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        if (searchResponse.status() == RestStatus.OK) {
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            for (SearchHit searchHit : searchHits) {
                Map<String, Object> map = searchHit.getSourceAsMap();
                System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map));
            }
        }
    }

    @Test
    public void getAllDocWithPage() throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(1);

        SearchRequest searchRequest = new SearchRequest("book");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        if (searchResponse.status() == RestStatus.OK) {
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            for (SearchHit searchHit : searchHits) {
                Map<String, Object> map = searchHit.getSourceAsMap();
                System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map));
            }
        }

    }

    @Test
    public void getAllDocWithFields() throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(1);
        searchSourceBuilder.fetchSource(new String[]{"name", "price"}, new String[]{});

        SearchRequest searchRequest = new SearchRequest("book");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        if (searchResponse.status() == RestStatus.OK) {
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            for (SearchHit searchHit : searchHits) {
                Map<String, Object> map = searchHit.getSourceAsMap();
                System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map));
            }
        }

    }

    @Test
    public void getAllDocWithSort() throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(4);
        searchSourceBuilder.fetchSource(new String[]{"name", "price"}, new String[]{});
        searchSourceBuilder.sort(new FieldSortBuilder("price").order(SortOrder.ASC));

        SearchRequest searchRequest = new SearchRequest("book");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        if (searchResponse.status() == RestStatus.OK) {
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            for (SearchHit searchHit : searchHits) {
                Map<String, Object> map = searchHit.getSourceAsMap();
                System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map));
            }
        }

    }

    @Test
    public void getAggregation() throws IOException {

        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("name_group").field("name.keyword");
        termsAggregationBuilder.subAggregation(AggregationBuilders.avg("avg_price").field("price"));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(termsAggregationBuilder);

        SearchRequest searchRequest = new SearchRequest("book");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        if (searchResponse.status() == RestStatus.OK) {
            System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(searchResponse.getAggregations().get("name_group")));
        }

    }


}
