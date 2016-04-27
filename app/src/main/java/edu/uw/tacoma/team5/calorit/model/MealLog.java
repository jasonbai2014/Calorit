package edu.uw.tacoma.team5.calorit.model;

import java.util.Date;

/**
 * Created by Levi on 4/26/2016.
 */
public class MealLog {

    private int calsConsumed;
    private Date todaysDate;

    public MealLog(int calsConsumed, Date todaysDate) {
        this.calsConsumed = calsConsumed;
        this.todaysDate = todaysDate;
    }

    public int getCalsConsumed() {
        return calsConsumed;
    }

    public void setCalsConsumed(int calsConsumed) {
        this.calsConsumed = calsConsumed;
    }

    public Date getTodaysDate() {
        return todaysDate;
    }

    public void setTodaysDate(Date todaysDate) {
        this.todaysDate = todaysDate;
    }

}
