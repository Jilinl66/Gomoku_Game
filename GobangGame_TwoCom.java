import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.naming.TimeLimitExceededException;
import javax.swing.*;
//import minimax.java;

public class GobangGame_TwoCom {
    public static void main(String[] args) {
    	System.gc();
        GameF game = new GameF();
        game.InitializeGame();
        int x = 0;
        int y = 0;
        while(true){
        	if(!game.over){
	            game.playerTurn();
	            game.JudgeWin();
	            if (!game.over){
	            game.ComputerTurn();
	        	game.JudgeWin();
	            }
	            // max 20 wins for an AI
	        	if (game.pwincount >= 20){
	        		System.out.println("AI is better!");
	        		break;
	        	}	
	        	if (game.cwincount >= 20){
	    			System.out.println("Computer AI is better!");
	    			break;
	        	}
         	}
        	else
        		game.InitializeGame();
        	
        	if(game.over){
        	System.out.print("***********************\r\n");
        	for(x=0; x < 16; x++){
				for(y = 0; y<16; y++)
				{
					if (game.board[x][y] == 0) //Player chess
						System.out.print("o");
					if (game.board[x][y] == 1) //Smart AI chess
						System.out.print("x");
					if (game.board[x][y] == 2) //No chess
						System.out.print("-");
				}
						System.out.print("\r\n");
        	}
        	System.out.print("***********************\r\n");
        	}
        	//System.gc();
        } 
    }
}

class Time{
	public Time(){
		start1 = System.currentTimeMillis();
	}
	double start1 = 0;
	double start2 = 0;
	double end = 0;
	
	public double elapseTime(){
		start2 = System.currentTimeMillis();
		return (start2 - start1)/1000;
	}
}

class IterativeRun implements Runnable {

	int[][] board = new int[16][16];
	int[][] originalBoard = new int[16][16];
	minimax mini = new minimax();
	int depth = 1, max = Integer.MAX_VALUE, min = Integer.MIN_VALUE;
	boolean turn = true;
	int ans_m=0, ans_n=0;
	
	public IterativeRun(int [][]board) {
		this.originalBoard = board;
		this.board = board;
//		for(int i=0;i<16;i++){
//			for(int j=0;j<16;j++){
//				if(board[i][j]==0) System.out.print("x");
//				if(board[i][j]==1) System.out.print("o");
//				if(board[i][j]==2) System.out.print("-");
//			}
//			System.out.println("");
//		}
//		System.out.println("***** ***** ***** ***** ***** ***** *****");
		depth =1;
	}
	
	public void run() {
    	mini = new minimax();
    	mini.initialize();
    	mini.super_depth = depth;
    	board = originalBoard;
    	while(true){
//    		System.out.println("Doing depth: " + depth + ", m: " + ans_m +", n: "+ans_n);
	    	mini.mmab(board,  depth, min, max,  turn); 	
	    	ans_m = mini.next_m;
	    	ans_n = mini.next_n;
//	    	System.gc();
	    	depth++;
//	    	this.board = originalBoard;
	    	mini = new minimax();
	    	mini.initialize();
	    	mini.super_depth = depth;
    	}
	}
	
}

class GameF {
	public GameF(){
	};
	public int pwincount, cwincount;
	public boolean player, computer,over, pwin, cwin, tie, start;
	private int i, j, k, mc, nc, mp, np, icount;
	public int[][] board = new int [16][16];
	private boolean[][][] ptable = new boolean[16][16][672];
	private boolean[][][] ctable = new boolean[16][16][672];
	private int[][] cgrades = new int[16][16]; // Marks for every spot
	private int[][] pgrades = new int[16][16]; // C for computer, P for player
	private int cgrade,pgrade; // Max grade for the computer/player
	private int[][] win = new int[2][672];
	private int bout = 1;
	private int pcount, ccount;
	private int mat, nat, mde, nde;
	
