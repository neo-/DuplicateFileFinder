package org.neveejr.duplicatefinder.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;

import org.neveejr.duplicatefinder.logic.Session;
import org.neveejr.duplicatefinder.logic.Session.SessionCallback;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Application implements ActionListener, SessionCallback {

	private enum Status {
		IDLE, DIRECTORY_SELECTED, STARTED, PAUSED, STOPPING, STOPPED
	}

	private Status status = Status.IDLE;
	private String rootDirectory;
	private Session session;

	// UI
	private JFrame frame;
	private JButton btnBrowse;
	private JLabel lblDirectory;
	private JProgressBar progressFiles;
	private JProgressBar progressProcess;
	private JLabel lblFiles;
	private JLabel lblProcess;
	private JButton btnStart;
	private JButton btnPause;
	private JButton btnStop;

	private JFileChooser directoryChooser;

	private void uiUpdate() {
		switch (status) {
		case IDLE:
			btnBrowse.setEnabled(true);
			btnStart.setEnabled(false);
			btnPause.setEnabled(false);
			btnStop.setEnabled(false);
			btnPause.setText("Pause");
			break;
		case DIRECTORY_SELECTED:
			btnBrowse.setEnabled(true);
			btnStart.setEnabled(true);
			btnPause.setEnabled(false);
			btnStop.setEnabled(false);
			btnPause.setText("Pause");
			break;
		case PAUSED:
			btnBrowse.setEnabled(false);
			btnStart.setEnabled(false);
			btnPause.setEnabled(true);
			btnStop.setEnabled(true);
			btnPause.setText("Resume");
			break;
		case STARTED:
			btnBrowse.setEnabled(false);
			btnStart.setEnabled(false);
			btnPause.setEnabled(true);
			btnStop.setEnabled(true);
			btnPause.setText("Pause");
			break;
		case STOPPED:
			btnBrowse.setEnabled(true);
			btnStart.setEnabled(true);
			btnPause.setEnabled(false);
			btnStop.setEnabled(false);
			btnPause.setText("Pause");
			break;
		case STOPPING:
			btnBrowse.setEnabled(false);
			btnStart.setEnabled(false);
			btnPause.setEnabled(false);
			btnStop.setEnabled(false);
			btnPause.setText("Pause");
			break;
		default:
			break;

		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application window = new Application();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Application() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblDirectoryText = new JLabel("Directory");
		GridBagConstraints gbc_lblDirectory = new GridBagConstraints();
		gbc_lblDirectory.insets = new Insets(0, 0, 5, 5);
		gbc_lblDirectory.gridx = 0;
		gbc_lblDirectory.gridy = 0;
		panel.add(lblDirectoryText, gbc_lblDirectory);

		lblDirectory = new JLabel("");
		GridBagConstraints gbc_lblDirecotyr = new GridBagConstraints();
		gbc_lblDirecotyr.insets = new Insets(0, 0, 5, 5);
		gbc_lblDirecotyr.gridx = 1;
		gbc_lblDirecotyr.gridy = 0;
		panel.add(lblDirectory, gbc_lblDirecotyr);

		btnBrowse = new JButton("Browse");
		GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
		gbc_btnBrowse.insets = new Insets(0, 0, 5, 0);
		gbc_btnBrowse.gridx = 2;
		gbc_btnBrowse.gridy = 0;
		panel.add(btnBrowse, gbc_btnBrowse);
		btnBrowse.addActionListener(Application.this);

		JLabel lblFilesRead = new JLabel("Files Read");
		GridBagConstraints gbc_lblFilesRead = new GridBagConstraints();
		gbc_lblFilesRead.insets = new Insets(0, 0, 5, 5);
		gbc_lblFilesRead.gridx = 0;
		gbc_lblFilesRead.gridy = 1;
		panel.add(lblFilesRead, gbc_lblFilesRead);

		progressFiles = new JProgressBar();
		GridBagConstraints gbc_progressBarRead = new GridBagConstraints();
		gbc_progressBarRead.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBarRead.insets = new Insets(0, 0, 5, 5);
		gbc_progressBarRead.gridx = 1;
		gbc_progressBarRead.gridy = 1;
		panel.add(progressFiles, gbc_progressBarRead);

		lblFiles = new JLabel("");
		GridBagConstraints gbc_labelFileCount = new GridBagConstraints();
		gbc_labelFileCount.insets = new Insets(0, 0, 5, 0);
		gbc_labelFileCount.gridx = 2;
		gbc_labelFileCount.gridy = 1;
		panel.add(lblFiles, gbc_labelFileCount);

		JLabel lblFilesProcessed = new JLabel("Processed");
		GridBagConstraints gbc_lblFilesProcessed = new GridBagConstraints();
		gbc_lblFilesProcessed.insets = new Insets(0, 0, 0, 5);
		gbc_lblFilesProcessed.gridx = 0;
		gbc_lblFilesProcessed.gridy = 2;
		panel.add(lblFilesProcessed, gbc_lblFilesProcessed);

		progressProcess = new JProgressBar();
		GridBagConstraints gbc_progressBarProcess = new GridBagConstraints();
		gbc_progressBarProcess.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBarProcess.insets = new Insets(0, 0, 0, 5);
		gbc_progressBarProcess.gridx = 1;
		gbc_progressBarProcess.gridy = 2;
		panel.add(progressProcess, gbc_progressBarProcess);

		lblProcess = new JLabel("");
		GridBagConstraints gbc_lblProcessCount = new GridBagConstraints();
		gbc_lblProcessCount.gridx = 2;
		gbc_lblProcessCount.gridy = 2;
		panel.add(lblProcess, gbc_lblProcessCount);

		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.SOUTH);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0 };
		gbl_panel_1.columnWeights = new double[] { 1, 1, 1 };
		gbl_panel_1.rowWeights = new double[] { Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		btnStart = new JButton("Start");
		btnStart.addActionListener(Application.this);
		gbc_lblDirectory = new GridBagConstraints();
		gbc_lblDirectory.insets = new Insets(0, 0, 5, 5);
		gbc_lblDirectory.gridx = 0;
		gbc_lblDirectory.gridy = 0;
		panel_1.add(btnStart, gbc_lblDirectory);
		btnStart.addActionListener(Application.this);

		btnPause = new JButton("Pause");
		gbc_lblDirectory = new GridBagConstraints();
		gbc_lblDirectory.insets = new Insets(0, 0, 5, 5);
		gbc_lblDirectory.gridx = 1;
		gbc_lblDirectory.gridy = 0;
		panel_1.add(btnPause, gbc_lblDirectory);
		btnPause.addActionListener(Application.this);

		btnStop = new JButton("Stop");
		gbc_lblDirectory = new GridBagConstraints();
		gbc_lblDirectory.insets = new Insets(0, 0, 5, 5);
		gbc_lblDirectory.gridx = 2;
		gbc_lblDirectory.gridy = 0;
		panel_1.add(btnStop, gbc_lblDirectory);
		btnStop.addActionListener(Application.this);

		JTextPane textPane = new JTextPane();
		frame.getContentPane().add(textPane, BorderLayout.CENTER);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnBrowse) {
			directoryChooser = new JFileChooser();
			directoryChooser.setCurrentDirectory(new java.io.File("."));
			directoryChooser.setDialogTitle("Directory");
			directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			directoryChooser.setAcceptAllFileFilterUsed(false);

			if (directoryChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
				rootDirectory = directoryChooser.getSelectedFile().getPath();
				session = new Session(rootDirectory, 5, Application.this);
				status = Status.DIRECTORY_SELECTED;
				uiUpdate();
			}
		} else if (e.getSource() == btnStart) {
			status = Status.STARTED;
			session.start();
			uiUpdate();
		} else if (e.getSource() == btnPause) {

		} else if (e.getSource() == btnStop) {

		}
	}

	@Override
	public void progressUpdate(int numberOfFiles, int addedFiles, int processedFiles) {
		progressFiles.setValue(addedFiles * 100 / numberOfFiles);
		progressProcess.setValue(processedFiles * 100 / numberOfFiles);
		lblFiles.setText(addedFiles + "/" + numberOfFiles);
		lblProcess.setText(processedFiles + "/" + numberOfFiles);

	}

	@Override
	public void duplicateFound(long fileSize, String md5, String location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopping() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopped() {
		// TODO Auto-generated method stub

	}

}
