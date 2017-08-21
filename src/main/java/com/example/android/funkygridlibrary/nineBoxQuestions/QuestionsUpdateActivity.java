package com.example.android.funkygridlibrary.nineBoxQuestions;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.android.funkygridlibrary.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

;

/**
 * Created by Paul Gallini on 4/9/16.
 */
public class QuestionsUpdateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Toolbar toolbar;
    private boolean x_axis_selected = true;
    private boolean standard_quesiton_type = true;
    private int questionId = 0;
    private TextInputLayout editQuestionTextLayout;
    private EditText editQuestionText;
    private TextInputLayout editWeight_Layout;
    private EditText editWeight;
    private Tracker mTracker;  // used for Google Analytics

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_entry);

        // make sure the soft keyboard doesn't push everything up ...
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        questionId = Integer.parseInt(getIntent().getStringExtra("questionId"));
        String questionText = getIntent().getStringExtra("questionText");
        int questionWeight = Integer.parseInt(getIntent().getStringExtra("questionWeight"));
        String questionAxis = getIntent().getStringExtra("questionAxis");
        String questionType = getIntent().getStringExtra("questionType");

        // find the ListView so we can work with it ...
        EditText questionValue = (EditText) findViewById(R.id.EditQuestionText);

        questionValue.setText(questionText);
        EditText weightValue = (EditText) findViewById(R.id.WeightValue);
        weightValue.setText(Integer.toString(questionWeight));

        final RadioButton x_axis_radio_button = (RadioButton) findViewById(R.id.x_axis_rb);
        final RadioButton y_axis_radio_button = (RadioButton) findViewById(R.id.y_axis_rb);
        final RadioButton s_type_radio_button = (RadioButton) findViewById(R.id.s_type_rb);
        final RadioButton i_type_radio_button = (RadioButton) findViewById(R.id.i_type_rb);
        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Obtain the shared Tracker instance.
        // TODO uncomment these lines when you add analytics back
