package org.activiti.cloud.starter.audit.tests.it.swagger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.nio.file.Files;
import org.activiti.cloud.starter.audit.tests.it.ContainersApplicationInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = ContainersApplicationInitializer.class)
public class AuditSwaggerITSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * This is not a test. It's actually generating the swagger.json and yaml definition of the service.
     * It is used by maven generate-swagger profile build.
     */
    @Test
    public void generateSwagger() throws Exception {
        mockMvc.perform(get("/v2/api-docs").accept(MediaType.APPLICATION_JSON))
            .andDo((result) -> {
                JsonNode jsonNodeTree = objectMapper.readTree(result.getResponse().getContentAsByteArray());
                Files.write(new File("target/swagger.json").toPath(),
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(jsonNodeTree));
                Files.write(new File("target/swagger.yaml").toPath(),
                    new YAMLMapper().writeValueAsBytes(jsonNodeTree));
            });
    }
}
