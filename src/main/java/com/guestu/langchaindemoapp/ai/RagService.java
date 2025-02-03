package com.guestu.langchaindemoapp.ai;

import com.guestu.langchaindemoapp.ai.documenttransformers.PersonalInfoExtractor;
import com.guestu.langchaindemoapp.ai.documenttransformers.ResumeSummarizationTransformer;
import com.guestu.langchaindemoapp.ai.retrieveraugmentors.AugmentDataFromDB;
import com.guestu.langchaindemoapp.ai.segmenttransformers.ResumeSegementTransformer;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RagService {
    @Autowired
    ChatLanguageModel chatLanguageModel;


    @Autowired
    ResumeSummarizationTransformer resumeSummarizationTransformer;

    @Autowired
    PersonalInfoExtractor personalInfoExtractor;

    @Autowired
    AugmentDataFromDB augmentDataFromDB;

    /**
     * Chat using an assistant powered with:
     *      -- a memory that can be redis
     *      -- An in memory embedding store (not postgres)
     * @param userMessage
     * @return the LLM response
     */
    public String chat(String userMessage) {
       // log.trace("Loading documents from src/main/resources/ragdoc");
        List<Document> documentList = FileSystemDocumentLoader.loadDocuments("src/main/resources/ragdoc");

    //    log.trace("Creating memory store");
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

  //      log.trace("Embedding in memory store");
        EmbeddingStoreIngestor.ingest(documentList, embeddingStore);

  //      log.trace("Creating the  Assistant");
        RagAssistant ragAssistant = AiServices.builder(RagAssistant.class)  //Todo: Create the RagAssitant as a  bean and inject It
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))  //Todo: Implement a redis memory and use it here
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

    //    log.trace("Chatting with the assistant");
        return ragAssistant.chat(userMessage);
    }

    /**
     * Rag with Postgresql
     * @param userMessage
     * @return
     */
    public String chatpg(String userMessage) {

        log.trace("Reading the document from src/main/resources/ragdoc ...");
        List<Document> documentList = FileSystemDocumentLoader.loadDocuments("src/main/resources/ragdoc",new ApacheTikaDocumentParser());

        documentList.stream().map(document -> document.metadata().put("PREFIX", UUID.randomUUID().toString())).collect(Collectors.toList());

        log.trace("Splitting the document by paragraph ...");
       DocumentByParagraphSplitter documentByParagraphSplitter = new DocumentByParagraphSplitter(1024, 24);

        log.trace("Creating AllMiniLmL6V2 embedding model ...");
        EmbeddingModel embeddingModel= new AllMiniLmL6V2EmbeddingModel();  //Todo: Create a bean and inject it

        log.trace("Creating a embbeding store ...");
        EmbeddingStore<TextSegment> embeddingStore = PgVectorEmbeddingStore.builder()  //Todo: Create a bean and inject it. All the informations from application.properties
                .host("localhost")
                .port(5432)
                .database("postgres")
                .user("postgres")
                .password("postgres")
                .table("vector_store2")
                .createTable(true)
                .dropTableFirst(true)
                .dimension(384)
                .build();


        log.trace("Ingesting the documents in the embedding store ...");
        EmbeddingStoreIngestor.builder()  //Todo: Create a bean and inject it
                .documentTransformer(resumeSummarizationTransformer)
                .documentTransformer(personalInfoExtractor)
                .documentSplitter(documentByParagraphSplitter)
                .textSegmentTransformer(new ResumeSegementTransformer())
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build()
                .ingest(documentList);
/*
        RetrievalAugmentor retrievalAugmentor= new RetrievalAugmentor() {
            @Override
            public UserMessage augment(UserMessage userMessage, Metadata metadata) {
                ChatMessage chatMessage = new UserMessage("CONTEXT:"+documentList.get(0).text());
                ChatMessage chatMessage1 = new UserMessage("Can you provide me with the informations in the context the nomber oy year of experience of the engineer?");

                ChatMessage systemMessage = new SystemMessage("This document is an ingeneer resume. You will be asked to extract informations from the document based on the context. If the information is not in the context, respond 'I don't know'.");

                Response<AiMessage> aiMessageResponse = chatLanguageModel.generate(List.of(systemMessage, chatMessage, chatMessage1));
                return new UserMessage(aiMessageResponse.content().text());
            }
        };

*/

        log.trace("Creating the assistant ...");
        RagAssistant ragAssistant = AiServices.builder(RagAssistant.class)  //Todo: Create a bean and inject it
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .retrievalAugmentor(augmentDataFromDB)
                .contentRetriever(EmbeddingStoreContentRetriever.builder().embeddingModel(embeddingModel).embeddingStore(embeddingStore).build())
                .build();

     //   log.trace("Chating with the assistant");
        return ragAssistant.chat(userMessage);
    }


    /**
     * Rag with Postgresql
     * @param userMessage
     * @return
     */
    public String testrag(String userMessage) {

          log.trace("Reading the document from src/main/resources/ragdoc ...");
        List<Document> documentList = FileSystemDocumentLoader.loadDocuments("src/main/resources/ragdoc",new ApacheTikaDocumentParser());

        DocumentByParagraphSplitter documentByParagraphSplitter = new DocumentByParagraphSplitter(1000, 24);
        List<TextSegment> split = documentByParagraphSplitter.split(documentList.get(0));

        ChatMessage chatMessage = new UserMessage("CONTEXT:"+documentList.get(0).text());
        ChatMessage chatMessage1 = new UserMessage("Can you provide me with the informations in the context the nomber oy year of experience of the engineer?");

        ChatMessage systemMessage = new SystemMessage("This document is an ingeneer resume. You will be asked to extract informations from the document based on the context. If the information is not in the context, respond 'I don't know'.");

        Response<AiMessage> aiMessageResponse = chatLanguageModel.generate(List.of(systemMessage, chatMessage, chatMessage1));

        log.trace("==========response ========== : "+aiMessageResponse.content().text());

        return "ragAssistant.chat(userMessage)";
    }
}
