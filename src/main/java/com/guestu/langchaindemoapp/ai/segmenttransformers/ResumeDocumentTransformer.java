package com.guestu.langchaindemoapp.ai.segmenttransformers;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentTransformer;

import java.util.List;

public class ResumeDocumentTransformer implements DocumentTransformer {

    @Override
    public Document transform(Document document) {
        return document;
        // use lightweight model
        //Named entity recognition
        //Summarization
        //Classification
        //Sentiment analysis
        //Language detection
        //Translation
        //Keyword extraction
        //Rangking
        //Text generation


        //extract personal informations like name, address, email, phone number
        //extract skills
        //extract experience
        //extract education
        //extract contact information
        //extract projects
        //extract certifications
        //extract nomber of years of experience
    }

    @Override
    public List<Document> transformAll(List<Document> documents) {
        return documents.stream().map(this::transform).toList();
    }
}
