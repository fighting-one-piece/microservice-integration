package org.cisiondata.modules.elastic.service.impl;

import java.util.List;

import org.cisiondata.modules.elastic.dao.TempDocRepository;
import org.cisiondata.modules.elastic.entity.TempDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

@Service
public class TempDocServiceImpl {

	@Autowired  
    private TempDocRepository tempDocRepository = null;  
	
    @Autowired  
    private ElasticsearchTemplate elasticsearchTemplate = null;
    
    public void insert(TempDoc tempDoc) {
    	tempDocRepository.save(tempDoc);
    }
    
    public void insertBatch(List<TempDoc> tempDocs) {
    	int counter = 0;  
        try {  
            if (!elasticsearchTemplate.indexExists("es_temp_inx")) {  
                elasticsearchTemplate.createIndex("es_temp_inx");  
            }  
            List<IndexQuery> queries = new ArrayList<>();  
            for (TempDoc tempDoc : tempDocs) {  
                IndexQuery indexQuery = new IndexQuery();  
                indexQuery.setId(tempDoc.getId() + "");  
                indexQuery.setObject(tempDoc);  
                indexQuery.setIndexName("es_temp_inx");  
                indexQuery.setType("temp_doc");  
  
                //上面的那几步也可以使用IndexQueryBuilder来构建  
                //IndexQuery index = new IndexQueryBuilder().withId(tempDoc.getId() + "").withObject(tempDoc).build();  
  
                queries.add(indexQuery);  
                if (counter % 500 == 0) {  
                    elasticsearchTemplate.bulkIndex(queries);  
                    queries.clear();  
                    System.out.println("bulkIndex counter : " + counter);  
                }  
                counter++;  
            }  
            if (queries.size() > 0) {  
                elasticsearchTemplate.bulkIndex(queries);  
            }  
            System.out.println("bulkIndex completed.");  
        } catch (Exception e) {  
            System.out.println("IndexerService.bulkIndex e;" + e.getMessage());  
            throw e;  
        }  
    }
	
}
