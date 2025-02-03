package com.guestu.langchaindemoapp.ai;

import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CompanyTool {

    public record CompanyResponse(
            String companyName,
            String companyDescription,
            String companyLocation
    ) {};

    @Tool("Return company description for {companyName} a given company")
    @Description("""
            This tool provides company description based on the company name. 
            It returns the company name, description and location.
            """)

    public CompanyResponse getCompanyDetails(String companyName) {
     //   log.trace("Company details information retrieval ...");
        return new CompanyResponse(companyName, "IT company that works on opensource projects", "Villa numero 1, Gibraltar1 , dakar , senegal");
    }
}
