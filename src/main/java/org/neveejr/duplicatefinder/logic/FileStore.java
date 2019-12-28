package org.neveejr.duplicatefinder.logic;

import java.util.HashSet;
import java.util.Set;

public class FileStore {

	private static Set<Long> fileSizes = new HashSet<Long>();

	public static void reset() {
		fileSizes = new HashSet<Long>();
	}

	public static boolean isPossibleDuplicate(Long fileSize) {
		return !fileSizes.add(fileSize);
	}

}
