package edu.mum.ml.group7.guessasketch.android;

/**
 * Created by nuaimat on 3/19/17.
 */

public class Constants {
    public static final String middleLayerEndpoint = "http://mo-macbook.local:8080/guess-a-sketch/api/";
    //public static final String middleLayerEndpoint = "http://mo-macbook.local:8000/";
    //public static final String middleLayerEndpoint = "http://69.64.58.110/app.py";
    public static final String guessAPI = middleLayerEndpoint + "";
    public static final String positiveFeedback = middleLayerEndpoint + "positive";
    public static final String negativeFeedback = middleLayerEndpoint + "negative";

    public static final int minScoreThershold = 20;
}
