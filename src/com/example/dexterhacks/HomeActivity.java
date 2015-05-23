package com.example.dexterhacks;

import net.sourceforge.zbar.android.CameraTest.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen);
		
		Button ScanButton = (Button) findViewById(R.id.ScanButton);
		ScanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), LaunchCamera.class);
				startActivity(i);				
			}
		});
		
		Button WishListButton = (Button) findViewById(R.id.WishListButton);
		WishListButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), MyWishList.class);
				startActivity(i);				
			}
		});
	}

}
