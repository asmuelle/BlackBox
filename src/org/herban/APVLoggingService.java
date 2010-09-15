package org.herban;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class APVLoggingService extends Service {
	private static final int MIN_DISTANCE = 10;
	private static final int MIN_TIME = 2000;

	private final class MySensorListener implements SensorEventListener {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				double netForce = event.values[0] * event.values[0];

				netForce += event.values[1] * event.values[1];
				netForce += event.values[2] * event.values[2];

				if (threshold < netForce) {
					try {
						GregorianCalendar greg = new GregorianCalendar();
						TimeZone tz = greg.getTimeZone();
						int offset = tz.getOffset(System.currentTimeMillis());
						greg.add(Calendar.SECOND, (offset / 1000) * -1);
						StringBuffer queryBuf = new StringBuffer();
						queryBuf.append("INSERT INTO ACCELERATION "
								+ " (GMTTIMESTAMP,X1,X2,X3) VALUES (" + "'"
								+ timestampFormat.format(greg.getTime()) + "',"
								+ event.values[0] + "," + event.values[1] + ","
								+ event.values[2] + ");");

						db = openOrCreateDatabase(DATABASE_NAME,
								SQLiteDatabase.OPEN_READWRITE, null);
						db.execSQL(queryBuf.toString());
					} catch (Exception e) {

					} finally {
						if (db.isOpen())
							db.close();
					}

				} else {

				}
			}

		}
	}

	private final class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location loc) {
			try {
				if (loc.hasAccuracy() && loc.getAccuracy() <= minAccuracyMeters) {

					GregorianCalendar greg = new GregorianCalendar();
					TimeZone tz = greg.getTimeZone();
					int offset = tz.getOffset(System.currentTimeMillis());
					greg.add(Calendar.SECOND, (offset / 1000) * -1);
					StringBuffer queryBuf = new StringBuffer();
					queryBuf
							.append("INSERT INTO "
									+ POINTS_TABLE_NAME
									+ " (GMTTIMESTAMP,LATITUDE,LONGITUDE,ALTITUDE,ACCURACY,SPEED,BEARING) VALUES ("
									+ "'"
									+ timestampFormat.format(greg.getTime())
									+ "',"
									+ loc.getLatitude()
									+ ","
									+ loc.getLongitude()
									+ ","
									+ (loc.hasAltitude() ? loc.getAltitude()
											: "NULL")
									+ ","
									+ (loc.hasAccuracy() ? loc.getAccuracy()
											: "NULL")
									+ ","
									+ (loc.hasSpeed() ? loc.getSpeed() : "NULL")
									+ ","
									+ (loc.hasBearing() ? loc.getBearing()
											: "NULL") + ");");

					db = openOrCreateDatabase(DATABASE_NAME,
							SQLiteDatabase.OPEN_READWRITE, null);
					db.execSQL(queryBuf.toString());
				}
			} catch (Exception e) {

			} finally {
				if (db.isOpen())
					db.close();
			}

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
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	private float minAccuracyMeters = 100f;
	private double threshold = 200d;
	private final DateFormat timestampFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	private SensorManager sensorManager;
	private LocationManager locationManager;
 	public static final String DATABASE_NAME = "GPSLOGGERDB";
	public static final String POINTS_TABLE_NAME = "LOCATION_POINTS";
	public static final String TRIPS_TABLE_NAME = "TRIPS";

	private SQLiteDatabase db;
	private LocationListener myLocationListener = new MyLocationListener();
	private SensorEventListener mySensorListener = new MySensorListener();

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
		initDatabase();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_TIME, MIN_DISTANCE, myLocationListener);
		sensorManager.registerListener(mySensorListener, sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onDestroy() {
		locationManager.removeUpdates(myLocationListener);
		sensorManager.unregisterListener(mySensorListener);
		super.onDestroy();
	}

	private void initDatabase() {
		db = this.openOrCreateDatabase(DATABASE_NAME,
				SQLiteDatabase.OPEN_READWRITE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + POINTS_TABLE_NAME
				+ " (GMTTIMESTAMP VARCHAR, LATITUDE REAL, LONGITUDE REAL,"
				+ "ALTITUDE REAL, ACCURACY REAL, SPEED REAL, BEARING REAL);");
		db
				.execSQL("CREATE TABLE IF NOT EXISTS ACCELERATION (GMTTIMESTAMP VARCHAR, X1 REAL, X2 REAL,"
						+ "X3 REAL);");

		db.close();

	}

}
