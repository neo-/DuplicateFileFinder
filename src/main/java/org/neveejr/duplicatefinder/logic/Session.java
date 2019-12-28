package org.neveejr.duplicatefinder.logic;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.neveejr.duplicatefinder.model.DuplicateFile;
import org.neveejr.duplicatefinder.model.PausableFileTraverser;
import org.neveejr.duplicatefinder.model.PausableFileTraverser.FileTravrerserCallback;

public class Session implements FileTravrerserCallback {

	public interface SessionCallback {
		void progressUpdate(int numberOfFiles, int addedFiles, int processedFiles);

		void duplicateFound(long fileSize, String md5, String location);

		void stopping();

		void stopped();
	}

	private static final Logger logger = Logger.getLogger(Session.class);
	private ExecutorService consumerService;
	private PausableFileTraverser fileTraverser;
	private Map<Long, DuplicateFile> possibleDuplicateFiles;
	private boolean isStarted;
	private int numberOfFiles = 1;
	private int addedFiles = 0;
	private int processedFiles = 0;
	private SessionCallback sessionCallback;

	public Session(String rootDirectory, int numberOfThreads, SessionCallback sessioncallback) {
		isStarted = false;
		consumerService = Executors.newFixedThreadPool(numberOfThreads);
		fileTraverser = new PausableFileTraverser(rootDirectory, Session.this);
		possibleDuplicateFiles = new ConcurrentHashMap<>();
		this.sessionCallback = sessioncallback;
	}

	@Override
	public void fileFound(File file) {
		addedFiles++;
		logger.info("Files for processing:" + addedFiles);
		sessionCallback.progressUpdate(numberOfFiles, addedFiles, processedFiles);
		consumerService.execute(new Consumer(file));
	}

	private class Consumer implements Runnable {

		private File file;

		public Consumer(File file) {
			this.file = file;
		}

		@Override
		public void run() {

			long threadId = Thread.currentThread().getId();
			String isDuplicate = null;
			DuplicateFile duplicateFile = new DuplicateFile(file);
			duplicateFile = possibleDuplicateFiles.putIfAbsent(file.length(), duplicateFile);
			if (duplicateFile != null) {
				synchronized (duplicateFile) {
					// logger.info(threadId + "#" + file.getName() + " : " + "Possible to be
					// duplicate");
					isDuplicate = duplicateFile.checkAndUpdate(file);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				// logger.info(threadId + "#" + file.getName() + " : " + "Not possible to be
				// duplicate");
			}

			logger.info(
					threadId + "#" + file.getName() + " : " + (isDuplicate != null ? "Duplicate" : "Not Duplicate"));
			if (isDuplicate != null) {
				sessionCallback.duplicateFound(duplicateFile.getSize(), isDuplicate, file.getPath());
			}
			processedFiles++;

			logger.info("Processed files:" + processedFiles);
			sessionCallback.progressUpdate(numberOfFiles, addedFiles, processedFiles);
		}

	}

	public void start() {
		if (!isStarted)
			new Thread(fileTraverser).start();
		else
			logger.warn("Filetraverser already started!");
	}

	@Override
	public void completed() {
		try {
			consumerService.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Shuttingdown consumer");
	}

	@Override
	public void directoryFound(long numberOfFiles) {
		this.numberOfFiles--;
		this.numberOfFiles += numberOfFiles;
		logger.info("Total files:" + this.numberOfFiles);
		sessionCallback.progressUpdate(this.numberOfFiles, addedFiles, processedFiles);

	}

}
