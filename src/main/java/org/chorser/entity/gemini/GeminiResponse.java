package org.chorser.entity.gemini;

import org.chorser.entity.gemini.response.Candidate;

import java.util.ArrayList;
import java.util.List;

public class GeminiResponse {

    private List<Candidate> candidates;

    public static class Builder{
        private final List<Candidate> candidates=new ArrayList<>();

        public Builder addCandidate(Candidate candidate){
            candidates.add(candidate);
            return this;
        }

        public GeminiResponse build(){
            return new GeminiResponse(this);
        }
    }

    private GeminiResponse(Builder builder){
        this.candidates=builder.candidates;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }
}
