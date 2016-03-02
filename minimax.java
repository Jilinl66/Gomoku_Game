//boolean turn true-computer(MAX); false-player(MIN)

// how to use?
// first: initialize( )
// then: mmab (bla bla bla max=inf min =!inf)... 
import java.lang.Object.*;
import java.util.AbstractList;
import java.util.ArrayList;


public class minimax {
	
	boolean[][][] ctable=new boolean[16][16][672];
	boolean[][][] ptable=new boolean[16][16][672];
	int[] mark = new int[672];
	int[][] win  = new int[2][672]; // 0 - ai; 1 - player
	int[][] next_step = new int[16][16];
	int op_counter = 0;
	int next_m=-1, next_n=-1;
	public int super_depth;
	
	boolean checkCombo(int[][] board, int i,int j){
		if (i<5||j<5||j>11||i>11)
			return false;
		else{
			int[] count = new int[8];
			
			for (int a=i-1; a>i-4; a--){
				if (a>i-3&& board[a][j]!=0)
					break;
				else if (a==i-3 && board[a][j]!=1)
					count[0]++;
			}
			
			for (int a=i+1; a<i+4; a++){
				if (a<i+3&& board[a][j]!=0)
					break;
				else if (a==i+3 && board[a][j]!=1)
					count[1]++;
			}
			
			for (int a=j+1; a<j+4; a++){
				if (a<j+3&& board[i][a]!=0)
					break;
				else if (a==j+3 && board[i][a]!=1)
					count[2]++;
			}
			
			for (int a=j-1; a>j-4; a--){
				if (a>j-3&& board[i][a]!=0)
					break;
				else if (a==j-3 && board[i][a]!=1)
					count[3]++;			
			}
			
			int b1 = i;
			for (int a=j-1; a>j-4; a--){
				if (a>j-3&& board[b1--][a]!=0)
					break;
				else if (a==j-3 && board[b1--][a]!=1)
					count[4]++;			
			}
			
			int b2 = i;
			for (int a=j-1; a>j-4; a--){
				if (a>j-3&& board[b2++][a]!=0)
					break;
				else if (a==j-3 && board[b2++][a]!=1)
					count[5]++;			
			}

			int b3 = i;
			for (int a=j+1; a<j+4; a++){
				if (a<j+3&& board[b3++][a]!=0)
					break;
				else if (a==j+3 && board[b3++][a]!=1)
					count[6]++;			
			}
			
			int b4 = i;
			for (int a=j+1; a<j+4; a++){
				if (a<j+3&& board[b4--][a]!=0)
					break;
				else if (a==j+3 && board[b4--][a]!=1)
					count[7]++;			
			}
			
			int counter =0;
			for(int a=0; a<7; a++)
			{
				if (count[a]>0)
					counter++;
			}
			
			return (counter>=2)?true:false;
		}
	}
	
	void initialize(){
		int i,j,k;
		int icount = 0;
		
		for (int a=0; a<16; a++){
			for (int b=0; b<16; b++){
				for (int c=0; c<672; c++)
				{
					ctable[a][b][c] = false;
					ptable[a][b][c] = false;
				}
			}
		}
		
		
		//Enumrate all 5-in-line situations
		//Horizontal
		for(i=0;i<16;i++)
			for(j=0;j<12;j++){
				for(k=0;k<5;k++){
					this.ptable[j+k][i][icount] = true;
					this.ctable[j+k][i][icount] = true;
					mark[icount] = 20-(i-8)-(j-8);
				}
				icount++;
			}
		//Vertical
		for(i=0;i<16;i++)
			for(j=0;j<12;j++){
				for(k=0;k<5;k++){
					this.ptable[i][j+k][icount] = true;
					this.ctable[i][j+k][icount] = true;
					mark[icount] = 20-(i-8)-(j-8);
				}
				icount++;
			}
		//Des-to-right
		for(i=0;i<12;i++)
			for(j=0;j<12;j++){
				for(k=0;k<5;k++){
					this.ptable[j+k][i+k][icount] = true;
					this.ctable[j+k][i+k][icount] = true;
					mark[icount] = 20-(i-8)-(j-8);
				}
				icount++;
			}
		//System.out.println(icount);
		//Des-to-left
		for(i=0;i<12;i++)
			for(j=15;j>=4;j--){
				for(k=0;k<5;k++){
					this.ptable[j-k][i+k][icount] = true;
					this.ctable[j-k][i+k][icount] = true;
					mark[icount] = 20-(i-8)-(j-8);
				}
				icount++;
			}
		//System.out.println(icount);
		for(i=0;i<=1;i++)  //enumerate the scores
			for(j=0;j<672;j++)
				this.win[i][j] = 0;
		
		//System.out.println(icount);
	}
	
