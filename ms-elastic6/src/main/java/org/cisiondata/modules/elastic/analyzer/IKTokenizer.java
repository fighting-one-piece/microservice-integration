package org.cisiondata.modules.elastic.analyzer;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class IKTokenizer extends Tokenizer {

	private IKSegmenter _IKImplement = null;

	private final CharTermAttribute termAtt;
	
	private final OffsetAttribute offsetAtt;
	
	private final TypeAttribute typeAtt;

	private int endPosition;
	
	public IKTokenizer(boolean useSmart) {
		offsetAtt = addAttribute(OffsetAttribute.class);
		termAtt = addAttribute(CharTermAttribute.class);
		typeAtt = addAttribute(TypeAttribute.class);
		_IKImplement = new IKSegmenter(input, useSmart);
	}

	public IKTokenizer(Reader in, boolean useSmart) {
		offsetAtt = addAttribute(OffsetAttribute.class);
		termAtt = addAttribute(CharTermAttribute.class);
		typeAtt = addAttribute(TypeAttribute.class);
		_IKImplement = new IKSegmenter(input, useSmart);
	}

	@Override
	public boolean incrementToken() throws IOException {
		clearAttributes();
		Lexeme nextLexeme = _IKImplement.next();
		if (nextLexeme != null) {
			termAtt.append(nextLexeme.getLexemeText());
			termAtt.setLength(nextLexeme.getLength());
			offsetAtt.setOffset(nextLexeme.getBeginPosition(), nextLexeme.getEndPosition());
			endPosition = nextLexeme.getEndPosition();
			typeAtt.setType(nextLexeme.getLexemeTypeString());
			return true;
		}
		return false;
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		_IKImplement.reset(input);
	}

	@Override
	public final void end() {
		int finalOffset = correctOffset(this.endPosition);
		offsetAtt.setOffset(finalOffset, finalOffset);
	}

}
