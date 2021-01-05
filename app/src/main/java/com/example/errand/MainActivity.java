package com.example.errand;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private static final String CHANNEL_ID = "ERRANDCHANNEL";

    private GoogleSignInClient mGoogleSignInClient;

    private static final int SIGN_IN = 231;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocationPermission();

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        configGoogleSignIn();

        findViewById(R.id.need_errand_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NeedErrandActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.make_errand_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DoingErrandActivity.class);
                startActivity(intent);
            }
        });

        createNotificationChannel();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNotification();
            }
        }, 3000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            //Toast.makeText(this, "Signed as " + account.getEmail(), Toast.LENGTH_LONG).show();
            showSignIn(false);
        } else {
            showSignIn(true);
        }
    }

    private void showSignIn(boolean show) {
        int signInVisible = show ? View.VISIBLE : View.INVISIBLE;
        int restVisible = show ? View.INVISIBLE : View.VISIBLE;
        findViewById(R.id.sign_in_button).setVisibility(signInVisible);
//        findViewById(R.id.description_text_view).setVisibility(signInVisible);
    findViewById(R.id.make_errand_button).setVisibility(restVisible);
        findViewById(R.id.need_errand_button).setVisibility(restVisible);
    }

    private void googlSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.e("TAG", account.getId());

            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("AccountID", account.getId());
            editor.apply();

            Toast.makeText(this, "Signed as " + account.getEmail(), Toast.LENGTH_LONG).show();
            showSignIn(false);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("ERRAND", "signInResult:failed code=" + e.getStatusCode());
            showSignIn(true);
        }
    }

    @Override
    public void onClick(View v) {
        googlSignIn();
    }

    private void configGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }


    private void showNotification() {

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, OngoingRequestsActivity.class);
        intent.putExtra("Name", "Tom");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        int height = 100;
        int width = 100;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.map_icon);
        Bitmap smallerIcon = Bitmap.createScaledBitmap(b, width, height, false);

        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.e)
                .setContentTitle("Errand request")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Tom just sent you an errand request! Click to see it"))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(smallerIcon)
                .setAutoCancel(true); */


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.e)
                .setContentTitle("Errand accepted")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLargeIcon(smallerIcon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Susan will do the errand you requested, she will be going to Target at 11pm, you should get your stuff sometime later that day!"));


        // Show:
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Errand channel";
            String description = "Channel for errands notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