	// main function
	int mmab(int[][] node_board, int depth, int min, int max, boolean turn){
		int temp_is = isTerminated(node_board);
		if (temp_is==1)
			return 0;
		if (temp_is==0&&turn)
			return 0;
		else if (temp_is==0&&!turn) 
			return 10000;
		
		if (depth==1 )
			return evaluate();
		else if (turn){ // MAX
			//if (depth == 4)
			int _temp;
			int v = min;
			
			for (int i=0; i<16; i++){
				for (int j=0; j<16; j++){
					if (node_board[i][j] != 2)
						continue;
					
					boolean inrange = false;
					for (int k=((i-3)>=0?i-3:0); k<((i+3)<=16?(i+3):16) ;k++ ){
						for (int l=((j-3)>=0?j-3:0); l<((j+3)<=16?(j+3):16) ;l++ ){
							if (node_board[k][l]!=2)
							{
								inrange = true;
								//break;
							}
						}
					}
					
					if (!inrange)
						continue;
					
					//System.out.println(op_counter++);
					int [][] next_node = new int[16][16];
					int [][] blocking_node = new int[16][16];
					for (int k=0; k<16; k++)
					{
						for (int l=0; l < 16; l++){
						next_node[k][l] = node_board[k][l];//.clone();
						blocking_node[k][l] = node_board[k][l];//.clone();
						}
					}
					next_node[i][j] = 0;
					_temp = mmab(next_node, depth-1, v, max ,!turn ); // v'
					next_node = null;
					
					//blocking:
					if(super_depth==depth){
						getWin(blocking_node);
						int s0 = Marks_for_blocking();
						//System.out.print(s0+" ");
						blocking_node[i][j]=1;
						getWin(blocking_node);
						
						int s = Marks_for_blocking();
						//System.out.println(s+" "+i + " "+j);
						if ((s-s0)>100000 && (s-s0)>v)
						{
							v = s-s0;
							//System.out.println(v);
							next_m=i;
							next_n=j;
							//System.out.println(next_m+" "+next_n);
						}
					}
					blocking_node = null;
					
					
					
					
					if (_temp>v)
					{
						//if(super_depth ==depth)
							//System.out.println(_temp);
						next_m=i;
						next_n=j;
						v = _temp;
					}
					if (v > max){				
						return max;
					}	
				}
			}
			
			return v;
		}
		else
		{
			int _temp;
			int v = max;
			for (int i=0; i<16; i++){
				for (int j=0; j<16; j++){
					if (node_board[i][j] != 2)
						continue;
					
					boolean inrange = false;
					for (int k=((i-3)>=0?i-3:0); k<((i+3)<16?(i+3):16) ;k++ ){
						for (int l=((j-3)>=0?j-3:0); l<((j+3)<16?(j+3):16) ;l++ ){
							if (node_board[k][l]!=2)
							{
								inrange = true;
								//break;
							}
						}
					}
					if (!inrange)
						continue;
					
					
					int [][] next_node = new int[16][16];
					for (int k=0; k<16; k++)
					{
						for (int l=0; l<16; l++)
						next_node[k][l] = node_board[k][l];
						//next_node[k] = node_board[k].clone();
					}
					next_node[i][j] = 1;
					_temp = mmab(next_node, depth-1, min, v ,!turn ); // v'
					next_node = null;
					if (_temp<v){
						//next_m=i;
						//next_n=j;
						v = _temp;
					}
					if (v < min)
						return min;
						
				}
			}
			return v;			
		}
					
		//return 0;
	}
	
	// get the n-in-line situation
	// scanning board
	void getWin(int[][] board){
		
		for (int i =0 ; i < 672; i++)
		{
			this.win[0][i]=0;
			this.win[1][i] = 0;
		}
		
		for (int m =0; m < 16; m++){
			for (int n = 0; n<16; n++){
				if (board[m][n]==0){
					boolean temp = checkCombo(board,m,n);
					for (int i =0 ; i < 672; i++)
					{
						
						if (this.ctable[m][n][i] ){
					
							if (this.win[0][i] != 7)
								this.win[0][i]++;
							this.win[1][i] = 7;
						}
						//if (temp)
							//this.win[0][i] = 8;
					}
					
				}
				if (board[m][n]==1){
					for (int i =0 ; i < 672; i++)
					{
						if (this.ptable[m][n][i] ){
							if (this.win[1][i] != 7)
								this.win[1][i]++;
							this.win[0][i]=7;
						}
					}
				}

			}
		}
	}
	
	int  Marks_for_blocking(){
		 int grade = 0;
		 for(int i=0; i<672; i++){
			 switch(this.win[1][i]){   
			   
				case 3: //score for 3-in-line
					grade+=1080;
					break;
				case 4: //score for 4-in-line
					grade+=102000;
					break;
				case 5:
					grade+=220000;
					break;
			}		 
		 }
		 return grade;
	}
	
	// evaluate function: based on current winning situation
	 int evaluate(){
		 int grade = 0;
		 
		 for(int i=0; i<672; i++){
			 switch(this.win[0][i]){   
			    case 1: //score for one
			    	grade+=mark[i];
					break;
				case 2: //score for 2-in-line
					grade+=50;
					break;
				case 3: //score for 3-in-line
					grade+=180;
					break;
				case 4: //score for 4-in-line
					grade+=400;
					break;
				case 8: //score for THREE_THREE FOUR_FOUR
					grade+=300;
					break;
			} 
		 }
		 return grade;
	 }
	 
	 // check if game ended
	 int isTerminated(int[][] board) // 0: solved; 1:full; -1:not terminated 
	 {
		 if (isFull(board))
			 return 1;
		 else{
			getWin(board);
			for (int i=0; i<672; i++)
			{
				if (win[0][i]==5||win[1][i]==5)
					return 0;
			}
			 
			 return -1;
		 }
	 }
	 
	 // check if the board is full
	 boolean isFull(int[][] board){	 
		 for (int i=0; i<16; i++)
			 for(int j=0; j<16; j++)
			 {
				 if (board[i][j]==2)
					 return false;
			 }
		 return true;
	 }
}