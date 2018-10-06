package ru.eltech.javafx;

import javafx.scene.control.Alert;

class InformationDialog extends Alert {
	private static final int WIDTH_DIALOG = 350;
	private static final int HEIGHT_DIALOG = 100;

	InformationDialog(String title, String content) {
		super(AlertType.INFORMATION);

		getDialogPane().setPrefSize(WIDTH_DIALOG, HEIGHT_DIALOG);
		setTitle(title);
		setContentText(content);
		setHeaderText(null);
	}
}
