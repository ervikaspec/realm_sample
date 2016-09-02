package com.vikas.dev.sampleapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vikas.dev.sampleapp.lib.multispinnerfilter.MultiSpinner;
import com.vikas.dev.sampleapp.lib.multispinnerfilter.MultiSpinnerListener;
import com.vikas.dev.sampleapp.models.Service;
import com.vikas.dev.sampleapp.models.User;
import com.vikas.dev.sampleapp.models.UserServices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmResults;

public class EditActivity extends AppCompatActivity {

    EditText userName, userDOB, userGender, userAddress, userEmail;
    Button submit;
    ProgressDialog progress;
    MultiSpinner simpleSpinner;
    Set<String> selectedServiceIds = new HashSet<>();
    List<String> serviceIds = new ArrayList<>();
    String user_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        userName = (EditText) this.findViewById(R.id.userName);
        userDOB = (EditText) this.findViewById(R.id.userDOB);
        userGender = (EditText) this.findViewById(R.id.userGender);
        userAddress = (EditText) this.findViewById(R.id.userAddress);
        userEmail = (EditText) this.findViewById(R.id.userEmail);

//        progress = new ProgressDialog(this);
//        progress.setTitle("Loading");
//        progress.setMessage("Please wait...");

        user_id = getIntent().getExtras().getString("user_id", "");

        submit = (Button) this.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setData(user_id);
            }
        });

        simpleSpinner = (MultiSpinner) findViewById(R.id.simpleMultiSpinner);

        populateServices(user_id);
    }

    private void populateServices(final String user_id) {
        new AsyncTask<Void, Void, Void>() {
            TreeMap<String, Boolean> itemsMap;
            User user;
            String name, dob, gender, address, email;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                progress.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                user = Realm.getDefaultInstance().where(User.class).equalTo("userId", user_id).findFirst();
                if (user != null) {
                    name = user.getName().toString();
                    dob = user.getDOB();
                    gender = user.getGender();
                    address = user.getAddress();
                    email = user.getEmailId();

                    RealmResults<Service> services = Realm.getDefaultInstance().where(Service.class).findAll();

                    RealmResults<UserServices> userServices = Realm.getDefaultInstance().where(UserServices.class).findAll();
                    for (int i = 0; i < userServices.size(); i++) {
                        UserServices uservice = userServices.get(i);
                        if (uservice.getUserId().equals(user_id)) {
                            selectedServiceIds.add(uservice.getServiceId());
                        }
                    }

                    itemsMap = new TreeMap<>();

                    for (int i = 0; i < services.size(); i++) {
                        Service service = services.get(i);
                        Log.d("Vikas", "service.getServiceName()");
                        serviceIds.add(service.getServiceId());
                        itemsMap.put(service.getServiceName(), selectedServiceIds.contains(service.getServiceId()));
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
//                progress.dismiss();

                if (user != null) {
                    userName.setText(name);
                    userDOB.setText(dob);
                    userGender.setText(gender);
                    userAddress.setText(address);
                    userEmail.setText(email);
                }

                simpleSpinner.setItems(itemsMap, new MultiSpinnerListener() {
                    @Override
                    public void onItemsSelected(boolean[] selected) {
                        for (int i = 0; i < selected.length; i++) {
                            boolean val = selected[i];
                            if (val) {
                                if (serviceIds != null) {
                                    selectedServiceIds.add(serviceIds.get(i));
                                }
                            }
                        }
                    }
                });
            }
        }.execute();
    }

    private void setData(final String userId) {
        new AsyncTask<Void, Void, Void>() {
            String address = "", email = "";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                address = userAddress.getText().toString();
                email = userEmail.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(EditActivity.this, "Please provide valid email.", Toast.LENGTH_SHORT).show();
                    email = null;
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    User user = Realm.getDefaultInstance().where(User.class).equalTo("userId", userId).findFirst();
                    if (email != null) {
                        user.setEmailId(email);
                    }
                    user.setAddress(address);

                    for (String serviceId : selectedServiceIds) {
                        UserServices uservice = new UserServices();
                        uservice.setUserId(userId);
                        uservice.setServiceId(serviceId);
                        realm.copyToRealm(uservice);
                    }

                    realm.commitTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
//                progress.dismiss();
            }
        }.execute();
    }

}
