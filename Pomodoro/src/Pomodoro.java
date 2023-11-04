import java.util.Timer;
import java.util.TimerTask;

public class Pomodoro {
    private Timer timer;
    private int workTime; // 작업 시간 (분)
    private int breakTime; // 휴식 시간 (분)
    private boolean isWorking; // 현재 작업 중인지 여부

    public Pomodoro(int workTime, int breakTime) {
        this.workTime = workTime;
        this.breakTime = breakTime;
        this.isWorking = true; // 시작할 때 작업 중으로 초기화
    }

    public void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int remainingTime = isWorking ? workTime * 60 : breakTime * 60; // 초로 변환
            int remMinut = 0;
            int Times = 59;

            @Override
            public void run() {
                if ((remainingTime > 0) && (remMinut >= 0)) {
                    remMinut = remainingTime / 60;
                    Times = remainingTime % 60;
                    if(isWorking) {
                        // 남은 시간을 초로 변환하여 표시
                        if(Times == 0) {
                            System.out.println("Working Time: " + remMinut + " : 00");
                            remainingTime--;
                        } else {
                            System.out.println("Working Time: " + remMinut + " : " + Times);
                            remainingTime--;
                        }
                    } else {
                        if(Times == 0) {
                            System.out.println("breaking Time: " + remMinut + " : 00");
                            remainingTime--;
                        } else {
                            System.out.println("breaking Time: " + remMinut + " : " + Times);
                            remainingTime--;
                        }
                    }
                } else {
                    // 작업 또는 휴식 시간 종료
                    if (isWorking) {
                        System.out.println("Work Time is over. Take a break!");
                    } else {
                        System.out.println("Break Time is over. Back to work!");
                    }

                    isWorking = !isWorking; // 상태 변경 (작업 <-> 휴식)
                    remainingTime = isWorking ? workTime * 60 : breakTime * 60; // 다음 시간 설정
                }
            }
        }, 0, 1000); // 1초마다 실행
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static void main(String[] args) {
        Pomodoro pomodoro = new Pomodoro(1, 1); // 25분 작업, 5분 휴식
        pomodoro.startTimer();
    }
}
