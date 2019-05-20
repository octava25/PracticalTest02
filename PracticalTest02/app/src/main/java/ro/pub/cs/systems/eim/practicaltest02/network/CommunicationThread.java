package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");
            String recv = bufferedReader.readLine();

            String[] parts = recv.split(",");

            if (parts.length < 3) {
                printWriter.println("ERR");
                printWriter.flush();
                return;
            }

            String op = parts[0];
            String op1 = parts[1];
            String op2 = parts[2];

            int res;
            int iop1;
            int iop2;
            try {
                iop1 = Integer.parseInt(op1);
            }
            catch (NumberFormatException e)
            {
                printWriter.println("ERR");
                printWriter.flush();
                return;
            }

            try {
                iop2 = Integer.parseInt(op2);
            }
            catch (NumberFormatException e)
            {
                printWriter.println("ERR");
                printWriter.flush();
                return;
            }

            Log.i(Constants.TAG, "RECV: " + op + " " + op1 + " " + op2);

            if (op.equals("add")) {
                res = iop1 + iop2;

            } else {
                Thread.sleep(2000);
                res = iop1 * iop2;
            }


            String resp = "" + res;

            printWriter.println(resp);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
