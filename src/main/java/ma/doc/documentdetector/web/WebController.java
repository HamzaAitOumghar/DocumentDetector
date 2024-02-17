package ma.doc.documentdetector.web;


import ma.doc.documentdetector.model.ResponseDto;
import ma.doc.documentdetector.services.DocumentDetector;
import ma.doc.documentdetector.services.DocumentDetectorFactory;
import org.apache.poi.util.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/test")
public class WebController {


    @PostMapping("/file")
    public ResponseEntity<ResponseDto> isSafe(@RequestBody MultipartFile file) {
        final String fileName = file.getOriginalFilename();
        if (StringUtil.isBlank(fileName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The file name is empty.");
        }
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        DocumentDetector documentDetector = DocumentDetectorFactory.getDocumentDetector(fileExtension);
        return ResponseEntity.ok(new ResponseDto(documentDetector.isSafe(file)));
    }


}
