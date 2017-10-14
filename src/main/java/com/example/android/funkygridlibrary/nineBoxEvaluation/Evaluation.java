package com.example.android.funkygridlibrary.nineBoxEvaluation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.funkygridlibrary.R;
import com.example.android.funkygridlibrary.common.Utilities;
import com.example.android.funkygridlibrary.nineBoxCandidates.CandidateOperations;
import com.example.android.funkygridlibrary.nineBoxCandidates.Candidates;
import com.example.android.funkygridlibrary.nineBoxQuestions.Questions;
import com.example.android.funkygridlibrary.nineBoxQuestions.QuestionsOperations;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

/**
 * Created by Paul Gallini on 4/17/16.
 * <p>
 * The slider drawables are generated from here:  http://android-holo-colors.com/
 */
public class Evaluation extends AppCompatActivity implements OnShowcaseEventListener {
    TextView cName;
    private final int EVALUATION_ACTIVITY_REQUEST_CODE = 0;
    public ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
    public ArrayList<Questions> questionsList = new ArrayList<>();
    public int currentQuestionNo = 1;
    public int maxQuestionNo = 0;
    private Toolbar toolbar;
    private int currentResponse = 0;

    public int getCurrentCandidateIndex() {
        return currentCandidateIndex;
    }

    public void setCurrentCandidateIndex(int currentCandidateIndex) {
        this.currentCandidateIndex = currentCandidateIndex;
    }

    public void incrementCurrentCandidateIndex() {
        this.currentCandidateIndex++;
    }

    public void decrementCurrentCandidateIndex() {
        this.currentCandidateIndex--;
    }

