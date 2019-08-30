package org.platform.modules.elastic.utils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticRestClientHelper {
	
	private static final Logger LOG = LoggerFactory.getLogger(ElasticRestClientHelper.class);
	
	@SuppressWarnings("deprecation")
	public void createIndex() throws IOException {
		CreateIndexRequest request = new CreateIndexRequest("twitter_two");//创建索引
        //创建的每个索引都可以有与之关联的特定设置。
        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
        );
        //创建索引时创建文档类型映射
        request.mapping("tweet",//类型定义
                "  {\n" +
                        "    \"tweet\": {\n" +
                        "      \"properties\": {\n" +
                        "        \"message\": {\n" +
                        "          \"type\": \"text\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }",//类型映射，需要的是一个JSON字符串
                XContentType.JSON);

        //为索引设置一个别名
        request.alias(new Alias("twitter_alias")
        );
        //可选参数
        request.timeout(TimeValue.timeValueMinutes(2));//超时,等待所有节点被确认(使用TimeValue方式)
        //request.timeout("2m");//超时,等待所有节点被确认(使用字符串方式)

        request.masterNodeTimeout(TimeValue.timeValueMinutes(1));//连接master节点的超时时间(使用TimeValue方式)
        //request.masterNodeTimeout("1m");//连接master节点的超时时间(使用字符串方式)

        request.waitForActiveShards(2);//在创建索引API返回响应之前等待的活动分片副本的数量，以int形式表示。
        //request.waitForActiveShards(ActiveShardCount.DEFAULT);//在创建索引API返回响应之前等待的活动分片副本的数量，以ActiveShardCount形式表示。

        //同步执行
        CreateIndexResponse createIndexResponse = ElasticRestClient.getInstance().getClient().indices().create(request);
        //异步执行
        //异步执行创建索引请求需要将CreateIndexRequest实例和ActionListener实例传递给异步方法：
        //CreateIndexResponse的典型监听器如下所示：
        //异步方法不会阻塞并立即返回。
        ActionListener<CreateIndexResponse> listener = new ActionListener<CreateIndexResponse>() {
            @Override
            public void onResponse(CreateIndexResponse createIndexResponse) {
                //如果执行成功，则调用onResponse方法;
            }
            @Override
            public void onFailure(Exception e) {
                //如果失败，则调用onFailure方法。
            }
        };
        ElasticRestClient.getInstance().getClient().indices().createAsync(request, listener);//要执行的CreateIndexRequest和执行完成时要使用的ActionListener

        //返回的CreateIndexResponse允许检索有关执行的操作的信息，如下所示：
        boolean acknowledged = createIndexResponse.isAcknowledged();//指示是否所有节点都已确认请求
        boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();//指示是否在超时之前为索引中的每个分片启动了必需的分片副本数
        LOG.info("{} {}", acknowledged, shardsAcknowledged);
	}
	
	@SuppressWarnings({ "unused", "deprecation" })
	public void createIndexData() throws IOException {
		RestHighLevelClient client = ElasticRestClient.getInstance().getClient();
		IndexRequest indexRequest1 = new IndexRequest(
	               "posts",//索引名称
	               "doc",//类型名称
	               "1");//文档ID

		//方式1：以字符串形式提供
		String jsonString = "{" +
               "\"user\":\"kimchy\"," +
               "\"postDate\":\"2013-01-30\"," +
               "\"message\":\"trying out Elasticsearch\"" +
               "}";
		indexRequest1.source(jsonString, XContentType.JSON);

		// 方式2：以Map形式提供
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("user", "kimchy");
		jsonMap.put("postDate", new Date());
		jsonMap.put("message", "trying out Elasticsearch");
		// Map会自动转换为JSON格式的文档源
		IndexRequest indexRequest2 = new IndexRequest("posts", "doc", "1").source(jsonMap);

		// 方式3：文档源以XContentBuilder对象的形式提供，Elasticsearch内部会帮我们生成JSON内容

		XContentBuilder builder = XContentFactory.jsonBuilder();
		builder.startObject();
		{
			builder.field("user", "kimchy");
			builder.field("postDate", new Date());
			builder.field("message", "trying out Elasticsearch");
		}
		builder.endObject();
		IndexRequest indexRequest3 = new IndexRequest("posts", "doc", "1").source(builder);

		// 方式4：以Object key-pairs提供的文档源，它会被转换为JSON格式
		IndexRequest indexRequest4 = new IndexRequest("posts", "doc", "1").source("user", "kimchy", "postDate",
				new Date(), "message", "trying out Elasticsearch");

		// ===============================可选参数start====================================
		indexRequest1.routing("routing");// 设置路由值
		indexRequest1.parent("parent");// 设置parent值

		// 设置超时：等待主分片变得可用的时间
		indexRequest1.timeout(TimeValue.timeValueSeconds(1));// TimeValue方式
		indexRequest1.timeout("1s");// 字符串方式

		// 刷新策略
		indexRequest1.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);// WriteRequest.RefreshPolicy实例方式
		indexRequest1.setRefreshPolicy("wait_for");// 字符串方式

		indexRequest1.version(2);// 设置版本

		indexRequest1.versionType(VersionType.EXTERNAL);// 设置版本类型

		// 操作类型
		indexRequest1.opType(DocWriteRequest.OpType.CREATE);// DocWriteRequest.OpType方式
		indexRequest1.opType("create");// 字符串方式, 可以是 create 或 update (默认)

		// The name of the ingest pipeline to be executed before indexing the
		// document
		indexRequest1.setPipeline("pipeline");

		// ===============================执行====================================
		// 同步执行
		IndexResponse indexResponse = client.index(indexRequest1);

		// 异步执行
		// IndexResponse 的典型监听器如下所示：
		// 异步方法不会阻塞并立即返回。
		ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
			@Override
			public void onResponse(IndexResponse indexResponse) {
				// 执行成功时调用。 Response以参数方式提供
			}

			@Override
			public void onFailure(Exception e) {
				// 在失败的情况下调用。 引发的异常以参数方式提供
			}
		};
		// 异步执行索引请求需要将IndexRequest实例和ActionListener实例传递给异步方法：
		client.indexAsync(indexRequest2, listener);

		// Index Response
		// 返回的IndexResponse允许检索有关执行操作的信息，如下所示：
		String index = indexResponse.getIndex();
		String type = indexResponse.getType();
		String id = indexResponse.getId();
		long version = indexResponse.getVersion();
		if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
			// 处理（如果需要）第一次创建文档的情况
		} else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
			// 处理（如果需要）文档被重写的情况
		}
		ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
		if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
			// 处理成功分片数量少于总分片数量的情况
		}
		if (shardInfo.getFailed() > 0) {
			for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
				String reason = failure.reason();// 处理潜在的失败
			}
		}

		// 如果存在版本冲突，则会抛出ElasticsearchException：
		IndexRequest request = new IndexRequest("posts", "doc", "1").source("field", "value").version(1);
		try {
			IndexResponse response = client.index(request);
		} catch (ElasticsearchException e) {
			if (e.status() == RestStatus.CONFLICT) {
				// 引发的异常表示返回了版本冲突错误
			}
		}

		// 如果opType设置为创建但是具有相同索引，类型和ID的文档已存在，则也会发生同样的情况：
		request = new IndexRequest("posts", "doc", "1").source("field", "value").opType(DocWriteRequest.OpType.CREATE);
		try {
			IndexResponse response = client.index(request);
		} catch (ElasticsearchException e) {
			if (e.status() == RestStatus.CONFLICT) {
				// 引发的异常表示返回了版本冲突错误
			}
		}
	}

}
