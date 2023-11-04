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
    private Button startButton;
    private Button pauseButton;
    private EditText workTimeEditText; // 작업 시간 입력 필드
    private EditText breakTimeEditText; // 휴식 시간 입력 필드
    private TextView timerTextView;
    private TextView statusTextView;

    private CountDownTimer timer;
    private int workTimeInSeconds = 0; // 작업 시간 (초)
    private int breakTimeInSeconds = 0; // 휴식 시간 (초)
    private boolean isWorking = true; // 현재 작업 중인지 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // XML에서 UI 요소를 찾습니다.
        workProgressBar = findViewById(R.id.workProgressBar);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        workTimeEditText = findViewById(R.id.workTimeEditText);
        breakTimeEditText = findViewById(R.id.breakTimeEditText);
        timerTextView = findViewById(R.id.timerTextView);
        statusTextView = findViewById(R.id.statusTextView);

        // 시작 버튼 클릭 이벤트 처리
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        // 일시 정지 버튼 클릭 이벤트 처리
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
            }
        });
    }

    private void startTimer(int durationInSeconds) {
        timer = new CountDownTimer(durationInSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 남은 시간을 초 단위로 계산
                long secondsRemaining = millisUntilFinished / 1000;

                // ProgressBar 업데이트
                workProgressBar.setProgress((int) ((durationInSeconds - secondsRemaining) * 100 / durationInSeconds));

                // 시간을 분과 초로 변환하여 TextView 업데이트
                long minutes = secondsRemaining / 60;
                long seconds = secondsRemaining % 60;
                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
                statusTextView.setText(isWorking ? "Work Time" : "Break Time");
            }

            @Override
            public void onFinish() {
                // 타이머 종료 후 상태 변경 (작업 <-> 휴식)
                isWorking = !isWorking;

                // 다음 시간 설정
                int nextDurationInSeconds = isWorking ? workTimeInSeconds : breakTimeInSeconds;

                // ProgressBar 초기화
                workProgressBar.setProgress(0);

                // 다음 타이머 시작
                startTimer(nextDurationInSeconds);
            }
        };

        // 타이머 시작
        timer.start();
    }

    private void startTimer() {
        // 사용자가 입력한 작업 시간과 휴식 시간을 초 단위로 변환합니다.
        workTimeInSeconds = Integer.parseInt(workTimeEditText.getText().toString()) * 60;
        breakTimeInSeconds = Integer.parseInt(breakTimeEditText.getText().toString()) * 60;

        // 작업 시간 또는 휴식 시간에 따라 타이머 설정
        int durationInSeconds = isWorking ? workTimeInSeconds : breakTimeInSeconds;

        // CountDownTimer를 사용하여 타이머 시작
        startTimer(durationInSeconds);

        // 시작 버튼 비활성화, 일시 정지 버튼 활성화
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
    }


    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
        }
    }
}
