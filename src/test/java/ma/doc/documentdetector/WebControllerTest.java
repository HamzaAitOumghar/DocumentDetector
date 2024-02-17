package ma.doc.documentdetector;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

@SpringBootTest
@AutoConfigureMockMvc
class WebControllerTest {


    @Autowired
    private MockMvc mockMvc;

    public final String API_URL = "/test/file";

    @Test
    void fakeImageFileShouldReturnFalse() throws Exception {
        MockMultipartFile mockMultipartFile = buildMockMultiPartFile("images/fake_image.png",MediaType.IMAGE_PNG_VALUE);

        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL).file(mockMultipartFile))
                .andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.jsonPath("isSafe").value(false));

    }

    @Test
    void reelImageFileShouldReturnTrue() throws Exception {
        MockMultipartFile mockMultipartFile = buildMockMultiPartFile("images/reel_image.png",MediaType.IMAGE_PNG_VALUE);

        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL).file(mockMultipartFile))
                .andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.jsonPath("isSafe").value(true));

    }

    private static MockMultipartFile buildMockMultiPartFile(String path, String mediaType) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        return new MockMultipartFile("file", classPathResource.getFilename(), mediaType, FileCopyUtils.copyToByteArray(classPathResource.getInputStream()));
    }

}
