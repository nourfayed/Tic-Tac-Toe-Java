package controllers;

import models.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author nourfayed
 */
public class GameMatch {

 Socket s1,s2;
 int player1Id,player2Id;
 int playerNumber = 0, cntr = 0;
 
 PrintStream[] ps= new PrintStream[2];
 String[][] grid=new String[3][3];
 String[] XO=new String[]{"X","O"};
 String[] users=new String[2];

 

     
  public GameMatch(String u1,String u2,Socket s1,Socket s2){
      try {
          users[0]=u1;
          users[1]=u2;
          
          this.s1=s1;
          this.s2=s2;
          
          player1Id=UserModel.playerId(u1);
          player2Id=UserModel.playerId(u2);
          
         //first check if there is a saved game???
          
          Match savedMatch=RecordMatch.getRecordedMatch(player1Id,player2Id);
          
          if(savedMatch== null){
              System.out.println("A new Match");
              for(int i=0;i<3;i++){
                  for(int j=0;j<3;j++){
                      grid[i][j]="-";
                  }
              }
          }
          else {
              System.out.println("A saved Match");
              for(int i=0;i<3;i++){
                  for(int j=0;j<3;j++){
                      grid[i][j]=savedMatch.grid[i][j];
                  }
              }
              RecordMatch.removeMatch(savedMatch.matchId);
          }
          
          System.out.println("this is s1 "+this.s1);
          System.out.println("this is s2 "+this.s2);
          
          //initialize ps and assign X or O to each player
         
          ps[0]=new PrintStream(this.s1.getOutputStream());
          ps[0].println(XO[0]);
          
       
          ps[1]=new PrintStream(this.s2.getOutputStream());
          ps[1].println(XO[1]);
          //start();
          
      } catch (IOException ex) {
          Logger.getLogger(GameMatch.class.getName()).log(Level.SEVERE, null, ex);
      }
      
  }    public void playerTurn(int playerNumber,String msg){
 
        
         
         
         if(msg.equals("pause")){
             System.out.println(msg);
             pauseGame(playerNumber);
         }
         else{
                             int x = Integer.parseInt(msg);
             int i = x % 3;
             int j = x / 3;
             grid[i][j] = XO[playerNumber];
            // ps[0].println(msg);
             System.out.println(users[(playerNumber+1)%2]+" and the move is "+msg);
             System.out.println("");
             ps[(playerNumber+1)%2].println(msg);
             
         }
         
         
     
     
     
 }
 public void sendNewMove(String move) {

     if (cntr < 9) {
         System.out.println("move number " + cntr);
         playerTurn(playerNumber, move);
         playerNumber++;
         playerNumber %= 2;
         cntr++;
     }

 }


 
 public void pauseGame(int playerNumber){
      
     //insert match to database
     boolean b=RecordMatch.addMatch(player1Id,player2Id,grid,playerNumber);
     
     //close everything 
    try {
         for(int i=0;i<2;i++){
                
                 ps[i].close();          
            }
         s1.close();
         s2.close();
         //send to both players 2enna shatabna?
         
     } catch (IOException ex) {
             Logger.getLogger(GameMatch.class.getName()).log(Level.SEVERE, null, ex);
         }
    
    
 
 }
 
}
