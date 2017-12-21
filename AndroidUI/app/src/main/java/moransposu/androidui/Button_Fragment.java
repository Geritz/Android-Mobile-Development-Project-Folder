package moransposu.androidui;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

import static moransposu.androidui.FacebookActivity.callbackManager;

public class Button_Fragment extends Fragment {
    FB_SQLiteHelper mSQLiteHelper;
    Cursor mSQLCursor;
    SimpleCursorAdapter mSQLCursorAdapter;
    private static final String TAG = "SQLActivity";
    SQLiteDatabase mSQLDB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fb_login_button, container, false);
        final TextView statusText = (TextView) view.findViewById(R.id.fb_textOut);

        mSQLiteHelper = new FB_SQLiteHelper(getActivity());
        mSQLDB = mSQLiteHelper.getWritableDatabase();

        // Inflate the layout for this fragment

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        final EditText postTextField = (EditText) view.findViewById(R.id.fb_post_info_text);
        EditText editTextField = (EditText) view.findViewById(R.id.fb_edit_info_text);
        EditText deleteTextField = (EditText) view.findViewById(R.id.fb_delete_info_text);
        Button postButton = (Button) view.findViewById(R.id.fb_post_info_button);
        Button editButton = (Button) view.findViewById(R.id.fb_edit_info_button);
        Button deleteButton = (Button) view.findViewById(R.id.fb_delete_info_button);

        // If using in a fragment
        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("email, publish_actions, public_profile, user_status"));
        // Callback registration
        statusText.setText("Hi I'm a status feed!");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                statusText.setText("Ready for action!");
            }

            @Override
            public void onCancel() {
                // App code
                statusText.setText("Eh, I didn't want to do that anyway.");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                statusText.setText("Whoopsie! Something broke...");
            }
        });

        postButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Bundle params = new Bundle();
                params.putString("message", postTextField.getText().toString());
                /* make the API call */
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/feed",
                        params,
                        HttpMethod.POST,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                            /* handle the result */
                                statusText.setText(getResources().getString(R.string.fb_OK));
                                statusText.setTextColor(Color.GREEN);
                                JSONObject result = response.getJSONObject();
                                try{
                                    String resultId = result.get("id").toString();
                                    //statusText.setText(resultId);
                                    EditText dbInputString = (EditText)getActivity().findViewById(R.id.fb_post_info_text);
                                    //statusText.setText(dbInputString.toString());
                                    ContentValues vals = new ContentValues();

                                    vals.put(DBContractFB.FB_DataTable.COLUMN_NAME_STRING,
                                            (dbInputString.getText().toString()));
                                    vals.put(DBContractFB.FB_DataTable.COLUMN_NAME_POSTID, resultId);
                                    mSQLDB.insert(DBContractFB.FB_DataTable.TABLE_NAME,null,vals);
                                    postTextField.setText("");

                                }catch(JSONException e){
                                    statusText.setText(e.toString());
                                }
                            }
                        }
                ).executeAsync();
                //postTextField.setText("");
            }
        });

        editButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Bundle params = new Bundle();
                params.putString("message", ((EditText)getActivity().findViewById(R.id.fb_edit_info_text)).getText().toString());
                /* make the API call */
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + ((EditText)getActivity().findViewById(R.id.fb_edit_info_id_text)).getText().toString(),
                        params,
                        HttpMethod.POST,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                            /* handle the result */
                                statusText.setText(getResources().getString(R.string.fb_OK));
                                statusText.setTextColor(Color.GREEN);
                                JSONObject result = response.getJSONObject();
                                try{
                                    String resultId = result.get("id").toString();
                                    //statusText.setText(resultId);
                                    //EditText dbInputString = (EditText)getActivity().findViewById(R.id.fb_edit_info_text);
                                    //statusText.setText(dbInputString.toString());
                                    //ContentValues vals = new ContentValues();

                                }catch(JSONException e){
                                    statusText.setText(e.toString());
                                }
                            }
                        }
                ).executeAsync();
                //postTextField.setText("");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Bundle params = new Bundle();
                /* make the API call */
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + ((EditText)getActivity().findViewById(R.id.fb_delete_info_text)).getText().toString(),
                        null,
                        HttpMethod.DELETE,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                            /* handle the result */
                                JSONObject result = response.getJSONObject();
                                try{
                                    String resultdelete = result.get("success").toString();
                                        EditText dbInputString = (EditText)getActivity().findViewById(R.id.fb_delete_info_text);
                                        //mSQLDB.execSQL("DELETE FROM " + DBContractFB.FB_DataTable.TABLE_NAME
                                          //      + " WHERE " + DBContractFB.FB_DataTable.COLUMN_NAME_POSTID + " = "
                                            //    + dbInputString.toString());
                                        ((EditText)getActivity().findViewById(R.id.fb_delete_info_text)).setText("");
                                        statusText.setText("Boom! It's gone! Boss!");
                                        statusText.setTextColor(Color.YELLOW);
                                }catch(JSONException e){
                                    statusText.setText(e.toString());
                                }
                            }
                        }
                ).executeAsync();
                //postTextField.setText("");
            }
        });
        populateTable(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                AccessToken.setCurrentAccessToken(null);
                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }

    public void onDestroy(){
        super.onDestroy();
        disconnectFromFacebook();
    }
    private void populateTable(View view){
        TextView errStatus = (TextView)getActivity().findViewById(R.id.fb_textOut);
        if(mSQLDB != null) {
            try {
                if(mSQLCursorAdapter != null && mSQLCursorAdapter.getCursor() != null){
                    if(!mSQLCursorAdapter.getCursor().isClosed()){
                        mSQLCursorAdapter.getCursor().close();
                    }
                }
                mSQLCursor = mSQLDB.query(DBContractFB.FB_DataTable.TABLE_NAME,
                        new String[]{DBContractFB.FB_DataTable._ID,
                                DBContractFB.FB_DataTable.COLUMN_NAME_STRING,
                                DBContractFB.FB_DataTable.COLUMN_NAME_POSTID},
                        DBContractFB.FB_DataTable.COLUMN_NAME_POSTID + " > ?",
                        new String[]{""},
                        null,
                        null,
                        null);
                ListView SQLListView = (ListView) view.findViewById(R.id.fb_recent_posts_listView);
                mSQLCursorAdapter = new SimpleCursorAdapter(getActivity(),
                        R.layout.fb_recent_item_layout,
                        mSQLCursor,
                        new String[]{DBContractFB.FB_DataTable.COLUMN_NAME_STRING,
                                DBContractFB.FB_DataTable.COLUMN_NAME_POSTID},
                        new int[]{R.id.fb_recent_item_layout_content,
                                R.id.fb_recent_item_layout_id},
                        0);
                SQLListView.setAdapter(mSQLCursorAdapter);
            } catch (Exception e) {
                Log.d(TAG, "Error loading data from database");
                errStatus.setText("Error loading data from database");
                errStatus.setTextColor(Color.RED);
            }
        }
    }
}

