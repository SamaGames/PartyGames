package net.samagames.partygames.tasks;

import org.bukkit.scheduler.BukkitRunnable;

public class Timer extends BukkitRunnable {

    private int initialTime;
    protected int time;

    public Timer(int time) {
        this.time = initialTime = time;
    }

    @Override
    public void run() {
        if(time == 0) {
            cancel();
        }

        time--;
    }

    public int getInitialTime() {
        return initialTime;
    }

    public int getTime() {
        return time;
    }
}
