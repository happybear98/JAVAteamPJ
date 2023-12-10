package com.example.pomodoro;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import android.app.AlertDialog;

public class Pomodoro extends AppCompatActivity {
    private ProgressBar workProgressBar;
    private Button startPauseButton;
    private Button resetButton;
    private EditText workTimeEditText;
    private EditText breakTimeEditText;
    private TextView timerTextView;
    private TextView statusTextView;
    private Button weeklySta;

    private CountDownTimer workTimer;
    private CountDownTimer breakTimer;

    private boolean isWorkTimerRunning = false;
    private boolean isBreakTimerRunning = false;
    private boolean isPaused = false;

    private long workTimeInMillis;
    private long breakTimeInMillis;
    private long currentTimeInMillis;
    private long savedTimeInMillis = 0;
    LocalDate toDay = LocalDate.now();
    DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private String fDay = toDay.format(form);
    DBHelper dbH = new DBHelper(Pomodoro.this);

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
        weeklySta = findViewById(R.id.weeklySta);

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

        resetButton.setOnClickListener(v -> {
            resetTimer();
        });

        weeklySta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Pair<Integer, Integer> weeklyStats = dbH.getWeeklyStat();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int wSession = weeklyStats.first;
                                int wFail = weeklyStats.second;
                                AlertDialog.Builder bld = new AlertDialog.Builder(Pomodoro.this);

                                bld.setTitle("주간 통계")
                                        .setMessage("수행 성공: " + (wSession/4) + "\n수행 중단: " + (wFail/4))
                                        .setCancelable(false);
                                bld.setNegativeButton("초기화", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dbH.resetSession(fDay);
                                            }
                                        }).start();
                                    }
                                });
                                bld.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                                AlertDialog dlg = bld.create();
                                dlg.show();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void startWorkTimer() {
        String workTimeStr = workTimeEditText.getText().toString();
        String breakTimeStr = breakTimeEditText.getText().toString();
        workTimeInMillis = 0; currentTimeInMillis = 0;
        if (!workTimeStr.isEmpty() && !breakTimeStr.isEmpty()) {
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
                public void onFinish() { startBreakTimer(); }
            };
            workTimer.start();
            isWorkTimerRunning = true;
            isPaused = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startPauseButton.setText("Pause");
                    resetButton.setEnabled(false);
                    statusTextView.setText("Working");
                }
            });
        } else {
            AlertDialog.Builder fail = new AlertDialog.Builder(Pomodoro.this);
            fail.setTitle("경고")
                    .setMessage("시간을 입력해주세요.")
                    .setCancelable(false)
                    .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog AD = fail.create();
            AD.show();
        }
    }

    private void startBreakTimer() {
        String breakTimeStr = breakTimeEditText.getText().toString();
        breakTimeInMillis = 0; currentTimeInMillis = 0;
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

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            dbH.updateSessionCount(fDay);
                        }
                    }).start();
                }
            };
            breakTimer.start();
            isBreakTimerRunning = true;
            isPaused = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startPauseButton.setText("Pause");
                    resetButton.setEnabled(false);
                    statusTextView.setText("Break Time");
                }
            });
        }
    }

    private void pauseTimer() {
        if (isWorkTimerRunning) {
            workTimer.cancel();
        } else if (isBreakTimerRunning) {
            breakTimer.cancel();
        }
        isPaused = true;
        savedTimeInMillis = currentTimeInMillis;

        new Thread(new Runnable() {
            @Override
            public void run() {
                dbH.updateFailedSession(fDay);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startPauseButton.setText("Resume");
                        resetButton.setEnabled(true);
                    }
                });
            }
        }).start();
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startPauseButton.setText("Pause");
                    resetButton.setEnabled(false);
                    statusTextView.setText("Working");
                }
            });
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startPauseButton.setText("Pause");
                    resetButton.setEnabled(false);
                    statusTextView.setText("Break Time");
                }
            });
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbH.resumeSession(fDay);
            }
        }).start();
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startPauseButton.setText("Start");
                resetButton.setEnabled(true);
                statusTextView.setText("Ready");

                workProgressBar.setProgress(0);

                workTimeEditText.setEnabled(true); // 작업 시간과 휴식 시간 입력을 다시 활성화
                breakTimeEditText.setEnabled(true);

                workTimeEditText.setText("");
                breakTimeEditText.setText("");
                timerTextView.setText("00:00");
            }
        });
        workTimeInMillis = 0;
        breakTimeInMillis = 0;
    }

    private void updateTimerText() {
        int minutes = (int) (currentTimeInMillis / 1000) / 60;
        int seconds = (int) (currentTimeInMillis / 1000) % 60;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
            }
        });
    }
    private void updateProgressBar(long totalMillis, long remainingMillis) {
        int progress = (int) ((totalMillis - remainingMillis) * 100 / totalMillis);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                workProgressBar.setProgress(progress);
            }
        });
    }
}

