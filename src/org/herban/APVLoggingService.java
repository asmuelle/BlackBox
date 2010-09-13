package org.herban;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class APVLoggingService extends Service {
	private SensorManager sensorManager;
	private LocationManager locationManager;
	private static boolean status = false;

	/**
	 * @see android.app.Service#onBind(Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Put your code here
		return null;
	}

	/**
	 * 
	 */
	public APVLoggingService() {
		super();

	}

	/**
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

	}

	/**
	 * @see android.app.Service#onStart(Intent,int)
	 */
	@Override
	public void onStart(Intent intent, int startId) {

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 10, new LocationListener() {

					@Override
					public void onLocationChanged(Location loc) {
						Toast.makeText(APVLoggingService.this, ""+loc.getSpeed(), 10);

					}

					@Override
					public void onProviderDisabled(String provider) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProviderEnabled(String provider) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
						// TODO Auto-generated method stub

					}
				});
		sensorManager.registerListener(new SensorEventListener() {

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				Toast.makeText(APVLoggingService.this, event.sensor+"", 10);

				Log.d("Sensor_", "" + event);

			}
		}, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	public static boolean isShowingDebugToast() {
		// TODO Auto-generated method stub
		return status;
	}

	public static void setShowingDebugToast(boolean b) {
		status = b;

	}

}
