/*
 * ����
A-B�� ����Ͻÿ�.

�Է�
ù° �ٿ� A�� B�� �־�����. (0< A,B < 10)

���
ù° �ٿ� A-B�� ����Ѵ�.

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
