package ru.eltech.javafx;

import java.io.Serializable;

public class Request implements Serializable {
	private static final long serialVersionUID = -8689733764257560174L;
	public int id;
	public int command;
	
	public double a;
	public double b;
	public double c;
	public double d;
	
	public Request(int id_, int command_, double values_[]) {
		id = id_;
		command = command_;
		a = values_[0];
		b = values_[1];
		c = values_[2];
		d = values_[3];
	}
	
	public Request(int id_) {
		id = id_;
	}
}
