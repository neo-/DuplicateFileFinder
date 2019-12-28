package org.neveejr.duplicatefinder;

import org.neveejr.duplicatefinder.logic.Session;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		Session session = new Session("/home/rajeevan/Desktop/TestDirectory", 5, new Session.SessionCallback() {

			@Override
			public void stopping() {
				// TODO Auto-generated method stub

			}

			@Override
			public void stopped() {
				// TODO Auto-generated method stub

			}

			@Override
			public void progressUpdate(int numberOfFiles, int addedFiles, int processedFiles) {
				// TODO Auto-generated method stub

			}

			@Override
			public void duplicateFound(long fileSize, String md5, String location) {
				// TODO Auto-generated method stub

			}
		});
		session.start();
	}
}
