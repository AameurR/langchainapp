package com.guestu.langchaindemoapp.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService(tools = {"CompanyTool.class"})
public interface AdvisorAssitant {
    @SystemMessage("""
            You are an assistant using the company tool.
            You will be asked to provide the company details based on the company name.
            You must provide informations based only on the tool's capabilities.
            """)
    /**
     * Assistant with a Tool that can help him retrieve information, perform calculation, or any other tasks
     */
    String chat(String userMessage);
}

