package com.oussama.space_renting.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oussama.space_renting.SpaceRentingApplication;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.FileInputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpaceRentingApplication.class)
@AutoConfigureMockMvc( addFilters = false)
@ActiveProfiles("test")  // optional
public class S3ImageControllerIntegrationTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should upload the image and return url")
    public void uploadImage_ShouldUploadAndReturnUrl() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/sample.png");

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "sample.png",
                "image/png",
                fileInputStream
        );

        MvcResult result = mockMvc.perform(multipart("/api/v1/s3/images/upload")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").exists())
                .andExpect(jsonPath("$.message").value("Image uploaded successfully"))
                .andExpect(jsonPath("$.fileName").isNotEmpty())
                .andExpect(jsonPath("$.size").value(mockFile.getSize()))
                .andExpect(jsonPath("$.contentType").value("image/png"))
                .andReturn();

        System.out.println( result);
    }

}
