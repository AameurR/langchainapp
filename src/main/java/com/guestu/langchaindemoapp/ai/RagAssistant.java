package com.guestu.langchaindemoapp.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
@SystemMessage("""
            You are an assistant working with employee resumes.
            You will be asked to provide informations relative to employees careers.
            You must provide informations based only on the provided informations.
            """)
public interface RagAssistant {
    String chat(String userMessage);
}
