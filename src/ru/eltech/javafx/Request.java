package ru.eltech.javafx;

import java.io.Serializable;

public class Request implements Serializable {
	private static final long serialVersionUID = -8689733764257560174L;
	public int id;
	public int command;
	
	public double values[];
	
	public Request(int id_, int command_, double values_[]) {
		id = id_;
		command = command_;
		values = values_;
	}
}
