package ru.eltech.javafx;

import java.io.Serializable;

class Request implements Serializable {
	private static final long serialVersionUID = -8689733764257560174L;
	int id;
	int command;
	
	double values[];
	
	Request(int id_, int command_, double values_[]) {
		id = id_;
		command = command_;
		values = values_;
	}
}
