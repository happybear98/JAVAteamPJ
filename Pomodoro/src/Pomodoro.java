package com.example.pomodoro;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Pomodoro extends AppCompatActivity {
    private ProgressBar workProgressBar;
    private Button startPauseButton;
    private Button resetButton;
    private EditText workTimeEditText;
    private EditText breakTimeEditText;
    private TextView timerTextView;
    private TextView statusTextView;

    private CountDownTimer workTimer;
    private CountDownTimer breakTimer;

    private boolean isWorkTimerRunning = false;
    private boolean isBreakTimerRunning = false;
    private boolean isPaused = false;

    private long workTimeInMillis;
    private long breakTimeInMillis;
    private long currentTimeInMillis;
    private long savedTimeInMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro_timer);

        workProgressBar = findViewById(R.id.workProgressBar);
        startPauseButton = findViewById(R.id.startPauseButton);
        resetButton = findViewById(R.id.resetButton);
        workTimeEditText = findViewById(R.id.workTimeEditText);
        breakTimeEditText = findViewById(R.id.breakTimeEditText);
        timerTextView = findViewById(R.id.timerTextView);
        statusTextView = findViewById(R.id.statusTextView);

        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWorkTimerRunning && !isBreakTimerRunning) {
                    startWorkTimer();
                } else {
                    if (isPaused) {
                        resumeTimer();
                    } else {
                        pauseTimer();
                    }
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    private void startWorkTimer() {
        String workTimeStr = workTimeEditText.getText().toString();
        if (!workTimeStr.isEmpty()) {
            workTimeInMillis = Long.parseLong(workTimeStr) * 60 * 1000;
            currentTimeInMillis = workTimeInMillis;

            workTimer = new CountDownTimer(currentTimeInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    currentTimeInMillis = millisUntilFinished;
                    updateTimerText();
                    updateProgressBar(workTimeInMillis, currentTimeInMillis);
                }

                @Override
                public void onFinish() {
                    startBreakTimer();
                }
            };

            workTimer.start();
            isWorkTimerRunning = true;
            isPaused = false;
            startPauseButton.setText("Pause");
            resetButton.setEnabled(false);
            statusTextView.setText("Working");
        }
    }

    private void startBreakTimer() {
        String breakTimeStr = breakTimeEditText.getText().toString();
        if (!breakTimeStr.isEmpty()) {
            breakTimeInMillis = Long.parseLong(breakTimeStr) * 60 * 1000;
            currentTimeInMillis = breakTimeInMillis;

            breakTimer = new CountDownTimer(currentTimeInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    currentTimeInMillis = millisUntilFinished;
                    updateTimerText();
                    updateProgressBar(breakTimeInMillis, currentTimeInMillis);
                }

                @Override
                public void onFinish() {
                    resetTimer();
                }
            };

            breakTimer.start();
            isBreakTimerRunning = true;
            isPaused = false;
            startPauseButton.setText("Pause");
            resetButton.setEnabled(false);
            statusTextView.setText("Break Time");
        }
    }

    private void pauseTimer() {
        if (isWorkTimerRunning) {
            workTimer.cancel();
        } else if (isBreakTimerRunning) {
            breakTimer.cancel();
        }
        isPaused = true;
        startPauseButton.setText("Resume");
        resetButton.setEnabled(true);
        savedTimeInMillis = currentTimeInMillis;
    }

    private void resumeTimer() {
        if (isWorkTimerRunning) {
            workTimer = new CountDownTimer(savedTimeInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    currentTimeInMillis = millisUntilFinished;
                    updateTimerText();
                    updateProgressBar(workTimeInMillis, currentTimeInMillis);
                }
                @Override
                public void onFinish() {
                    startBreakTimer();
                }
            };
            workTimer.start();
            isWorkTimerRunning = true;
            isPaused = false;
            startPauseButton.setText("Pause");
            resetButton.setEnabled(false);
            statusTextView.setText("Working");
        } else if (isBreakTimerRunning) {
            breakTimer = new CountDownTimer(savedTimeInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    currentTimeInMillis = millisUntilFinished;
                    updateTimerText();
                    updateProgressBar(breakTimeInMillis, currentTimeInMillis);
                }
                @Override
                public void onFinish() {
                    resetTimer();
                }
            };
            breakTimer.start();
            isBreakTimerRunning = true;
            isPaused = false;
            startPauseButton.setText("Pause");
            resetButton.setEnabled(false);
            statusTextView.setText("Break Time");
        }
    }


    private void resetTimer() {
        if (isWorkTimerRunning) {
            workTimer.cancel();
        } else if (isBreakTimerRunning) {
            breakTimer.cancel();
        }

        isWorkTimerRunning = false;
        isBreakTimerRunning = false;
        isPaused = false;
        startPauseButton.setText("Start");
        resetButton.setEnabled(true);
        statusTextView.setText("Ready");

        workProgressBar.setMax(1);
        workProgressBar.setProgress(0);

        workTimeEditText.setEnabled(true); // 작업 시간과 휴식 시간 입력을 다시 활성화
        breakTimeEditText.setEnabled(true);

        workTimeEditText.setText("");
        breakTimeEditText.setText("");
        timerTextView.setText("00:00");
        workTimeInMillis = 0;
        breakTimeInMillis = 0;
    }

    private void updateTimerText() {
        int minutes = (int) (currentTimeInMillis / 1000) / 60;
        int seconds = (int) (currentTimeInMillis / 1000) % 60;
        timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }
    private void updateProgressBar(long totalMillis, long remainingMillis) {
        int progress = (int) ((totalMillis - remainingMillis) * 100 / totalMillis);
        workProgressBar.setProgress(progress);
    }
}
