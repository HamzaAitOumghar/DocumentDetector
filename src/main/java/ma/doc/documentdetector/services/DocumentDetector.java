package ma.doc.documentdetector.services;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentDetector {
    boolean isSafe(MultipartFile file);
}