//        common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
//        mTracker = application.getDefaultTracker();
//        sendScreenImageName(); // send tag to Google Analytics

        // setting this up to help support display of error when Name is empty
        editQuestionTextLayout = (TextInputLayout) findViewById(R.id.EditQuestionText_Layout);
        editQuestionText = (EditText) findViewById(R.id.EditQuestionText);
        editQuestionText.addTextChangedListener(new MyTextWatcher(editQuestionText));
        findViewById(R.id.EditQuestionText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    verifyNotEmpty_QText();
                }
            }
        });

        // setting this up to help support display of error when Weight is empty
        editWeight_Layout = (TextInputLayout) findViewById(R.id.EditWeight_Layout);
        editWeight = (EditText) findViewById(R.id.WeightValue);
        editWeight.addTextChangedListener(new MyWeightTextWatcher(editWeight));
        findViewById(R.id.WeightValue).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus) {
//                    verifyValid_QWeight();
//                }
            }
        });

        if (questionAxis.equals("X")) {
            x_axis_selected = true;
            x_axis_radio_button.setChecked(true);
            y_axis_radio_button.setChecked(false);
        } else {
            x_axis_selected = false;
            x_axis_radio_button.setChecked(false);
            y_axis_radio_button.setChecked(true);
        }
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

        if (questionType.equals("S")) {
            standard_quesiton_type = true;
            s_type_radio_button.setChecked(true);
            i_type_radio_button.setChecked(false);
        } else {
            standard_quesiton_type = false;
            s_type_radio_button.setChecked(false);
            i_type_radio_button.setChecked(true);
        }
        s_type_radio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                standard_quesiton_type = true;
                i_type_radio_button.setChecked(false);
            }
        });

        i_type_radio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                standard_quesiton_type = false;
                s_type_radio_button.setChecked(false);
            }
        });
        findViewById(R.id.save_candidate).setOnClickListener(new View.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(View view) {
                                                                     saveQuestion(view);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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
            verifyNotEmpty_QText();
        }
    }

    private class MyWeightTextWatcher implements TextWatcher {

        private View view;

        private MyWeightTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            verifyValid_QWeight();
        }
    }

    private boolean verifyNotEmpty_QText() {
        // verify that Name is not empty, display error message if it is
        boolean returnBool = false;
        final EditText Questiontext = (EditText) findViewById(R.id.EditQuestionText);
        String questionText = Questiontext.getText().toString();

        if (questionText.isEmpty()) {
            // TODO Remove
            System.out.println(" question text IS empty");
            editQuestionTextLayout.setError(getResources().getString(R.string.question_missing_error)); // show error
            Questiontext.setFocusableInTouchMode(true);
//            Questiontext.requestFocus();
            // We can't simply cann requesFocus here - because this gets called within onFocusChanged() ... see ..
            // https://stackoverflow.com/questions/3003062/focus-issue-with-multiple-edittexts
            Questiontext.post(new Runnable() {
                @Override
                public void run() {
                    Questiontext.requestFocus();
                }
            });

            returnBool = true;
        } else {
            editQuestionTextLayout.setError(null);
        }
        return returnBool;
    }

    private boolean verifyValid_QWeight() {
        // verify that Name is not empty, display error message if it is
        boolean returnBool = false;

        int questionWeight = 0;
        // verify that Name is not empty
        EditText weightValue = (EditText) findViewById(R.id.WeightValue);
        String questionWeightText = weightValue.getText().toString();

        if (questionWeightText.isEmpty()) {
            // TODO Remove
            System.out.println(" question Weight IS empty");
            editWeight_Layout.setError(getResources().getString(R.string.weight_missing_error)); // show error
            weightValue.setFocusableInTouchMode(true);
            weightValue.requestFocus();
            returnBool = true;
        } else {
            questionWeight = Integer.parseInt(questionWeightText);
        }
        if (questionWeight < 0 || questionWeight > 100) {
            // TODO Remove
            System.out.println(" question Weight IS In Valid");
            editWeight_Layout.setError(getResources().getString(R.string.weight_range_error)); // show error
            weightValue.setFocusableInTouchMode(true);
            weightValue.requestFocus();
            returnBool = true;
        }
        if (!returnBool) {
            editWeight_Layout.setError(null);
        }
        return returnBool;
    }

    public void saveQuestion(View view) {
        boolean errorFound = false;
        // find the ListView so we can work with it ...
        EditText questionValue = (EditText) findViewById(R.id.EditQuestionText);
        String questionText = questionValue.getText().toString();
        EditText weightValue = (EditText) findViewById(R.id.WeightValue);
        String questionWeightText = weightValue.getText().toString();
        int questionWeight = 0;
        RadioButton x_axis_radio_button = (RadioButton) findViewById(R.id.x_axis_rb);

        String questionAxis = "Y";

        if (x_axis_radio_button.isChecked()) {
            questionAxis = "X";
        }
        RadioButton s_type_radio_button = (RadioButton) findViewById(R.id.s_type_rb);
        String questionType = "I";
        if (s_type_radio_button.isChecked()) {
            questionType = "S";
        }
        // TODO add logic to prevent sum of X or Y > 100

        // verify that Name is not empty
        errorFound = verifyNotEmpty_QText();
        if (!errorFound) {
            errorFound = verifyValid_QWeight();
        }

        if (!errorFound) {
            editWeight_Layout.setError(null); // hide error
            //create a new intent so we can return Data ...
            Intent intent = new Intent();

            intent.putExtra("returnKey", Long.toString(questionId));
            intent.putExtra("returnQuestionText", questionText);
            intent.putExtra("returnQuestionWeight", questionWeightText);
            intent.putExtra("returnQuestionAxis", questionAxis);
            intent.putExtra("returnQuestionType", questionType);
            intent.putExtra("returnMode", "UPDATE");
            //get ready to send the result back to the caller (MainActivity)
            //and put our intent into it (RESULT_OK will tell the caller that
            //we have successfully accomplished our task..
            setResult(RESULT_OK, intent);

            finish();
        }
    }
//    public void onRadioButtonClicked(View view) {
//        // Is the button now checked?
//        boolean checked = ((RadioButton) view).isChecked();
//        // grab resources
//        Resources R = getResources();
//        // Check which radio button was clicked
//        switch(view.getId()) {
//            case R.id.x_axis_rb:
//                if (checked)
//                    // Pirates are the best
//                    break;
//            case R.id.y_axis_rb:
//                if (checked)
//                    // Ninjas rule
//                    break;
//        }
//    }

    /**
     * Record a screen view hit for this activity
     */
    private void sendScreenImageName() {
        // grab resources
//        Resources R = getResources();
        // TODO see how to diffrentiate between adding and editing a candidate
        String name = getResources().getString(R.string.anal_tag_questions_edit);
//        String name = getResources().getString(R.getIdentifier("anal_tag_questions_edit", "string", getPackageName()));

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