/*
 * 문제
조규현과 백승환은 터렛에 근무하는 직원이다. 하지만 워낙 존재감이 없어서 인구수는 차지하지 않는다. 다음은 조규현과 백승환의 사진이다.



이석원은 조규현과 백승환에게 상대편 마린(류재명)의 위치를 계산하라는 명령을 내렸다. 조규현과 백승환은 각각 자신의 터렛 위치에서 현재 적까지의 거리를 계산했다.

조규현의 좌표 (x1, y1)와 백승환의 좌표 (x2, y2)가 주어지고, 조규현이 계산한 류재명과의 거리 r1과 백승환이 계산한 류재명과의 거리 r2가 주어졌을 때, 류재명이 있을 수 있는 좌표의 수를 출력하는 프로그램을 작성하시오.

입력
첫째 줄에 테스트 케이스의 개수 T가 주어진다. 각 테스트 케이스는 다음과 같이 구성되어있다.

첫째 줄에 x1, y1, r1, x2, y2, r2가 주어진다. x1, y1, x2, y2는 -10,000보다 크거나 같고, 10,000보다 작거나 같은 정수이고, r1, r2는 10,000보다 작거나 같은 자연수이다.

출력
각 테스트 케이스마다 류재명이 있을 수 있는 위치의 수를 출력한다. 만약 류재명이 있을 수 있는 위치의 개수가 무한대일 경우에는 -1을 출력한다.
 */

package baekjoon;

import java.util.Scanner;

public class Baekjoon1002 {


	static int func(int x1, int y1, int r1, int x2, int y2, int r2){
		int cases = 0;
		
		int X=x2-x1;
		int Y=y2-y1;
		
		int P = (int)Math.pow((r2+r1),2);
		int M = (int)Math.pow((r2-r1),2);
		
		int d = (int)(Math.pow(X, 2)+Math.pow(Y, 2));
		if(d==0&&r1==r2){
			cases=-1;
		}else if((d>M) && (d<P)){
			cases=2;
		}else if((d==P) || (d== M)){
			cases=1;
		}else if((d>P) || (d<M)){
			cases=0;
		}
		
		return cases;
	}
	
	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		
		String sel = sc.nextLine();
		
		int[] cases = new int[Integer.parseInt(sel)];
		
		for(int i=0; i < Integer.parseInt(sel); i++){
		
			String[] arr = sc.nextLine().split(" ");
			
			cases[i] = func(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]),Integer.parseInt(arr[2]),
					Integer.parseInt(arr[3]),Integer.parseInt(arr[4]),Integer.parseInt(arr[5]));
			
		}
		
		for(int i=0;i<Integer.parseInt(sel);i++){
			
			System.out.println(cases[i]);
			
		}
	}
}

/*
 * 두 원의 교점 갯수 구하기
 * 두 원의 중심점 거리  = >  d**2 = (x2-x1)**2 + (y2-y1)**2
 * 
 * 두 점에서 만날 조건 = > (r1-r2)**2 < d**2   AND    (r1+r2)**2 > d
 * 
 * 한 점에서 만날 조건 = > (r1+r2)**2 == d**2(외접) OR     (r1-r2)**2==d**2(내접)
 * 
 * 만나지 않는 조건 = > (r1+r2)**2 < d**2(서로 떨어져있음)  OR  (r1-r2)**2 > d (한 원이 다른 원 안에 위치함)
 * 
 * 무한대의 점 = > r1==r2  AND  d==0   (반지름이 같고 중심점이 같음)
 */