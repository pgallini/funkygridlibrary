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
class SortbyX implements Comparator<Candidates>
{
    // Used for sorting in ascending order of x Coordinate
    public int compare(Candidates a, Candidates b)
    {
        int tempReturn = (int) a.getxCoordinate() - (int) b.getxCoordinate();
        return tempReturn;
    }
}

public class Utilities extends AppCompatActivity {

    public static Candidates getPerson_A(ArrayList<Candidates> candidatesList) {
        int listSize = candidatesList.size();
        Candidates returnCandidate = null;

        // TODO - fix this!!!!
        if (listSize > 0) {
            Collections.sort(candidatesList, new SortbyX());
            returnCandidate = candidatesList.get(0);
            System.out.println(" #####   returnCandidate.getCandidateName() =");
            System.out.println(returnCandidate.getCandidateName());

        };
        return returnCandidate;
    }

    public static Candidates getPerson_B(ArrayList<Candidates> candidatesList) {
        int listSize = candidatesList.size();
        Candidates tempCandidate = null;

        Collections.sort(candidatesList,new SortbyX());

        if( listSize > 1 ) {
            tempCandidate = candidatesList.get(1);

            for (int i = 1; i < listSize; i++) {
                // TODO Remove
                System.out.println(" #####   i =");
                System.out.println( i );
                System.out.println(" #####   candidatesList.get(i).getCandidateName() =");
                System.out.println(candidatesList.get(i).getCandidateName());

                if( tempCandidate.getxCoordinate() < (candidatesList.get(i).getxCoordinate() + 2.0) ) {
                    if( tempCandidate.getyCoordinate() < (candidatesList.get(i).getyCoordinate() + 2.0) ) {
                        // While the current person has a great X value, it's not more than 2 AND
                        //    the next person has a much higher Y value - so return them as Person B
                        tempCandidate = candidatesList.get(i);
//                        break;
//                    } else {
//                        break;
                    }

                } else {
                        // the one we have is MORE than 2 points ahead of the next one, so lets return what we have
                        break;
                    }
                }
            }

        return tempCandidate;
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

//    public static void evalTutorialToggles(SharedPreferences.Editor editor) {
//        if (!MainActivity.displayTutorialMain && !MainActivity.displayTutorialAdd && !MainActivity.displayTutorialEval &&
//                !MainActivity.displayTutorialRpt) {
//            // if ALL of the individual tutorials have now been displayed, turn-off the preference
////            SharedPreferences settings = getSharedPreferences("preferences", Context.MODE_PRIVATE);
////            SharedPreferences.Editor editor = settings.edit();
//            editor.putBoolean("pref_sync", false);
//            editor.apply();
//            editor.commit();
//        }
    }

//
//    public RelativeLayout.LayoutParams getLayoutParms() {
//        // set-up Layout parameters for the Tutorial
//        //   Some more ideas on targets:
//        //        http://stackoverflow.com/questions/33379121/using-showcaseview-to-target-action-bar-menu-item
//        //
//        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
//        lps.setMargins(margin, margin, margin, margin);
//        return lps;
//    }
//}
