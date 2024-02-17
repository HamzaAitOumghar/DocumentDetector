package ma.doc.documentdetector.services;


import ma.doc.documentdetector.services.impl.ExcelDocumentDetector;
import ma.doc.documentdetector.services.impl.ImageDocumentDetector;
import ma.doc.documentdetector.services.impl.PdfDocumentDetector;

public class DocumentDetectorFactory {
    private DocumentDetectorFactory() {
    }

    public static DocumentDetector getDocumentDetector(String fileExtension) {
        return switch (fileExtension.toLowerCase()) {
            case "xls", "xlsx" -> new ExcelDocumentDetector();
            case "pdf" -> new PdfDocumentDetector();
            case "png", "jpeg", "jpg", "tiff", "tif" -> new ImageDocumentDetector();
            default -> throw new IllegalArgumentException("Unsupported file type: " + fileExtension);
        };
    }

}
