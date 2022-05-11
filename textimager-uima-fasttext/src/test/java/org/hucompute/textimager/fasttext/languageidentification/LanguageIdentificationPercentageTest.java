package org.hucompute.textimager.fasttext.languageidentification;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.Language;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import java.util.Iterator;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertEquals;


public class LanguageIdentificationPercentageTest {

	/*
	@Test
	public void simpleExampleDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist ein guter Test. This is a test.");
		DocumentMetaData.create(cas).setDocumentId("test");
		new Sentence(cas, 0, 22).addToIndexes();
		new Sentence(cas, 23, cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				LanguageIdentificationPercentage.class
				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		System.out.println(JCasUtil.select(cas, Language.class));
		assertEquals(JCasUtil.select(cas, Language.class).size(),2);
		Iterator<Language> iter = JCasUtil.select(cas, Language.class).iterator();
		assertEquals(iter.next().getValue(), "de");
		assertEquals(iter.next().getValue(), "en");
	}
	*/


	@Test
	public void simpleExamplePara() throws UIMAException{
		JCas cas = JCasFactory.createText("This is an example. This should yield one paragraph. " +
				"Wobei dieser Absatz jetzt ein zweiter sein soll und, soviel ich weiß auf Deutsch ist." +
				"Si!");
		DocumentMetaData.create(cas).setDocumentId("test");
		new Paragraph(cas, 0, 52).addToIndexes();
		new Paragraph(cas, 53, 138).addToIndexes();
		new Paragraph(cas, 139, cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(LanguageIdentificationPercentage.class));

		SimplePipeline.runPipeline(cas, builder.createAggregate());
		System.out.println(JCasUtil.select(cas, Language.class));

		assertEquals(JCasUtil.select(cas, Language.class).size(), 3);
		Iterator<Language> iter = JCasUtil.select(cas, Language.class).iterator();
		assertEquals(iter.next().getValue(), "en");
		assertEquals(iter.next().getValue(), "de");
		assertEquals(iter.next().getValue(), "it");
	}
}
