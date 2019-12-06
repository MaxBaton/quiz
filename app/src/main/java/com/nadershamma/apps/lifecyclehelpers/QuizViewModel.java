package com.nadershamma.apps.lifecyclehelpers;

import android.arch.lifecycle.ViewModel;
import android.content.res.AssetManager;
import android.util.Log;

import com.nadershamma.apps.quiz.ReferenceFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


public class QuizViewModel extends ViewModel {
    private static final String TAG = "Quiz Activity";//string for error message
    private static final int QUESTIONS_IN_QUIZ = 10;

    private List<String> fileNameList;//question file name
    private List<String> quizAnswersList;//answer options
    private Set<String> categoriesSet;//quiz categories
    private String correctAnswer;//correct answer
    private int totalGuesses;//number of attempts
    private int correctAnswers;//number of correct answers
    private int guessRows;//number of options
    private int timeToAnswer;
    private ReferenceFragment referenceFragment;
    public QuizViewModel() {
        fileNameList = new ArrayList<>();
        quizAnswersList = new ArrayList<>();
    }

    public static String getTag() {
        return TAG;
    }

    public static int getQuestionsInQuiz() {
        return QUESTIONS_IN_QUIZ;
    }

    public int getTotalGuesses() {
        return totalGuesses;
    }

    public void setTotalGuesses(int totalGuesses) {
        this.totalGuesses += totalGuesses;
    }

    public void resetTotalGuesses() {
        totalGuesses = 0;
    }

    public List<String> getFileNameList() {
        return fileNameList;
    }


    public void setFileNameList(AssetManager assets) {
        try {
            for (String category : categoriesSet) {
                //get a list of all file names in this category
                String[] paths = assets.list(category);
                for (String path : paths) {
                 fileNameList.add(path.replace(".txt",""));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading .txt file names", e);
        }
    }


    public void clearFileNameList(){
        fileNameList.clear();
    }

    public void shuffleFilenameList(){
        Collections.shuffle(fileNameList);
        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));
    }

    public List<String> getQuizAnswersList() {
        return quizAnswersList;
    }

    public void clearQuizQuestionsList(){
        quizAnswersList.clear();
    }


    public void setCategoriesSet(Set<String> categories) {
        this.categoriesSet = categories;
    }

    public int getNumberOfCategories() { return categoriesSet.size();}

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getOnlyCorrectAnswer() {
        return correctAnswer.substring((correctAnswer.indexOf('-') + 1),correctAnswer.indexOf('_'));
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers += correctAnswers;
    }

    public void resetCorrectAnswers() {
        correctAnswers = 0;
    }

    public int getGuessRows() {
        return guessRows;
    }

    public void setGuessRows(String choices) {
        guessRows = Integer.parseInt(choices) / 2;
    }

    public String getNextQuestion(){
        return quizAnswersList.remove(0);
    }

    public int getTimeToAnswer() { return timeToAnswer; }

    public void setTimeToAnswer(String timer) {
        if(!timer.equals("Без таймера")){
        timeToAnswer = Integer.parseInt(timer);
        }else {
            timeToAnswer = 100;
        }
    }

    public ReferenceFragment getReferenceFragment() {
        return referenceFragment;
    }

    public void setReferenceFragment(ReferenceFragment referenceFragment) {
        this.referenceFragment = referenceFragment;
    }
}
