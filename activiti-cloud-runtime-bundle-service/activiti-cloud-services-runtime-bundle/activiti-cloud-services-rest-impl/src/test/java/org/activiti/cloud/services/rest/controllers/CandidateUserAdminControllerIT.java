package org.activiti.cloud.services.rest.controllers;

import java.util.Arrays;
import java.util.List;

import org.activiti.api.task.runtime.TaskAdminRuntime;
import org.activiti.cloud.alfresco.config.AlfrescoWebAutoConfiguration;
import org.activiti.cloud.services.core.pageable.SpringPageConverter;
import org.activiti.cloud.services.events.ProcessEngineChannels;
import org.activiti.cloud.services.events.configuration.CloudEventsAutoConfiguration;
import org.activiti.cloud.services.events.configuration.RuntimeBundleProperties;
import org.activiti.cloud.services.events.listeners.CloudProcessDeployedProducer;
import org.activiti.cloud.services.rest.conf.ServicesRestWebMvcAutoConfiguration;
import org.activiti.common.util.conf.ActivitiCoreCommonUtilAutoConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.spring.process.conf.ProcessExtensionsAutoConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CandidateUserAdminControllerImpl.class, secure = true)
@EnableSpringDataWebSupport
@AutoConfigureMockMvc(secure = false)
@Import({RuntimeBundleProperties.class,
        CloudEventsAutoConfiguration.class,
        TaskSamples.class,
        ActivitiCoreCommonUtilAutoConfiguration.class,
        ProcessExtensionsAutoConfiguration.class,
        ServicesRestWebMvcAutoConfiguration.class,
        AlfrescoWebAutoConfiguration.class})
public class CandidateUserAdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskAdminRuntime taskAdminRuntime;

    @MockBean
    private RepositoryService repositoryService;

    @SpyBean
    private SpringPageConverter pageConverter;

    @MockBean
    private ProcessEngineChannels processEngineChannels;

    @MockBean
    private CloudProcessDeployedProducer processDeployedProducer;

    @BeforeEach
    public void setUp() {
        assertThat(pageConverter).isNotNull();
        assertThat(processEngineChannels).isNotNull();
        assertThat(processDeployedProducer).isNotNull();
    }

    @Test
    public void getUserCandidatesShouldUseAlfrescoGuidelineWhenMediaTypeIsApplicationJson() throws Exception {

        List<String> stringList = Arrays.asList("hruser",
                                                "testuser");
        when(taskAdminRuntime.userCandidates("1")).thenReturn(stringList);

        MvcResult result = this.mockMvc.perform(get("/admin/v1/tasks/{taskId}/candidate-users",
                                                    1).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertThatJson(result.getResponse().getContentAsString())
                .node("list.entries[0].entry.user")
                .isEqualTo("hruser");
        assertThatJson(result.getResponse().getContentAsString())
                .node("list.entries[1].entry.user")
                .isEqualTo("testuser");
    }

    @Test
    public void getUserCandidatesShouldHaveProperHALFormat() throws Exception {

        List<String> stringList = Arrays.asList("hruser",
                                                "testuser");
        when(taskAdminRuntime.userCandidates("1")).thenReturn(stringList);

        MvcResult result = this.mockMvc.perform(get("/admin/v1/tasks/{taskId}/candidate-users",
                                                    1).accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        assertThatJson(result.getResponse().getContentAsString())
                .node("_embedded.candidateUsers[0].user")
                .isEqualTo("hruser");
        assertThatJson(result.getResponse().getContentAsString())
                .node("_embedded.candidateUsers[1].user")
                .isEqualTo("testuser");
    }

}
