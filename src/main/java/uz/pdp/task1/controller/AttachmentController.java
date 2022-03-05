package uz.pdp.task1.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.pdp.task1.AttachmentRepository;
import uz.pdp.task1.entity.Attachment;
import uz.pdp.task1.entity.AttachmentContent;
import uz.pdp.task1.repository.AttachmentContentRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class AttachmentController {

    final static String directory = "uploaded_files";
    final AttachmentRepository attachmentRepository;
    final AttachmentContentRepository attachmentContentRepository;


    // uploading file :
    @SneakyThrows
    @PostMapping("/upload") // to DB
    public String upload(MultipartHttpServletRequest request) {
        Iterator<String> fileNames = request.getFileNames();

        while (fileNames.hasNext()) {
            MultipartFile file = request.getFile(fileNames.next());

            Attachment attachment = new Attachment();
            if (file != null) {
                attachment.setContentType(file.getContentType());
                attachment.setSize(file.getSize());
                attachment.setOriginalFileName(attachment.getOriginalFileName());

                Attachment save = attachmentRepository.save(attachment);

                // saving content :

                AttachmentContent attachmentContent = new AttachmentContent();

                attachmentContent.setAttachment(save);
                attachmentContent.setBytes(file.getBytes());

                AttachmentContent save1 = attachmentContentRepository.save(attachmentContent);

                return "saqlandi ID: " + save.getId();
            }
        }
        return "saqlanmadi!";
    }

    @SneakyThrows
    @GetMapping("/download/{id}")// download from db
    public void download(@PathVariable Integer id, HttpServletResponse response) {
        Optional<Attachment> byId = attachmentRepository.findById(id);
        if (byId.isPresent()) {
            Attachment attachment = byId.get();
            if (attachmentContentRepository.findByAttachmentId(id).isPresent()) {
                AttachmentContent attachmentContent = attachmentContentRepository.findByAttachmentId(id).get();

                // fayl nomi
                response.setHeader("Content-Disposition",
                        "attachment; filename = \""
                                + attachment.getOriginalFileName() + "\"");

                //type:
                response.setContentType(attachment.getContentType());// nima bo'lasa shuni ayatadi

                // content :
                FileCopyUtils.copy(attachmentContent.getBytes(), response.getOutputStream());
            }
        }
    }

    // getting info :

    @GetMapping("/info")
    public List<Attachment> info() {
        return attachmentRepository.findAll();
    }

    @GetMapping("/info/{id}")
    public Attachment info(@PathVariable Integer id) {
        return attachmentRepository.findById(id).orElse(new Attachment());
    }

    ///===================================Server upload and download================

    @SneakyThrows
    @PostMapping("/upload_server")
    public String upload_server(MultipartHttpServletRequest request) {
        Iterator<String> fileNames = request.getFileNames();

        while (fileNames.hasNext()) {
            MultipartFile file = request.getFile(fileNames.next());

            Attachment attachment = new Attachment();
            if (file != null) {
                attachment.setContentType(file.getContentType());
                attachment.setSize(file.getSize());
                attachment.setOriginalFileName(file.getOriginalFilename());

                String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
                String name = split[split.length - 1];
                attachment.setName(UUID.randomUUID() + "." + name);

                Attachment save = attachmentRepository.save(attachment);

                // saving content to server:

                Path path = Path.of(directory+"/"+attachment.getName());

                Files.copy(request.getInputStream(),path);

                return "saqlandi ID: " + save.getId();
            }
        }
        return "saqlanmadi!";
    }

    @SneakyThrows
    @GetMapping("/download_server/{id}")
    public void download_server(@PathVariable Integer id,HttpServletResponse response){
        Optional<Attachment> byId = attachmentRepository.findById(id);
        if (byId.isPresent()) {
            Attachment attachment = byId.get();

            // fayl nomi
            response.setHeader("Content-Disposition",
                    "attachment; filename = \""
                            + attachment.getOriginalFileName() + "\"");

            //type:
            response.setContentType(attachment.getContentType());// nima bo'lasa shuni ayatadi
            // content :
            FileInputStream fileInputStream = new FileInputStream(directory+"/"+attachment.getName());
            FileCopyUtils.copy(fileInputStream, response.getOutputStream());

        }
    }


}
