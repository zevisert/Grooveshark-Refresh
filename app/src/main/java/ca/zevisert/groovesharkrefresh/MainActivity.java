package ca.zevisert.groovesharkrefresh;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity{

    TextView mainTextView; // Text at top of screen
    private EditText mainEditText; //Text Entry Bar
    private ArrayList mArrayList = new ArrayList(); //Stores previous entries
    private ShareActionProvider mShareActionProvider; //Provides sharing capabilities
    private static final String PREFS = "prefs"; //filename for sharedPrefrences, possibly non-modifiable
    private static final String PREF_NAME = "name"; //Key that saves user's name in sharedPrefs
    SharedPreferences mSharedPrefrences; //Used to access shared prefs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button mainButton; // Update textview button
        ImageButton mainRefresh; // Reset array button
        ListView mainListView;   // Shows previous text entires
        final ArrayAdapter<ArrayList> mArrayAdapter; // Used as middleman for EditText and ArrayList

        //Assign id's to each object from XML
        mainTextView = (TextView) findViewById(R.id.main_titleText);
        mainButton = (Button) findViewById(R.id.main_button);
        mainEditText = (EditText) findViewById(R.id.main_editText);
        mainListView = (ListView) findViewById(R.id.main_ListView);
        mainRefresh = (ImageButton) findViewById(R.id.main_Refresh);

        //Setup the array Adapter
        mArrayAdapter = new ArrayAdapter<ArrayList>(this, android.R.layout.simple_list_item_1, mArrayList);
        mainListView.setAdapter(mArrayAdapter);

        // Action when mainButton is pressed
        mainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainTextView.setText(mainEditText.getText().toString());
                mArrayList.add(mainTextView.getText().toString());
                mainEditText.setText(null);
                mArrayAdapter.notifyDataSetChanged();
            }
        });

        //Action when text entry is completed
        mainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    Log.d("[GS REFRESH]", "IME_ACTION_DONE");
                    mainButton.performClick();
                }
                return false;
            }
        });

        // Action when refresh button pressed
        mainRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mArrayList.clear();
                mainTextView.setText(R.string.title_text);
                mainEditText.setText(null);
            }
        });

        //Action when item in list is pressed
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mainTextView.setText((String)mArrayList.get(position));
                Log.d("[GS REFRESH]", position + ": " + mArrayList.get(position));
            }
        });

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        displayWelcome();
    }

    // Displays popup alert when app draws main
    private void displayWelcome(){
        mSharedPrefrences = getSharedPreferences(PREFS, MODE_PRIVATE);
        String name = mSharedPrefrences.getString(PREF_NAME, "");
        if (name.length() > 0){
            Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_LONG).show();
        }
        else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Hello!");
            alert.setMessage("Please enter a name!");

            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String input_Name = input.getText().toString();

                    SharedPreferences.Editor prefEditor = mSharedPrefrences.edit();
                    prefEditor.putString(PREF_NAME, input_Name);
                    prefEditor.commit();

                    Toast.makeText(getApplicationContext(), "Welcome, " + input_Name + "!", Toast.LENGTH_LONG).show();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });
            alert.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        if(shareItem != null){
            mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.menu_item_share && mShareActionProvider != null){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Grooveshark Refresh Sharing Development");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mainTextView.getText());
            mShareActionProvider.setShareIntent(shareIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
