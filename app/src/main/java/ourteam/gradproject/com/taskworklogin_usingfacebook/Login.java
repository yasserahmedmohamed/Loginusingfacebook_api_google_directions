package ourteam.gradproject.com.taskworklogin_usingfacebook;

import android.content.Intent;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.jaeger.library.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginManager loginManager;
    TextView details_txt;
    AccessToken accessToken;
    Profile profile;
    private static final String EMAIL = "email";
    @BindView(R.id.login_button)
    LoginButton loginButton;

    @OnClick(R.id.rel_log)
    public void peromlog() {
        loginButton.performClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarUtil.setTransparent(Login.this);

        ButterKnife.bind(this);
        initializedata();

    }


    public void initializedata() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList(EMAIL));

        accessToken = AccessToken.getCurrentAccessToken();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                profile=Profile.getCurrentProfile();
               // Toast.makeText(getApplicationContext(),profile.getName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("username",profile.getName());
                intent.putExtra("img",profile.getId());
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), getString(R.string.check_conn), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();


     if (isLoggedIn) {
         profile=Profile.getCurrentProfile();
        // Toast.makeText(getApplicationContext(),profile.getName(),Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(this, MapActivity.class);
         intent.putExtra("username",profile.getName());
         intent.putExtra("img",profile.getId());
         startActivity(intent);
            finish();
       }

    }
}
