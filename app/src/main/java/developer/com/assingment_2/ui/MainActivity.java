package developer.com.assingment_2.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import developer.com.assingment_2.R;
import developer.com.assingment_2.adapter.SpinnerAdapter;
import developer.com.assingment_2.model.TransportModel;
import developer.com.assingment_2.model.TransportModel.Fromcentral;
import developer.com.assingment_2.model.TransportModel.Location;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private AppCompatSpinner spinner;
    private TextView txtCar, txtTrain;
    private Button btnNavigate;
    private ArrayList<TransportModel> transportModels = new ArrayList<>();

    private String strUrl = "http://express-it.optusnet.com.au/sample.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();
    }

    private void initView() {

        spinner = (AppCompatSpinner) findViewById(R.id.spinner);
        txtCar = (TextView) findViewById(R.id.txtCar);
        txtTrain = (TextView) findViewById(R.id.txtTrain);
        btnNavigate = (Button) findViewById(R.id.btnNavigate);
    }

    private void initData() {

        spinner.setOnItemSelectedListener(this);
        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransportModel transportModel = transportModels.get(spinner.getSelectedItemPosition());
                double latitude = transportModel.getLocation().getLatitude();
                double longitude = transportModel.getLocation().getLongitude();
                Intent intent = new Intent(MainActivity.this, MapActivity.class);

                intent.putExtra("name",transportModel.getName());
                intent.putExtra("lat", latitude);
                intent.putExtra("long", longitude);
                startActivity(intent);
            }
        });

        new WebserviceTask().execute();
    }

    String callWebservice() {

        String response = null;
        try {

            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET"); // read the response System.out.println("Response Code: " + conn.getResponseCode());
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            System.out.println(response);

        } catch (Exception e) {

            e.printStackTrace();

            return response;
        }
        return response;

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        TransportModel transportModel = transportModels.get(position);
        String car = transportModel.getFromcentral().getCar();
        String train = transportModel.getFromcentral().getTrain();

        if (car != null) {

            txtCar.setVisibility(View.VISIBLE);
            txtCar.setText("Car : " + car);
        } else {

            txtCar.setVisibility(View.GONE);
        }
        if (train != null) {

            txtTrain.setVisibility(View.VISIBLE);
            txtTrain.setText("Train : " + train);
        } else {

            txtTrain.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class WebserviceTask extends AsyncTask<Void, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            return callWebservice();
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            transportModels.clear();
            try {
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    TransportModel transportModel = new TransportModel();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    transportModel.setId(jsonObject.getInt("id"));
                    transportModel.setName(jsonObject.getString("name"));
                    JSONObject fromcentralJson = jsonObject.getJSONObject("fromcentral");

                    Fromcentral fromcentral = transportModel.new Fromcentral();

                    if (fromcentralJson.has("car")) {

                        fromcentral.setCar(fromcentralJson.getString("car"));
                    } else {

                        fromcentral.setCar(null);
                    }

                    if (fromcentralJson.has("train")) {

                        fromcentral.setTrain(fromcentralJson.getString("train"));

                    } else {

                        fromcentral.setTrain(null);
                    }

                    transportModel.setFromcentral(fromcentral);
                    JSONObject locationJson = jsonObject.getJSONObject("location");

                    Location location = transportModel.new Location();
                    location.setLatitude(locationJson.getDouble("latitude"));
                    location.setLongitude(locationJson.getDouble("longitude"));

                    transportModel.setLocation(location);
                    transportModels.add(transportModel);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(MainActivity.this, transportModels);
            spinner.setAdapter(spinnerAdapter);
            super.onPostExecute(result);
        }
    }
}
