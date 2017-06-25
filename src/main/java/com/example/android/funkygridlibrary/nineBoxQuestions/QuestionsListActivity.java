/*
 * Copyright 2016 Paul Gallini
 *
 */

package com.example.android.funkygridlibrary.nineBoxQuestions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.funkygridlibrary.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

;

/**
 * This activity lists out the existing Questions and allows for additions, edits, and deletions.
 */
public class QuestionsListActivity extends AppCompatActivity {
    private final int QUESTIONSENTRY_ACTIVITY_REQUEST_CODE = 0;
    private final int QUESTIONSUPDATE_ACTIVITY_REQUEST_CODE = 0;
    public ArrayList<Questions> questionsList = new ArrayList<>();
    //    private CandidateOperations candidateOperations;
    private ListView mainListView;
    public int maxQuestionNo = 0;
    private ArrayAdapter<String> mainArrayAdapter;
    //    Context context = QuestionsListActivity.this;
    private ArrayList<String> displayList;
    private Toolbar toolbar;
    private Tracker mTracker;  // used for Google Analytics

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // grab resources
//        Resources R = getResources();
//        setContentView(R.getIdentifier("questions_list", "id", getPackageName()));
        setContentView(R.layout.questions_list);

        // set-up questions ...
        QuestionsOperations questionsOperations = new QuestionsOperations(this);
        questionsOperations.open();
        questionsList = questionsOperations.getAllQuestions();
        maxQuestionNo = questionsList.size();
        // make a list that works within the view ..
        displayList = buildDisplayList(questionsList);

        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
//        toolbar = (Toolbar) findViewById(R.getIdentifier("tool_bar", "id",  getPackageName()));
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);

        // Obtain the shared Tracker instance.
        // TODO uncomment these lines when you add analytics back
//        common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
//        mTracker = application.getDefaultTracker();
//        sendScreenImageName(); // send tag to Google Analytics

        // find the ListView so we can work with it ...
        mainListView = (ListView) findViewById(R.id.questions_list);
