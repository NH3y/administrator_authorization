package net.mcreator.administratorauthorization.classes;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.LocalPlayerAccess;
import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.mcreator.administratorauthorization.network.RouterIndexPacket;
import net.mcreator.administratorauthorization.procedures.MouseDetect;
import net.mcreator.administratorauthorization.procedures.RouterDataOperant;
import net.minecraft.world.entity.player.Player;

public class PlayerRouter {
    private int routerMain = 0;
    private int routerAlter = 0;
    private final Player player;
    public PlayerRouter(Player player){
        this.player = player;
    }

    public int getRouterMain() {
        return routerMain;
    }

    public int getRouterAlter() {
        return routerAlter;
    }

    public void updateCapability(){
        int route = ((PlayerAccess) this.player).administrator_authorization$isPressAlter() ? this.routerAlter : this.routerMain;
        RouterDataOperant.updatePlayerRouterIndex(this.player, route);
        AdministratorAuthorizationMod.PACKET_HANDLER.sendToServer(new RouterIndexPacket(route));
    }

    public void inputRouter(){
        if(this.player instanceof LocalPlayerAccess localPlayerAccess){
            int input = 1 + MouseDetect.mouseDistrict(localPlayerAccess.administrator_authorization$getMinecraft());
            if(input == 33) return;
            if(((PlayerAccess) this.player).administrator_authorization$isPressAlter()){
                this.routerAlter = input;
            }else{
                this.routerMain = input;
            }
        }
    }
}
