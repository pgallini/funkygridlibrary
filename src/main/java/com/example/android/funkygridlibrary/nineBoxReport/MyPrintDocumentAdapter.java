package com.example.android.funkygridlibrary.nineBoxReport;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;

import com.example.android.funkygridlibrary.R;
import com.example.android.funkygridlibrary.common.Utilities;
import com.example.android.funkygridlibrary.drawables.drawPoint;
import com.example.android.funkygridlibrary.nineBoxCandidates.CandidateOperations;
import com.example.android.funkygridlibrary.nineBoxCandidates.Candidates;
import com.example.android.funkygridlibrary.nineBoxEvaluation.EvaluationOperations;
import com.example.android.funkygridlibrary.nineBoxQuestions.Questions;
import com.example.android.funkygridlibrary.nineBoxQuestions.QuestionsOperations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.android.funkygridlibrary.nineBoxReport.ReportHelper.drawableToBitmap;
import static com.example.android.funkygridlibrary.nineBoxReport.ReportHelper.readBitmapFromFile;

/**
 * Created by Paul Gallini on 11/10/17.
 */

// http://www.techotopia.com/index.php/An_Android_Custom_Document_Printing_Tutorial
@TargetApi(Build.VERSION_CODES.KITKAT)
public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
    Context context;
    private CandidateOperations candidateOperations;
    private ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
    // variables to control placement and size of text ....
    private int drawTabH0 = 100;
    private int drawTabH1 = 48;
    private int drawTabH2 = 82;
    private int drawTabH3 = 120;
    private int drawTabPara = 64;
    private int lineSpacingBig = 42;
    private int lineSpacing = 36;
    private int headingMain = 32;
    private int headingPara = 24;
    private int lineLength = 480;
    private int lineStrokeThick = 8;
    private int lineStrokeThin = 4;
    private int iconWidth = 80;
    private int iconTab = 440;
    private int iconAdjustment = 24;
    public static final int LINEPOSITIONSTART = 60;
    private int linePosition = LINEPOSITIONSTART;
    private int pageHeight;
    private int pageWidth;
    private PdfDocument myPdfDocument;
    private int totalpages = 0;  // start pages at 0 - always increment before adding

    private Candidates currCandidate;
    private int numCandidates;
    private int totalPageSize = 750; // number of pixels available on a single page
    private int canidateDetailHeight = 220;  // number of pixels taken-up by each Candidate details

    private Canvas canvas;
    private QuestionsOperations questionsOperations;
    private ArrayList<Questions> questionsList;
    private EvaluationOperations evaluationOperations;

    MyPrintDocumentAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback,
                         Bundle metadata) {

        myPdfDocument = new PrintedPdfDocument(context, newAttributes);
        // These dimensions are stored in the object in the form of thousandths of an inch. Since the methods that
        // will use these values later work in units of 1/72 of an inch these numbers are converted before they are stored:
        pageHeight =
                newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
        pageWidth =
                newAttributes.getMediaSize().getWidthMils() / 1000 * 72;
        // NOTE:  the user’s color selection can be obtained via a call to the getColorMode() method
        // of the PrintAttributes object which will return a value of either COLOR_MODE_COLOR or COLOR_MODE_MONOCHROME.
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        // calculate the number of pages needed ...
        // TODO - de-dup this with the same action in onWrite below
        candidateOperations = new CandidateOperations(this.context);
        candidateOperations.open();
        candidatesList = candidateOperations.getAllCandidates();
        numCandidates = candidatesList.size();

        double spaceNeededPerPage = (numCandidates * canidateDetailHeight);
        totalpages = (int) (Math.round((spaceNeededPerPage / (double) totalPageSize) + 0.5));
        totalpages++; // add one for first page and a second for the recommendation page
        if( numCandidates > 0 ) {
            totalpages++;
        }
        if (totalpages > 0) {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalpages);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        } else {
            callback.onLayoutFailed("Page count is zero.");
        }
    }

    @Override
    public void onWrite(final PageRange[] pageRanges,
                        final ParcelFileDescriptor destination,
                        final CancellationSignal cancellationSignal,
                        final WriteResultCallback callback) {
        linePosition = LINEPOSITIONSTART;
        int pageNumber = 1;
        // Draw the Main page first ....
        PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                pageHeight, pageNumber).create();

        PdfDocument.Page page = myPdfDocument.startPage(newPage);
        if (cancellationSignal.isCanceled()) {
            callback.onWriteCancelled();
            myPdfDocument.close();
            myPdfDocument = null;
            return;
        }
        drawSummaryReport(page, 1);
        myPdfDocument.finishPage(page);
        pageNumber++;

        // grab a list of all of the questions ..
        questionsOperations = new QuestionsOperations(context);
        questionsOperations.open();
        questionsList = questionsOperations.getAllQuestions();

        // set-up Evaulations operations ...
        evaluationOperations = new EvaluationOperations(context);
        evaluationOperations.open();

        // Now, loop through candidates and write subsequent pages ...
        candidateOperations = new CandidateOperations(this.context);
        candidateOperations.open();
        candidatesList = candidateOperations.getAllCandidates();
        numCandidates = candidatesList.size();

        if (numCandidates == 0) {
            // start the next page ... no candidates
            newPage = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
            page = myPdfDocument.startPage(newPage);
            drawDetailPageNoCanidates(page );
            myPdfDocument.finishPage(page);
        } else {
            // start the next page ... candidates exist
            newPage = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
            page = myPdfDocument.startPage(newPage);
            // reset linePosition
            linePosition = LINEPOSITIONSTART;
            // Loop through the candidates to build the details for the PDF File
            for (int i = 0; i < numCandidates; i++) {

                // TODO Remove
                System.out.println( "######## linePosition (before drawDetailPage = " + linePosition );

                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    myPdfDocument.close();
                    myPdfDocument = null;
                    return;
                }
                // Add the details for the current Candidate to the current page
                drawDetailPage(page, i, questionsList, evaluationOperations);

                // TODO Remove
                System.out.println( "######## linePosition (after drawDetailPage) = " + linePosition );
//                linePosition = linePosition + canidateDetailHeight;

                // TODO Remove
                System.out.println( "######## Candidate = " +candidatesList.get(i).getCandidateName() );
                System.out.println( "######## linePosition = " + linePosition );
                System.out.println( "######## canidateDetailHeight = " +canidateDetailHeight );
                System.out.println( "######## totalPageSize = " + totalPageSize);

                if ( (i < numCandidates - 1 ) && linePosition > (totalPageSize - canidateDetailHeight)) {
                    // TODO Remove
                    System.out.println( "######## Adding new page " );

                    // if we can't fit any more canidates on the current page, finish it and start a new one
                    myPdfDocument.finishPage(page);
                    pageNumber++;
                    newPage = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                    page = myPdfDocument.startPage(newPage);
                    linePosition = LINEPOSITIONSTART;
                }
            }
            myPdfDocument.finishPage(page);

            // TODO change this to NOT start a new page if there is room
            // Now, draw the Recommendations page
            pageNumber++;
            // TODO Remove
            System.out.println( "######## pageNumber = " + pageNumber);

            newPage = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
            page = myPdfDocument.startPage(newPage);
