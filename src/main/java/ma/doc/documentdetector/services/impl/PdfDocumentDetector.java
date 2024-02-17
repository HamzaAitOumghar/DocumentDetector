package ma.doc.documentdetector.services.impl;

import ma.doc.documentdetector.services.DocumentDetector;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationFileAttachment;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static ma.doc.documentdetector.utils.DocumentDetectorUtils.*;
import static org.apache.poi.util.StringUtil.isBlank;

public class PdfDocumentDetector implements DocumentDetector {

    private static final Logger logger = LoggerFactory.getLogger(PdfDocumentDetector.class);

    @Override
    public boolean isSafe(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            if (isBlank(originalFilename)) {
                return false;
            }
            String contentType = getContentType(fileBytes, originalFilename);
            if (!isValidPdfContentType(contentType)) {
                return false;
            }

            return !containsJavaScriptOrEmbeddedFiles(fileBytes);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;

        }
    }

    private boolean isValidPdfContentType(String contentType) {
        return contentType != null && PDF_MIME_TYPES.contains(contentType);
    }


    public static boolean containsJavaScriptOrEmbeddedFiles(byte[] pdfFileByte) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFileByte)) {
            if (containsJavaScript(document)) return true;

            if (containsEmbeddedFiles(document)) return true;
        }
        return false;
    }

    private static boolean containsEmbeddedFiles(PDDocument document) throws IOException {

        // Document-level embedded files check
        if (document.getDocumentCatalog().getNames() != null) {
            var embeddedFiles = document.getDocumentCatalog().getNames().getEmbeddedFiles();
            if (embeddedFiles != null && !embeddedFiles.getNames().isEmpty()) {
                return true;
            }
        }

        // Annotations with file attachments check
        for (int i = 0; i < document.getNumberOfPages(); ++i) {
            var annotations = document.getPage(i).getAnnotations();
            for (var annotation : annotations) {
                if (annotation instanceof PDAnnotationFileAttachment fileAttachment) {
                    PDFileSpecification fileSpec = fileAttachment.getFile();
                    if (fileSpec instanceof PDComplexFileSpecification complexFileSpec) {
                        if (complexFileSpec.getEmbeddedFile() != null) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean containsJavaScript(PDDocument document) throws IOException {

        // Check document level JavaScript

        if (document.getDocumentCatalog().getOpenAction() instanceof PDActionJavaScript) {
            return true;
        }

        // Check for JavaScript in form fields
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        if (acroForm != null) {
            for (PDField field : acroForm.getFields()) {
                if (field.getActions() != null) {
                    PDFormFieldAdditionalActions actions = field.getActions();
                    if (actions.getC() instanceof PDActionJavaScript) {
                        return true;
                    }
                }
            }
        }

        // Check for JavaScript in annotations
        for (int i = 0; i < document.getNumberOfPages(); ++i) {
            List<PDAnnotation> annotations = document.getPage(i).getAnnotations();
            for (PDAnnotation annotation : annotations) {
                if (annotation instanceof PDAnnotationLink pdAnnotationLink && (pdAnnotationLink.getAction() instanceof PDActionJavaScript)) {
                    return true;

                }
            }
        }
        return false;
    }

}
