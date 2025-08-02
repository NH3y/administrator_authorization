package net.mcreator.administratorauthorization.classes;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;

public class TickRepeater {
    final int total;
    int current;
    final Runnable task;

    public TickRepeater(int all, Runnable task){
        this.total = all;
        this.current = 0;
        this.task = task;
    }

    public void tick(){
        if(this.current < this.total){
            AdministratorAuthorizationMod.queueServerWork(1, task);
            this.current++;
        }
    }
}