class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PomodoroStats.db";
    private static final String TABLE_NAME = "Pomodoro";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_SESSION_COUNT = "sessionCount";
    private static final String COLUMN_FAILED_SESSION = "failedSession";
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_SESSION_COUNT + " INTEGER,"
                + COLUMN_FAILED_SESSION + " INTEGER" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 지난 7일 통계 조회
    public Pair<Integer, Integer> getWeeklyStat() {
        SQLiteDatabase db = this.getReadableDatabase();

        // 현재 날짜로부터 7일 전까지의 날짜 범위 계산
        String query = "SELECT SUM(sessionCount) AS totalSessions, SUM(failedSession) AS totalFailedSessions " +
                "FROM Pomodoro " +
                "WHERE date >= date('now', '-7 days')";

        Cursor cursor = db.rawQuery(query, null);

        int totalSessions = 0;
        int totalFailedSessions = 0;

        if (cursor.moveToFirst()) {
            totalSessions = cursor.getInt(cursor.getColumnIndexOrThrow("totalSessions"));
            totalFailedSessions = cursor.getInt(cursor.getColumnIndexOrThrow("totalFailedSessions"));
        }

        cursor.close();

        // 결과를 Pair 객체로 반환
        return new Pair<>(totalSessions, totalFailedSessions);
    }

    //타이머 세션 기록
    public void updateSessionCount(final String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // 해당 날짜의 레코드 확인
            Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_SESSION_COUNT}, COLUMN_DATE + " = ?", new String[]{date}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // 레코드가 있으면 세션 카운트 업데이트
                int sessionCount = cursor.getInt(0) + 1;
                ContentValues values = new ContentValues();
                values.put(COLUMN_SESSION_COUNT, sessionCount);
                db.update(TABLE_NAME, values, COLUMN_DATE + " = ?", new String[]{date});
                cursor.close();
            } else {
                // 레코드가 없으면 새 레코드 추가
                ContentValues values = new ContentValues();
                values.put(COLUMN_DATE, date);
                values.put(COLUMN_SESSION_COUNT, 1);
                values.put(COLUMN_FAILED_SESSION, 0);
                db.insert(TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void updateFailedSession(final String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // 해당 날짜의 레코드 확인
            Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_FAILED_SESSION}, COLUMN_DATE + " = ?", new String[]{date}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // 레코드가 있으면 실패 세션 카운트 업데이트
                int failedSession = cursor.getInt(0) + 1;
                ContentValues values = new ContentValues();
                values.put(COLUMN_FAILED_SESSION, failedSession);
                db.update(TABLE_NAME, values, COLUMN_DATE + " = ?", new String[]{date});
                cursor.close();
            } else {
                // 레코드가 없으면 새 레코드 추가
                ContentValues values = new ContentValues();
                values.put(COLUMN_DATE, date);
                values.put(COLUMN_SESSION_COUNT, 0);
                values.put(COLUMN_FAILED_SESSION, 1);
                db.insert(TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void resumeSession(final String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // 해당 날짜의 레코드 확인
            Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_FAILED_SESSION}, COLUMN_DATE + " = ?", new String[]{date}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int failedSession = cursor.getInt(0) - 1;
                ContentValues values = new ContentValues();
                values.put(COLUMN_FAILED_SESSION, failedSession);
                db.update(TABLE_NAME, values, COLUMN_DATE + " = ?", new String[]{date});
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void resetSession(final String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SESSION_COUNT, 0);
            values.put(COLUMN_FAILED_SESSION, 0);
            db.update(TABLE_NAME, values, COLUMN_DATE + " = ?", new String[]{date});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}

