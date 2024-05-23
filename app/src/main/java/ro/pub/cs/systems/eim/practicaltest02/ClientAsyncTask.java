package ro.pub.cs.systems.eim.practicaltest02;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientAsyncTask extends AsyncTask<String, String, Void>{
    private String city;

    private String informationType;
    private TextView informationTextView;

    public ClientAsyncTask(String city, String informationType, TextView informationTextView) {
        this.city = city;
        this.informationType = informationType;
        this.informationTextView = informationTextView;
    }
    @Override
    protected Void doInBackground(String... strings) {
        try {
            Socket socket = new Socket(strings[0], Integer.parseInt(strings[1]));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println(city);
            printWriter.println(informationType);

            BufferedReader bufferedReader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                publishProgress(line);
            }
            socket.close();
        } catch (IOException e) {
            Log.d("PracticalTest02", "[CLIENT THREAD] An exception has occurred: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        informationTextView.setText("");
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        informationTextView.append(values[0] + "\n");
    }
}
