import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.texttechnologylab.annotation.semaf.semafsr.SrLink;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticRoleLabelingTest {

    @Test
    public void testKafka() throws UIMAException {
        JCas jCas = JCasFactory.createText("Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheuren Ungeziefer verwandelt. Über dem Atlantik befand sich ein barometrisches Minimum; es wanderte ostwärts, einem über Russland lagernden Maximum zu, und verriet noch nicht die Neigung, diesem nördlich auszuweichen.");
        Sentence kafkaSatz = new Sentence(jCas, 0, 133);
        jCas.addFsToIndexes(kafkaSatz);
        Sentence barometrischerSatz = new Sentence(jCas, 134, 321);
        jCas.addFsToIndexes(barometrischerSatz);

        for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
            System.out.printf("'%s'\n", sentence.getCoveredText());
        }

        Pattern pattern = Pattern.compile("(?=\\p{Blank}|$|((?<= )[^\\P{Punct}\\.\\-]+|[^\\P{Punct}\\-]+(?= )|(?<=^)[\\p{Punct}\\p{Blank}]+|[\\p{Punct}\\p{Blank}]+(?=$)))");
        Matcher matcher = pattern.matcher(jCas.getDocumentText());

        int prevIndex = 0;
        while (matcher.find(prevIndex)) {
            int nextIndex = matcher.start();
            if (prevIndex == nextIndex) {
                prevIndex--;
            }
            Token token = new Token(jCas, prevIndex, nextIndex);
            jCas.addFsToIndexes(token);
            prevIndex = nextIndex + 1;
            if (prevIndex > jCas.getDocumentText().length())
                break;
        }
        for (Token token : JCasUtil.select(jCas, Token.class)) {
            System.out.printf("'%s'\n", token.getCoveredText());
        }

        AnalysisEngine engine = AnalysisEngineFactory.createEngine(SemanticRoleLabeling.class);

        SimplePipeline.runPipeline(jCas,
                engine
        );

        for (SrLink srLink : JCasUtil.select(jCas, SrLink.class)) {
            System.out.printf(
                    "'%s' -[%s]-> '%s'\n",
                    srLink.getFigure().getCoveredText(),
                    srLink.getRel_type(),
                    srLink.getGround().getCoveredText()
            );
            System.out.println(srLink.toString(2));
        }
    }
}
