package com.firstGame.cases;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.event.Event;
import com.google.android.gms.games.event.EventBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    int RC_SIGN_IN =0;
    SignInButton signInButton;
    Button leftButton,rightButton, leaderButton;
    TextView resultText;
    Random ran= new Random();
    private int count;
    private int r1,r2;
    private static final int RC_ACHIEVEMENT_UI = 9003;
    private PlayersClient mPlayersClient;
    TextView Welcometxt;
    private static final int RC_LEADERBOARD_UI = 9004;
    int score= 0;
    private static final String TAG = "MyGame";
    Button signOutButton ,showEventButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        signInButton = findViewById(R.id.sign_in_button);
        leftButton = findViewById(R.id.buttonL);
        rightButton= findViewById(R.id.buttonR);
        resultText = findViewById(R.id.textViewResults);
        Welcometxt = findViewById(R.id.txtWecome);
        leaderButton= findViewById(R.id.btnLearedBD);
        signOutButton=findViewById(R.id.sign_out_button);
        signOutButton.setVisibility(View.GONE);
        showEventButton= findViewById(R.id.btnEvent);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignInIntent();
                signOutButton.setVisibility(View.VISIBLE);
                signInButton.setVisibility(View.GONE);
            }
        });


        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();

                signOutButton.setVisibility(View.GONE);
                signInButton.setVisibility(View.VISIBLE);
            }
        });


        NewRandom();



        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (r1>r2){
                    count ++;
                    submitEvent("CgkI4LX1tdAYEAIQAQ");

                }
                else{
                    count --;
                }

                resultText.setText(String.valueOf(count));
                NewRandom();

            }
        });


        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (r1<r2){
                    count ++;
                    submitEvent("CgkI4LX1tdAYEAIQAQ");
                }
                else{
                    count --;
                }
                resultText.setText(String.valueOf(count));
                NewRandom();

            }
        });


        leaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeaderboard();

            }
        });


        score= count;

        showEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadEvents();
            }
        });

    }

    private void startSignInIntent() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
        Log.i("LoginActivity","Calling onActivityResult");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("RC_SIGN_IN:::",String.valueOf(RC_SIGN_IN));

        Log.i("RQQQQeCode",String.valueOf(requestCode));
        Log.i("RQQQQesultCode",String.valueOf(resultCode));

        if (requestCode == RC_SIGN_IN) {
            Log.i("Success","Success");


            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                Log.i("Success2","Success2");

                GoogleSignInAccount signedInAccount = result.getSignInAccount();
                onConnected(signedInAccount);

                Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .unlock(getString(R.string.achievement_login));
                showAchievements();
            } else {


                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton("Login Fail", null).show();
            }
        }
    }
    private void onConnected(GoogleSignInAccount googleSignInAccount) {


        mPlayersClient = Games.getPlayersClient(this, googleSignInAccount);



        // Set the greeting appropriately on main menu
        mPlayersClient.getCurrentPlayer()
                .addOnCompleteListener(new OnCompleteListener<Player>() {
                    @Override
                    public void onComplete(@NonNull Task<Player> task) {
                        String displayName= null;
                        if (task.isSuccessful()) {
                            displayName = task.getResult().getDisplayName();
                        } else {
                            Exception e = task.getException();
//                            displayName = "???";
                        }
                        Welcometxt.setText   ("Hello, " + displayName);
                    }
                });



    }


    public void NewRandom(){
        r1 =ran.nextInt(10);
        while(true) {
            r2 = ran.nextInt(10);
            if(r1!=r2)
                break;
        }
        leftButton.setText(Integer.toString(r1));
        rightButton.setText(Integer.toString(r2));

    }

    private void showAchievements() {
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_ACHIEVEMENT_UI);
                    }
                });
    }



    @Override
    protected void onResume() {
        super.onResume();
        signInSilently();
    }

    private void signInSilently() {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            GoogleSignInAccount signedInAccount = account;
            onConnected(signedInAccount);

        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);
            signInClient
                    .silentSignIn()
                    .addOnCompleteListener(
                            this,
                            new OnCompleteListener<GoogleSignInAccount>() {
                                @Override
                                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                    if (task.isSuccessful()) {
                                        // The signed in account is stored in the task's result.
                                        GoogleSignInAccount signedInAccount = task.getResult();
                                    } else {
                                        // Player will need to sign-in explicitly using via UI.
                                        // See [sign-in best practices](http://developers.google.com/games/services/checklist) for guidance on how and when to implement Interactive Sign-in,
                                        // and [Performing Interactive Sign-in](http://developers.google.com/games/services/android/signin#performing_interactive_sign-in) for details on how to implement
                                        // Interactive Sign-in.
                                    }
                                }
                            });
        }
    }



    private void showLeaderboard() {

        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .submitScore(getString(R.string.leaderboard), 900);


        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))

                .getLeaderboardIntent(getString(R.string.leaderboard))
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                });
    }

    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    private void signOut() {
        Log.d(TAG, "signOut()");

        if (!isSignedIn()) {
            Log.w(TAG, "signOut() called, but was not signed in!");
            return;
        }

        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // at this point, the user is signed out.
                    }
                });

        Welcometxt.setText("Not Signed in");
    }


    public void submitEvent(String eventId) {
        Games.getEventsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .increment(eventId, 1);
    }


    public void loadEvents() {
        Games.getEventsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .load(true)
                .addOnCompleteListener(new OnCompleteListener<AnnotatedData<EventBuffer>>() {
                    @Override
                    public void onComplete(@NonNull Task<AnnotatedData<EventBuffer>> task) {
                        if (task.isSuccessful()) {
                            // Process all the events.
                            for (Event event : task.getResult().get()) {
                                Log.d(TAG, "loaded event " + event.getName());
                                Log.d(TAG, "Value  " + event.getValue());

                            }
                        } else {
                            // Handle Error
                            Exception exception = task.getException();
                            int statusCode = CommonStatusCodes.DEVELOPER_ERROR;
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                statusCode = apiException.getStatusCode();
                            }
//                            showError(statusCode);
                        }
                    }
                });
    }

}
