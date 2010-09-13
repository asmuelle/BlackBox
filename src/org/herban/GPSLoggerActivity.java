package org.herban;

import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GPSLoggerActivity extends Activity implements SurfaceHolder.Callback{
	
	private static double threshold=200.0d;
	private long lastShot=0;
	private MediaPlayer mMediaPlayer;
	private RingBuffer mRingBuffer;
 
	
	private final class MySensorEventListener implements SensorEventListener {
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
				double netForce=event.values[0]*event.values[0];
				
				netForce+=event.values[1]*event.values[1];
				netForce+=event.values[2]*event.values[2];
				
				if (threshold<netForce &&  lastShot+1000<SystemClock.uptimeMillis()) {
					
					lastShot=SystemClock.uptimeMillis();
					 
					  
								//mRingBuffer.writeTo(null);
					camera.takePicture(new ShutterCallback() {
						
						@Override
						public void onShutter() {
							mMediaPlayer.start();
							
						}
					}, null, new PictureCallback() {
						
						@Override
						public void onPictureTaken(byte[] data, Camera camera) {
							try {
								FileOutputStream fos = new FileOutputStream("/sdcard/s"+SystemClock.uptimeMillis()+".jpg");
								fos.write(data);
								fos.close();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					});
							  
						 
					        
					  
				 
					
				}
				else {
					 
				}
			}

		}
	}

	private SurfaceView preview=null;
	private SurfaceHolder previewHolder=null;
	private Camera camera=null;
	private SensorManager sensorManager;
	private MySensorEventListener mySensorEventListener;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		preview=(SurfaceView)findViewById(R.id.surface_camera);
		previewHolder=preview.getHolder();
		previewHolder.addCallback(this);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mySensorEventListener=new MySensorEventListener();
		mMediaPlayer = MediaPlayer.create(this, R.raw.beep);
	    mMediaPlayer.setLooping(false);
	    mMediaPlayer.setVolume(0.05f, 0.05f);
		mRingBuffer=new RingBuffer();
		 
    }

  

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int width, int height) {
		
		camera.startPreview();
		sensorManager.registerListener(mySensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		/*camera.setPreviewCallback(new PreviewCallback(){

	 

			@Override
			public void onPreviewFrame(byte[] imageBytes, Camera camera) {
				mRingBuffer.add(imageBytes, camera.getParameters().getPreviewSize());
				
			}});
		
*/
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera=Camera.open();
		Camera.Parameters parameters=camera.getParameters();
		//parameters.setPreviewSize(100, 100);
		parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
		//parameters.setPictureFormat(Camera.Parameters.)
		//parameters.setPictureSize(100, 100);
		//parameters.setPreviewFormat(PixelFormat.JPEG);
		parameters.setPreviewFrameRate(10);
	
		camera.setParameters(parameters);
		
		try {
			camera.setPreviewDisplay(previewHolder);
		}
		catch (Throwable t) {
			Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
		
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		sensorManager.unregisterListener(mySensorEventListener);
		camera.stopPreview();
		camera.release();
		camera=null;
		
	}
}