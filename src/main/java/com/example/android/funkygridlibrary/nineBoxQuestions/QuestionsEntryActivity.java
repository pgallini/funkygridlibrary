package com.example.android.funkygridlibrary.nineBoxQuestions;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

;

/**
 * Created by Paul Gallini on 4/9/16.
 */
public class QuestionsEntryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private boolean x_axis_selected = true;
    private Tracker mTracker;  // used for Google Analytics

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // grab resources
        Resources R = getResources();
        setContentView(R.getIdentifier("questions_entry", "id", getPackageName()));

//        final RadioButton x_axis_radio_button = (RadioButton) findViewById(R.id.x_axis_rb);
//        final RadioButton y_axis_radio_button = (RadioButton) findViewById(R.id.y_axis_rb);

        final RadioButton x_axis_radio_button = (RadioButton) findViewById(R.getIdentifier("x_axis_rb", "id", getPackageName()));
        final RadioButton y_axis_radio_button = (RadioButton) findViewById(R.getIdentifier("y_axis_rb", "id", getPackageName()));
        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
//        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar = (Toolbar) findViewById(R.getIdentifier("tool_bar", "id", getPackageName()));
        setSupportActionBar(toolbar);

        // Obtain the shared Tracker instance.
        // TODO uncomment these lines when you add analytics back
//        common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
//        mTracker = application.getDefaultTracker();
//        sendScreenImageName(); // send tag to Google Analytics

        x_axis_radio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                x_axis_selected = true;
                y_axis_radio_button.setChecked(false);
            }
        });

        y_axis_radio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                x_axis_selected = false;
                x_axis_radio_button.setChecked(false);
            }


        });
    }

    public void saveQuestion(View view) {
        boolean errorFound = false;
        // grab resources
        Resources R = getResources();
        // find the ListView so we can work with it ...
        EditText questionValue = (EditText) findViewById(R.getIdentifier( "EditQuestionText", "id", getPackageName()));
        String questionText = questionValue.getText().toString();
        EditText weightValue = (EditText) findViewById(R.getIdentifier( "WeightValue", "id", getPackageName()));
        String questionWeightText = weightValue.getText().toString();
        int questionWeight = 0;
        RadioButton x_axis_radio_button = (RadioButton) findViewById(R.getIdentifier( "x_axis_rb", "id", getPackageName()));
        String questionAxis = "Y";
        if( x_axis_radio_button.isChecked()) {
            questionAxis = "X";
        }

        if( questionText.isEmpty() ){
            questionValue.setError("Question cannot be empty!");
            errorFound = true;
        }
        if( questionWeightText.isEmpty() ){
            questionValue.setError("Weight cannot be empty!");
            errorFound = true;
        } else
        {
            questionWeight = Integer.parseInt(questionWeightText);
        }
        if( questionWeight < 0 || questionWeight > 100 ) {
            weightValue.setError("Weight must be between 0 and 100!");
            errorFound = true;
        }
        if( !errorFound ){
            //create a new intent so we can return Data ...
            Intent intent = new Intent();

            intent.putExtra("returnQuestionText",questionText);
            intent.putExtra("returnQuestionWeight",questionWeightText);
            intent.putExtra("returnQuestionAxis",questionAxis);
            intent.putExtra("returnMode","ADD");
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
        // grab resources
        Resources R = getResources();
        // TODO see how to diffrentiate between adding and editing a candidate
        String name = getResources().getString(R.getIdentifier("anal_tag_questions_add", "string", getPackageName()));

        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void CancelSave(View view) {
        this.kill_activity();
    }

    void kill_activity() {
        finish();
    }
}
