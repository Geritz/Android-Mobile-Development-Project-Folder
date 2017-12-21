package moransposu.androidui;



import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.google.android.gms.common.GooglePlayServicesUtil.getErrorDialog;

public class SqlNav_Activity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    //Location Init Block
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double mLat;
    private double mLon;
    private Location mLastLocation;
    private LocationListener mLocationListener;
    private static final int LOCATION_PERMISSON_RESULT = 17;

    //SQLite Init block
    SQLiteHelper mSQLiteHelper;
    Button mSQLSubmitButton;
    Cursor mSQLCursor;
    SimpleCursorAdapter mSQLCursorAdapter;
    private static final String TAG = "SQLActivity";
    SQLiteDatabase mSQLDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_nav_);

        if (mGoogleApiClient == null) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        }

        mLat = 44.5;
        mLon = -123.2;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    mLon = location.getLongitude();
                    mLat = location.getLatitude();
                } else {
                    mLon = -123.2;
                    mLat = 44.5;
                }
            }
        };

        mSQLiteHelper = new SQLiteHelper(this);
        mSQLDB = mSQLiteHelper.getWritableDatabase();
        mSQLSubmitButton = (Button) findViewById(R.id.submit_button);
        mSQLSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSQLDB != null){
                    ContentValues vals = new ContentValues();
                    vals.put(DBContractS.DataTable.COLUMN_NAME_STRING,
                            ((EditText)findViewById(R.id.text_input)).getText().toString());
                    vals.put(DBContractS.DataTable.COLUMN_NAME_LON, mLon);
                    vals.put(DBContractS.DataTable.COLUMN_NAME_LAT, mLat);
                    mSQLDB.insert(DBContractS.DataTable.TABLE_NAME,null,vals);
                    populateTable();
                } else {
                    Log.d(TAG, "Unable to access database for writing.");
                }
            }
        });

        populateTable();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        //mLatText.setText("Activity Started");
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //mLatText.setText("onConnect");
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSON_RESULT);
            //mLonText.setText("Lacking Permissions");
            return;
        }
        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Dialog errDialog = GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0);
        errDialog.show();
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == LOCATION_PERMISSON_RESULT){
            if(grantResults.length > 0){
                updateLocation();
            }
        }
    }

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLastLocation != null){
            mLon = mLastLocation.getLongitude();
            mLat = mLastLocation.getLatitude();
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,mLocationListener);
        }
    }
    private void populateTable(){
        if(mSQLDB != null) {
            try {
                if(mSQLCursorAdapter != null && mSQLCursorAdapter.getCursor() != null){
                    if(!mSQLCursorAdapter.getCursor().isClosed()){
                        mSQLCursorAdapter.getCursor().close();
                    }
                }
                mSQLCursor = mSQLDB.query(DBContractS.DataTable.TABLE_NAME,
                        new String[]{DBContractS.DataTable._ID,
                                DBContractS.DataTable.COLUMN_NAME_STRING,
                                DBContractS.DataTable.COLUMN_NAME_LON,
                                DBContractS.DataTable.COLUMN_NAME_LAT},
                        DBContractS.DataTable.COLUMN_NAME_STRING + " > ?",
                        new String[]{""},
                        null,
                        null,
                        null);
                ListView SQLListView = (ListView) findViewById(R.id.sql_nav_list);
                mSQLCursorAdapter = new SimpleCursorAdapter(this,
                        R.layout.sql_nav_item,
                        mSQLCursor,
                        new String[]{DBContractS.DataTable.COLUMN_NAME_STRING,
                                DBContractS.DataTable.COLUMN_NAME_LON,
                                DBContractS.DataTable.COLUMN_NAME_LAT},
                        new int[]{R.id.sql_nav_text_listview_string,
                                R.id.sql_nav_lon_listview_string,
                                R.id.sql_nav_lat_listview_string},
                        0);
                SQLListView.setAdapter(mSQLCursorAdapter);
            } catch (Exception e) {
                Log.d(TAG, "Error loading data from database");
            }
        }
    }
}

class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper (Context context) {
        super(context, DBContractS.DataTable.DB_NAME, null, DBContractS.DataTable.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContractS.DataTable.SQL_CREATE_TABLE);

        ContentValues testValues = new ContentValues();
        testValues.put(DBContractS.DataTable.COLUMN_NAME_LON, 4.23);
        testValues.put(DBContractS.DataTable.COLUMN_NAME_STRING, "The Database is working!");
        testValues.put(DBContractS.DataTable.COLUMN_NAME_LAT, 2.01);
        db.insert(DBContractS.DataTable.TABLE_NAME,null,testValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContractS.DataTable.SQL_DROP_TABLE);
        onCreate(db);
    }
}

final class DBContractS {
    private DBContractS(){};

    public final class DataTable implements BaseColumns {
        public static final String DB_NAME = "data_db";
        public static final String TABLE_NAME = "dataholder";
        public static final String COLUMN_NAME_STRING = "UserText";
        public static final String COLUMN_NAME_LON = "LON";
        public static final String COLUMN_NAME_LAT = "LAT";
        public static final int DB_VERSION = 4;


        public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
                DataTable.TABLE_NAME + "(" + DataTable._ID + " INTEGER PRIMARY KEY NOT NULL," +
                DataTable.COLUMN_NAME_STRING + " VARCHAR(255)," +
                DataTable.COLUMN_NAME_LON + " REAL," +
                DataTable.COLUMN_NAME_LAT + " REAL);";


        public static final String SQL_TEST_TABLE_INSERT = "INSERT INTO " + TABLE_NAME +
                " (" + COLUMN_NAME_STRING + "," + COLUMN_NAME_LON + "," + COLUMN_NAME_LAT +
                ") VALUES ('test', '12.34', '56.78');";

        public  static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DataTable.TABLE_NAME;
    }
}