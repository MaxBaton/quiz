package com.nadershamma.apps.eventhandlers;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.nadershamma.apps.lifecyclehelpers.QuizViewModel;
import com.nadershamma.apps.quiz.GameActivityFragment;
import com.nadershamma.apps.quiz.R;
import com.nadershamma.apps.quiz.ResultsDialogFragment;

public class GuessButtonListener implements OnClickListener {
    private GameActivityFragment gameActivityFragment;
    private Handler handler;
    private static int numOfGuesses= 0;

    public static int getNumOfGuesses() {return numOfGuesses;}

    public GuessButtonListener(GameActivityFragment gameActivityFragment) {
        this.gameActivityFragment = gameActivityFragment;
        this.handler = new Handler();
    }

    @Override
    public void onClick(View v) {
        Button guessButton = ((Button) v);
        String guess = guessButton.getText().toString();
        String answer = gameActivityFragment.getQuizViewModel().getOnlyCorrectAnswer();
        gameActivityFragment.getQuizViewModel().setTotalGuesses(1);
        numOfGuesses++;

        if (guess.equals(answer)) {
            numOfGuesses = 0;
            gameActivityFragment.getQuizViewModel().setCorrectAnswers(1);
            gameActivityFragment.getAnswerTextView().setText(answer + "!");
            gameActivityFragment.getAnswerTextView().setTextColor(
                    gameActivityFragment.getResources().getColor(R.color.correct_answer));

            gameActivityFragment.disableButtons();

            if (gameActivityFragment.getQuizViewModel().getCorrectAnswers()
                    == QuizViewModel.getQuestionsInQuiz()) {
                ResultsDialogFragment quizResults = new ResultsDialogFragment();
                quizResults.setCancelable(false);
                gameActivityFragment.stopTimer();
                try {
                    quizResults.show(gameActivityFragment.getChildFragmentManager(), "Quiz Results");
                } catch (NullPointerException e) {
                    Log.e(QuizViewModel.getTag(),
                            "GuessButtonListener: mainActivityFragment.getFragmentManager() " +
                                    "returned null",
                            e);
                }
            } else {
                handler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                gameActivityFragment.animate(true);
                            }
                        }, 100);
            }
        } else {
            gameActivityFragment.incorrectAnswerAnimation();
            guessButton.setEnabled(false);
        }
    }
}
