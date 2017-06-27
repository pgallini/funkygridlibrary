package com.example.android.funkygridlibrary.nineBoxQuestions;

/**
 * Created by ase408 on 2/22/16.
 */

import java.util.ArrayList;

public class Questions {
    // The base class Question allows for a collection of questions including:
    //    - Question Text - the question itself
    //    - Question Weight - how much weight does the question carry?
    //
    private long questionID = 0;
    private String questionText;
    private Integer questionWeight;
    private String questionAxis;
    private String question_type;   // default question type to Standard
    private String question_label_left;
    private String question_label_mid;
    private String question_label_right;
    private ArrayList<String> questionTextList = new ArrayList<String>();
    private ArrayList<Integer> questionWeightList = new ArrayList<Integer>();

    public ArrayList<String> getQuestionTextList() {
        return questionTextList;
    }

    public ArrayList<Integer> getQuestionWeightList() {
        return questionWeightList;
    }

    public void setQuestionText( String text ) { questionText = text; }

    public String getQuestionText() { return questionText; }

    public String getQuestionText(int maxLength ) {
        int tempLength = Math.min(maxLength, questionText.length());
        return questionText.substring(0, tempLength); }

    public void setQuestionWeight( int newWeight ) { questionWeight = newWeight; };

    public int getQuestionWeight() { return questionWeight; }

    public void setQuestionID( long id ) { questionID = id; }

    public long getQuestionID() { return questionID; }

    public String getQuestionAxis() { return questionAxis; }

    public void setQuestionAxis(String questionAxis) { this.questionAxis = questionAxis; }

    public String getQuestion_label_left() {
        return question_label_left;
    }

    public void setQuestion_label_left(String question_label_left) {
        this.question_label_left = question_label_left;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }

    public String getQuestion_label_mid() {
        return question_label_mid;
    }

    public void setQuestion_label_mid(String question_label_mid) {
        this.question_label_mid = question_label_mid;
    }

    public String getQuestion_label_right() {
        return question_label_right;
    }

    public void setQuestion_label_right(String question_label_right) {
        this.question_label_right = question_label_right;
    }

}
