package com.example.android.funkygridlibrary.nineBoxCandidates;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.funkygridlibrary.R;
import com.example.android.funkygridlibrary.common.appColor;
import com.example.android.funkygridlibrary.databaseOpenHelper.DatabaseOpenHelper;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Gallini on 4/9/16.
 */
public class CandidatesEntryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Toolbar toolbar;
    private DatabaseOpenHelper dbHelper;
    private String currentColor;
    private String candidateInitials = " ";
    public ArrayList<appColor> colorList;
    private EditText editTextName;
    private TextInputLayout editTextNameLayout;
    // Spinner element
    Spinner spinner;
    private Tracker mTracker;  // used for Google Analytics

    // TODO - get candidates_entry layout to work in landscape mode - Nexus S API 22
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidates_entry);
        // make sure the soft keyboard doesn't push everything up ...
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        // load colors from DB for spinner ...
        // Spinner Drop down elements
        dbHelper = new DatabaseOpenHelper(this);
        colorList = dbHelper.getAllColors();

        List<String> labels = getColorLabels(colorList);

        // setting this up to help support display of error when Name is empty
        editTextNameLayout = (TextInputLayout) findViewById(R.id.EditTextNameLayout);
        editTextName = (EditText) findViewById(R.id.EditTextName);
        editTextName.addTextChangedListener(new MyTextWatcher(editTextName));

        ImageView currentIcon = (ImageView) findViewById(R.id.current_icon);
        currentColor = getNetAvailableColor( colorList );
        currentIcon.setImageDrawable(Candidates.get_icon(getApplicationContext(), currentColor, candidateInitials));

        // TODO - re-add once tracking turned on
        // Obtain the shared Tracker instance.
