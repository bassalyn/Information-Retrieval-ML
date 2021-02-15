/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* Modified 9 January 2020 by Daniel Lizotte dlizotte@uwo.ca */
/* Modified 20 January 2020 by Bradley Assaly-Nesrallah bassalyn@uwo.ca */


/* Editter imports to add arrays and lists from utils */

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.en.PorterStemFilter;

/**
 * Filters {@link StandardTokenizer} with {@link LowerCaseFilter} and
 * We also import the porter stem filter
 * {@link StopFilter}, using a configurable list of stop words.
 *
 * @since 3.1
 */
public final class MyStandardAnalyzer extends StopwordAnalyzerBase {
  
  /** Default maximum allowed token length */
  public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
  /** We define the stopwords in a chararrayset as follows */
  static final List<String> wordList = Arrays.asList("about", "dost", "from", "hath", "his", "o", "that", "the", "thou");
  static final CharArraySet wordCharArraySet = new CharArraySet(wordList, false);

  private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

  /** Builds an analyzer with the given stop words.
   * @param stopWords stop words */
  public MyStandardAnalyzer(CharArraySet stopWords) {
    super(stopWords);
  }

  /** we implement the stopwords for the analyzer as follows
   */
  public MyStandardAnalyzer() {
    this(wordCharArraySet);
  }

  /** Builds an analyzer with the stop words from the given reader.
   * @see WordlistLoader#getWordSet(Reader)
   * @param stopwords Reader to read stop words from */
  public MyStandardAnalyzer(Reader stopwords) throws IOException {
    this(loadStopwordSet(stopwords));
  }

  /**
   * Set the max allowed token length.  Tokens larger than this will be chopped
   * up at this token length and emitted as multiple tokens.  If you need to
   * skip such large tokens, you could increase this max length, and then
   * use {@code LengthFilter} to remove long tokens.  The default is
   * {@link MyStandardAnalyzer#DEFAULT_MAX_TOKEN_LENGTH}.
   */
  public void setMaxTokenLength(int length) {
    maxTokenLength = length;
  }
    
  /** Returns the current maximum token length
   * 
   *  @see #setMaxTokenLength */
  public int getMaxTokenLength() {
    return maxTokenLength;
  }

  @Override
  protected TokenStreamComponents createComponents(final String fieldName) {
    final StandardTokenizer src = new StandardTokenizer();
    src.setMaxTokenLength(maxTokenLength);
    TokenStream tok = new LowerCaseFilter(src);
    tok = new StopFilter(tok, stopwords);
    /* We implement the porterstem filter in the tokenstream as follows */
    tok = new PorterStemFilter(tok);
    return new TokenStreamComponents(r -> {
      src.setMaxTokenLength(MyStandardAnalyzer.this.maxTokenLength);
      src.setReader(r);
    }, tok);
  }

  @Override
  protected TokenStream normalize(String fieldName, TokenStream in) {
    return new LowerCaseFilter(in);
  }
}
