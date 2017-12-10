package com.example.android.funkygridlibrary.nineBoxReport;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.funkygridlibrary.R;
import com.example.android.funkygridlibrary.common.BuildConfigUtils;
import com.example.android.funkygridlibrary.common.Utilities;
import com.example.android.funkygridlibrary.drawables.drawPoint;
import com.example.android.funkygridlibrary.nineBoxCandidates.CandidateOperations;
import com.example.android.funkygridlibrary.nineBoxCandidates.Candidates;
import com.example.android.funkygridlibrary.nineBoxEvaluation.EvaluationOperations;
import com.example.android.funkygridlibrary.nineBoxQuestions.Questions;
import com.example.android.funkygridlibrary.nineBoxQuestions.QuestionsOperations;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.util.ArrayList;

import static com.example.android.funkygridlibrary.nineBoxReport.ReportHelper.drawableToBitmap;
import static com.example.android.funkygridlibrary.nineBoxReport.ReportHelper.get_X_ResultForCandiate;
import static com.example.android.funkygridlibrary.nineBoxReport.ReportHelper.get_Y_ResultForCandiate;
import static com.example.android.funkygridlibrary.nineBoxReport.ReportHelper.saveBitmapToFile;

/**
 * Created by Paul Gallini on 5/11/16.
 * <p>
 * This activity drives the generation and presentation of the results grid.
 */
public class ReportActivity extends AppCompatActivity implements OnShowcaseEventListener {
    private Toolbar toolbar;
    private CandidateOperations candidateOperations;
    private ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
    private QuestionsOperations questionsOperations;
    private ArrayList<Questions> questionsList;
    private EvaluationOperations evaluationOperations;
    private Candidates currCandidate;
    private double result_X_axis = 0;
    private double result_Y_axis = 0;
    boolean returnHitCancel = false;  // TODO remove if not used dude
    int returnIndex = -1;

    CustomDrawableView mCustomDrawableView;
    ShowcaseView sv;   // for the showcase (tutorial) screen:
    ShowcaseView sv2;
    private Tracker mTracker;  // used for Google Analytics
    private String displayTutorialRptString = "true"; // used to tell Main Activity if we're done with this part of the Tutorial
    private Boolean displayTutorialRpt = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        // TODO - Looks like the report gets X & Y mixed up - FIX THIS!!!!!!

        Intent intent = getIntent();
        // the Main activity will send in the main toggle for the Tutorial ... grab it
        if (intent != null) {
            String displayTutorialMainString = intent.getStringExtra("displayTutorialMain");
            displayTutorialRpt = Boolean.valueOf(displayTutorialMainString);
        }

        // attach the layout to the toolbar object and then set the toolbar as the ActionBar ...
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);
        // create bitmap of the main Grid background
        Bitmap mainBackground = BitmapFactory.decodeResource(getResources(), R.drawable.grid_background);
        // reduce the size of the bitmap to save memory
