package net.franzka.sgilt.core.ficheia.dto;

import net.franzka.sgilt.core.prestataire.domain.DetailCategory;

import java.util.List;

/**
 * Structure du schema JSON demandé à OpenAI (Structured Outputs).
 * Champs publics non-record : le générateur de schema du SDK OpenAI (victools, sans support des
 * records Java) introspecte les champs publics des classes simples — voir l'exemple officiel
 * {@code ResponsesStructuredOutputsExample} du SDK. Cette classe n'est qu'un format d'échange
 * technique avec l'API ; {@link FicheIaGenerationDto} reste le DTO exposé par le service.
 */
public class FicheIaGenerationSchema {

    public String shortDescription;
    public String baseline;
    public List<String> offerings;
    public Identity identity;
    public List<Testimonial> testimonials;
    public List<Detail> details;
    public List<Faq> faq;
    public String budget;

    public static class Identity {
        public String quote;
        public String bio;
    }

    public static class Testimonial {
        public String author;
        public String text;
    }

    public static class Detail {
        public String content;
        public DetailCategory category;
    }

    public static class Faq {
        public String question;
        public String answer;
    }
}
