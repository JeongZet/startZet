/*
 * 문제
A-B를 계산하시오.

입력
첫째 줄에 A와 B가 주어진다. (0< A,B < 10)

출력
첫째 줄에 A-B를 출력한다.

 */

package baekjoon;

import java.util.Scanner;

public class baekjoon1001 {

	public static void main(String[] args){
	Scanner sc = new Scanner(System.in);
		
		String[] arr = sc.nextLine().split(" ");
		
		int temp = Integer.parseInt(arr[0])-Integer.parseInt(arr[1]);
		
		System.out.println(temp);
		
	}
	
}
