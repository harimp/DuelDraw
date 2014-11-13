package ca.ubc.dueldraw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private int activePlayers = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
//            	setContentView(R.layout.activity_splash);
                setupUserData_ProtocolA();
                requestListOfActivePlayers_ProtocolC();
            	/* Create an Intent that will start the Menu-Activity. */
                Toast.makeText(getApplicationContext(), String.valueOf(activePlayers), Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(SplashActivity.this,MenuActivity.class);
                mainIntent.putExtra("numberOfActivePlayers", activePlayers);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
    
    public void setActivePlayers(int activePlayers) {
		this.activePlayers = activePlayers;
	}

	private void setupUserData_ProtocolA() {
    	SocketApp app = (SocketApp) getApplicationContext();
		app.sendMessage("A"
				+ Secure.getString(getContentResolver(), Secure.ANDROID_ID));
	}
    
    private void requestListOfActivePlayers_ProtocolC() {
    	SocketApp app = (SocketApp) getApplicationContext();
		app.sendMessage("C");
	}
}
