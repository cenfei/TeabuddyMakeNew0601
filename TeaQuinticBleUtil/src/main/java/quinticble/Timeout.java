package quinticble;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 超时
 */
class Timeout {

    /**
     * 超时毫秒
     */
    private int interval;

    /**
     * 超时回调
     */
    private TimerEvent timerEvent;

    /**
     * 定时器
     */
    private Timer timer;

    /**
     * 超时任务
     */
    private TimeoutTask timeoutTask;

    /**
     * 是否已经启动
     */
    private boolean started;

    /**
     * 初始化超时
     * @param interval 超时毫秒
     * @param timerEvent 超时回调
     */
    public Timeout(int interval, TimerEvent timerEvent) {
        this(interval, timerEvent, false);
    }

    /**
     * 初始化超时
     * @param interval 超时毫秒
     * @param timerEvent 超时回调
     * @param autoStart 是否立即开始
     */
    public Timeout(int interval, TimerEvent timerEvent, boolean autoStart) {
        this.interval = interval;
        this.timerEvent = timerEvent;
        this.timer = new Timer();
        if (autoStart) {
            start();
        }
    }

    /**
     * 设置超时毫秒数
     * @param interval 毫秒数
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * 启动
     */
    public synchronized void start() {
        if (!started) {
            started =  true;
            timer.schedule(new TimeoutTask(), interval);
        }
    }

    /**
     * 取消
     */
    public synchronized void cancel() {
        if(timer!=null) {
            timer.cancel();
            timer.purge();
            timer = null;
            timer = new Timer();
            started = false;
        }
    }

    /**
     * 强制发生超时
     */
    public synchronized void forceTimeout() {
        cancel();
        timerEvent.onTimeout();
    }

    /**
     * 重置
     */
    public synchronized void restart() {
        cancel();
        start();
    }

    /**
     * 重置
     * @param interval 超时毫秒数
     */
    public synchronized void restart(int interval) {
        setInterval(interval);
        restart();
    }

    public synchronized boolean isStarted() {
        return started;
    }

    private class TimeoutTask extends TimerTask {

        @Override
        public void run() {
            timerEvent.onTimeout();
            started = false;
        }
    }

    /**
     * 超时回调
     */
    interface TimerEvent {
        /**
         * 发生超时
         */
        void onTimeout();
    }

}

