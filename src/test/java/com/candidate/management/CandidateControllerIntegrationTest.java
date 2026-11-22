
package com.candidate.management;

import com.candidate.management.dto.CandidateRequest;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:candidates-test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class CandidateControllerIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    JsonMapper mapper;

    @Test
    void createsReadsSearchesUpdatesAndDeletesCandidate() throws Exception {

        CandidateRequest body = new CandidateRequest(
                "Sam Candidate",
                "sam@example.com",
                "9999999999",
                "Backend Engineer",
                "Java, SQL",
                5,
                "Pune"
        );

        String response = mvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = mapper.readTree(response).get("id").asLong();

        // Read candidate
        mvc.perform(get("/api/candidates/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("sam@example.com"));

        // Search candidates
        mvc.perform(get("/api/candidates")
                        .param("keyword", "sam")
                        .param("location", "Pune"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        // Update candidate
        CandidateRequest updated = new CandidateRequest(
                "Sam Updated",
                "sam@example.com",
                null,
                "Senior Backend Engineer",
                "Java",
                6,
                "Pune"
        );

        mvc.perform(put("/api/candidates/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sam Updated"));

        // Delete candidate
        mvc.perform(delete("/api/candidates/{id}", id))
                .andExpect(status().isNoContent());

        // Verify candidate no longer exists
        mvc.perform(get("/api/candidates/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidCandidateInput() throws Exception {

        CandidateRequest invalid = new CandidateRequest(
                "",
                "not-an-email",
                null,
                null,
                null,
                -1,
                null
        );

        mvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
