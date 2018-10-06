package ru.eltech.javafx;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;

public class ServerFormController implements Initializable {
	
	@FXML
    private Label lblStatus;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public StringProperty statusProperty() {
		return lblStatus.textProperty();
	}
}
