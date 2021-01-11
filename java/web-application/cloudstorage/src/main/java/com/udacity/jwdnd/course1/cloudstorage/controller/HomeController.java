package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.form.CredentialForm;
import com.udacity.jwdnd.course1.cloudstorage.form.DeleteForm;
import com.udacity.jwdnd.course1.cloudstorage.form.NoteForm;
import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final NoteService noteService;
    private final UserService userService;
    private final FileService fileService;
    private final CredentialService credentialService;

    public HomeController(NoteService noteService, UserService userService, FileService fileService, CredentialService credentialService) {
        this.noteService = noteService;
        this.userService = userService;
        this.fileService = fileService;
        this.credentialService = credentialService;
    }

    @GetMapping()
    public String getHomeView(Authentication authentication,
                              @ModelAttribute("noteForm") NoteForm noteForm,
                              @ModelAttribute("credentialForm") CredentialForm credentialForm,
                              @ModelAttribute("deleteForm") DeleteForm deleteForm,
                              Model model) {
        String username = authentication.getName();
        if (!username.isEmpty()) {
            User user = userService.getUser(username);
            List<Note> notes = noteService.getAllNotesForUser(user.getUserid());
            model.addAttribute("notes", notes);
            List<File> files = fileService.getAllFilesForUser(user.getUserid());
            model.addAttribute("files", files);
            List<CredentialDisplay> credentials = credentialService.getAllCredentialsForUser(user.getUserid());
            model.addAttribute("credentials", credentials);
        }
        return "home";
    }

    @PostMapping(value = "/notes")
    public String createOrUpdateNote(Authentication authentication,
                                     @ModelAttribute("noteForm") NoteForm noteForm,
                                     @ModelAttribute("credentialForm") CredentialForm credentialForm,
                                     @ModelAttribute("deleteForm") DeleteForm deleteForm,
                                     Model model) {
        String username = authentication.getName();
        String errorMessage = null;
        if (!username.isEmpty()) {
            User user = userService.getUser(username);

            if (noteForm.getNotetitle() != null) {
                if (noteForm.getNoteid() != null) {
                    if (noteService.updateNote(new Note(noteForm.getNoteid(), noteForm.getNotetitle(), noteForm.getNotedescription(), user.getUserid())) <= 0) {
                        errorMessage = "Error: Failed to update Note";
                    }
                } else {
                    if (noteService.createNote(new Note(noteForm.getNoteid(), noteForm.getNotetitle(), noteForm.getNotedescription(), user.getUserid())) <= 0) {
                        errorMessage = "Error: Failed to create Note";
                    }
                }
                noteForm.setNoteid(null);
                noteForm.setNotetitle(null);
                noteForm.setNotedescription(null);
            }
            model.addAttribute("notes", noteService.getAllNotesForUser(user.getUserid()));
        }
        if (errorMessage == null) {
            model.addAttribute("success", true);
        }
        else {
            model.addAttribute("error", errorMessage);
        }
        return "result";
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public String uploadFile(Authentication authentication,
                             @RequestParam("fileUpload") MultipartFile file,
                             @ModelAttribute("noteForm") NoteForm noteForm,
                             @ModelAttribute("credentialForm") CredentialForm credentialForm,
                             @ModelAttribute("deleteForm") DeleteForm deleteForm, Model model) {
        String username = authentication.getName();
        String errorMessage = null;
        if (!username.isEmpty()) {
            User user = userService.getUser(username);
            if (file != null && !file.isEmpty()) {
                if (!fileService.isFilenameAvailable(file.getOriginalFilename())) {
                    return "home";
                }
                byte[] filedata = null;
                try {
                    filedata = file.getBytes();
                } catch (IOException e) {
                    errorMessage = "Error: Fail to upload file.";
                }
                if (errorMessage == null && fileService.addFile(new File(null, file.getOriginalFilename(), file.getContentType(), "" + file.getSize(), user.getUserid(), filedata)) <= 0) {
                    errorMessage = "Error: Fail to add file to drive.";
                }
                file = null;
            }
            model.addAttribute("files", fileService.getAllFilesForUser(user.getUserid()));
        }
        if (errorMessage == null) {
            model.addAttribute("success", true);
        }
        else {
            model.addAttribute("error", errorMessage);
        }
        return "result";
    }

    @GetMapping(value = "/files/{filename}")
    public void downloadFile(@PathVariable("filename") String fileName,
                             @ModelAttribute("noteForm") NoteForm noteForm,
                             @ModelAttribute("credentialForm") CredentialForm credentialForm,
                             @ModelAttribute("deleteForm") DeleteForm deleteForm,
                             HttpServletResponse response) {
        File file = fileService.getFileByName(fileName);
        // Content-Type
        // application/pdf
        response.setContentType(file.getContenttype());

        // Content-Disposition
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getFilename());

        // Content-Length
        response.setContentLength(Integer.parseInt(file.getFilesize()));

        try {
            InputStream inputStream = new ByteArrayInputStream(file.getFiledata());
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
        catch(IOException e) {
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    @PostMapping(value = "/credentials")
    public String createOrUpdateCredentials(Authentication authentication,
                                     @ModelAttribute("credentialForm") CredentialForm credentialForm,
                                     @ModelAttribute("noteForm") NoteForm noteForm,
                                     @ModelAttribute("deleteForm") DeleteForm deleteForm,
                                     Model model) {
        String username = authentication.getName();
        String errorMessage = null;
        if (!username.isEmpty()) {
            User user = userService.getUser(username);

            if (credentialForm.getUrl() != null) {
                if (credentialForm.getCredentialid() != null) {
                    Credential oldCredential = credentialService.getCredentialById(credentialForm.getCredentialid());
                    if (credentialService.updateCredential(oldCredential, credentialForm) <= 0) {
                        errorMessage = "Error: Failed to update credential";
                    }
                } else {
                    if (credentialService.createCredential(new Credential(null, credentialForm.getUrl(), credentialForm.getUsername(),
                            null, credentialForm.getPassword(), user.getUserid())) <= 0) {
                        errorMessage = "Error: Failed to create credential";
                    }
                }
                credentialForm.setCredentialid(null);
                credentialForm.setUrl(null);
                credentialForm.setUsername(null);
                credentialForm.setPassword(null);
            }
            model.addAttribute("credentials", credentialService.getAllCredentialsForUser(user.getUserid()));
        }
        if (errorMessage == null) {
            model.addAttribute("success", true);
        }
        else {
            model.addAttribute("error", errorMessage);
        }
        return "result";
    }

    @PostMapping(value = "/delete")
    public String deleteData(Authentication authentication,
                             @ModelAttribute("deleteForm") DeleteForm deleteForm,
                             @ModelAttribute("noteForm") NoteForm noteForm,
                             @ModelAttribute("credentialForm") CredentialForm credentialForm,
                             Model model) {
        String username = authentication.getName();
        String errorMessage = null;
        if (!username.isEmpty()) {
            User user = userService.getUser(username);

            if (deleteForm.getId() != null) {
                switch (deleteForm.getDataType()) {
                    case "Notes":
                        if (noteService.deleteNote(deleteForm.getId()) <= 0) {
                            errorMessage = "Error: Failed to delete Note";
                        }
                        break;
                    case "Files":
                        if (fileService.deleteFile(deleteForm.getId()) <= 0) {
                            errorMessage = "Error: Failed to delete File";
                        }
                        break;
                    case "Credentials":
                        if (credentialService.deleteCredential(deleteForm.getId()) <= 0) {
                            errorMessage = "Error: Failed to delete Credential";
                        }
                        break;
                }
                deleteForm.setId(null);
                deleteForm.setDataType(null);
            }
            model.addAttribute("notes", noteService.getAllNotesForUser(user.getUserid()));
            model.addAttribute("files", fileService.getAllFilesForUser(user.getUserid()));
            model.addAttribute("credentials", credentialService.getAllCredentialsForUser(user.getUserid()));
        }
        if (errorMessage == null) {
            model.addAttribute("success", true);
        }
        else {
            model.addAttribute("error", errorMessage);
        }
        return "result";
    }
}
