package org.hucompute.textimager.uima.local;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.dkpro.core.api.resources.CompressionMethod;
import org.dkpro.core.io.xmi.XmiReader;
import org.dkpro.core.io.xmi.XmiWriter;
import org.hucompute.textimager.fasttext.labelannotator.LabelAnnotator;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger3;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class SimpleLocalPipelineSpacyDdc {
    public static void main(String[] args) throws UIMAException, IOException {
        if (args.length != 4) {
            System.out.println("Usage:");
            System.out.println("  language inputDir outputDir dockerPort");
            System.exit(1);
        }

        String language = args[0];                      // de, en, ...
        Path inputDir = Paths.get(args[1]);             // path
        Path outputDir = Paths.get(args[2]);            // path
        int dockerPort = Integer.parseInt(args[3]);     // 8462

        System.out.println("lang: " + language);
        System.out.println("in: " + inputDir);
        System.out.println("out: " + outputDir);
        System.out.println("docker port: " + dockerPort);

        CollectionReader reader = CollectionReaderFactory.createReader(
                XmiReader.class
                , XmiReader.PARAM_SOURCE_LOCATION, inputDir.toString()
                , XmiReader.PARAM_PATTERNS, "**/*.xmi*"
                , XmiReader.PARAM_LENIENT, false
                , XmiReader.PARAM_ADD_DOCUMENT_METADATA, false
                , XmiReader.PARAM_OVERRIDE_DOCUMENT_METADATA, false
                , XmiReader.PARAM_MERGE_TYPE_SYSTEM, false
                , XmiReader.PARAM_USE_DEFAULT_EXCLUDES, true
                , XmiReader.PARAM_INCLUDE_HIDDEN, false
                , XmiReader.PARAM_LOG_FREQ, 1
        );

        AnalysisEngineDescription writer = createEngineDescription(
                XmiWriter.class
                , XmiWriter.PARAM_TARGET_LOCATION, outputDir.toString()
                , XmiWriter.PARAM_VERSION, "1.1"
                , XmiWriter.PARAM_COMPRESSION, CompressionMethod.GZIP
                , XmiWriter.PARAM_PRETTY_PRINT, true
                , XmiWriter.PARAM_OVERWRITE, true
        );

        AnalysisEngineDescription ddc2;
        if (language.equalsIgnoreCase("de")) {
            System.out.println("ddc: de");
            ddc2 = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText_original_for_ducc_annotators/fasttext"
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "de,/resources/nlp/models/categorization/ddc/ddc_2018/wikipedia_de.v8.lemma.nopunct.pos.no_functionwords_gnd_ddc.v4.with_categories-lr0.2-lrUR150-minC5-dim100-ep10000-vec_vec_token_lemmapos.vec.epoch5000.bin,98"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100
                    , LabelAnnotator.PARAM_CUTOFF, false
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_TAGS, "ddc2"
                    , LabelAnnotator.PARAM_USE_LEMMA, true
                    , LabelAnnotator.PARAM_ADD_POS, true
                    , LabelAnnotator.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt"
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, true
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, true
            );
        }
        else if (language.equalsIgnoreCase("en")) {
            System.out.println("ddc: en");
            ddc2 = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText_original_for_ducc_annotators/fasttext"
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "en,/resources/nlp/models/categorization/ddc/ddc2_2018/wikipedia_en.v8.lemma.nopunct_gnd_ddc.v3.with_wikidata_model_dim100_pretreined-glove.6B.100d.txt_epoch100000.epoch10000.bin,95"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100
                    , LabelAnnotator.PARAM_CUTOFF, false
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_TAGS, "ddc2"
                    , LabelAnnotator.PARAM_USE_LEMMA, true
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt"
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, false
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, true
            );
        }
        else if (language.equalsIgnoreCase("ar") || language.equalsIgnoreCase("es") || language.equalsIgnoreCase("fr") || language.equalsIgnoreCase("tr")) {
            System.out.println("ddc: ar, es, fr, tr");
            ddc2 = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText_original_for_ducc_annotators/fasttext"
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "ar,/resources/nlp/models/categorization/ddc/ddc2_2018/wikipedia_ar.v8.token_gnd_ddc.v4_lr0.1-lrUR150-dim300-ws5-ep10000-minC5-minCL0-neg7-ngrams1-bucket2000000-minn0-maxn0-t0.0001-lossns-vec-wiki.ar.vec.epoch100.bin,96,es,/resources/nlp/models/categorization/ddc/ddc2_2018/wikipedia_es.v8.token_gnd_ddc.v4_lr0.1-lrUR150-dim300-ws5-ep10000-minC5-minCL0-neg7-ngrams1-bucket2000000-minn0-maxn0-t0.0001-lossns-vec-wiki.es.vec.bin,95,fr,/resources/nlp/models/categorization/ddc/ddc2_2018/wikipedia_fr.v8.token_gnd_ddc.v4_lr0.1-lrUR150-dim300-ws5-ep10000-minC5-minCL0-neg7-ngrams1-bucket2000000-minn0-maxn0-t0.0001-lossns-vec-wiki.fr.vec.epoch5000.bin,95,tr,/resources/nlp/models/categorization/ddc/ddc2_2018/wikipedia_tr.v8.token_gnd_ddc.v4_lr0.1-lrUR150-dim300-ws5-ep10000-minC5-minCL0-neg7-ngrams1-bucket2000000-minn0-maxn0-t0.0001-lossns-vec-wiki.tr.vec.epoch100.bin,93"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100
                    , LabelAnnotator.PARAM_CUTOFF, false
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_TAGS, "ddc2"
                    , LabelAnnotator.PARAM_USE_LEMMA, false
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, false
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, false
                    , LabelAnnotator.PARAM_LAZY_LOAD, true
                    , LabelAnnotator.PARAM_LAZY_LOAD_MAX, 1
            );
        }
        else {
            System.out.println("ddc: others");
            ddc2 = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText_2018_03_22_test_every_epoch_for_ducc/fasttext"
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "an,/resources/nlp/models/categorization/ddc/ddc_2018_andere/anwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,80,bar,/resources/nlp/models/categorization/ddc/ddc_2018_andere/barwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,81,bn,/resources/nlp/models/categorization/ddc/ddc_2018_andere/bnwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,85,bs,/resources/nlp/models/categorization/ddc/ddc_2018_andere/bswiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,87,ckb,/resources/nlp/models/categorization/ddc/ddc_2018_andere/ckbwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,82,da,/resources/nlp/models/categorization/ddc/ddc_2018_andere/dawiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,93,el,/resources/nlp/models/categorization/ddc/ddc_2018_andere/elwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,91,fa,/resources/nlp/models/categorization/ddc/ddc_2018_andere/fawiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,94,he,/resources/nlp/models/categorization/ddc/ddc_2018_andere/hewiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,93,hi,/resources/nlp/models/categorization/ddc/ddc_2018_andere/hiwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,86,hu,/resources/nlp/models/categorization/ddc/ddc_2018_andere/huwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,91,id,/resources/nlp/models/categorization/ddc/ddc_2018_andere/idwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,92,it,/resources/nlp/models/categorization/ddc/ddc_2018_andere/itwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,93,ja,/resources/nlp/models/categorization/ddc/ddc_2018_andere/jawiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,92,kn,/resources/nlp/models/categorization/ddc/ddc_2018_andere/knwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,79,ko,/resources/nlp/models/categorization/ddc/ddc_2018_andere/kowiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,92,li,/resources/nlp/models/categorization/ddc/ddc_2018_andere/liwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,80,lv,/resources/nlp/models/categorization/ddc/ddc_2018_andere/lvwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,89,mk,/resources/nlp/models/categorization/ddc/ddc_2018_andere/mkwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,88,ml,/resources/nlp/models/categorization/ddc/ddc_2018_andere/mlwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,84,mn,/resources/nlp/models/categorization/ddc/ddc_2018_andere/mnwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,74,mr,/resources/nlp/models/categorization/ddc/ddc_2018_andere/mrwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,83,pt,/resources/nlp/models/categorization/ddc/ddc_2018_andere/ptwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,92,ro,/resources/nlp/models/categorization/ddc/ddc_2018_andere/rowiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,93,ru,/resources/nlp/models/categorization/ddc/ddc_2018_andere/ruwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,96,sh,/resources/nlp/models/categorization/ddc/ddc_2018_andere/shwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,94,simple,/resources/nlp/models/categorization/ddc/ddc_2018_andere/simplewiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,92,si,/resources/nlp/models/categorization/ddc/ddc_2018_andere/siwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,80,sr,/resources/nlp/models/categorization/ddc/ddc_2018_andere/srwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,91,te,/resources/nlp/models/categorization/ddc/ddc_2018_andere/tewiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,80,th,/resources/nlp/models/categorization/ddc/ddc_2018_andere/thwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,86,ur,/resources/nlp/models/categorization/ddc/ddc_2018_andere/urwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,82,vi,/resources/nlp/models/categorization/ddc/ddc_2018_andere/viwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,88,zh,/resources/nlp/models/categorization/ddc/ddc_2018_andere/zhwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,91,pl,/resources/nlp/models/categorization/ddc/models_20191001/plwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim300-ep1000-vec-cc.pl.300.vec.best_epoch.bin,95"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100
                    , LabelAnnotator.PARAM_CUTOFF, false
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_TAGS, "ddc2"
                    , LabelAnnotator.PARAM_USE_LEMMA, false
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, false
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, false
                    , LabelAnnotator.PARAM_LAZY_LOAD, true
                    , LabelAnnotator.PARAM_LAZY_LOAD_MAX, 1
            );
        }

        AnalysisEngineDescription segmenter;
        if (language.equalsIgnoreCase("ja")) {
            System.out.println("segmenter: break");
            segmenter = createEngineDescription(
                    BreakIteratorSegmenter.class
            );
        }
        else {
            System.out.println("segmenter: spacy");
            segmenter = createEngineDescription(
                    SpaCyMultiTagger3.class
                    , SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, dockerPort
                    , SpaCyMultiTagger3.PARAM_DOCKER_REGISTRY, "141.2.89.20:5000"
                    , SpaCyMultiTagger3.PARAM_DOCKER_IMAGE_TAG, "0.8"
            );
        }

        SimplePipeline.runPipeline(reader, segmenter, ddc2, writer);

        System.out.println("lang: " + language);
        System.out.println("in: " + inputDir);
        System.out.println("out: " + outputDir);
    }
}