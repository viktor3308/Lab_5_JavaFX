package ru.eltech.javafx;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class ClientFormController implements Initializable {
	
	private final static int PORT = 6666;
	private final static String ADDRESS = "127.0.0.1";
	private final static String DIALOG_TITLE = "Client";
	private final static String CONNECTION_SUCCESSFUL = "Connection to the server was successful";
	private final static String CONNECTION_FAILED = "Unable to connect to server";
	private final static String CONNECTION_CLOSED = "Connection to the server was closed";
	
	private Socket m_socket = null;
	private ObjectOutputStream m_out = null;
	private ObjectInputStream m_in = null;
	private double m_values[] = new double[4];
	
	private Timeline m_heartbeatTimeline = new Timeline(
		    new KeyFrame(Duration.seconds(1), handler -> {			    	
		    	try {
		    		Request heartBeatRequest = new Request(0);
		    		ClientFormController.this.sendRequest(heartBeatRequest);
				} catch (Exception e) {
					ClientFormController.this.m_heartbeatTimeline.stop();
					System.out.println(CONNECTION_CLOSED);
		    		final InformationDialog informationDialog =
		    				new InformationDialog(DIALOG_TITLE, CONNECTION_CLOSED);
		    		informationDialog.setOnHidden(evt -> {
		    				ClientFormController.this.closeConnection();
			    			Platform.exit();
		    			});
		    		informationDialog.show();
				}
		    })
		);

    @FXML
    private TextField tfdFirstNumber;
    
    @FXML
    private TextField tfdSecondNumber;

    @FXML
    private TextField tfdThirdNumber;

    @FXML
    private TextField tfdFourthNumber;
    
    private void initConnection() {
    	try {
    		m_socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
    		m_out = new ObjectOutputStream(m_socket.getOutputStream());
    		System.out.println(CONNECTION_SUCCESSFUL);
    	}
    	catch (Exception x) {
    		System.out.println(CONNECTION_FAILED);
    		final InformationDialog informationDialog =
    				new InformationDialog(DIALOG_TITLE, CONNECTION_FAILED);
    		informationDialog.setOnHidden(evt -> {
				ClientFormController.this.closeConnection();
    			Platform.exit();
			});
    		informationDialog.showAndWait();
		}
    }
    
    private void closeConnection() {
			try {
				m_in.close();
			}
			catch (Exception e) {
			}
			try {
				m_out.close();
			}
			catch (Exception e) {
			}
			try {
				m_socket.close();
			}
			catch (Exception e) {
			}
    }
    
    private void sendRequest(Request request) throws IOException {
    	m_out.writeObject(request);
    	m_out.flush();
    }
    
    private void processRequest(Request request) {
    	try {
    		sendRequest(request);
			
			if(m_in == null)
				m_in = new ObjectInputStream(m_socket.getInputStream());
			
			Response response = (Response) m_in.readObject();
			
			if(response != null) {
				final String[] resultStrings = {
						"Triangle square = ",
						"Trapeze square = ",
						"Parallelogram perimeter = ",
						"Pyramid volume = "
				};
				final InformationDialog informationDialog =
	    				new InformationDialog(
	    						DIALOG_TITLE,
	    						resultStrings[request.command] + response.result);
	    		informationDialog.show();
	    		clearValues();				
			}
    	}
    	catch (Exception e) {
		}
    }
    
    @SuppressWarnings({ "unchecked" })
	private void readValues() {
		m_values[0] = ((TextFormatter<Double>) tfdFirstNumber.textFormatterProperty().getValue()).getValue();
		m_values[1] = ((TextFormatter<Double>) tfdSecondNumber.textFormatterProperty().getValue()).getValue();
		m_values[2] = ((TextFormatter<Double>) tfdThirdNumber.textFormatterProperty().getValue()).getValue();
		m_values[3] = ((TextFormatter<Double>) tfdFourthNumber.textFormatterProperty().getValue()).getValue();
    }
    
	private void clearValues() {
		tfdFirstNumber.setText("0.0");
		tfdSecondNumber.setText("0.0");
		tfdThirdNumber.setText("0.0");
		tfdFourthNumber.setText("0.0");
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		initConnection();
		
		UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
		    String text = change.getControlNewText();
		    try {
		    	if(text.matches("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?")) {
		    		Double.parseDouble(text);
		    		return change;
		    	}
		    }
		    catch (NullPointerException | NumberFormatException ex) {
		    }
		    return null;
		};
		
		StringConverter<Double> doubleConverter = new StringConverter<Double>() {

		    @Override
		    public Double fromString(String s) {
		        if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
		            return 0.0 ;
		        } else {
		            return Double.valueOf(s);
		        }
		    }

		    @Override
		    public String toString(Double d) {
		        return d.toString();
		    }
		};
		
		tfdFirstNumber.setTextFormatter(new TextFormatter<>(doubleConverter, 0.0, doubleFilter));
		tfdSecondNumber.setTextFormatter(new TextFormatter<>(doubleConverter, 0.0, doubleFilter));
		tfdThirdNumber.setTextFormatter(new TextFormatter<>(doubleConverter, 0.0, doubleFilter));
		tfdFourthNumber.setTextFormatter(new TextFormatter<>(doubleConverter, 0.0, doubleFilter));
		

		m_heartbeatTimeline.setCycleCount(Timeline.INDEFINITE);
		m_heartbeatTimeline.play();
	}
	
	public void onCalculateTriangleSquare() {
		readValues();
		processRequest(new Request(1, 0, m_values));
	}
	
	public void onCalculateTrapezeSquare() {
		readValues();
		processRequest(new Request(1, 1, m_values));
	}
	
	public void onCalculateParallelogramPerimeter() {
		readValues();
		processRequest(new Request(1, 2, m_values));
	}
	
	public void onCalculatePyramidVolume() {
		readValues();
		processRequest(new Request(1, 3, m_values));
	}

}
