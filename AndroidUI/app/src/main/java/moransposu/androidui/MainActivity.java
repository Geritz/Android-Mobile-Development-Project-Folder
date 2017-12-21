package moransposu.androidui;

import android.content.Intent;
import android.database.AbstractCursor;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView listViewItem = (TextView)findViewById(R.id.listViewHook);
        listViewItem.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, List_Activity.class);
                startActivity(intent);
            }
        });

        TextView vertViewItem = (TextView)findViewById(R.id.listViewVert);
        vertViewItem.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, List_ActivityV.class);
                startActivity(intent);
            }
        });
        TextView gridViewItem = (TextView)findViewById(R.id.gridViewHook);
        gridViewItem.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, Grid_Activity.class);
                startActivity(intent);
            }
        });

        TextView relViewItem = (TextView)findViewById(R.id.relViewHook);
        relViewItem.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, Rel_Activity.class);
                startActivity(intent);
            }
        });

        TextView sqlViewItem = (TextView)findViewById(R.id.sqliteViewHook);
        sqlViewItem.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SQLiteActivity.class);
                startActivity(intent);
            }
        });

        TextView navViewItem = (TextView)findViewById(R.id.navViewHook);
        navViewItem.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                startActivity(intent);
            }
        });

        TextView sqlNavAssignment = (TextView)findViewById(R.id.sqlnavHook);
        sqlNavAssignment.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SqlNav_Activity.class);
                startActivity(intent);
            }
        });

        TextView httpoauthAssignment = (TextView)findViewById(R.id.httpoauth);
        httpoauthAssignment.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, API_Activity.class);
                startActivity(intent);
            }
        });

        TextView fb_qpost = (TextView)findViewById(R.id.facebook_quick_post);
        fb_qpost.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, FacebookActivity.class);
                startActivity(intent);
            }
        });
    }
}