	private double tLimit = 5;

//      Initializing Game
        public void InitializeGame()
		{ 
			//Initializing Panel
			for(i=0;i<16;i++)
				for(j=0;j<16;j++)
				{
					this.pgrades[i][j] = 0;
					this.cgrades[i][j] = 0;
					this.board[i][j] = 2;
				}
			//Enumrate all 5-in-line situations
			//Horizontal
			for(i=0;i<16;i++)
				for(j=0;j<12;j++){
					for(k=0;k<5;k++){
						this.ptable[j+k][i][icount] = true;
						this.ctable[j+k][i][icount] = true;
					}
					icount++;
				}
			//Vertical
			for(i=0;i<16;i++)
				for(j=0;j<12;j++){
					for(k=0;k<5;k++){
						this.ptable[i][j+k][icount] = true;
						this.ctable[i][j+k][icount] = true;
					}
					icount++;
				}
			//Des-to-right
			for(i=0;i<12;i++)
				for(j=0;j<12;j++){
					for(k=0;k<5;k++){
						this.ptable[j+k][i+k][icount] = true;
						this.ctable[j+k][i+k][icount] = true;
					}
					icount++;
				}
			//Des-to-left
			for(i=0;i<12;i++)
				for(j=15;j>=4;j--){
					for(k=0;k<5;k++){
						this.ptable[j-k][i+k][icount] = true;
						this.ctable[j-k][i+k][icount] = true;
					}
					icount++;
				}
			for(i=0;i<2;i++)  //enumerate the scores
				for(j=0;j<672;j++)
					this.win[i][j] = 0;
            this.player = true;
			this.icount = 0;
			this.ccount = 0;
			this.pcount = 0;
			this.start = true;
			this.over = false;
			this.pwin = false;
			this.cwin = false;
			this.tie = false;
			this.bout=1;
		}
        
    public void ComputerTurn(){     //found the best step for MAX
		   for(i=0;i < 16;i++)     //enumerate all positions
				for(j=0;j < 16;j++){   
					this.pgrades[i][j]=0;  //
					if(this.board[i][j] == 2)  //enumerate
						for(k=0;k<672;k++)    //
							if(this.ptable[i][j][k]){
								switch(this.win[0][k]){   
								    case 1: //score for one
										this.pgrades[i][j]+=5;
										break;
									case 2: //score for 2-in-line
										this.pgrades[i][j]+=50;
										break;
									case 3: //score for 3-in-line
										this.pgrades[i][j]+=180;
										break;
									case 4: //score for 4-in-line
										this.pgrades[i][j]+=400;
										break;
								}
							}
					this.cgrades[i][j]=0;//
					if(this.board[i][j] == 2)  //enumarate all possible min locis
						for(k=0;k<672;k++)     //
							if(this.ctable[i][j][k]){
								switch(this.win[1][k]){  
									case 1:  //1
										this.cgrades[i][j]+=5;
										break;
									case 2:  //2-in-line
										this.cgrades[i][j]+=52;
										break;
									case 3: //3-in-line
										this.cgrades[i][j]+=100;
										break;
									case 4:  //4-in-line
										this.cgrades[i][j]+=400;
										break;
								}
							}
				}
			if(this.start){      //the first white
				if(this.board[4][4]==2){
					mc = 4;
					nc = 4;
				}else{
					mc = 5;
					nc = 5;
				} 
				this.start = false;
			}else{
				for(i=0;i<16;i++)
					for(j=0;j<16;j++)
						if(this.board[i][j] == 2){  //find the max for MAX and min
							if(this.cgrades[i][j]>=this.cgrade){
								this.cgrade = this.cgrades[i][j];   
								this.mat = i;
								this.nat = j;
							}
							if(this.pgrades[i][j]>=this.pgrade){
								this.pgrade = this.pgrades[i][j];   
								this.mde = i;
								this.nde = j;
							}
						}
				if(this.cgrade>=this.pgrade){   //decision making
					mc = mat;
					nc = nat;
				}else{
					mc = mde;
					nc = nde;
				}
			}
		//	System.out.print("The current position of computer is: ["+ String.valueOf(mc) + "," + String.valueOf(nc) + "]" + "\r\n");
			this.cgrade = 0;		
			this.pgrade = 0;
			this.board[mc][nc] = 1;  //caculate the decision
			ccount++;
			if((ccount == 100) && (pcount == 100))  //Tie
			{
				this.tie = true;
				this.over = true;
			}
			for(i=0;i<672;i++){
				if(this.ctable[mc][nc][i] && this.win[1][i] != 7)
					this.win[1][i]++;     //updating
				if(this.ptable[mc][nc][i]){
					this.ptable[mc][nc][i] = false;
					this.win[0][i]=7;
				}
			}
			this.player = true;     //turn for player
			this.computer = false;  //finish AI run
		} 
    
