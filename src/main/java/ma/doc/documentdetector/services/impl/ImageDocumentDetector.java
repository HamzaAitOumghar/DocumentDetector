package ma.doc.documentdetector.services.impl;

import ma.doc.documentdetector.services.DocumentDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;


@Component
public class ImageDocumentDetector implements DocumentDetector {

    private static final Logger logger = LoggerFactory.getLogger(ImageDocumentDetector.class);

    @Override
    public boolean isSafe(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

    }
}
