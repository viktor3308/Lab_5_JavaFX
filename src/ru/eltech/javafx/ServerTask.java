package ru.eltech.javafx;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerTask extends Task<Void> {

    private static final String DIALOG_TITLE = "Server";
    private static final String DIALOG_DISCONNECTION_MESSAGE = "The client has disconected. Server will shut down now.";
    private static final String SERVER_WAIT = "Server is waiting for connection...";
    private static final String CLIENT_CONNECT = "Client connected to the server";
    private static final String CLOSE_CONNECT = "Connection with the client is closed";
    private static final String CLOSE_TASK = "Server stopped";
    private static final String ERROR = "Something went wrong";
    private static final int PORT = 6666;

    private Request m_request;
    private Socket m_socket = null;
    private final Lock m_socketLock = new ReentrantLock(true);

    private Double getResult() {
        Double result = null;

        switch (m_request.command) {
            case 0:
                result = m_request.values[0] *
                        m_request.values[1] *
                        m_request.values[2] /
                        (4 * m_request.values[3]);
                break;
            case 1:
                double a = m_request.values[0];
                double b = m_request.values[1];
                double c = m_request.values[2];
                double d = m_request.values[3];
                double p = (a + b + c + d) / 2.;
                result = (a + b) / Math.abs(a - b);
                result *= Math.sqrt((p - a) * (p - b) *
                        (p - a - c) * (p - a - d));
                break;
            case 2:
                result = 2 * (m_request.values[0] + m_request.values[1]);
                break;
            case 3:
                result = (1. / 3.) * m_request.values[0] * m_request.values[1];
                break;
        }

        return result;
    }

    @Override
    protected Void call() {
        try (ServerSocket ss = new ServerSocket(PORT)) {
            System.out.println(SERVER_WAIT);
            updateMessage(SERVER_WAIT);
            ss.setSoTimeout(1000);

            while (!isCancelled()) {
                try (Socket socket = ss.accept();
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                    m_socketLock.lock();
                    m_socket = socket;
                    m_socketLock.unlock();

                    System.out.println(CLIENT_CONNECT);
                    updateMessage(CLIENT_CONNECT);

                    while (!isCancelled()) {
                        m_request = (Request) in.readObject();
                        Double result = getResult();
                        if (result != null) {
                            Response response = new Response();
                            response.id = m_request.id;
                            response.result = result;
                            out.writeObject(response);
                            out.flush();
                        }
                    }
                } catch (java.net.SocketTimeoutException e) {
                    e.printStackTrace();
                }
            }
        } catch (java.net.SocketException e) {
            if (!isCancelled()) {
                System.out.println(CLOSE_CONNECT);
                updateMessage(CLOSE_CONNECT);
            }
        } catch (EOFException e) {
            System.out.println(CLOSE_CONNECT);
            updateMessage(CLOSE_CONNECT);
        } catch (Exception e) {
            System.out.println(ERROR);
            updateMessage(ERROR);
            e.printStackTrace();
        }

        System.out.println(CLOSE_TASK);
        updateMessage(CLOSE_TASK);

        return null;
    }

    @Override
    protected void succeeded() {
        super.succeeded();

        final InformationDialog informationDialog =
                new InformationDialog(DIALOG_TITLE, DIALOG_DISCONNECTION_MESSAGE);
        informationDialog.setOnHidden(evt -> {
            Platform.exit();
        });
        informationDialog.show();
    }

    @Override
    protected void cancelled() {
        super.cancelled();

        m_socketLock.lock();

        if (m_socket != null && !m_socket.isClosed()) {
            try {
                m_socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        m_socketLock.unlock();
    }
}
