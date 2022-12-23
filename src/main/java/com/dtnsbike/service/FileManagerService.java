package com.dtnsbike.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.commons.io.FilenameUtils;

@Service
public class FileManagerService {

	@Autowired
	ServletContext app;

	private Path getPath(String folder, String filename) {
//        File dir = ResourceUtils.getFile("classpath:static/upload" + "/" + folder);
		File dir = new File("src/main/resources/static/upload/" + folder);
		return Paths.get(dir.getAbsolutePath(), filename);
	}

	public byte[] read(String folder, String filename) {
		try {
			Path path = this.getPath(folder, filename);
			return Files.readAllBytes(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public File save(String folder, MultipartFile files, String id, RedirectAttributes ra) {
		String ext = FilenameUtils.getExtension(files.getOriginalFilename());
		try {
			if (files.getSize() <= 2000000) {
				if (ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpge")) {
					File dir = new File("src/main/resources/static/upload/" + folder);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					String name = System.currentTimeMillis() + files.getOriginalFilename();
					if (id == null) {
						id = "";
					}
					String filename = id + Integer.toHexString(name.hashCode()) + name.substring(name.lastIndexOf("."));
					File saveFile = new File(dir.getAbsolutePath(), filename);
					files.transferTo(saveFile);

					BufferedImage img = null;
					File f = new File("src/main/resources/static/upload/" + folder + "/" + filename);
					Path path = Paths.get("src/main/resources/static/upload/" + folder + "/" + filename);
					// đọc hình ảnh từ máy tính
					try {
						img = ImageIO.read(f);
						if (img == null) {
							Files.delete(path);
							ra.addAttribute("error", "Wrong image format");
							return null;
						}
					} catch (IOException e) {
						ra.addAttribute("error", "Wrong image format");
						return null;
					}
					return saveFile;
				} else {
					ra.addAttribute("error", "Wrong image format");
					return null;
				}
			} else {
				ra.addAttribute("error", "File size exceeds the allowed limit");
				return null;
			}
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	public void delete(String folder, String filename) {
		Path path = this.getPath(folder, filename);
		path.toFile().delete();
	}

	public List<String> list(String folder) {
		List<String> filenames = new ArrayList<String>();
		File dir;
		dir = new File("src/main/resources/static/upload/" + folder);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				filenames.add(file.getName());
			}
		}
		return filenames;
	}

}
