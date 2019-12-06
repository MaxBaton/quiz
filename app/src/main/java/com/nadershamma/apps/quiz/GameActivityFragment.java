package com.nadershamma.apps.quiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.nadershamma.apps.eventhandlers.GuessButtonListener;
import com.nadershamma.apps.lifecyclehelpers.QuizViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameActivityFragment extends Fragment {

    private SecureRandom random;
    private Animation shakeAnimation;//incorrect answer animation
    private ConstraintLayout quizConstraintLayout;
    private TextView questionNumberTextView;//current question number
    private TextView questionTextView;
    private TableRow[] guessTableRows;//number of lines with buttons
    private TextView answerTextView;//correct answer
    private TextView countDownTimerTextView;//timer
    private QuizViewModel quizViewModel;
    private CountDownTimer countDownTimer;
    private int  numOfRows;
    private long timeForResumeTimer;
    private int  sec;
    private Handler handler;
    private int restOfGuesses;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quizViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(QuizViewModel.class);
        handler = new Handler();
    }

    public CountDownTimer getCountDownTimer() {return countDownTimer;}

    public void startTimer(long timeToAnswerL){
            numOfRows = quizViewModel.getGuessRows() * 2;
            countDownTimer = new CountDownTimer(timeToAnswerL,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeForResumeTimer = millisUntilFinished;
                    sec = (int)millisUntilFinished/1000;
                    countDownTimerTextView.setText(String.valueOf(sec));
                }

                @Override
                public void onFinish() {
                    disableButtons();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            answerTextView.setText(getQuizViewModel().getOnlyCorrectAnswer() + "!");
                            answerTextView.setTextColor(getResources().getColor(R.color.correct_answer));
                        }
                    });
                    restOfGuesses = GuessButtonListener.getNumOfGuesses();
                    numOfRows-=restOfGuesses;
                    quizViewModel.setCorrectAnswers(1);
                    quizViewModel.setTotalGuesses(numOfRows);
                    if(quizViewModel.getCorrectAnswers()==QuizViewModel.getQuestionsInQuiz()){
                        ResultsDialogFragment quizResults = new ResultsDialogFragment();
                        quizResults.setCancelable(false);
                        try {
                            quizResults.show(getChildFragmentManager(), "Quiz Results");
                        } catch (NullPointerException e) {
                            Log.e(QuizViewModel.getTag(),
                                    "GuessButtonListener: mainActivityFragment.getFragmentManager() " +
                                            "returned null",
                                    e);
                        }
                    }else{
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadNextQuestion();
                            }
                        },800);
                    }
                }
            }.start();
    }

    public void stopTimer(){
        if(quizViewModel.getTimeToAnswer() != 100)
        countDownTimer.cancel();
    }

    public void resumeTimer(){
            if(quizViewModel.getTimeToAnswer() != 100){
            countDownTimerTextView.setText(String.valueOf(sec));
            startTimer(timeForResumeTimer);
            }else {
                countDownTimerTextView.setText(" ");
            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        OnClickListener guessButtonListener = new GuessButtonListener(this);
        TableLayout answersTableLayout = view.findViewById(R.id.answersTableLayout);
        random = new SecureRandom();
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3);//repeat animation 3 times
        quizConstraintLayout = view.findViewById(R.id.quizConstraintLayout);
        questionNumberTextView = view.findViewById(R.id.questionNumberTextView);
        questionTextView = view.findViewById(R.id.questionTextView);
        countDownTimerTextView = view.findViewById(R.id.countDownTimerTextView);
        guessTableRows = new TableRow[4];
        answerTextView = view.findViewById(R.id.answerTextView);

        for (int i = 0; i < answersTableLayout.getChildCount(); i++) {
            try {
                if (answersTableLayout.getChildAt(i) instanceof TableRow) {
                    guessTableRows[i] = (TableRow) answersTableLayout.getChildAt(i);
                }
            } catch (ArrayStoreException e) {
                Log.e(QuizViewModel.getTag(),
                        "Error getting button rows on loop #" + String.valueOf(i), e);
            }
        }
        //listener settings for answer buttons
        for (TableRow row : guessTableRows) {
            for (int column = 0; column < row.getChildCount(); column++) {
                (row.getChildAt(column)).setOnClickListener(guessButtonListener);
            }
        }

        questionNumberTextView.setText(
                getString(R.string.question, 1, QuizViewModel.getQuestionsInQuiz()));
        return view;
    }

    public void updateGuessRows() {

        int numberOfGuessRows = quizViewModel.getGuessRows();
        for (TableRow row : guessTableRows) {
            row.setVisibility(View.GONE);
        }
        //display of necessary components in LinearLayout
        for (int rowNumber = 0; rowNumber < numberOfGuessRows; rowNumber++) {
            guessTableRows[rowNumber].setVisibility(View.VISIBLE);
        }
    }

    public void resetQuiz() {
        quizViewModel.clearFileNameList();
        quizViewModel.setFileNameList(getActivity().getAssets());
        quizViewModel.resetTotalGuesses();
        quizViewModel.resetCorrectAnswers();
        quizViewModel.clearQuizQuestionsList();

        int questionCounter = 1;
        int numberOfQuestion = quizViewModel.getFileNameList().size();
        while (questionCounter <= QuizViewModel.getQuestionsInQuiz()) {
            int randomIndex = random.nextInt(numberOfQuestion);

            String filename = quizViewModel.getFileNameList().get(randomIndex);

            if (!quizViewModel.getQuizAnswersList().contains(filename)) {
                quizViewModel.getQuizAnswersList().add(filename);
                ++questionCounter;
            }
        }

        updateGuessRows();
        if(quizViewModel.getTimeToAnswer() != 100)
        startTimer((long)quizViewModel.getTimeToAnswer() * 1000 + 800);
        else{
            countDownTimerTextView.setText(" ");
        }
        loadNextQuestion();
    }
    public void finalQuiz() {
        Intent mainActivityIntent = new Intent(getActivity(),MainActivity.class);
        getActivity().startActivity(mainActivityIntent);
    }

    private void loadNextQuestion() {
        stopTimer();
        if(quizViewModel.getTimeToAnswer() != 100)
        startTimer((long)quizViewModel.getTimeToAnswer() * 1000 + 800);
        else {
            countDownTimerTextView.setText(" ");
        }
      AssetManager assets = getActivity().getAssets();
         List<String> variants = new ArrayList<>();
        String nextQuestion = quizViewModel.getNextQuestion();
        String category = nextQuestion.substring(0, nextQuestion.indexOf('-'));
        String text = "";

        quizViewModel.setCorrectAnswer(nextQuestion);
        answerTextView.setText("");
        questionNumberTextView.setText(getString(R.string.question,
                (quizViewModel.getCorrectAnswers() + 1), QuizViewModel.getQuestionsInQuiz()));

        InputStream stream = null;
        try
        {
            stream = assets.open(category +  "/" + nextQuestion +   ".txt");
        int size = stream.available();
             byte [] buffer = new byte[size];
             stream.read(buffer);
             stream.close();
             text = new String(buffer);
            animate(false);
        } catch (IOException e) {
            Log.e(QuizViewModel.getTag(), "Error Loading " + nextQuestion, e);
        }
        questionTextView.setText(text);

        quizViewModel.shuffleFilenameList();
        String corrAnsw = quizViewModel.getCorrectAnswer();
        String str = corrAnsw.substring(corrAnsw.indexOf('-') + 1);
        String onlycorrAnsw = str.substring((str.indexOf('-') + 1),str.indexOf('_'));
        String variant = null;
        int i = onlycorrAnsw.length() + 1;
        int i2=i;
        while (i!=str.length()){
            if(str.charAt(i)=='_'){
                variant = str.substring(i2,i);
                variants.add(variant);
                i2=i+1;
            }
            i++;
        }
        Collections.shuffle(variants);

        for (int rowNumber = 0; rowNumber < quizViewModel.getGuessRows(); rowNumber++) {
         for (int column = 0; column < guessTableRows[rowNumber].getChildCount(); column++) {
          Button guessButton = (Button) guessTableRows[rowNumber].getVirtualChildAt(column);
          guessButton.setEnabled(true);
                String var = variants.get((rowNumber*2+column));
               guessButton.setText(var);
            }
        }


        int row = random.nextInt(quizViewModel.getGuessRows());
        int column = random.nextInt(2);
        TableRow randomRow = guessTableRows[row];
        ((Button) randomRow.getChildAt(column)).setText(quizViewModel.getOnlyCorrectAnswer());
    }



    public void animate(boolean animateOut) {
        if (quizViewModel.getCorrectAnswers() == 0) {
            return;
        }
        int centreX = (quizConstraintLayout.getLeft() + quizConstraintLayout.getRight()) / 2;
        int centreY = (quizConstraintLayout.getTop() + quizConstraintLayout.getBottom()) / 2;
        int radius = Math.max(quizConstraintLayout.getWidth(), quizConstraintLayout.getHeight());
        Animator animator;
        if (animateOut) {
            animator = ViewAnimationUtils.createCircularReveal(
                    quizConstraintLayout, centreX, centreY, radius, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    loadNextQuestion();
                }
            });
        } else {
            animator = ViewAnimationUtils.createCircularReveal(
                    quizConstraintLayout, centreX, centreY, 0, radius);
        }

        animator.setDuration(500);
        animator.start();
    }

    public void incorrectAnswerAnimation(){
        questionTextView.startAnimation(shakeAnimation);

        answerTextView.setText(R.string.incorrect_answer);
        answerTextView.setTextColor(getResources().getColor(R.color.wrong_answer));
    }

    public void disableButtons() {
        for (TableRow row : guessTableRows) {
            for (int column = 0; column < row.getChildCount(); column++) {
                (row.getChildAt(column)).setEnabled(false);
            }
        }
    }

    public TextView getAnswerTextView() {
        return answerTextView;
    }

    public QuizViewModel getQuizViewModel() {
        return quizViewModel;
    }
}