//        mainBackground = Utilities.getResizedBitmap(mainBackground, 200);
        // convert the bitmap to make it mutable (otherwise, unmutable error will occur)
        Bitmap mainBackground_mb = mainBackground.copy(Bitmap.Config.ARGB_8888, true);
        // get dimenstions from the integers file
        Resources res = this.getResources();
        float scale = res.getDisplayMetrics().density;

        int reportgrid_height = 0;
        int reportgrid_width = 0;
        // calc the width of the circle ...
        int widget_width = 0;
        double widget_width_scale_factor = 0.0;
        // Calculate ActionBar height to use when in landscape mode ?
        int actionBarHeight = 0;
        int heightAdj = 0;
        int leftBound = 0;
        int rightBound = 0;
        int topBound = 0;
        int bottomBound = 0;
        int rightBoundAddon = 0;
        int leftBoundAddon = 0;
        int reportgrid_width_adjusted = 0;
        TypedValue tv = new TypedValue();

        // display message if we can't rotate
        checkOrientationChanged();
        // set it so user cannot rotate the screen for app versions < M
        // This is needed because I have been unable to get the report to draw properly in landscape
        // for earlier app versions.  Marshmellow introduces addLayer other methods that help a lot
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // for API 22 and below, we need to prevent rotating to portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        }
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        // if we're in landscape mode, it appears we need to subtract the height of the ActionBar (scaled)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            heightAdj = (int) ((actionBarHeight * scale) + res.getDimension(R.dimen.margin_small));

            reportgrid_height = (int) Math.min(res.getDisplayMetrics().widthPixels, res.getDisplayMetrics().heightPixels) - heightAdj;
            reportgrid_width = reportgrid_height;  // for now, I want these to be the same, so ignore the silly warning
            widget_width = reportgrid_height / 4;


        } else {
            reportgrid_height = (int) Math.min(res.getDisplayMetrics().widthPixels, res.getDisplayMetrics().heightPixels);
            reportgrid_width = reportgrid_height;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // trying to factor-in scale because the widgets are too large on lower density devices
                widget_width = reportgrid_height / (12 / (int) scale);
            } else {
                // trying to factor-in scale because the widgets are too large on lower density devices
                // changing widget width doesn't seem to matter for API 21 and 22
                widget_width = reportgrid_height / 20;
                // for API 22 and below, we need to turn-off the ability to flip the report to portrait (the rest of the app should still flip)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            }
        }

        // create a canvas upon which we will draw the final grid
        Canvas gridCanvas = new Canvas(Bitmap.createBitmap(reportgrid_height, reportgrid_width, Bitmap.Config.ARGB_8888));

        // Convert Bitmap to Drawable
        Drawable mainBackgroundDrawable = new BitmapDrawable(getResources(), mainBackground_mb);
        GridLayerDrawable layerDrawable = generateGridLayer(reportgrid_height, reportgrid_width, mainBackgroundDrawable);

        // for early API levels - we need to use a temp drawable to manage adding layers
        Drawable[] layers = new Drawable[2];

        // using getIntrinsicHeight() inside setWidgetPosition returns different values at times ... so
        //    we will try grabbing it once and passing it in ...
        int gridHeight = layerDrawable.getIntrinsicHeight();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // for API 22 and below, we start with adding the background grid to layer 1
            layers[0] = mainBackgroundDrawable;
        }
        // grab a list of all of the questions ..
        questionsOperations = new QuestionsOperations(this);
        questionsOperations.open();
        questionsList = questionsOperations.getAllQuestions();

        // set-up Evaulations operations ...
        evaluationOperations = new EvaluationOperations(this);
        evaluationOperations.open();

        // Loop through Candidates and add their results to the Grid ...
        // create a list of candidates from what's in the database ...
        // set-up the operations class for Candidates ...
        int currLayer = 1;
        candidateOperations = new CandidateOperations(this);
        candidateOperations.open();
        candidatesList = candidateOperations.getAllCandidates();

        // grab the number of candidates ....
        int numberOfCandidates = candidatesList.size();

        for (int i = 0; i < numberOfCandidates; i++) {

            currCandidate = candidatesList.get(i);

            // grab the next available color ...
            String currentColor = currCandidate.getCandidateColor();
            // convert the String color to an int
            int tmpcolor = Color.parseColor(currentColor);
            Drawable d1 = ResourcesCompat.getDrawable(getResources(), R.drawable.empty_drawable, null);
            Drawable[] emptyDrawableLayers = {d1};

            // TODO Remove
            System.out.println(" $$$$  currCandidate ==  " + currCandidate.getCandidateName());

            drawPoint currDrawPoint = new drawPoint(getApplicationContext(), emptyDrawableLayers, 6, 6, tmpcolor);
            LayerDrawable newPoint = currDrawPoint.getPoint(currCandidate.getCandidateInitials());
            Drawable tempPoint = newPoint.mutate();

            result_X_axis = get_X_ResultForCandiate(currCandidate, questionsList, evaluationOperations);
            currCandidate.setxCoordinate(result_X_axis);
            result_Y_axis = get_Y_ResultForCandiate(currCandidate, questionsList, evaluationOperations);
            currCandidate.setyCoordinate(result_Y_axis);

            // TODO Remove
            System.out.println(" $$$$  result_Y_axis ==  " + result_Y_axis);

            if (widgetsWillOverlap(result_X_axis, result_Y_axis, i, candidatesList)) {
                // If this icon/widget will overlap with one already drawn, then make small adjustments so both are visible
                result_X_axis = makeSmallAdjustment(result_X_axis);
                result_Y_axis = makeSmallAdjustment(result_Y_axis);
            }

            // set the physical locations (X & Y ) for current candidate
            candidatesList.get(i).setxPhysicalLocation(calcXphysicalLocation(result_X_axis, gridHeight, widget_width));
            candidatesList.get(i).setyPhysicalLocation(calcYphysicalLocation(result_Y_axis, gridHeight, widget_width, actionBarHeight));

            // Now that we have calculated X & Y values for the current candidate, we need to save it to the DB

            // if API level is 23 or greater, than we can use addLayer
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Note - I'm unclear on the last three parameters
                layerDrawable.addLayer(tempPoint, 4, widget_width, widget_width);
                layerDrawable.setLayerSize(currLayer, widget_width, widget_width);
                layerDrawable.setWidgetPosition(currLayer, result_X_axis, result_Y_axis, widget_width, gridHeight);

            } else {
                // for API 22 and below, we have to add the new point to a new layer within the
                //    array and then replace the index 0 layer of the original
                layers[1] = tempPoint;

                // by creating tmpLayerDrawable, we collapse the existing layers into one
                layerDrawable = new GridLayerDrawable(layers);

                //  the index (1) is hard-coded because we smush the layers down at the end of each iteration
                //    params are index, left offset, top offset, right, bottom
                int adjustedResult_X_axis = (int) Math.max(1, result_X_axis);
                int adjustedResult_Y_axis = (int) Math.max(1, result_Y_axis);

                // if we're in landscape mode, it appears we need to subtract the height of the ActionBar (scaled)
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                    leftBoundAddon = adjustedResult_X_axis * 6;
                    rightBoundAddon = (10 - adjustedResult_X_axis) * 20;
                    reportgrid_width_adjusted = reportgrid_width;
                    widget_width_scale_factor = 2;
                    leftBound = (int) (reportgrid_width_adjusted - (reportgrid_width_adjusted * (0.1 * (10 - adjustedResult_X_axis))) - (widget_width / widget_width_scale_factor)) + leftBoundAddon;
                    topBound = (int) ((reportgrid_height * (0.1 * (10 - adjustedResult_Y_axis))) - (widget_width / widget_width_scale_factor));
                    rightBound = reportgrid_width_adjusted - (leftBound + (int) (widget_width / widget_width_scale_factor)) + rightBoundAddon;
                    bottomBound = reportgrid_height - (topBound + (int) (widget_width / widget_width_scale_factor));

                    layerDrawable.setLayerInset(1, leftBound, topBound, rightBound, bottomBound);

                } else {
                    widget_width_scale_factor = 32;
                    leftBound = (int) (reportgrid_width - (reportgrid_width * (0.1 * (10 - adjustedResult_X_axis))) - (widget_width / widget_width_scale_factor));
                    topBound = (int) ((reportgrid_height * (0.1 * (10 - adjustedResult_Y_axis))) - (widget_width / widget_width_scale_factor));
                    rightBound = reportgrid_width - leftBound + ((int) (widget_width / widget_width_scale_factor));
                    bottomBound = reportgrid_height - topBound + (int) (widget_width / widget_width_scale_factor);

                    layerDrawable.setLayerInset(1, leftBound, topBound, rightBound, bottomBound);
                }
                // reset the layers so that the base (0) layer is the grid background plus any icons added thus far ..
                layers[0] = layerDrawable;
            }
            currLayer++;
        }  // end Loop

        // Now that we are done building the grid, add it to our View ....
        ImageView gridImageView = (ImageView) findViewById(R.id.grid_background);

        layerDrawable.draw(gridCanvas);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // setting layout params for the gridImageView ....
            ViewGroup.LayoutParams lp = gridImageView.getLayoutParams();
            lp.width = gridCanvas.getWidth();    // 5 almost works for API 21
            lp.height = gridCanvas.getHeight();
            gridImageView.requestLayout();
        }

        gridImageView.setImageDrawable(layerDrawable);

        // see if we should show the Tutorial ....
        if (getShowTutorial_Rpt()) {
            displayTutorialRpt();
            // Now that it's been displayed, lets turn it off
            displayTutorialRpt = false;
        }
        // convert the layerDrawable to bitmap so we can save it ...
        Bitmap bitMapToSave = drawableToBitmap(layerDrawable);

        File created_folder = getDir("custom", MODE_PRIVATE);
        File dir = new File(created_folder, "custom_child");
        dir.mkdirs();

        boolean doSave = true;
        if (!dir.exists()) {
            doSave = dir.mkdirs();
        }
        if (doSave) {
            saveBitmapToFile(dir, "current_report.png", bitMapToSave, Bitmap.CompressFormat.PNG, 100);
        } else {
            Log.e("app", "Couldn't create target directory.");
        }

        // Obtain the shared Tracker instance.
        // TODO uncomment these lines when you add analytics back
