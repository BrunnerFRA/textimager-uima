import de.julielab.jcore.types.Enzyme;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import java.io.IOException;
import static org.junit.Assert.assertArrayEquals;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
/**
 * Eccode
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide Eccode test case */
public class EccodeTest {
    /**
     * Test for simple text.
     * @throws UIMAException
     */
    @Test
    public void testProcess() throws IOException, UIMAException {
        JCas jCas = JCasFactory.createText("Acetylesterase has number EC 3.1.1.6");

        //AnalysisEngineDescription engine = createEngineDescription(ECCode.class, ECCode.PARAM_REST_ENDPOINT, "http://localhost:8080");
        AnalysisEngineDescription engine = createEngineDescription(ECCode.class, ECCode.PARAM_DOCKER_REGISTRY, "localhost:5000",
                ECCode.PARAM_DOCKER_NETWORK, "bridge",
                ECCode.PARAM_DOCKER_HOSTNAME, "localhost",
                ECCode.PARAM_DOCKER_HOST_PORT, 8000);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casEnzyme = (String[]) JCasUtil.select(jCas, Enzyme.class).stream().map(a -> a.getSpecificType()).toArray(String[]::new);

        String[] testEnzyme = new String[] {"3.1.1.6"};

        assertArrayEquals(testEnzyme, casEnzyme);
    }
}
