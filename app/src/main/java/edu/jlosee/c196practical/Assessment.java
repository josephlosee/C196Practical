package edu.jlosee.c196practical;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Joe on 7/2/2017.
 */

public class Assessment {
    private String title = "PlaceholderTitle";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public void setTargetScore(int iTargetScore) {
        this.targetScore = iTargetScore;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setDueDate (Calendar dueDate){
        this.dueDate=dueDate;
    }

    public Calendar getDueDate(){
        return this.dueDate;
    }

    private int targetScore = 65; //default value
    private int score = 0;
    private Calendar dueDate = Calendar.getInstance();

}
