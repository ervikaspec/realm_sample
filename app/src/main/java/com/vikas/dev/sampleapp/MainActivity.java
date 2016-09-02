package com.vikas.dev.sampleapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vikas.dev.sampleapp.Utils.Util;
import com.vikas.dev.sampleapp.models.Service;
import com.vikas.dev.sampleapp.models.User;
import com.vikas.dev.sampleapp.models.UserServices;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    EditText userId, userPassword;
    Button submit;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userId = (EditText) this.findViewById(R.id.userId);
        userPassword = (EditText) this.findViewById(R.id.userPass);
        submit = (Button) this.findViewById(R.id.submit);
//        progress = new ProgressDialog(this);
//        progress.setTitle("Login");
//        progress.setMessage("Signing in...");
//        progress.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = userId.getText().toString().trim();
                String pass = userPassword.getText().toString().trim();

                if (TextUtils.isEmpty(id)) {
                    Toast.makeText(view.getContext(), "Please provide a valid user id.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(view.getContext(), "Please provide a valid password.", Toast.LENGTH_SHORT).show();
                } else {
                    checkPassword(id, pass);
                }
            }
        });
        RealmConfiguration realmConfig = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        generateDummyData();
    }

    private void generateDummyData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    for (int i = 0; i < 5; i++) {
                        User user = new User();
                        user.setName("User Name " + i);
                        user.setId("user" + i);
                        user.setEmailId("User" + i + "@gmail.com");
                        user.setDOB(i + 1 + "/1/1988");
                        user.setGender((new Random().nextInt(2 - 1) + 1) == 1 ? "Male" : "Female");
                        user.setAddress("Address of User " + i);
                        user.setPassword(Util.toSha1Hash("pass" + i));
                        realm.copyToRealm(user);
                    }

                    for (int i = 0; i < 5; i++) {
                        Service service = new Service();
                        service.setServiceName("Service Name " + i);
                        service.setServiceId("service" + i);
                        realm.copyToRealm(service);
                    }

                    for (int i = 0; i < 5; i++) {
                        UserServices uservice = new UserServices();
                        uservice.setUserId("user" + i);
                        uservice.setServiceId("service" + i);
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

    private void checkPassword(final String id, final String pass) {
        new AsyncTask<Void, Void, Void>() {
            boolean loggedIn;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                progress.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                User user = Realm.getDefaultInstance().where(User.class).equalTo("userId", id).findFirst();
                String currentHash = Util.toSha1Hash(pass);
                loggedIn = false;
                if (user != null && user.getPassword().equals(currentHash)) {
                    loggedIn = true;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
//                progress.dismiss();
                if (loggedIn) {
                    Toast.makeText(MainActivity.this, "Logged In !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, EditActivity.class);
                    intent.putExtra("user_id", id);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "User Id and Password do not match", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
