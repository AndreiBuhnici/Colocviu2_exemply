package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText serverPortEditText = null;
    Button serverButton = null;
    EditText addressEditText = null;
    EditText clientPortEditText = null;
    EditText cityEditText = null;
    Button clientButton = null;
    Spinner informationTypeSpinner = null;
    ServerThread serverThread = null;
    TextView informationTextView = null;

    class ServerButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            serverThread.startServer();
        }
    }

    class ClientButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = addressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            String city = cityEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty() || clientPort == null || clientPort.isEmpty() || city == null || city.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Client address, client port and city should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            ClientAsyncTask clientAsyncTask = new ClientAsyncTask(city, informationTypeSpinner.getSelectedItem().toString(), informationTextView);
            clientAsyncTask.execute(clientAddress, clientPort);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);
        serverPortEditText = (EditText)findViewById(R.id.serverPortEditText);
        serverButton = (Button)findViewById(R.id.serverButton);
        serverButton.setOnClickListener(new ServerButtonClickListener());
        addressEditText = (EditText)findViewById(R.id.addressEditText);
        clientPortEditText = (EditText)findViewById(R.id.clientPortEditText);
        cityEditText = (EditText)findViewById(R.id.cityEditText);
        clientButton = (Button)findViewById(R.id.clientButton);
        clientButton.setOnClickListener(new ClientButtonClickListener());
        informationTypeSpinner = (Spinner)findViewById(R.id.spinner);
        informationTextView = (TextView)findViewById(R.id.infoTextView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverThread.stopServer();


    }
}