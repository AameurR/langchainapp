package com.guestu.langchaindemoapp.ai.documenttransformers;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentTransformer;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class PersonalInfoExtractor implements DocumentTransformer {

    @Autowired
    ChatLanguageModel chatLanguageModel;


    @Override
    public Document transform(Document document) {
        SystemMessage systemMessage = new SystemMessage("""
                You are an assistant.
                Consider the given information as a resume of an ingeneer.
                Extract from the resume the the name, email, phone number, address, and any other personal information.
                """);

        UserMessage userMessage = new UserMessage(document.text());
        String response = chatLanguageModel.generate(List.of(systemMessage, userMessage)).content().text();
        StringBuffer sb = new StringBuffer();
        sb.append(document.text());
        sb.append("\n\n");
        sb.append("====================PERSONNAL INFORMATION====================\n");
        sb.append(response);

        log.info("================== Personnal Infos = " + sb.toString());

        Document newDocument = new Document(sb.toString());
        newDocument.metadata().put("personal_info", response);
        return newDocument;
    }

    @Override
    public List<Document> transformAll(List<Document> documents) {
        return documents.stream().map(this::transform).toList();
    }
}
