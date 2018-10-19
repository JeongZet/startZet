/*
 * 문제
두 수 A와 B를 입력받은 다음, A+B를 출력하는 프로그램을 작성하시오.

입력
첫째 줄에 A와 B가 주어진다. (0 < A,B < 10)

 */

package baekjoon;

import java.util.Scanner;

public class Baekjoon1000 {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		String[] arr = sc.nextLine().split(" ");
		
		int temp = Integer.parseInt(arr[0])+Integer.parseInt(arr[1]);
		
		System.out.println(temp);
		
	}

}
