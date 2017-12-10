package com.example.android.funkygridlibrary.common;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.android.funkygridlibrary.nineBoxCandidates.Candidates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//import nineBoxMain.MainActivity;

/**
 * Created by Paul Gallini on 12/13/16.
 * <p/>
 * Class to hold common utilities, functions, and methods.
 */
public class Utilities extends AppCompatActivity {

    public static ArrayList<String> getRecommendations(ArrayList<Candidates> candidatesList) {
        class CandidatesReccomended {
            private String candidateName = " ";
            private double xyTotal = 0.0;
            private long candidateID = 0;

            public CandidatesReccomended() {
            }

            public String getCandidateName() {
                return candidateName;
            }

            public void setCandidateName(String candidateName) {
                this.candidateName = candidateName;
            }

            public double getXyTotal() {
                return xyTotal;
            }

            public void setXyTotal(double xyTotal) {
                this.xyTotal = xyTotal;
            }

            public long getCandidateID() {
                return candidateID;
            }

            public void setCandidateID(long candidateID) {
                this.candidateID = candidateID;
            }
        }

        class SortbyXYTotal implements Comparator<CandidatesReccomended> {
            // Used for sorting in ascending order of X+Y Total
            public int compare(CandidatesReccomended a, CandidatesReccomended b) {
                double tempReturn = b.getXyTotal() - a.getXyTotal();
                if (tempReturn > 0) return 1;
                if (tempReturn < 0) return -1;
                return 0;
            }
        }

        int allCandidatesListSize = candidatesList.size();
        int MIN_X_VALUE = 4;
        int MIN_Y_VALUE = 4;
        ArrayList<String> returnCandidateNames = new ArrayList<String>();
        ArrayList<CandidatesReccomended> candidatesReccomendedList = new ArrayList<CandidatesReccomended>();

        for (int i = 0; i < allCandidatesListSize; i++) {
            if (candidatesList.get(i).getxCoordinate() > MIN_X_VALUE &&
                    candidatesList.get(i).getyCoordinate() > MIN_Y_VALUE) {
                // if the X & Y vaues are at least above the min, let's add them to the temp list
                CandidatesReccomended currentCandidate = new CandidatesReccomended();
                currentCandidate.setCandidateID(candidatesList.get(i).getCandidateID());
                currentCandidate.setCandidateName(candidatesList.get(i).getCandidateName());
                currentCandidate.setXyTotal(candidatesList.get(i).getxCoordinate() + candidatesList.get(i).getyCoordinate());
                candidatesReccomendedList.add(currentCandidate);
                // TODO Remove
                // System.out.println(" &&&&&&&&&& candidatesList.get(i).getCandidateName() ====");
                // System.out.println(candidatesList.get(i).getCandidateName());
            }
        }

        // Now sort to grab the one with the highest x + y total
        Collections.sort(candidatesReccomendedList, new SortbyXYTotal());

        // Now grab top 1 or 2 people
        int crListSize = candidatesReccomendedList.size();

        if (allCandidatesListSize > 3) {
            // if there are 4 or more all together ... grab the top 2 that meet the mins
            if (crListSize > 1) {
                returnCandidateNames.add(candidatesReccomendedList.get(0).getCandidateName());
                returnCandidateNames.add(candidatesReccomendedList.get(1).getCandidateName());
            } else if (crListSize > 0) {
                returnCandidateNames.add(candidatesReccomendedList.get(0).getCandidateName());
            }
        } else {
            // if there are 3 or fewer all together ... only grab two if all three meet the mins
            if (crListSize > 2) {
                returnCandidateNames.add(candidatesReccomendedList.get(0).getCandidateName());
                returnCandidateNames.add(candidatesReccomendedList.get(1).getCandidateName());
            } else if (crListSize > 0) {
                returnCandidateNames.add(candidatesReccomendedList.get(0).getCandidateName());
            }
        }

        return returnCandidateNames;
    }

    public static Point getPointTarget(View targetView, double x_divisor) {
        // given a resource id, return a point to use for the tutorial
        // note that this is set-up to align to the right
        // change the / 6 to / 2 to center it
        int[] location = new int[2];
        targetView.getLocationInWindow(location);
        int x = (int) (location[0] + (int) targetView.getWidth() / x_divisor);
        int y = location[1] + targetView.getHeight() / 2;
        return new Point(x, y);
    }

    public static Point getPointTarget(View targetView, double x_divisor, double y_divisor) {
        // given a resource id, return a point to use for the tutorial
        // note that this is set-up to align to the right
        // change the / 6 to / 2 to center it
        // this variation takes a divisor for Y
        int[] location = new int[2];
        targetView.getLocationInWindow(location);
        int x = (int) (location[0] + (int) targetView.getWidth() / x_divisor);
        int y = (int) ((int) location[1] + (int) targetView.getHeight() / y_divisor);
        return new Point(x, y);
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
