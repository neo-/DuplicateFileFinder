package org.neveejr.duplicatefinder.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.neveejr.duplicatefinder.util.Crypto;

public class DuplicateFile {

	private long fileSize;

	private File rootFile;

	private Map<String, List<File>> duplicateList;
	private boolean isDuplicatesAvailable;

	public DuplicateFile(File file) {
		this.fileSize = file.length();
		this.rootFile = file;
		duplicateList = new ConcurrentHashMap<>();
		isDuplicatesAvailable = false;
	}

	public String checkAndUpdate(File file) {
		if (duplicateList.isEmpty()) {
			String rootMD5 = Crypto.getMd5(rootFile.getPath());
			// logger.info(threadId + "# (root file)" + rootFile.getName() + " " + rootMD5);
			List<File> duplicateFiles = new ArrayList<>();
			duplicateFiles.add(rootFile);
			duplicateList.put(rootMD5, duplicateFiles);
		}

		String md5 = Crypto.getMd5(file.getPath());
		// logger.info(threadId + "#" + file.getName() + " " + md5);
		List<File> fileList = duplicateList.get(md5);
		String isDuplicate = fileList != null && !fileList.isEmpty() ? md5 : null;

		if (fileList == null) {
			fileList = new ArrayList<>();
			duplicateList.put(md5, fileList);
		}

		fileList.add(file);

		return isDuplicate;
	}

	public boolean isDuplicatesAvailable() {
		return isDuplicatesAvailable;
	}

	public long getSize() {
		return fileSize;
	}

}
