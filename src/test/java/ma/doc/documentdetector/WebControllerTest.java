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
        MockMultipartFile mockMultipartFile = buildMockMultiPartFile("images/fake_image.png");

        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL).file(mockMultipartFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("isSafe").value(false));

    }

    @Test
    void reelImageFileShouldReturnTrue() throws Exception {
        MockMultipartFile mockMultipartFile = buildMockMultiPartFile("images/reel_image.png");

        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL).file(mockMultipartFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("isSafe").value(true));

    }

    @Test
    void simpleExcelFileShouldReturnTrue() throws Exception {
        MockMultipartFile mockMultipartFile = buildMockMultiPartFile("excel/excel_normal.xlsx");

        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL).file(mockMultipartFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("isSafe").value(true));

    }


    @Test
    void excelFileWithOleObjectShouldReturnFalse() throws Exception {
        MockMultipartFile mockMultipartFile = buildMockMultiPartFile("excel/excel_ole_object.xlsx");

        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL).file(mockMultipartFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("isSafe").value(false));

    }

    @Test
    void fakeExcelFileShouldReturnFalse() throws Exception {
        MockMultipartFile mockMultipartFile = buildMockMultiPartFile("excel/fake_excel.xls");

        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL).file(mockMultipartFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("isSafe").value(false));

    }


    @Test
    void simplePdfFileShouldReturnTrue() throws Exception {
        MockMultipartFile mockMultipartFile = buildMockMultiPartFile("pdf/reel_pdf.pdf");
        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL).file(mockMultipartFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("isSafe").value(true));

    }

    @Test
    void fakePdfFileShouldReturnFalse() throws Exception {
        MockMultipartFile mockMultipartFile = buildMockMultiPartFile("pdf/fake_pdf.pdf");
        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL).file(mockMultipartFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("isSafe").value(false));

    }

    @Test
    void pdfFileWithJsShouldReturnFalse() throws Exception {
        MockMultipartFile mockMultipartFile = buildMockMultiPartFile("pdf/pdf_with_js.pdf");
        mockMvc.perform(MockMvcRequestBuilders.multipart(API_URL).file(mockMultipartFile))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("isSafe").value(false));

    }

    private static MockMultipartFile buildMockMultiPartFile(String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        return new MockMultipartFile("file", classPathResource.getFilename(), MediaType.MULTIPART_FORM_DATA_VALUE, FileCopyUtils.copyToByteArray(classPathResource.getInputStream()));
    }

}
