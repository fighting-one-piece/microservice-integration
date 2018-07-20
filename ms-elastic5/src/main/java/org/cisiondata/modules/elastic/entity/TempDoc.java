package org.cisiondata.modules.elastic.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id; 
import org.springframework.data.elasticsearch.annotations.Document;  

@SuppressWarnings("serial")
@Document(indexName="es_temp_inx", type="temp_doc", indexStoreType="fs", shards=5, replicas=1, refreshInterval="-1")
public class TempDoc implements Serializable {

	@Id  
    private Long id = null;
	
	private String title = null;
	
	private String content = null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