//            linePosition = LINEPOSITIONSTART;
            drawRecommendation(page);
            myPdfDocument.finishPage(page);
        }

        try {
            myPdfDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            myPdfDocument.close();
            myPdfDocument = null;
        }
        callback.onWriteFinished(pageRanges);
    }

    private void drawRecommendation(PdfDocument.Page page ) {
        // Grab top one or two people for recommendation
        Candidates person_A = Utilities.getPerson_A(candidatesList);
        Candidates person_B = Utilities.getPerson_B(candidatesList);
        canvas = page.getCanvas();
        Paint paint = new Paint();

        linePosition = LINEPOSITIONSTART;
        String Message = "Bird-in-Hand Recommendation ";
        drawMessage( Message, Color.BLACK, headingMain, true, drawTabH1 );

        drawLine(Color.BLUE, lineStrokeThin, headingMain );

        Message = "Based on the insights provided, the Bird-in-Hand";
        drawMessage( Message, Color.BLUE, headingPara, false, drawTabH1 );

        Message = "app makes the following recommendation:";
        drawMessage( Message, Color.BLUE, headingPara, false, drawTabH1 );

        if( person_A != null) {
            Message = "Spend more time and energy on ";
            drawMessage( Message, Color.BLUE, headingPara, false, drawTabH1 );
            if( person_B != null) {
                // We have two top people - lets recommend them
                Message = person_A.getCandidateName() + " and " + person_B.getCandidateName();
                drawMessage( Message, Color.BLUE, headingPara, false, drawTabH1 );
            } else {
                // We only have one person - lets recommend them
                Message = person_A.getCandidateName();
                drawMessage(Message, Color.BLUE, headingPara, false, drawTabH1);
            }
        }
        Message = "By spending more time ... ";
        drawMessage( Message, Color.BLUE, headingPara, false, drawTabH1 );
    }

    private void drawDetailPage(PdfDocument.Page page, int candidateNum, ArrayList<Questions> questionsList, EvaluationOperations evaluationOperations) {

        canvas = page.getCanvas();

        String detailString = " ";
        currCandidate = candidatesList.get(candidateNum);
        Paint paint = new Paint();
        drawLine(Color.BLUE, lineStrokeThick, drawTabH1 );

        // grab candidate ICON from file system and draw it on the PDF page
        File created_folder = context.getDir("custom", MODE_PRIVATE);
        File dir = new File(created_folder, "custom_child");

        // build the candidate icon, save it to storage, and grab the file name
        String iconBitmapName = buildIconForEmail(this.context, currCandidate);

        // Read in the icon bitmap
        String fullUrl = dir + "/" + iconBitmapName;
        Bitmap candIcon = BitmapFactory.decodeFile(fullUrl);

        //scale bitmap to desired size
        Bitmap finalIconScalled = Bitmap.createScaledBitmap(candIcon, iconWidth, iconWidth, true); // Make sure w and h are in the correct order

        canvas.drawBitmap(finalIconScalled, iconTab, linePosition - iconAdjustment, paint);

        detailString = currCandidate.getCandidateName();
        drawMessage( detailString, Color.BLACK, headingMain, true, drawTabH1);

        detailString = "(initials: ";
        detailString += currCandidate.getCandidateInitials();
        detailString += ")   ";
        drawMessage( detailString, Color.BLACK, headingPara, false, drawTabH1 );

        detailString = "Scores (out of 10): ";
        drawMessage( detailString, Color.BLACK, headingPara, false, drawTabH1 );

        detailString = "Attraction:  ";
        detailString += ReportHelper.get_X_ResultForCandiate(currCandidate, questionsList, evaluationOperations);
        drawMessage( detailString, Color.BLACK, headingPara, false, drawTabH1 );

        detailString = "Availability:  ";
        detailString += ReportHelper.get_Y_ResultForCandiate(currCandidate, questionsList, evaluationOperations);
        drawMessage( detailString, Color.BLACK, headingPara, false, drawTabH1 );
    }

    private void drawDetailPageNoCanidates(PdfDocument.Page page ) {
        canvas = page.getCanvas();
        String detailString = "No People were entered. ";
        linePosition = LINEPOSITIONSTART;
        drawMessage( detailString, Color.BLACK, headingPara, false, drawTabH1 );
        drawLine(Color.BLUE, lineStrokeThick, drawTabH1 );
        detailString = "Please select Add People from the main menu. ";
        drawMessage( detailString, Color.BLACK, headingPara, false, drawTabH1 );
    }


    private void drawSummaryReport(PdfDocument.Page page,
                                   int pagenumber) {
        canvas = page.getCanvas();
        pagenumber++; // Make sure page numbers start at 1
        Paint paint = new Paint();
        // draw the bih icon
        Drawable bih_icon = null;
        try {
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);

            bih_icon = context.getPackageManager().getApplicationIcon(app);
            int left = 40;
            int top  = 40;
            int right = 80;
            int bottom = 80;
            bih_icon.setBounds(left, top, right, bottom);
            bih_icon.draw(canvas);

        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("Failed to grab Application info.");
            e.printStackTrace();
        }

        String Message = "Bird-in-Hand Plus Summary ";
        drawMessage( Message, Color.BLACK, headingMain, true, drawTabH0 );

        // draw line under title
        drawLine(Color.BLUE, lineStrokeThin, headingMain );

        Message = "Here are your results.  We've included the";
        drawMessage( Message, Color.BLACK, headingPara, false, drawTabPara );
        Message = "main grid, our recomendations, plus details";
        drawMessage( Message, Color.BLACK, headingPara, false, drawTabPara );
        Message = "on each person. ";
        drawMessage( Message, Color.BLACK, headingPara, false, drawTabPara );

        // grab grid from file system and draw it on the PDF page
        File created_folder = context.getDir("custom", MODE_PRIVATE);
        File dir = new File(created_folder, "custom_child");

        Bitmap finalGrid = readBitmapFromFile(dir, "current_report.png");
        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setFilterBitmap(true);
        paint2.setDither(true);
        //scale bitmap
        int h = 400; // height in pixels
        int w = 400; // width in pixels
        Bitmap finalGridScalled = Bitmap.createScaledBitmap(finalGrid, w, h, true); // Make sure w and h are in the correct order

        canvas.drawBitmap(finalGridScalled, 100, 280, paint2);
    }

    public void drawMessage( String message, int color, int textSize, Boolean bold, int tabStart ) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(textSize);
        paint.setFakeBoldText(bold);
        canvas.drawText(message, tabStart, linePosition, paint);
        linePosition = linePosition + lineSpacing;
        return;
    }

    public void drawLine(int color, int lineStroke, int tabStart ) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(lineStroke);
        canvas.drawLine(tabStart, linePosition, (tabStart + lineLength), linePosition, paint);
        linePosition = linePosition + lineSpacing;
        return;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String buildIconForEmail(Context context, Candidates currCandidate) {
        // build icon for this candidate, save it to storage and return a handle for the image
        String currentColor = currCandidate.getCandidateColor();
        // convert the String color to an int
        int tmpcolor = Color.parseColor(currentColor);
        Drawable d1 = context.getResources().getDrawable(R.drawable.empty_drawable, null);
        Drawable[] emptyDrawableLayers = {d1};

        drawPoint currDrawPoint = new drawPoint(context.getApplicationContext(), emptyDrawableLayers, 6, 6, tmpcolor);
        LayerDrawable newPoint = currDrawPoint.getPoint(currCandidate.getCandidateInitials());

        // convert the layerDrawable to bitmap so we can save it ...
        Bitmap bitMapToSave = drawableToBitmap(newPoint);

        File created_folder = context.getDir("custom", MODE_PRIVATE);
        File dir = new File(created_folder, "custom_child");
        dir.mkdirs();

        String iconBitmapName = "icon_image_" + Long.toString(currCandidate.getCandidateID()) + ".png";
        boolean doSave = true;
        if (!dir.exists()) {
            doSave = dir.mkdirs();
        }
        if (doSave) {
            ReportHelper.saveBitmapToFile(dir, iconBitmapName, bitMapToSave, Bitmap.CompressFormat.PNG, 100);
        } else {
            Log.e("app", "Couldn't create target directory for saving icon bitmap.");
        }
        String iconImageID = "image-icon" + Long.toString(currCandidate.getCandidateID());

        return iconBitmapName;
    }
}