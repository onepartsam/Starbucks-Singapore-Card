package sw.starbuckssgcard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AccountActivity extends Activity {

	private SharedPreferences sp;
	private EditText txtEmail, txtPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		
		txtEmail = (EditText) findViewById(R.id.txt_email);
		txtPassword = (EditText) findViewById(R.id.txt_password);
		
		sp = this.getSharedPreferences("etc", 0);
		txtEmail.setText(sp.getString("E", ""));
		txtPassword.setText(sp.getString("C", ""));
	}
	
	public void runSave(View v) {
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("E", txtEmail.getText()+"");
		editor.putString("C", txtPassword.getText()+"");
		
		editor.commit();
		
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}
}
