package com.example.android.funkygridlibrary.nineBoxReport;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

import com.example.android.funkygridlibrary.nineBoxCandidates.Candidates;
import com.example.android.funkygridlibrary.nineBoxEvaluation.EvaluationOperations;
import com.example.android.funkygridlibrary.nineBoxQuestions.Questions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ase408 on 11/10/17.
 */

public class ReportHelper {

    public static double get_X_ResultForCandiate(Candidates currCandidate, ArrayList<Questions> questionsList, EvaluationOperations evaluationOperations) {
        double result = 0.0;
        long questionID = -1;
        long candidateID = currCandidate.getCandidateID();

        for (int i = 0; i < questionsList.size(); i++) {
            questionID = questionsList.get(i).getQuestionID();
            if (candidateID != -1 && questionID != -1) {
                // If the Axis of the current question is not X, then ignore it ...
                if (questionsList.get(i).getQuestionAxis().equals("X")) {
                    result = calcResponse(i, candidateID, questionID, result, questionsList, evaluationOperations);
                }
            }
        }
        // divide result by 100, round to hundredth's place and return it.
        return (((double) Math.round((result * 0.01) * 100d) / 100d));
    }

    public static double get_Y_ResultForCandiate(Candidates currCandidate, ArrayList<Questions> questionsList, EvaluationOperations evaluationOperations) {
        double result = 0.0;
        long questionID = -1;
        long candidateID = currCandidate.getCandidateID();

        for (int i = 0; i < questionsList.size(); i++) {
            questionID = questionsList.get(i).getQuestionID();
            if (candidateID != -1 && questionID != -1) {
                // If the Axis of the current question is not Y, then ignore it ...
                if (questionsList.get(i).getQuestionAxis().equals("Y")) {
                    result = calcResponse(i, candidateID, questionID, result, questionsList, evaluationOperations);
                }
            }
        }
        // divide result by 100, round to hundredth's place and return it.
        return (((double) Math.round((result * 0.01) * 100d) / 100d));
    }

    private static double calcResponse(int i, long candidateID, long questionID, double result, ArrayList<Questions> questionsList, EvaluationOperations evaluationOperations) {
        double returnResult = result;
        int currResponse = 1;
        int currWeight = 0;
        String questionType = "S"; // default question type to Standard

        // grab the weight
        currWeight = questionsList.get(i).getQuestionWeight();
        // grab the response ...
        currResponse = evaluationOperations.getResponseValue(candidateID, questionID);
        //  grab the quesiton type S = Standard, I = Inverse
        questionType = questionsList.get(i).getQuestion_type();
        if (currResponse > -1) {
            // add the response multiplied by the weight and add it to the result ...
            // if Standard ...
            if (questionType.trim().equals("S")) {
//                System.out.println( "  Inside questionType == S");

                returnResult = result + (currResponse * currWeight);
            } else {   // else must be Inverse, so subtract from 10
                returnResult = result + ((10 - currResponse) * currWeight);
            }
            // TODO Remove
//            System.out.println( "  --- result       = " + result);
//            System.out.println( "  --- currWeight   = " + currWeight);
//            System.out.println( "  --- currResponse = " + currResponse);
//            System.out.println( "  --- questionType = " + questionType + "***");
//            System.out.println( "  -> returnResult  = " + returnResult);
        }
        return returnResult;
    }
    // TODO move this (and other methods to a ReportHelper class
    public static Bitmap readBitmapFromFile(File dir, String fileName) {
        FileInputStream fis = null;

        Bitmap tmpBitMap = null;
        String fileString = dir + "/" + fileName;
        File file = new File(fileString);
        try {
            fis = new FileInputStream(file);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            tmpBitMap = BitmapFactory.decodeFile(fileString, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tmpBitMap;
    }

    // TODO move this (and other methods to a ReportHelper class
    /*
    * Bitmap.CompressFormat can be PNG,JPEG or WEBP.
    *
    * quality goes from 1 to 100. (Percentage).
    *
    * dir you can get from many places like Environment.getExternalStorageDirectory() or mContext.getFilesDir()
    * depending on where you want to save the image.
    */
    public static boolean saveBitmapToFile(File dir, String fileName, Bitmap bm,
                                    Bitmap.CompressFormat format, int quality) {

        File imageFile = new File(dir, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bm.compress(format, quality, fos);
            fos.close();

            return true;
        } catch (IOException e) {
            Log.e("app", e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }
    // TODO move this (and other methods to a ReportHelper class
    public static Bitmap drawableToBitmap(LayerDrawable pd) {
        Bitmap bm = Bitmap.createBitmap(pd.getIntrinsicWidth(), pd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        pd.setBounds(0, 0, pd.getIntrinsicWidth(), pd.getIntrinsicHeight());
        pd.draw(new Canvas(bm));
        return bm;
    }
}
