package pl.polsl.snapsort.controller;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.polsl.snapsort.models.Photo;
import pl.polsl.snapsort.models.PhotoData;
import pl.polsl.snapsort.models.ThumbnailData;
import pl.polsl.snapsort.service.PhotoDataService;
import pl.polsl.snapsort.service.PhotoService;
import pl.polsl.snapsort.service.ThumbnailDataService;



@RestController
@RequestMapping ("/photos")
public class PhotoController {
    private final PhotoService photoService;
    private final PhotoDataService photoDataService;
    private final ThumbnailDataService thumbnailDataService;

    public PhotoController(PhotoService photoService, PhotoDataService photoDataService, ThumbnailDataService thumbnailDataService) {
        this.photoService = photoService;
        this.photoDataService = photoDataService;
        this.thumbnailDataService = thumbnailDataService;
    }

    // Endpoint methods will be implemented here
    @PostMapping("/upload")
    public ResponseEntity<Photo> uploadPhoto(@RequestParam("file") MultipartFile file, @RequestParam("description") String description) {
        try {
            // Create a new PhotoData entity and save the photo data
            PhotoData photoData = new PhotoData(file.getBytes());
            photoData = photoDataService.savePhotoData(photoData);

            // Generate the thumbnail data
            byte[] thumbnailData = generateThumbnail(file);

            // Create a new ThumbnailData entity and save the thumbnail data
            ThumbnailData thumbnail = new ThumbnailData(thumbnailData);
            thumbnail = thumbnailDataService.saveThumbnailData(thumbnail);

            // Create a new Photo entity and associate it with the PhotoData and ThumbnailData entities
            Photo photo = new Photo();
            photo.setDescription(description);
            photo.setPhotoData(photoData);
            photo.setThumbnailData(thumbnail);

            // Save the Photo entity in the Photo table
            photo = photoService.createPhoto(photo);

            return ResponseEntity.ok(photo);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint to fetch a specific photo by ID
    @GetMapping ("/{id}")
    public ResponseEntity<Photo> getPhoto(@PathVariable Long id) {
        try {
            Photo photo = photoService.getPhotoById(id);
            return ResponseEntity.ok(photo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint to fetch the thumbnail of a specific photo by ID
    @GetMapping("/{id}/thumbnail")
    public ResponseEntity<ThumbnailData> getPhotoThumbnail(@PathVariable Long id) {
        try {
            ThumbnailData thumbnail = photoService.getThumbnailByPhotoId(id);
            return ResponseEntity.ok(thumbnail);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }



    private byte[] generateThumbnail(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
        // Load the original image
        BufferedImage originalImage = ImageIO.read(inputStream);

        // Define the desired thumbnail size
        int thumbnailWidth = 200;
        int thumbnailHeight = 200;

        // Create a thumbnail image with the desired size
        BufferedImage thumbnailImage = new BufferedImage(thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_RGB);

        // Resize the original image to fit the thumbnail size
        Graphics2D graphics2D = thumbnailImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, thumbnailWidth, thumbnailHeight, null);
        graphics2D.dispose();

        // Compress the thumbnail image into a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(thumbnailImage, "JPEG", outputStream);

        // Get the compressed thumbnail data as a byte array
        byte[] thumbnailData = outputStream.toByteArray();

        // Close the streams
        inputStream.close();
        outputStream.close();

        // Return the thumbnail data
        return thumbnailData;

        }
    }
}