//        mainListView = (ListView) findViewById(R.getIdentifier("questions_list", "id", getPackageName()));

        mainArrayAdapter = new ArrayAdapter<String>(this, R.layout.questions_list_item, R.id.question_text, displayList) {
//        mainArrayAdapter = new ArrayAdapter<String>(this, R.getIdentifier("questions_list_item", "id", getPackageName()),
//                    R.getIdentifier("question_text", "id", getPackageName()), displayList) {

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                // grab resources
//                Resources R = getResources();
                if (convertView == null) {
//                    convertView = getLayoutInflater().inflate(R.getIdentifier("questions_list_item", "id", getPackageName()), parent, false);
                    convertView = getLayoutInflater().inflate(R.layout.questions_list_item, parent, false);

                }

                View view = super.getView(position, convertView, parent);

                // set the Question ID on the view
                TextView questionId = (TextView) view.findViewById(R.id.question_id);
//                TextView questionId = (TextView) view.findViewById(R.getIdentifier("question_id", "id", getPackageName()));

                questionId.setText(Long.toString(questionsList.get(position).getQuestionID()));

                // set the Question Text on the view
                TextView questionText = (TextView) view.findViewById(R.id.question_text);
//                TextView questionText = (TextView) view.findViewById(R.getIdentifier("question_text", "id", getPackageName()));

                Resources res = getResources();
                int subLength = res.getInteger( R.integer.questionTextSubLegth);
                // TODO - not sure if integer is the correct second param here
//                int subLength = res.getInteger( R.getIdentifier("questionTextSubLegth", "integer", getPackageName()));


                questionText.setText(questionsList.get(position).getQuestionText(subLength));

                // set the Question Weight on the view
                TextView questionWeight = (TextView) view.findViewById(R.id.question_weight);
//                TextView questionWeight = (TextView) view.findViewById(R.getIdentifier("question_weight", "id", getPackageName()));

                questionWeight.setText(Long.toString(questionsList.get(position).getQuestionWeight()) + "%");

                // set the Question Axis on the view
                TextView questionAxis = (TextView) view.findViewById(R.id.question_axis);
//                TextView questionAxis = (TextView) view.findViewById(R.getIdentifier("question_axis", "id", getPackageName()));

                questionAxis.setText(questionsList.get(position).getQuestionAxis() + " Axis");

                // Because the list item contains multiple touch targets, you should not override
                // onListItemClick. Instead, set a click listener for each target individually.
                convertView.findViewById(R.id.primary_target).setOnClickListener(
//                convertView.findViewById(R.getIdentifier("primary_target", "id", getPackageName())).setOnClickListener(

                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // start new intent to send current Question values to the activity ...
                                Intent intent = new Intent(QuestionsListActivity.this, QuestionsUpdateActivity.class);
                                intent.putExtra("questionId", Long.toString(questionsList.get(position).getQuestionID()));
                                intent.putExtra("questionText",questionsList.get(position).getQuestionText());
                                intent.putExtra("questionWeight", Integer.toString(questionsList.get(position).getQuestionWeight()));
                                intent.putExtra("questionAxis", questionsList.get(position).getQuestionAxis());
                                startActivityForResult(intent, QUESTIONSUPDATE_ACTIVITY_REQUEST_CODE);
                            }
                        });
                convertView.findViewById(R.id.delete_action).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Delete icon selected - delete the current Question
                                showDeleteDialog(position);
                            }
                        });

                return convertView;
            }
        };

        mainListView.setAdapter(mainArrayAdapter);

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // call QuestionsEntryActivity to add a new Question
                Intent intent = new Intent(QuestionsListActivity.this, QuestionsEntryActivity.class);
                intent.putExtra("myKey", "sampleText");
                startActivityForResult(intent, QUESTIONSENTRY_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO - un-comment-out this when you add Analytics back
//        sendScreenImageName(); // send tag to Google Analytics
        mainArrayAdapter.notifyDataSetChanged();
    }

    private ArrayList<String> buildDisplayList(ArrayList<Questions> questionsList) {
        ArrayList<String> returnList = new ArrayList();
        for (int i = 0; i < questionsList.size(); i++) {
            returnList.add(questionsList.get(i).getQuestionText());
        }
        return returnList;
    }

    //we need a handler for when the secondary activity (add new and Update question) finishes it's work
    //and returns control to this activity...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        QuestionsOperations questionsOperations = new QuestionsOperations(this);
        questionsOperations.open();
        int returnQuestionWeight = 0;
        long returnQuestionId = 0;

        if (intent != null ) {
            Bundle extras = intent.getExtras();
            String returnQuestionIdString = (extras != null ? extras.getString("returnKey") : "nothing returned");
            if(returnQuestionIdString != null ) {
                returnQuestionId = Long.parseLong(returnQuestionIdString);
            };
            String returnQuestionText = (extras != null ? extras.getString("returnQuestionText") : " ");
            String tempQuestionWeight = (extras != null ? extras.getString("returnQuestionWeight") : " ");
            if(tempQuestionWeight != null ) {
                returnQuestionWeight = Integer.parseInt(tempQuestionWeight);
            };
            String returnQuestionAxis = (extras != null ? extras.getString("returnQuestionAxis") : " ");
            String returnMode = (extras != null ? extras.getString("returnMode") : " ");
            if(returnMode.equals("ADD")) {
                // save the question
                Questions question = questionsOperations.addQuestion(returnQuestionText, returnQuestionWeight, (returnQuestionAxis.equals("X")));

                questionsList.add(question);
                displayList.add( returnQuestionText );

            } else {
                // save updated question to the database
                boolean returnVal =  questionsOperations.updateQuestion(returnQuestionId, returnQuestionText, returnQuestionWeight, (returnQuestionAxis.equals("X")));

                // locate the question in questionList and update it ...
                for( int i = 0; i < questionsList.size(); i++ ) {
                    if( questionsList.get(i).getQuestionID() == returnQuestionId) {
                        questionsList.get(i).setQuestionText(returnQuestionText);
                        questionsList.get(i).setQuestionWeight(returnQuestionWeight);
                        questionsList.get(i).setQuestionAxis(returnQuestionAxis);
                        break;
                    }
                }
            }

        }
        mainArrayAdapter.notifyDataSetChanged();
    }

    private void delete_question(int position) {
        QuestionsOperations questionsOperations = new QuestionsOperations(this);
        questionsOperations.open();

        questionsOperations.deleteCQuestion(questionsList.get(position));
        questionsList.remove(questionsList.get(position));
        displayList.remove(position);
        // notify mainArrayAdapter that things have changed and a refresh is needed ...
        mainArrayAdapter.notifyDataSetChanged();

    }

    private void showDeleteDialog(int position) {
        // grab resources
        Resources R = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsListActivity.this);
//        builder.setTitle(getString(R.string.confirm_delete_title));
        builder.setTitle(getString(R.getIdentifier("confirm_delete_title", "string", getPackageName())));

//        builder.setIcon(R.drawable.ic_bih_icon);
        builder.setIcon(R.getIdentifier("ic_bih_icon", "drawable", getPackageName()));

//        builder.setMessage(getString(R.string.confirm_delete_question_message));
        builder.setMessage(getString(R.getIdentifier("confirm_delete_question_message", "string", getPackageName())));

        boolean returnBool = false;
        final int curr_postion = position;
        Tracker mTracker;  // used for Google Analytics

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete_question(curr_postion);
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

        // Obtain the shared Tracker instance.
        // TODO uncomment these lines when you add analytics back
//        common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
//        mTracker = application.getDefaultTracker();
//        // send tag to Google Analytics
//        mTracker.setScreenName("Image~" + getResources().getString(R.string.anal_tag_questions_delete));
//        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    /**
     * Record a screen view hit for this activity
     */
    private void sendScreenImageName() {
        // grab resources
//        Resources R = getResources();
        // TODO see how to diffrentiate between adding and editing a candidate
        String name = getResources().getString(R.string.anal_tag_questions_list);
//        String name = getResources().getString(R.getIdentifier("anal_tag_questions_list", "string", getPackageName()));


        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
