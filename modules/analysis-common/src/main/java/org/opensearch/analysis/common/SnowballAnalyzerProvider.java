/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.analysis.common;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.opensearch.common.settings.Settings;
import org.opensearch.env.Environment;
import org.opensearch.index.IndexSettings;
import org.opensearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.opensearch.index.analysis.Analysis;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * Creates a SnowballAnalyzer initialized with stopwords and Snowball filter. Only
 * supports Dutch, English (default), French, German and German2 where stopwords
 * are readily available. For other languages available with the Lucene Snowball
 * Stemmer, use them directly with the SnowballFilter and a CustomAnalyzer.
 * Configuration of language is done with the "language" attribute or the analyzer.
 * Also supports additional stopwords via "stopwords" attribute
 * <p>
 * The SnowballAnalyzer comes with a LowerCaseFilter, StopFilter
 * and the SnowballFilter.
 *
 *
 */
public class SnowballAnalyzerProvider extends AbstractIndexAnalyzerProvider<SnowballAnalyzer> {
    private static final Map<String, CharArraySet> DEFAULT_LANGUAGE_STOPWORDS;

    static {
        Map<String, CharArraySet> defaultLanguageStopwords = new HashMap<>();
        defaultLanguageStopwords.put("English", EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        defaultLanguageStopwords.put("Dutch", DutchAnalyzer.getDefaultStopSet());
        defaultLanguageStopwords.put("German", GermanAnalyzer.getDefaultStopSet());
        defaultLanguageStopwords.put("German2", GermanAnalyzer.getDefaultStopSet());
        defaultLanguageStopwords.put("French", FrenchAnalyzer.getDefaultStopSet());
        DEFAULT_LANGUAGE_STOPWORDS = unmodifiableMap(defaultLanguageStopwords);
    }

    private final SnowballAnalyzer analyzer;

    SnowballAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);

        String language = settings.get("language", settings.get("name", "English"));
        CharArraySet defaultStopwords = DEFAULT_LANGUAGE_STOPWORDS.getOrDefault(language, CharArraySet.EMPTY_SET);
        CharArraySet stopWords = Analysis.parseStopWords(env, settings, defaultStopwords);

        analyzer = new SnowballAnalyzer(language, stopWords);
        analyzer.setVersion(version);
    }

    @Override
    public SnowballAnalyzer get() {
        return this.analyzer;
    }
}
