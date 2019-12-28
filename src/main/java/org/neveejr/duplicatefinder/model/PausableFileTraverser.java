package org.neveejr.duplicatefinder.model;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

public class PausableFileTraverser implements Runnable {

	public interface FileTravrerserCallback {
		void fileFound(File file);

		void directoryFound(long numberOfFiles);

		void completed();
	}

	private static final Logger logger = Logger.getLogger(PausableFileTraverser.class);

	private volatile boolean running = true;
	private volatile boolean paused = false;
	private final Object pauseLock = new Object();
	private final Queue<File> directoryQueue;
	private final FileTravrerserCallback callback;

	public PausableFileTraverser(String rootDirectory, FileTravrerserCallback callback) {
		directoryQueue = new LinkedList<File>();
		File file = new File(rootDirectory);
		if (file.exists() && file.isDirectory()) {
			directoryQueue.add(file);
		}
		this.callback = callback;

	}

	private void pauseThread() {
		synchronized (pauseLock) {
			if (!running) {
				return;
			}
			if (paused) {
				try {
					pauseLock.wait();
				} catch (InterruptedException ex) {
					return;
				}
				if (!running) {
					return;
				}
			}
		}
	}

	private void checkDirectory() {
		if (!directoryQueue.isEmpty()) {
			File directory = directoryQueue.poll();
			File[] fileList = directory.listFiles();
			callback.directoryFound(fileList.length);
			// logger.info("Check Directory:" + directory.getName());
			for (File file : fileList) {
				if (file.isDirectory()) {
					// logger.info("Add Directory:" + file.getName());
					directoryQueue.add(file);
				} else {
					// logger.info("Process file:" + file.getName());
					callback.fileFound(file);
				}
				pauseThread();
			}
		}

	}

	public void run() {
		while (!directoryQueue.isEmpty() && running) {
			checkDirectory();
		}
		callback.completed();
	}

	public void stop() {
		running = false;
		resume();
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		synchronized (pauseLock) {
			paused = false;
			pauseLock.notifyAll();
		}
	}

}
