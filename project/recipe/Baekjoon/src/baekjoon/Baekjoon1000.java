/*
 * ����
�� �� A�� B�� �Է¹��� ����, A+B�� ����ϴ� ���α׷��� �ۼ��Ͻÿ�.

�Է�
ù° �ٿ� A�� B�� �־�����. (0 < A,B < 10)

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
