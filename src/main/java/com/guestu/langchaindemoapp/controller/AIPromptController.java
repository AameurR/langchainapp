package com.guestu.langchaindemoapp.controller;

import com.guestu.langchaindemoapp.ai.AdvisorAssitant;
import com.guestu.langchaindemoapp.ai.RagService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class AIPromptController {

    @Autowired
    ChatLanguageModel chatLanguageModel;


    @Autowired
    AdvisorAssitant advisorAssitant;

    @Autowired
    RagService ragService;

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return chatLanguageModel.generate(message);
    }

    @GetMapping("/assistant")
    public String assistant(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return advisorAssitant.chat(message);
    }

    @GetMapping("/rag")
    public String rag(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return ragService.chat(message);
    }

    @GetMapping("/ragpg")
    public String ragpg(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return ragService.chatpg(message);
    }

    @GetMapping("/ragtest")
    public String ragtest(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return ragService.testrag(message);
    }

    @GetMapping("/testchat")
    public String ragtestchat(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        OllamaChatModel llama1 = OllamaChatModel.builder().modelName("llama3.2").temperature(0.8D).baseUrl("http://localhost:11434").build();
        ChatMessage chatMessage= new UserMessage("Hello");
        ChatRequest chatRequest = ChatRequest.builder().messages(List.of(chatMessage)).build();
        ChatResponse chatResponse = llama1.chat(chatRequest);
        log.trace("Chat response : "+chatResponse.aiMessage().text());
        return chatResponse.aiMessage().text();
      //  return "";

    }

}