//        common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
//        mTracker = application.getDefaultTracker();
//        sendScreenImageName(); // send tag to Google Analytics

        findViewById(R.id.EditTextName).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                boolean errorFound = false;
                if(!hasFocus) {
                    errorFound = verifyNotEmpty_Name();
                }
                if(!errorFound) {
                    setCandidateNickName();
                    setCandidateIcon();
                }
            }
        });

        findViewById(R.id.edit_candidate_icon).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // edit Initials selected - call dialog
                        showEditInitialsDialog(candidateInitials);
                    }
                });

        findViewById(R.id.save_candidate).setOnClickListener(new View.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(View view) {
                                                                     saveCandidate();
                                                                 }
                                                             }
        );

        findViewById(R.id.cancel_save_candidate).setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            finish();
                                                                        }
                                                                    }
        );
    }

    @Override
    public void onBackPressed(){
        saveCandidate();
        super.onBackPressed();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {

            verifyNotEmpty_Name();
        }
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void setCandidateIcon() {
        TextView candidateNameTV = (TextView) findViewById( R.id.EditTextName );
        String candidateName = candidateNameTV.getText().toString();
        candidateInitials = calculateInitials( candidateName );
        ImageView currentIcon = (ImageView) findViewById(R.id.current_icon);
        currentColor = getNetAvailableColor(colorList);
        currentIcon.setImageDrawable(Candidates.get_icon(getApplicationContext(), currentColor, candidateInitials));
    }

    private void setCandidateNickName() {
        TextView candidateNameTV = (TextView) findViewById(R.id.EditTextName);
        String candidateName = candidateNameTV.getText().toString();
        TextView currenNickNameTV = (TextView) findViewById(R.id.EditTextNickName);
        String currentNick = currenNickNameTV.getText().toString().trim();
        String nickName = " ";
        int firstSpace = candidateName.indexOf(" ");

        if( currentNick.length() == 0 ) {
            if (firstSpace != -1) {
                nickName = candidateName.substring(0, firstSpace);
            } else {
                nickName = candidateName;
            }
            currenNickNameTV.setText(nickName);
        }
    }

    public List<String> getColorLabels(ArrayList<appColor> colorList ) {
        List<String> labels = new ArrayList<String>();

        for (appColor currColor : colorList) {
            labels.add(currColor.getColor_text());
        }
        return labels;
    }

    private String calculateInitials( String candidateName ) {
        String returnInitials = " ";

        String tempName = candidateName.trim();
        if( tempName.length() > 0 )  {
            returnInitials = tempName.substring(0, 1) ;
            int firstSpace = tempName.indexOf(" ");

            if( firstSpace != -1  ){
                returnInitials = returnInitials.concat(tempName.substring((firstSpace+1), (firstSpace + 2)));
            }
            else if( tempName.length() > 1 ) {
                returnInitials = returnInitials.concat(tempName.substring(1, 2));
            }
        }
        return returnInitials;
    }

    private String getNetAvailableColor(ArrayList<appColor> colorList  ) {
        String returnval = " ";
        for (appColor currColor : colorList) {
            if( currColor.getColor_inuse() == 0 ) {
                returnval = currColor.getColor_number();
                break;
            }
        }
        return returnval;
    }

    private void showEditInitialsDialog(String currentInitials ) {
        Tracker mTracker;  // used for Google Analytics
        // Get the layout inflater
        LayoutInflater inflater = CandidatesEntryActivity.this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialoglayout = inflater.inflate(R.layout.candidates_edit_initials, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CandidatesEntryActivity.this);
        builder.setView(dialoglayout);
        // TODO make this generic
        builder.setIcon(R.drawable.ic_bih_icon);
        builder.setTitle(getString(R.string.edit_candidate_initials_hint));
        builder.setMessage(getString(R.string.confirm_edit_initials_message));

        TextView Initialstext = (TextView) dialoglayout.findViewById(R.id.new_initials);
        Initialstext.setText(currentInitials);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // grab the initials

                        TextView Initialstext = (TextView) dialoglayout.findViewById( R.id.new_initials);
                        if(Initialstext != null) {
                            candidateInitials = Initialstext.getText().toString();

                            ImageView currentIcon = (ImageView) findViewById(R.id.current_icon);
                            currentColor = getNetAvailableColor( colorList );
                            currentIcon.setImageDrawable(Candidates.get_icon(getApplicationContext(), currentColor, candidateInitials));
                        }
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setIcon(R.drawable.ic_bih_icon);


        // TODO - re-add once tracking turned on
        // Obtain the shared Tracker instance.
//        common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
//        mTracker = application.getDefaultTracker();
//        // send tag to Google Analytics
//        mTracker.setScreenName("Image~" + getResources().getString(R.string.anal_tag_candidates_initials));
//        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        // display dialog
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String tmpColorNum = colorList.get( pos ).getColor_number();
        currentColor = tmpColorNum;
//        int tmpcolor = Color.parseColor(currentColor);
        // refresh current icon based on the current color for this candidate ...
        ImageView currentIcon = (ImageView) findViewById(R.id.current_icon);
        currentColor = getNetAvailableColor( colorList );
        currentIcon.setImageDrawable(Candidates.get_icon(getApplicationContext(), currentColor, candidateInitials));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Selections cleared.", Toast.LENGTH_SHORT).show();
    }

    private boolean verifyNotEmpty_Name() {
        boolean errorFound = false;
        TextInputLayout inputLayout = (TextInputLayout) findViewById(R.id.EditTextNameLayout);

        final EditText Nametext = (EditText) findViewById( R.id.EditTextName );
        String canidateName = Nametext.getText().toString();

        if( canidateName.isEmpty() ){
            inputLayout.setError(getResources().getString(R.string.name_missing_error)); // show error
            // prevents the old school pop-up ...
            Nametext.setError(null);
            errorFound = true;
            Nametext.setFocusableInTouchMode(true);
            // We can't simply requesFocus here - because this gets called within onFocusChanged() ... see ..
            // https://stackoverflow.com/questions/3003062/focus-issue-with-multiple-edittexts
            Nametext.post(new Runnable() {
                @Override
                public void run() {
                    Nametext.requestFocus();
                }
            });
        } else {
            inputLayout.setError(null);
        }

        return errorFound;
    }


    public void saveCandidate() {
        boolean errorFound = false;

        // find the ListView so we can work with it ...
        TextInputLayout inputLayout = (TextInputLayout) findViewById(R.id.EditTextNameLayout);
        EditText Nametext = (EditText) findViewById( R.id.EditTextName );
        String canidateName = Nametext.getText().toString();
        EditText NickNametext = (EditText) findViewById( R.id.EditTextNickName );
        String canidateNickName = NickNametext.getText().toString();
        EditText Notestext = (EditText) findViewById( R.id.NotesText);
        String candidateNotes = Notestext.getText().toString();

        errorFound = verifyNotEmpty_Name();

        // if the initials have yet to be set, set them now
        if(candidateInitials.trim().length() == 0 ) {
            setCandidateIcon();
        }
        // save to database
        //create a new intent so we can return Candidate Data ...
        Intent intent = new Intent();
        if( !errorFound ) {
            String returnKeyValue = "0";

            //add "returnKey" as a key and assign it the value in the textbox...
            intent.putExtra("returnKey", returnKeyValue);
            intent.putExtra("returnName", canidateName);
            intent.putExtra("returnNickName", canidateNickName);
            intent.putExtra("returnNotes", candidateNotes);
            intent.putExtra("returnColor", currentColor);
            intent.putExtra("returnInitials", candidateInitials);
            intent.putExtra("returnMode", "ADD");
            //get ready to send the result back to the caller (MainActivity)
            //and put our intent into it (RESULT_OK will tell the caller that
            //we have successfully accomplished our task..
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * Record a screen view hit for this activity
     */
    private void sendScreenImageName() {
        // TODO see how to diffrentiate between adding and editing a candidate
        String name = getResources().getString(R.string.anal_tag_candidates_add);

        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void CancelSave(View view) {
        this.kill_activity();
    }

    void kill_activity()
    {
        finish();
    }

}
