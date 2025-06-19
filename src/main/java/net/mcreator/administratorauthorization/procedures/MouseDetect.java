package net.mcreator.administratorauthorization.procedures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

public class MouseDetect {
    public static double getMouseX(MouseHandler mouse){
        return mouse.xpos();
    }
    public static double getMouseY(MouseHandler mouse){
        return mouse.ypos();
    }
    public static int mouseDistrict(Minecraft minecraft){
        int height = minecraft.getWindow().getScreenHeight();
        int width = minecraft.getWindow().getScreenWidth();
        MouseHandler mouse = minecraft.mouseHandler;
        double fixedX = getMouseY(mouse) - ((double) height / 2);
        double fixedY = getMouseX(mouse) - ((double) width / 2);
        if (fixedX * fixedX + fixedY * fixedY <= 10) return -1;
        double degree = Math.atan2(fixedX, fixedY) * 180 / Math.PI;
        int district =  (int) Math.floor(degree / 45) + 2;
        return district>=0 ? district : district + 8;
    }
}