    public void playerTurn(){
    	
    	//System.out.println("entering minimax..");

    	if(this.start){	
			mp = 3+(int)(Math.random()*8);
			np = 3+(int)(Math.random()*8);
			this.start = false;
    	}else {
	    	IterativeRun minimax = new IterativeRun(board);
	    	Thread iter = new Thread(minimax);
	    	iter.start();
	    	Time t = new Time();
	    	int counter = 1;
	    	while (true) {
	//    		System.out.println("time: " + t.elapseTime());
	    		if(t.elapseTime() > counter){
	    			//System.out.println(counter + " second passed");
	    			counter++;
	    		}
	    		if(t.elapseTime() > tLimit-0.5){
	    			iter.interrupt();
	    			break;
	    		}
	            // Wait maximum of 1 second
	            // for MessageLoop thread
	            // to finish.
	        }
	    	try {
				iter = null;
			} catch (Exception e) {
				System.out.println("cannot stop the thread");
			}
	
			mp = minimax.ans_m;
			np = minimax.ans_n;
			minimax = null;
    	}
		/*
		
    	int max = Integer.MAX_VALUE;
    	int min = Integer.MIN_VALUE;
    	boolean turn = true;
    	minimax ai_mmab = new minimax();
    	int depth = 4;
    	ai_mmab.initialize();
    	ai_mmab.super_depth = depth;
    	ai_mmab.mmab(board,  depth, min, max,  turn);
    	System.out.println("time use: " + t.elapseTime());
    	
    	if(this.start){      //the first white
			
			mp = 3+(int)(Math.random()*8);
			np =  3+(int)(Math.random()*8);
			
			this.start = false;
		}
    	else{
	    	mp = ai_mmab.next_m;
	    	np = ai_mmab.next_n;
    	}
    	ai_mmab= null;
    	System.gc();
		
		*/
		
		System.gc();
		//System.out.println();
    	System.out.println("The current position of player is: ["+ String.valueOf(mp) + "," + String.valueOf(np) + "] , ");
    	 if(this.board[mp][np] == 2){   
		    	this.bout++;
				this.board[mp][np] = 0;	
				pcount++;
				if((ccount == 20) && (pcount == 20)){
					this.tie = true;
					this.over = true;
				}
				for(i=0;i < 672;i++){
				if(this.ptable[mp][np][i] && this.win[0][i] != 7)
					this.win[0][i]++;     //Updating MAX
				if(this.ctable[mp][np][i]){
					this.ctable[mp][np][i] = false;
					this.win[1][i]=7;
				}
			}
			this.player = false;      
			this.computer = true;
	    }
    }
    	
	 public void JudgeWin(){
		//Judging
			for(i=0;i<=1;i++)
				for(j=0;j<672;j++){   
					if(this.win[i][j] == 5)
						if(i==0){                //Player won
							this.pwin = true;
							this.over = true;    //game over
							break;
						}else{
							this.cwin = true;    //AI won
							this.over = true;
							break;
						}
					if(this.over)               //exiting
						break;
				}
			//Player won
			if(this.pwin){
				pwincount = pwincount + 1;
			    System.out.print("Player Won! Game Is Over!\r\n");
			}
							//AI won
			if(this.cwin){
				cwincount = cwincount + 1;
				System.out.print("AI Won! Game Is Over! \r\n");
			}	
			//Tie
			if(this.tie)
				System.out.print("Chill! It's a tie game\r\n");
	 }
}