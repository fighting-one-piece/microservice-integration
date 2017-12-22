package org.cisiondata.modules.elastic.analyzer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IKSynonymsAnalyzer extends Analyzer {
	
	private Logger LOG = LoggerFactory.getLogger(IKSynonymsAnalyzer.class);
	
	private Boolean useSmart = null;
	
	public IKSynonymsAnalyzer() {
		
	}
	
	public IKSynonymsAnalyzer(boolean useSmart) {
		this.useSmart = useSmart;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Map<String, String> filterArgs = new HashMap<String, String>();
		filterArgs.put("synonyms", "elastic/synonyms_1.txt,elastic/synonyms_2.txt");
		filterArgs.put("luceneMatchVersion", Version.LUCENE_5_5_4.toString());
		filterArgs.put("expand", "true");
		SynonymFilterFactory factory = new SynonymFilterFactory(filterArgs);
		try {
			factory.inform(new ClasspathResourceLoader());
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		Tokenizer tokenizer = null == useSmart ? new WhitespaceTokenizer() : new IKTokenizer(useSmart);
		return new TokenStreamComponents(tokenizer, factory.create(tokenizer));  
	} 
	
}
