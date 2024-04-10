package org.chorser.entity.gemini.request;

import java.util.ArrayList;
import java.util.List;

public class Content {
    private String role;

    private List<Part> parts;

    public static class Builder{
        private String role;
        private final List<Part> parts=new ArrayList<>();

        public Builder role(String role){
            this.role=role;
            return this;
        }

        public Builder addPart(Part part){
            this.parts.add(part);
            return this;
        }

        public Content build(){
            return new Content(this);
        }

    }

    private Content(Builder builder){
        this.role=builder.role;
        this.parts=builder.parts;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }
}
