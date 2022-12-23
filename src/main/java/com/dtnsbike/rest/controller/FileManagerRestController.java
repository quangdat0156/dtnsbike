package com.dtnsbike.rest.controller;

import java.io.File;
import java.util.List;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.dtnsbike.service.FileManagerService;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@CrossOrigin("*")
@RestController
public class FileManagerRestController {

	@Autowired
	FileManagerService fileService;
	@Autowired
	SessionService session;

	@GetMapping(value = "/rest/upload/{folder}/{file}", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> download(@PathVariable("folder") String folder, @PathVariable("file") String file) {
		byte[] bytes = fileService.read(folder, file);
		return ResponseEntity.ok().body(bytes);
	}

	@PostMapping("/rest/upload/{folder}")
	public ResponseEntity<JsonNode> upload(@PathVariable("folder") String folder,
			@PathParam("files") MultipartFile files, @PathParam("id") String id, RedirectAttributes ra) {
		File savedFile = fileService.save(folder, files, id, ra);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("name", savedFile.getName());
		node.put("size", savedFile.length());
		session.set("uploadImg", node);
		return ResponseEntity.ok(node);

	}

	@DeleteMapping("/rest/uploads/{folder}/{file}")
	public void delete(@PathVariable("folder") String folder, @PathVariable("file") String file) {
		fileService.delete(folder, file);
	}

	@GetMapping("/rest/uploads/{folder}")
	public List<String> list(@PathVariable("folder") String folder) {
		return fileService.list(folder);
	}
}
