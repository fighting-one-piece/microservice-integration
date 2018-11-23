package org.platform.modules.elastic.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.platform.modules.elastic.analyzer.IKSynonymsAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class ElasticUtils {

	private static Logger LOG = LoggerFactory.getLogger(ElasticUtils.class);
	
	private static Analyzer ikanalyzer = new IKSynonymsAnalyzer();
	
	/**
	 * 分词
	 * @param input
	 * @param userSmart  true 用智能分词   false 细粒度分词
	 * @return
	 */
	public static String[] analyze(String input, boolean userSmart) {
		List<String> results = new ArrayList<String>();
		try {
			IKSegmenter ikSeg = new IKSegmenter(new StringReader(input.trim()), userSmart);
			for (Lexeme lexeme = ikSeg.next(); lexeme != null; lexeme = ikSeg.next()) {
				results.add(lexeme.getLexemeText());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return results.toArray(new String[0]);
	}
	
	public static String analyzeToString(String input, boolean userSmart) {
		StringBuilder sb = new StringBuilder();
		try {
			IKSegmenter ikSeg = new IKSegmenter(new StringReader(input.trim()), userSmart);
			for (Lexeme lexeme = ikSeg.next(); lexeme != null; lexeme = ikSeg.next()) {
				sb.append(lexeme.getLexemeText()).append(" ");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return sb.toString();
	}
	
	public static String[] convertSynonyms(String input) {
		return convertSynonyms(ikanalyzer, input);
	}

	/**
	 * 同义词匹配，返回TokenStream
	 */
	public static String[] convertSynonyms(Analyzer analyzer, String input) {
		Set<String> results = new HashSet<String>();
		TokenStream tokenStream = analyzer.tokenStream("fields", input);
		CharTermAttribute termAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		try {
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				results.add(termAttribute.toString());
			}
			tokenStream.end();
			tokenStream.close();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return results.toArray(new String[0]);
	}
	
}
