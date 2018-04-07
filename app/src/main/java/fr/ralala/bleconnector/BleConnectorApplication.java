package fr.ralala.bleconnector;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import fr.ralala.bleconnector.utils.gatt.GattHelper;

/********************************************************************************
 * <p><b>Project BleConnector</b><br/>
 * Application context.
 * </p>
 *
 * @author Keidan
 * <p>
 *******************************************************************************/
public class BleConnectorApplication extends Application {
  private static final String KEY_SERVER = "kStartServer";
  private static final String KEY_CURRENT_TIME_SERVICE = "kCurrentTimeService";
  private static final String KEY_BATTERY_SERVICE = "kBatteryService";
  public static final boolean DEFAULT_SERVER  = true;
  public static final boolean DEFAULT_CURRENT_TIME_SERVICE  = true;
  public static final boolean DEFAULT_BATTERY_SERVICE = true;
  private GattHelper mGattHelper;
  private SharedPreferences mSharedPreferences;
  private static BleConnectorApplication singleton;

  public static BleConnectorApplication getInstance(){
    return singleton;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    singleton = this;
    mGattHelper = new GattHelper().loadFromAssets();
    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
  }

  /**
   * Returns the instance to the GATT.
   * @return Gatt
   */
  public GattHelper getGattHelper() {
    return mGattHelper;
  }

  public boolean useServer() {
    return mSharedPreferences.getBoolean(KEY_SERVER, DEFAULT_SERVER);
  }
  public boolean useCurrentTimeService() {
    return mSharedPreferences.getBoolean(KEY_CURRENT_TIME_SERVICE, DEFAULT_CURRENT_TIME_SERVICE);
  }
  public boolean useBatteryService() {
    return mSharedPreferences.getBoolean(KEY_BATTERY_SERVICE, DEFAULT_BATTERY_SERVICE);
  }
}
