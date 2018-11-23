package org.platform.modules.elastic.dao;

import org.platform.modules.elastic.entity.TempDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TempDocRepository extends ElasticsearchRepository<TempDoc, Long> {

}
