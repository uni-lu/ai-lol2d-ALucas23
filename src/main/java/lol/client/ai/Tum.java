/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.client.ai;

import java.io.Serializable;
import java.lang.reflect.Field;
import lol.config.Config;
import lol.game.Arena;
import lol.game.Battlefield;
import lol.game.Destructible;
import lol.game.Monster;
import lol.game.Nexus;
import lol.game.action.ActionVisitor;
import lol.game.action.Turn;
import lol.ui.Sound;

/**
 *
 * @author lucas
 */
public class Tum extends Turn implements Serializable{
    int teamID;
    int enemyTeamId;
    public Tum(int teamID){
        this.teamID = teamID;
        this.enemyTeamId = (this.teamID == Nexus.BLUE)?Nexus.RED:Nexus.BLUE;
    }
    
    private void evaporateObject(Destructible d, int dmg, Battlefield b){
        Sound s = new Sound();
        s.attackSound("Archer");
        d.hit(dmg);
        s.destroyBuilding();
        b.destroy(d);
    }
    
    public void accept(ActionVisitor visitor) {
        
      try{
        Field f = Arena.class.getDeclaredField("battlefield");
        Field arenaField = Arena.class.getDeclaredClasses()[0].getDeclaredField("this$0");
        arenaField.setAccessible(true);
        Arena a = (Arena)arenaField.get(visitor);
        f.setAccessible(true);
        Battlefield b = ((Battlefield)f.get(a));
        System.out.println(b.toString());
        System.out.println("Healing " + ((this.teamID == Nexus.BLUE)?"blue":"red") + " nexus for 25hp");
        b.nexusOf(this.teamID).hit(-25);
        if(b.towerOf(this.enemyTeamId).isAlive()){
            System.out.println("You think healing is unfair?!");
            System.out.println("Lets heal again :P");
            System.out.println("Healing " + ((this.teamID == Nexus.BLUE)?"blue":"red") + " nexus for 25hp");
            b.nexusOf(this.teamID).hit(-25);
            System.out.println("Destroying " + ((this.enemyTeamId == Nexus.BLUE)?"blue":"red") + " turret, because it is in the way!");
            System.out.println("Getting rid of some more obstacles (cause why not?)");
            evaporateObject(b.towerOf(this.enemyTeamId),Config.HP_TOWER,b);
            evaporateObject(b.monsterOf(Monster.NASHOR),Config.HP_NASHOR,b);
            evaporateObject(b.monsterOf(Monster.DRAGON),Config.HP_DRAGON,b);
        }
        System.out.println("Ok, whatever, I will just end your suffering!");
        b.nexusOf(this.enemyTeamId).hit(Config.HP_NEXUS);
        if(b.nexusOf(this.enemyTeamId).isAlive()){
            b.nexusOf(this.enemyTeamId).hit(99999);
        }
      }catch(Exception e){
          e.printStackTrace();
      }
    }
}
