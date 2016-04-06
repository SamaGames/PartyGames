package net.samagames.partygames.tasks;

import org.bukkit.scheduler.BukkitRunnable;

public class Timer extends BukkitRunnable {

    protected int time;

    public Timer(int time) {
        this.time = time;
    }

    @Override
    public void run() {
        if(time == 0) {
            cancel();
        }

        time--;
    }

    public int getTime() {
        return time;
    }
}
