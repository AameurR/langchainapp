package com.guestu.langchaindemoapp.ai.retrieveraugmentors;

import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.query.Metadata;
import org.springframework.stereotype.Component;

@Component
public class AugmentDataFromDB implements RetrievalAugmentor {
    @Override
    public UserMessage augment(UserMessage userMessage, Metadata metadata) {
       Content content= new TextContent("information from the database ");

        userMessage.contents().add(content);
        return userMessage;
    }
}
