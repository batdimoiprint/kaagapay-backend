package backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    private static final String DEFAULT_FOLDER = "a_kaagapay";

    public String uploadImage(byte[] data) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(data, ObjectUtils.asMap(
                "resource_type", "image",
                "folder", DEFAULT_FOLDER
        ));
        return uploadResult.get("url").toString();
    }

    public String uploadVideo(byte[] data) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(data, ObjectUtils.asMap(
                "resource_type", "video",
                "folder", DEFAULT_FOLDER
        ));
        return uploadResult.get("url").toString();
    }

    public String getMediaUrl(String publicId, String resourceType) {
        String effectiveId = publicId;
        if (!publicId.contains("/")) {
            effectiveId = DEFAULT_FOLDER + "/" + publicId;
        }
        return cloudinary.url().resourceType(resourceType).generate(effectiveId);
    }
}
