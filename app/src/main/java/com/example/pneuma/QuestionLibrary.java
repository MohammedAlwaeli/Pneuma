package com.example.pneuma;

public class QuestionLibrary {
    private String mQuestions [] = {
            "I am not interested in other people's problems. ",
            "I use others for my own ends. ",
            "I cheat to get ahead. ",
            "I am concerned about others. ",
            "I believe that others have good intentions. ",
            "I love to help others. ",
            "I try not to think about the needy. ",
            "I am indifferent to the feelings of others. ",
            "I boast about my virtues. ",
            "I obstruct others' plans. ",
            "I yell at people. ",
            "I love a good fight. ",
            "I trust others. ",
            "I feel sympathy for those who are worse off than myself. ",
            "I distrust people. ",
            "I think highly of myself. ",
            "I insult people. ",
            "I get back at others. ",
            "I take no time for others. ",
            "I trust what people say. ",
            "I have a high opinion of myself. ",
            "I sympathize with the homeless. "

    };


    private String mChoices [][] = {
            {"Agree", "Neutral", "Disagree"},
    };








    public String getQuestion(int a) {
        String question = mQuestions[a];
        return question;
    }


    public String getChoice1() {
        String choice0 = mChoices[0][0];
        return choice0;
    }


    public String getChoice2() {
        String choice1 = mChoices[0][1];
        return choice1;
    }

    public String getChoice3() {
        String choice2 = mChoices[0][2];
        return choice2;
    }


}