//        AnalyticsApplication application = (AnalyticsApplication) getApplication();
//        mTracker = application.getDefaultTracker();
//        sendScreenImageName(); // send tag to Google Analytics

        findViewById(R.id.save_report).setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View view) {
                                                                  printDocument(view);
                                                              }
                                                          }
        );

        findViewById(R.id.cancel_report).setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    Intent intent = new Intent();
                                                                    intent.putExtra("displayTutorialRptString", String.valueOf(displayTutorialRpt));
                                                                    setResult(RESULT_OK, intent);
                                                                    finish();
                                                                }
                                                            }
        );
    }

    private GridLayerDrawable generateGridLayer(int reportgrid_height, int reportgrid_width, Drawable mainBackgroundDrawable) {
        // for cases where API level is 23 or greater, we can use addLayer so we
        // create drawable array starting with just our background
        Drawable finalGrid[] = new Drawable[]{mainBackgroundDrawable};
        // create a custom layerDrawable object ... to which we will add our circles
        GridLayerDrawable layerDrawable = new GridLayerDrawable(finalGrid);
        return layerDrawable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int X = (int) event.getX();
        int Y = (int) event.getY();
        int eventaction = event.getAction();

        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                displayCandidateCard( X, Y );
                break;
        }
        return true;
    }

    private int calcXphysicalLocation(double result_X_axis, int gridHeight, int widget_width) {
        int xPhysicalLocation = (int) ((result_X_axis / 10.0) * (double) gridHeight) + (widget_width / 4);
        // TODO Remove
        System.out.println("***   result_X_axis     = " + result_X_axis);
        System.out.println("***   xPhysicalLocation = " + xPhysicalLocation);
        return xPhysicalLocation;
    }

    private int calcYphysicalLocation(double result_Y_axis, int gridHeight, int widget_width, int actionBarHeight) {
//        double tmpLocation =  gridHeight - (((result_Y_axis / 10.0)  * (double) gridHeight)  + ( widget_width / 2 ));
        double tmpLocation = gridHeight - (((result_Y_axis / 10.0) * (double) gridHeight));

        // add adjustment for the height of the action bar ....
        tmpLocation = tmpLocation + actionBarHeight;
        int yPhysicalLocation = (int) tmpLocation;

            //        // Used for debug
            //        System.out.println("***   result_Y_axis     = " + result_Y_axis);
            //        System.out.println("***   actionBarHeight   = " + actionBarHeight);
            //        System.out.println("***   yPhysicalLocation = " + yPhysicalLocation);
        return yPhysicalLocation;
    }

    private void displayCandidateCard(int touchX, int touchY) {
        // for the given touch point, display info on the candidate(s) with icons at that spot ....
        int maxDispays = 4;
        int xPhysicalLocation = 0;
        int yPhysicalLocation = 0;
        // grab the number of candidates ....
        int numberOfCandidates = candidatesList.size();
        int currentTouchRange = getTouchRange();
        ArrayList<Candidates> displayCandidatesList = new ArrayList<Candidates>();

        for (int i = 0; i < numberOfCandidates; i++) {

            currCandidate = candidatesList.get(i);
            xPhysicalLocation = currCandidate.getxPhysicalLocation();
            yPhysicalLocation = currCandidate.getyPhysicalLocation();

            // see if current Candidate is within the range of the X component of the touch ...
            if (xPhysicalLocation > (touchX - currentTouchRange) &&
                    xPhysicalLocation < (touchX + currentTouchRange)
                    ) {

                // see if current Candidate is within the range of the Y component of the touch ...
                if (yPhysicalLocation > (touchY - currentTouchRange) &&
                        yPhysicalLocation < (touchY + currentTouchRange)
                        ) {
                    displayCandidatesList.add(currCandidate);;
                }
            }
        }
        // Loop through candidates needing display (if any)
        boolean displayNextBtn = false;
        boolean displayPrevBtn = false;
        int displayCandidatesListSize = displayCandidatesList.size();
        int i = 0;
        while (i != -1 ) {
            if (i < displayCandidatesListSize ) {

                displayPrevBtn = ( i > 0 );
                displayNextBtn = ( i < ( displayCandidatesListSize - 1));
                i = displayPopUp( displayCandidatesList.get(i), i, displayPrevBtn, displayNextBtn );

            } else {
                break;
            }
        }
    }

    private int displayPopUp( final Candidates candidate, int currIndex, boolean displayPrevBtn, boolean displayNextBtn ) {
        final Dialog dialog = new Dialog(this);
        returnIndex = currIndex;
        int maxDialogHeight = 320;

        // TODO Add star to Person A & Person B
        dialog.setContentView(R.layout.candidate_pop_up);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView candidateName = (TextView) dialog.findViewById(R.id.candidateName);
        final TextView xValue = (TextView) dialog.findViewById(R.id.xValue);
        final TextView yValue = (TextView) dialog.findViewById(R.id.yValue);
        final TextView notesValue = (TextView) dialog.findViewById(R.id.notesValue);
        final Button closeButton = (Button) dialog.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View view) {
                                                                  dialog.cancel();
                                                              }
                                                          }
        );

        candidateName.setText( candidate.getCandidateName());
        xValue.setText(String.format("%3.1f", candidate.getxCoordinate()));
        yValue.setText(String.format("%3.1f", candidate.getyCoordinate()));
        notesValue.setText(candidate.getCandidateNotes());

        dialog.show();
        returnIndex++;
        return returnIndex;
    }

    private int getTouchRange() {
        // TODO see if we need to vary this based on screen density or API level or things
        return 120;
    }

    double makeSmallAdjustment(double incomingResult) {
        double adjustedResult = 0.0;

        if (incomingResult < 4.9) {
            adjustedResult = incomingResult + 0.4;
        } else {

            adjustedResult = incomingResult - 0.4;
        }
        return adjustedResult;
    }

    boolean widgetsWillOverlap(double result_X_current, double result_Y_current, int currPosition, ArrayList<Candidates> candidatesList) {
        boolean boolResult = false;

        for (int i = 0; i < currPosition; i++) {
            currCandidate = candidatesList.get(i);
            double existing_X = get_X_ResultForCandiate(currCandidate, questionsList, evaluationOperations);
            double existing_Y = get_Y_ResultForCandiate(currCandidate, questionsList, evaluationOperations);

            // If both X & Y are within 0.3 of another icon, return true
            if (Math.abs(existing_X - result_X_current) < 0.3 &&
                    Math.abs(existing_Y - result_Y_current) < 0.3) {
                boolResult = true;
            }
        }
        return boolResult;
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrientationChanged();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Display message if user trying to go landscape and API < 23
        checkOrientationChanged();

    }

    // TODO - get this to work when rotation happens during viewing the report
    private void checkOrientationChanged() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Landscape rotation Not supported for this feature.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean getShowTutorial_Rpt() {
        // returns value for whether to show tutorial for Report screen or not
        Boolean returnBool = false;
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        ;
        Boolean showTutorial = settings.getBoolean("pref_sync", true);
        if (showTutorial & displayTutorialRpt) {
            returnBool = true;
        }
        return returnBool;
    }

    private void displayTutorialRpt() {
        // set-up Layout Parameters for the tutorial
        final RelativeLayout.LayoutParams lps = getLayoutParms();
        // locate the target for the hint
        ViewTarget target = new ViewTarget(R.id.grid_background, this) {
            @Override
            public Point getPoint() {
                return Utilities.getPointTarget(findViewById(R.id.grid_background), 1.2, 4);
            }
        };
        // Create an OnClickListener to use with Tutorial and to display the next page ...
        View.OnClickListener tutBtnListener = new View.OnClickListener() {
            public void onClick(View v) {
                ViewTarget target2 = new ViewTarget(R.id.save_report, ReportActivity.this) {
                    @Override
                    public Point getPoint() {
                        return Utilities.getPointTarget(findViewById(R.id.save_report), 2);
                    }
                };
                // hide the previous view
                sv.hide();
                sv2 = buildTutorialView(target2, R.string.showcase_rpt_message2, null);
                sv2.setButtonText(getResources().getString(R.string.showcase_btn_last));
                sv2.setButtonPosition(lps);
            }
        };
        // instantiate a new view for the the tutorial ...
        sv = buildTutorialView(target, R.string.showcase_rpt_message1, tutBtnListener);
        sv.setButtonPosition(lps);
        displayTutorialRpt = false;
        SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
    }

    // TODO see if we can combine this with others
    private ShowcaseView buildTutorialView(ViewTarget target, int tutorialText, View.OnClickListener tutBtnListener) {
        return new ShowcaseView.Builder(ReportActivity.this)
                .withHoloShowcase()    // other options:  withHoloShowcase, withNewStyleShowcase, withMaterialShowcase,
                .setTarget(target)
                .setContentTitle(R.string.showcase_main_title)
                .setContentText(tutorialText)
                .setStyle(R.style.CustomShowcaseTheme)
                .setShowcaseEventListener(ReportActivity.this)
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

    // Moving to it's own class to facilitate varying by Build Flavor
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void printDocument(View view) {
        Tracker mTracker;  // used for Google Analytics

        String flavor = (String) BuildConfigUtils.getBuildConfigValue(this, "FLAVOR");
        if (flavor == "free") {
            // If this is the Free version of the app - show the Upgrade Now dialog
            showFeatureNotAvailableDialog(this);
        } else {
            // Obtain the shared Tracker instance.
            // TODO uncomment these lines when you add analytics back
//            common.AnalyticsApplication application = (common.AnalyticsApplication) getApplication();
//            mTracker = application.getDefaultTracker();
//            // send tag to Google Analytics
//            mTracker.setScreenName("Image~" + getResources().getString(R.string.anal_tag_rpt_save));
//            mTracker.send(new HitBuilders.ScreenViewBuilder().build());

            String appName = getString(R.string.appname_export);
            PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
            String jobName = appName + "_Results";
            // we need to send candidatesList into the print adapter becuase it has the calculated results (and the DB does not)
            printManager.print(jobName, new MyPrintDocumentAdapter(this, candidatesList),
                    null);
        }
    }

    /**
     * Record a screen view hit for the this activity
     */
    private void sendScreenImageName() {
        // grab resources
        String name = getResources().getString(R.string.anal_tag_report);

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

    // TODO find way to centralize this.  Can't simply add it to Utilites (can't call non-static method from static context)
    private void showFeatureNotAvailableDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setIcon(R.drawable.ic_bih_icon);
        builder.setTitle(getString(R.string.feature_not_available_title));
        builder.setMessage(getString(R.string.feature_not_available_message));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If they click OK, take them to the App Store to buy the Pro version of the app
                        try {
                            Intent rateIntent = upgradeIntentForUrl("market://details");
                            startActivity(rateIntent);
                        } catch (ActivityNotFoundException e) {
                            Intent rateIntent = upgradeIntentForUrl("https://play.google.com/store/apps/details");
                            startActivity(rateIntent);
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
        dialog.show();
    }

    private Intent upgradeIntentForUrl(String url) {
        // grab resources
        String targetPackageName = getResources().getString(R.string.package_name_pro);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, targetPackageName)));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }
}