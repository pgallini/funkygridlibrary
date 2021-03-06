package com.example.android.funkygridlibrary.nineBoxCandidates;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import com.example.android.funkygridlibrary.R;
import com.example.android.funkygridlibrary.drawables.drawPoint;

import java.util.Scanner;

//import com.birdinhand.funkynetsoftware.R;
//import drawables.drawPoint;
;

/**
 * Created by Paul Gallini on 2/22/16.
 */
public class Candidates {
    private String candidateName = " ";
    private String candidateNickName = " ";
    private String candidateNotes = " ";
    private double xCoordinate = 0.0;
    private double yCoordinate = 0.0;
    private int xPhysicalLocation = 0;
    private int yPhysicalLocation = 0;
    private long candidateID = 0;
    private String candidateColor = " ";
    private String candidateInitials = " ";
    private Responses responseSet = new Responses();

    private int pomptForResponse(String qText, Scanner scanner) {
        int tmpResponse = 1;
        System.out.println(qText.toString());
        System.out.print("Enter response [1 ... 10]: ");

        boolean needValidResp = true;
        while( needValidResp ) {
            String respStr = scanner.nextLine();
            needValidResp = false;
            try {
                tmpResponse = Integer.parseInt(respStr);
                if(tmpResponse > 10 || tmpResponse < 1) {
                    needValidResp = true;
                    System.out.println("Invalid Response!!!  Please enter an integer between 1 and 10:  ");
                }
            } catch (NumberFormatException e) {
                needValidResp = true;
                System.out.println("Invalid Response!!!  Please enter an integer between 1 and 10:  ");
            }
        }
        return tmpResponse;
    }

    public Candidates() {
        super();
        int tempResponse = 1;
    }

    public Candidates(String candidateName) {
        super();
        this.candidateName = candidateName;
        int tempResponse = 1;
    }

    static public LayerDrawable get_icon(Context context, String currentColor,String candidateInitials ) {
        // this method generates and returns an icon for a candidate
        // convert the String color to an int
        int tmpcolor = Color.parseColor(currentColor);
        // set-up current icon based on the current color for this candidate ...
        Drawable d1 =  context.getResources().getDrawable(R.drawable.empty_drawable, null);
        Drawable[] emptyDrawableLayers = {d1};
        drawPoint currDrawPoint = new drawPoint(context, emptyDrawableLayers, 6, 6, tmpcolor);
        LayerDrawable newPoint = currDrawPoint.getPoint( candidateInitials );

        return newPoint;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public void setCandidateName( String name ) { candidateName = name; }

    public String getCandidateName() {
        return candidateName;
    }

    public String getCandidateNickName() { return candidateNickName; }

    public void setCandidateNickName(String candidateNickName) { this.candidateNickName = candidateNickName; }

    public void setCandidateNotes( String notes ) { candidateNotes = notes; };

    public String getCandidateNotes() { return candidateNotes; }

    public void setCandidateID( long id ) { candidateID = id; }

    public long getCandidateID() { return candidateID; }

    public String getCandidateColor() { return candidateColor; }

    public void setCandidateColor(String candidateColor) { this.candidateColor = candidateColor; }

    public String getCandidateInitials() { return candidateInitials; }

    public void setCandidateInitials(String candidateInitials) { this.candidateInitials = candidateInitials; }


    public int getxPhysicalLocation() {return xPhysicalLocation; }

    public void setxPhysicalLocation(int xPhysicalLocation) { this.xPhysicalLocation = xPhysicalLocation; }

    public int getyPhysicalLocation() { return yPhysicalLocation; }

    public void setyPhysicalLocation(int yPhysicalLocation) { this.yPhysicalLocation = yPhysicalLocation; }

}
