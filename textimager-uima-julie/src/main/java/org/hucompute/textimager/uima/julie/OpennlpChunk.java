package org.hucompute.textimager.uima.julie;

public class OpennlpChunk extends JulieBase {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/opennlpChunk";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }
}

