package org.cisiondata.modules.elastic.dao;

import org.cisiondata.modules.elastic.entity.TempDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TempDocRepository extends ElasticsearchRepository<TempDoc, Long> {

}
