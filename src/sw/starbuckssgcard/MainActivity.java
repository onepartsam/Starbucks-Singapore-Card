package sw.starbuckssgcard;

import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.GeolocationPermissions;

import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMInterstitial;
import com.millennialmedia.android.MMAd;
import com.millennialmedia.android.MMBroadcastReceiver;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;
import com.millennialmedia.android.RequestListener.RequestListenerImpl;

public class MainActivity extends Activity {
	
	public String currentURL = "https://card.starbucks.com.sg/myrewards.php";
	private WebView w;
	public Timer t;
	
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private String email, password;
	private int count;

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        
        sp = this.getSharedPreferences("etc", 0);
        editor = sp.edit();
        email = sp.getString("E", "");
        password = sp.getString("C", "");
        count = sp.getInt("A", 5);
        
        if(email.isEmpty() || password.isEmpty()) {
        	startActivity(new Intent(this, AccountActivity.class));
        	finish();
        }
        
        w = (WebView) findViewById(R.id.webView);
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setGeolocationEnabled(true);
        w.setWebChromeClient(new WebChromeClient() {
        	public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        		callback.invoke(origin, true, false);
        	}
        });

        w.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	
            	if(url.contains("sg/card")) {
            		view.loadUrl("https://card.starbucks.com.sg/myrewards.php");
            	} else {
            		view.loadUrl(url);
            	}
            	
            	if(url.contains("changepassword.php")) {
            		AlertDialog.Builder builder1 =
            				new AlertDialog.Builder(MainActivity.this);
            		builder1.setMessage("If you're going to change your password, please remember to update password under \"Account Settings\" too.");
            		builder1.setPositiveButton("Okay!",
            				new DialogInterface.OnClickListener() {
            			public void onClick(DialogInterface dialog, int id) {
            				dialog.cancel();
            			}
            		});
            		builder1.show();
            	}
            	
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            	
            	editor.putInt("A", ++count);
            	editor.commit();

            	if(count > 20) {
            		Context that = MainActivity.this;
            		MMSDK.initialize(that);

            		final MMInterstitial interstitial = new MMInterstitial(that);
            		MMRequest request = new MMRequest();
            		interstitial.setMMRequest(request);
            		interstitial.setApid("188100");
            		
            		interstitial.fetch();
            		interstitial.setListener(new RequestListenerImpl() {
            			@Override
            			public void requestCompleted(MMAd mmAd) {
            				interstitial.display();
            				count=0;
            				editor.putInt("A", count);
            				editor.commit();
            			}
            		});
            	}
            	
            	currentURL = url;
            	
            	String javascript="javascript:(function(){ $('li:contains(\"LOG OUT\")').remove(); })()";
        		view.loadUrl(javascript);
            	javascript="javascript:(function(){ $('a:contains(\"LOG OUT\")').remove(); })()";
        		view.loadUrl(javascript);
        		javascript="javascript:(function(){document.getElementById('Header_T4C00B4C5035_Col02').remove();})()";
        		view.loadUrl(javascript);
        		javascript="javascript:(function(){document.getElementById('footer').remove();})()";
        		view.loadUrl(javascript);
        		javascript="javascript:(function(){document.getElementById('main_content').style.marginBottom = '55px';})()";
        		view.loadUrl(javascript);
        		
        		if(url.contains("index.php")) {
        			javascript="javascript:(function(){document.getElementById('userid').value='"+email+"';document.getElementById('password').value='"+password+"';})()";
        			view.loadUrl(javascript);
        			javascript="javascript:ajaxLogin();";
        			view.loadUrl(javascript);
        			
        			javascript="javascript:( function() { var a = document.getElementById('errormsg'); a.style.fontSize = '30px'; document.getElementById('left_main').innerHTML = ''; document.getElementById('left_main').appendChild(a); } )()";
        			view.loadUrl(javascript);
            	}
            }
        });
        
        w.loadUrl(currentURL);
    }
    
    public void runAccount(View v) {
    	Intent i = new Intent(this, AccountActivity.class);
    	startActivity(i);
    }
}