    private int currentCandidateIndex = 0;
    // for the showcase (hint) screen:
    ShowcaseView sv;
    ShowcaseView sv2;
    private Tracker mTracker;  // used for Google Analytics
    private String displayTutorialAddString = "true"; // used to tell Main Activity if we're done with this part of the Tutorial

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int returnCode = 0;
        Intent intent = getIntent();
        // While testing with an emulator - every now and then the intent is null.  I cannot figure it out ... so we need to trap for ull
        if (intent == null) {
            super.onCreate(savedInstanceState);
            Toast.makeText(Evaluation.this, "Opps!  Something went wrong - please try again.", Toast.LENGTH_LONG);
            finish();
        } else {

            String currentPositionString = intent.getStringExtra("com.example.android.funkygridlibrary.nineBoxEvaluation.position");
            String evalCurrentCandidateOnlyString = intent.getStringExtra("evalCurrentCandidateOnly");
            final Boolean evalCurrentCandidateOnly = Boolean.parseBoolean(evalCurrentCandidateOnlyString);

            // TODO Remove
            System.out.println(" *****  Inside  Evaluation.class ****");
            System.out.println("currentPositionString = " + currentPositionString);

            // While testing with an emulator - every now and then the intent is null.  I cannot figure it out ... so we need to trap for ull
            if (currentPositionString == null) {
                super.onCreate(savedInstanceState);
                Toast.makeText(Evaluation.this, "Opps!  Something went wrong - please try again.", Toast.LENGTH_LONG);
                finish();
            } else {
                setCurrentCandidateIndex(Integer.parseInt(currentPositionString));

                super.onCreate(savedInstanceState);
                setContentView(R.layout.evaluation_entry);

                CandidateOperations candidateOperations;
                // set-up the operations class for Candidates ...
                candidateOperations = new CandidateOperations(this);
                candidateOperations.open();
                // create a list of candidates from what's in the database ...
                candidatesList = candidateOperations.getAllCandidates();

                // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
                toolbar = (Toolbar) findViewById(R.id.tool_bar);
                setSupportActionBar(toolbar);

                // set-up questions ...
                QuestionsOperations questionsOperations = new QuestionsOperations(this);
                questionsOperations.open();
                questionsList = questionsOperations.getAllQuestions();
                maxQuestionNo = questionsList.size();

                if (maxQuestionNo < 1) {
                    // If there are NO questions, then show a dialog to explain the situation and quit
                    showNoQuestionsDialog();
                }

                // For the current candidate, see if we have any responses - if we do - jump to the next question
                EvaluationOperations evaluationOperations = new EvaluationOperations(this);
                evaluationOperations.open();
                int candidateIndex = getCurrentCandidateIndex();
                long candidateID = candidatesList.get( candidateIndex ).getCandidateID();
                int responseCnt = evaluationOperations.getResponseCnt(candidateID);
                if( responseCnt > 0 && responseCnt < maxQuestionNo ) {
                    // if the count of responses is between 0 and total question count, then lets jump to the next question ...
                    currentQuestionNo = responseCnt + 1;
                }

                final TextView nextQuestionButtonView = (TextView) findViewById(R.id.next_question_button);

                // Obtain the shared Tracker instance.
                // TODO uncomment these lines when you add analytics back
                //        common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
                //        mTracker = application.getDefaultTracker();
                //        sendScreenImageName(); // send tag to Google Analytics

                //        SeekBar seek = (SeekBar) findViewById(R.getIdentifier("responseSeekBar", "id", getPackageName()));
                SeekBar seek = (SeekBar) findViewById(R.id.responseSeekBar);
                final TextView seekBarValue = (TextView) findViewById(R.id.seekbarvalue);

                seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        currentResponse = progress;
                        // Display the current value of the seekBar ...
                        seekBarValue.setText(String.valueOf(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(final SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(final SeekBar seekBar) {
                    }
                });

                // TODO change Done button to Next
                findViewById(R.id.previous_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // save the response
                        int candidateIndex = getCurrentCandidateIndex();
                        // grab resources
                        Resources R = getResources();

                        if (candidateIndex < candidatesList.size()) {

                            if (currentQuestionNo  > 0 ) {
                                saveResponse(candidatesList.get(candidateIndex).getCandidateID(), questionsList.get((currentQuestionNo - 1)).getQuestionID(), currentResponse);
                                if (currentQuestionNo > 1) {
                                    currentQuestionNo--;

                                    onResume();
                                } else {
                                    if( !evalCurrentCandidateOnly ) {       // if we are Not evaluating just the current candidate ...
                                        // increment the index for the next candidate ...
                                        decrementCurrentCandidateIndex();
                                        // reset the Questions
                                        currentQuestionNo = questionsList.size() - 1;
                                        if (candidateIndex > 0 ) {
                                            // TODO disable Previous button when at the first question first candidate
                                            // reset label of the Next btn
//                                            nextQuestionButtonView.setText(R.getIdentifier("next_question_button", "string", getPackageName()));
                                            onResume();
                                        } else {
                                            // unless we are on the last candidate
                                            setCurrentCandidateIndex(0);
                                            Intent intent = new Intent();
                                            intent.putExtra("displayTutorialAddString", displayTutorialAddString);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                        // if we ARE only evaluting the current candidate, then we're done, let's get out of here!
                                    } else {
                                        Intent intent = new Intent();
                                        intent.putExtra("displayTutorialAddString", displayTutorialAddString);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                }
                            }
                        } else {
                            // if we've looped past the last candidate, reset the index to 0 and finish
                            setCurrentCandidateIndex(0);
                            Intent intent = new Intent();
                            intent.putExtra("displayTutorialAddString", displayTutorialAddString);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });

                findViewById(R.id.next_question_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // save the response
                        int candidateIndex = getCurrentCandidateIndex();
                        // grab resources
                        Resources R = getResources();

                        if (candidateIndex < candidatesList.size()) {

                            if ((currentQuestionNo - 1) <= questionsList.size()) {
                                saveResponse(candidatesList.get(candidateIndex).getCandidateID(), questionsList.get((currentQuestionNo - 1)).getQuestionID(), currentResponse);
                                if (currentQuestionNo < maxQuestionNo) {
                                    currentQuestionNo++;
                                    if (currentQuestionNo == maxQuestionNo) {
                                        // if we are now on the last question, change the text of the button to Next Candidate or Done
                                        if ((candidateIndex == (candidatesList.size() - 1)) || evalCurrentCandidateOnly)  {
                                            nextQuestionButtonView.setText(R.getIdentifier("done_button", "string", getPackageName()));
                                        } else {
                                            nextQuestionButtonView.setText(R.getIdentifier("next_question_button_alt", "string", getPackageName()));
                                        }
                                    }
                                    onResume();
                                } else {
                                    // if we are Not evaluating just the current candidate ...
                                    if( !evalCurrentCandidateOnly ) {
                                        // increment the index for the next candidate ...
                                        incrementCurrentCandidateIndex();
                                        // reset the Questions
                                        currentQuestionNo = 1;
                                        if (candidateIndex < candidatesList.size()) {
                                            // reset label of the Next btn
                                            nextQuestionButtonView.setText(R.getIdentifier("next_question_button", "string", getPackageName()));
                                            onResume();
                                        } else {
                                            // unless we are on the last candidate
                                            setCurrentCandidateIndex(0);
                                            Intent intent = new Intent();
                                            intent.putExtra("displayTutorialAddString", displayTutorialAddString);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                     // if we ARE only evaluting the current candidate, then we're done, let's get out of here!
                                    } else {
                                        Intent intent = new Intent();
                                        intent.putExtra("displayTutorialAddString", displayTutorialAddString);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                }
                            }
                        } else {
                            // if we've looped past the last candidate, reset the index to 0 and finish
                            setCurrentCandidateIndex(0);
                            Intent intent = new Intent();
                            intent.putExtra("displayTutorialAddString", displayTutorialAddString);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
                findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            //create a new intent so we can return Candidate Data ...
                                                                            Intent intent = new Intent();
                                                                            intent.putExtra("displayTutorialAddString", displayTutorialAddString);
                                                                            setResult(RESULT_CANCELED, intent);
                                                                            finish();
                                                                        }
                                                                    }
                );
            }
        }
    }

    private void saveResponse(long candidate_id, long question_id, int response) {
        boolean wasSuccessful = false;
        EvaluationOperations evaluationOperations = new EvaluationOperations(this);
        evaluationOperations.open();
        long foundRespID = evaluationOperations.getResponseID(candidate_id, question_id);
        if (foundRespID == -1) {
            wasSuccessful = evaluationOperations.addResponse(candidate_id, question_id, response);
        } else {
            wasSuccessful = evaluationOperations.updateResponse(foundRespID, candidate_id, question_id, response);
        }
    }

    public void onResume() {
        super.onResume();
        // grab resources
//        Resources R = getResources();
//        TextView displayName = (TextView) findViewById(R.getIdentifier("evalCandidateName", "id", getPackageName()));
//        TextView currQuestionNoView = (TextView) findViewById(R.getIdentifier("curr_question_no", "id", getPackageName()));
//        TextView maxQuestionNoView = (TextView) findViewById(R.getIdentifier("max_question_no", "id", getPackageName()));
//        TextView quesitonTextView = (TextView) findViewById(R.getIdentifier("question_text", "id", getPackageName()));
        TextView displayName = (TextView) findViewById(R.id.evalCandidateName);
        TextView currQuestionNoView = (TextView) findViewById(R.id.curr_question_no);
        TextView maxQuestionNoView = (TextView) findViewById(R.id.max_question_no);
        TextView quesitonTextView = (TextView) findViewById(R.id.question_text);
        TextView questionLabelLeftTextView = (TextView) findViewById(R.id.question_label_left);
        TextView questionLabelMidTextView = (TextView) findViewById(R.id.question_label_mid);
        TextView questionLabelRightTextView = (TextView) findViewById(R.id.question_label_right);

        String currentQuestionText = " ";
        int currResponse = 1;
        long candidateID = -1;
        long questionID = -1;
        int candidateIndex = getCurrentCandidateIndex();

        currQuestionNoView.setText(Integer.toString(currentQuestionNo));
        maxQuestionNoView.setText(Integer.toString(maxQuestionNo));

        // TODO commenting-out for now - otherwise we get a count for every question.  Would rather have once per candidate
//        sendScreenImageName(); // send tag to Google Analytics

        if (candidateIndex < candidatesList.size()) {
            displayName.setText(candidatesList.get(candidateIndex).getCandidateName());
            candidateID = candidatesList.get(candidateIndex).getCandidateID();

            if (currentQuestionNo <= questionsList.size() && currentQuestionNo > 0) {
                currentQuestionText = questionsList.get((currentQuestionNo - 1)).getQuestionText();
                // If the question needs a substitution for Nick Name, apply it here
                currentQuestionText = currentQuestionText.replaceAll("%%NAME%%", candidatesList.get(candidateIndex).getCandidateNickName());
                quesitonTextView.setText(currentQuestionText);
                questionID = questionsList.get((currentQuestionNo - 1)).getQuestionID();
                questionLabelLeftTextView.setText(questionsList.get((currentQuestionNo - 1)).getQuestion_label_left());
                questionLabelMidTextView.setText(questionsList.get((currentQuestionNo - 1)).getQuestion_label_mid());
                questionLabelRightTextView.setText(questionsList.get((currentQuestionNo - 1)).getQuestion_label_right());
            }

            // See if there is already a response for this combo of candidate and question ..
            if (candidateID != -1 && questionID != -1) {
                EvaluationOperations evaluationOperations = new EvaluationOperations(this);
                evaluationOperations.open();

                if (getShowTutorial_Eval()) {
                    displayHint();
                    displayTutorialAddString = "false";
                }

                long foundRespID = evaluationOperations.getResponseID(candidateID, questionID);
                if (foundRespID != -1) {
                    // if there is a response in the DB, then set the seekbar to that value ...
                    currResponse = evaluationOperations.getResponseValue(candidateID, questionID);
                    SeekBar seek = (SeekBar) findViewById(R.id.responseSeekBar);
                    seek.setProgress(currResponse);
                }
            }
        } else {
            setCurrentCandidateIndex(0);

            // if there aren't any candidates, then display a dialog to that effect and get out
            if (candidatesList.size() < 1) {
                showNoCandidatesDialog();
            } else {
                Intent intent = new Intent();

                intent.putExtra("displayTutorialAddString", displayTutorialAddString);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private boolean getShowTutorial_Eval() {
        // returns value for whether to show tutorial for Main screen or not
        Boolean returnBool = false;
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        ;
        Boolean showTutorial = settings.getBoolean("pref_sync", true);
        if (showTutorial & Boolean.parseBoolean(displayTutorialAddString)) {
            returnBool = true;
        }
        return returnBool;
    }

    public void CancelSave(View view) {
        this.kill_activity();
    }

    void kill_activity() {
        Intent intent = new Intent();
        intent.putExtra("displayTutorialAddString", displayTutorialAddString);
        setResult(RESULT_OK, intent);
        finish();
    }

    // TODO consider removing this since we added it to EvalCandidatesListActivity
    private void showNoQuestionsDialog() {
        // grab resources
        Resources R = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(Evaluation.this);
        builder.setTitle(getString(R.getIdentifier("dialog_no_questions_title", "string", getPackageName())));
        builder.setMessage(getString(R.getIdentifier("dialog_no_questions_message", "string", getPackageName())));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        kill_activity();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        kill_activity();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    private void showNoCandidatesDialog() {
        // grab resources
        Resources R = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.getIdentifier("dialog_no_candidates_title", "string", getPackageName())));
        builder.setIcon(R.getIdentifier("ic_bih_icon", "id", getPackageName()));
        builder.setMessage(getString(R.getIdentifier("dialog_no_candidates_message", "string", getPackageName())));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        kill_activity();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        // display dialog
        dialog.show();
    }

    private void displayHint() {
        // set-up Layout Parameters for the tutorial
        final RelativeLayout.LayoutParams lps = getLayoutParms();
        // grab resources
//        Resources R = getResources();
        // locate the target for the hint
        ViewTarget target = new ViewTarget(R.id.responseSeekBar, this) {
            @Override
            public Point getPoint() {
                return Utilities.getPointTarget(findViewById(R.id.responseSeekBar), 6);

            }
        };

        // Create an OnClickListener to use with Tutorial and to display the next page ...
        View.OnClickListener tutBtnListener2 = new View.OnClickListener() {
            public void onClick(View v) {
                ViewTarget target2 = new ViewTarget(R.id.next_question_button, Evaluation.this) {
                    @Override
                    public Point getPoint() {
                        return Utilities.getPointTarget(findViewById(R.id.next_question_button), 2);
                    }
                };
                // hide the previous view
                sv.hide();
                // instantiate a new view for the the tutorial ...
                sv2 = buildTutorialView(target2, R.string.showcase_eval_message2, null);
                sv2.setButtonText(getResources().getString(R.string.showcase_btn_last));
                sv2.setButtonPosition(lps);
            }
        };

        // instantiate a new view for the the tutorial ...
        sv = buildTutorialView(target, R.string.showcase_eval_message1, tutBtnListener2);
        sv.setButtonPosition(lps);
//        MainActivity.displayTutorialEval = false;
        displayTutorialAddString = "false"; // used to tell Main Activity if we're done with this part of the Tutorial
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
//        MainActivity.evalTutorialToggles(editor);
    }

    // TODO see if we can combine this with others
    private ShowcaseView buildTutorialView(ViewTarget target, int tutorialText, View.OnClickListener tutBtnListener) {
        return new ShowcaseView.Builder(Evaluation.this)
                .withHoloShowcase()    // other options:  withHoloShowcase, withNewStyleShowcase, withMaterialShowcase,
                .setTarget(target)
                .setContentTitle(R.string.showcase_main_title)
                .setContentText(tutorialText)
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(Evaluation.this)
                .replaceEndButton(R.layout.view_custom_button)
                .setOnClickListener(tutBtnListener)
                .build();
    }

    // TODO find a way to combine this with the same method in MainActivity
    private RelativeLayout.LayoutParams getLayoutParms() {
        // set-up Layout parameters for the Tutorial
        //   Some more ideas on targets:
        //        http://stackoverflow.com/questions/33379121/using-showcaseview-to-target-action-bar-menu-item
        //
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);
        return lps;
    }

    /**
     * Record a screen view hit for the this activity
     */
    private void sendScreenImageName() {
        String name = getResources().getString(R.string.anal_tag_eval);

        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

    }
}
