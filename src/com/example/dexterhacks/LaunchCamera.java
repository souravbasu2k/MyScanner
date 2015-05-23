/*
 * Basic no frills app which integrates the ZBar barcode scanner with
 * the camera.
 * 
 * Created by lisah0 on 2012-02-24
 */
package com.example.dexterhacks;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.example.dexterhacks.CameraPreview;

import net.sourceforge.zbar.android.CameraTest.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Button;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;

import android.widget.TextView;
/* Import ZBar Class files */
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

public class LaunchCamera extends Activity
{
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    TextView scanText;
    Button scanButton;

    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;
    
    public AlertDialog alertDialog=null;
	AlertDialog.Builder mydialogue=null;
	
	String scan_type = "", scan_data = "";
	
	private final static String DB_NAME = "MyWishList";
	private final static String TABLE_NAME = "my_wishlist";
	
	SQLiteDatabase database = null;

    static {
        System.loadLibrary("iconv");
    } 

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        mydialogue= new AlertDialog.Builder(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        scanText = (TextView)findViewById(R.id.scanText);

        scanButton = (Button)findViewById(R.id.ScanButton);

        scanButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (barcodeScanned) {
                        barcodeScanned = false;
                        scanText.setText("Scanning...");
                        mCamera.setPreviewCallback(previewCb);
                        mCamera.startPreview();
                        previewing = true;
                        mCamera.autoFocus(autoFocusCB);
                    }
                }
            });
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
            public void run() {
                if (previewing)
                    mCamera.autoFocus(autoFocusCB);
            }
        };

    PreviewCallback previewCb = new PreviewCallback() {
            @SuppressWarnings("static-access")
			public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Parameters parameters = camera.getParameters();
                Size size = parameters.getPreviewSize();

                Image barcode = new Image(size.width, size.height, "Y800");
                barcode.setData(data);

                int result = scanner.scanImage(barcode);
                
                if (result != 0) {
                	scanText.setText("");
					MediaPlayer mp = new MediaPlayer();
                	
                	try {

                	    if (mp.isPlaying()) {
                	        mp.stop();
                	        mp.release();
                	        mp=MediaPlayer.create(LaunchCamera.this, R.raw.beep1);
                	    }
                	    mp.start();
					} catch (Exception e) {
						e.printStackTrace();
					} 
                	 
                	//Set the pattern for vibration  
                     long pattern[]={0,200,100,300,400};
              
                     //Start the vibration
                     Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                     //start vibration with repeated count, use -1 if you don't want to repeat the vibration
                     vibrator.vibrate(pattern, -1);
                	
                    previewing = false;
                    mCamera.setPreviewCallback(null);
                    mCamera.stopPreview();
                    
                    SymbolSet syms = scanner.getResults();
                    
                    for (Symbol sym : syms) {
                        //scanText.setText("Result: " + sym.getData()+"\n"+sym.getType());
                        scan_type = sym.getType()+"";
                        scan_data = sym.getData()+"";
                        
                        barcodeScanned = true;
                    }
                    
                    if(!(alertDialog!=null && alertDialog.isShowing()))
					{
						mydialogue.setTitle("");
						mydialogue.setMessage("Do you want to add this product to your Wish List ?");
						mydialogue.setPositiveButton("OK", new DialogInterface.OnClickListener()
					    {
					        @Override
					        public void onClick(DialogInterface dialog, int which)
					        {
					        	insertIntoDB(scan_type, scan_data);
					        	alertDialog.cancel();
					        }
					    });
						mydialogue.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
					    {
					        @Override
					        public void onClick(DialogInterface dialog, int which)
					        {
					        	finish();
					        }
					    });
						alertDialog=mydialogue.show();
					}
                }
            }
        };
        
        
        public void insertIntoDB(String val_type, String value) {
    		String sqlstr1 = null;
    		
    		try {
    			//database = SQLiteDatabase.openOrCreateDatabase(database_name, null);
    			database = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
    			
    			sqlstr1 = "create table if not exists "+TABLE_NAME+" (id INTEGER PRIMARY KEY AUTOINCREMENT, val_type VARCHAR[10] NOT NULL, value VARCHAR[50] NOT NULL, scan_date VARCHAR[50] NOT NULL)";
    			database.execSQL(sqlstr1);
    			
    			Date dt = new Date();

    			ContentValues values = new ContentValues();
    		    values.put("val_type", val_type);
    		    values.put("value", value);
    		    values.put("scan_date", dt.toString());
    		    
    		    database.insert(TABLE_NAME, null, values);
    			database.close();
    			
    		} catch(Exception e) {
    			e.printStackTrace();
    		} finally {
    			database.close();
    		}
      }

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                autoFocusHandler.postDelayed(doAutoFocus, 1000);
            }
        };
}
