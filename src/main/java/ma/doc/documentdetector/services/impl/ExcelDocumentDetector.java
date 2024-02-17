package ma.doc.documentdetector.services.impl;


import ma.doc.documentdetector.services.DocumentDetector;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static ma.doc.documentdetector.utils.DocumentDetectorUtils.*;
import static org.apache.poi.util.StringUtil.isBlank;


@Component
public class ExcelDocumentDetector implements DocumentDetector {

    private static final Logger logger = LoggerFactory.getLogger(ExcelDocumentDetector.class);


    @Override
    public boolean isSafe(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();

            if (isBlank(originalFilename)) {
                return false;
            }

            String contentType = getContentType(fileBytes, originalFilename);

            if (!isValidExcelContentType(contentType)) {
                return false;
            }

            if (isXlsFile(originalFilename)) {
                return !existOLEObjectsOrMacrosInXLS(fileBytes);
            } else if (isXlsxFile(originalFilename)) {
                return !existOLEObjectsOrMacrosInXLSX(fileBytes);
            }

            return false;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    private boolean isValidExcelContentType(String contentType) {
        return contentType != null && EXCEL_MIME_TYPES.contains(contentType);
    }


    private boolean existOLEObjectsOrMacrosInXLS(byte[] filePath) throws IOException {
        try (ByteArrayInputStream fis = new ByteArrayInputStream(filePath); POIFSFileSystem fileSystem = new POIFSFileSystem(fis)) {
            boolean hasOLEObjects = fileSystem.getRoot().hasEntry("ObjectPool");
            boolean hasMacros = fileSystem.getRoot().hasEntry("Macros") || fileSystem.getRoot().hasEntry("_VBA_PROJECT_CUR");
            return hasOLEObjects || hasMacros;
        }
    }

    private boolean existOLEObjectsOrMacrosInXLSX(byte[] filePath) throws IOException, OpenXML4JException {
        try (ByteArrayInputStream fis = new ByteArrayInputStream(filePath); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            boolean hasOLEObjects = !workbook.getAllEmbeddedParts().isEmpty();
            boolean hasMacros = workbook.isMacroEnabled();
            return hasOLEObjects || hasMacros;
        }
    }

    private static boolean isXlsFile(String originalFilename) {
        return originalFilename.endsWith("xls");
    }

    private static boolean isXlsxFile(String originalFilename) {
        return originalFilename.endsWith("xlsx");
    }
}
