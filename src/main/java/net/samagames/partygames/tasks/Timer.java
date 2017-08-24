package net.samagames.partygames.tasks;

import org.bukkit.scheduler.BukkitRunnable;

/*
 * This file is part of PartyGames.
 *
 * PartyGames is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PartyGames is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PartyGames.  If not, see <http://www.gnu.org/licenses/>.
 */
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