class FB_SQLiteHelper extends SQLiteOpenHelper {

    public FB_SQLiteHelper (Context context) {
        super(context, DBContractFB.FB_DataTable.DB_NAME, null, DBContractFB.FB_DataTable.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContractFB.FB_DataTable.SQL_CREATE_TABLE);

        ContentValues testValues = new ContentValues();
        testValues.put(DBContractFB.FB_DataTable.COLUMN_NAME_POSTID, 12345);
        testValues.put(DBContractFB.FB_DataTable.COLUMN_NAME_STRING, "The Database is working!");
        //testValues.put(DBContractFB.FB_DataTable.COLUMN_NAME_LAT, 2.01);
        db.insert(DBContractFB.FB_DataTable.TABLE_NAME,null,testValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContractFB.FB_DataTable.SQL_DROP_TABLE);
        onCreate(db);
    }
}

final class DBContractFB {
    private DBContractFB(){}

    public final class FB_DataTable implements BaseColumns {
        public static final String DB_NAME = "fb_db";
        public static final String TABLE_NAME = "recentPosts";
        public static final String COLUMN_NAME_STRING = "UserText";
        public static final String COLUMN_NAME_POSTID = "postId";
        public static final String COLUMN_NAME_LAT = "LAT";
        public static final int DB_VERSION = 5;


        public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
                FB_DataTable.TABLE_NAME + "(" + FB_DataTable._ID + " INTEGER PRIMARY KEY NOT NULL," +
                FB_DataTable.COLUMN_NAME_STRING + " VARCHAR(255)," +
                FB_DataTable.COLUMN_NAME_POSTID + " VARCHAR(255)," +
                FB_DataTable.COLUMN_NAME_LAT + " INT);";


        public static final String SQL_TEST_TABLE_INSERT = "INSERT INTO " + TABLE_NAME +
                " (" + COLUMN_NAME_STRING + "," + COLUMN_NAME_POSTID + "," + COLUMN_NAME_LAT +
                ") VALUES ('test', '12.34', '56.78');";

        public  static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + FB_DataTable.TABLE_NAME;
    }
}