package baekjoon;
/*문제 
다음 소스는 N번째 피보나치 함수를 구하는 함수이다.

int fibonacci(int n) {
    if (n==0) {
        printf("0");
        return 0;
    } else if (n==1) {
        printf("1");
        return 1;
    } else {
        return fibonacci(n‐1) + fibonacci(n‐2);
    }
}
fibonacci(3)을 호출하면 다음과 같은 일이 일어난다.
fibonacci(3)은 fibonacci(2)와 fibonacci(1) (첫 번째 호출)을 호출한다.
fibonacci(2)는 fibonacci(1) (두 번째 호출)과 fibonacci(0)을 호출한다.
두 번째 호출한 fibonacci(1)은 1을 출력하고 1을 리턴한다.
fibonacci(0)은 0을 출력하고, 0을 리턴한다.
fibonacci(2)는 fibonacci(1)과 fibonacci(0)의 결과를 얻고, 1을 리턴한다.
첫 번째 호출한 fibonacci(1)은 1을 출력하고, 1을 리턴한다.
fibonacci(3)은 fibonacci(2)와 fibonacci(1)의 결과를 얻고, 2를 리턴한다.
이 때, 1은 2번 출력되고, 0은 1번 출력된다. N이 주어졌을 때, fibonacci(N)을 호출했을 때, 0과 1이 각각 몇 번 출력되는지 구하는 프로그램을 작성하시오.
입력 
첫째 줄에 테스트 케이스의 개수 T가 주어진다. 각 테스트 케이스는 다음과 같이 구성되어있다.
첫째 줄에 N이 주어진다. N은 40보다 작거나 같은 자연수 또는 0이다.
출력 
각 테스트 케이스마다 0이 출력되는 횟수와 1이 출력되는 횟수를 공백으로 구분해서 출력한다.
 */

import java.util.Scanner;

public class Baekjoon1003 {
	
	public static void main(String[] args){
		
		Scanner sc = new Scanner(System.in);
		
		String sel = sc.nextLine();
		
		int[] ones = new int[41];
		int[] zeros = new int[41];
		int[][] count = new int[Integer.parseInt(sel)][2];
		
		ones[0]=0;
		ones[1]=1;
		
		
		zeros[0]=1;
		zeros[1]=0;
		
		for(int i=2; i < 41; i++){
		
			ones[i]=ones[i-2]+ones[i-1];
			zeros[i]=zeros[i-2]+zeros[i-1];
					
		}
		
		for(int i=0;i<Integer.parseInt(sel);i++){
			String temp = sc.nextLine();
			count[i][1]=ones[Integer.parseInt(temp)];
			count[i][0]=zeros[Integer.parseInt(temp)];
		
		}
		
		for(int i=0;i<Integer.parseInt(sel);i++){
			System.out.println(count[i][0]+" "+count[i][1]);
		}
		
	}
}
/*
 *피보나치 수열의 40번째까지의 숫자이니 미리 40까지 0과 1의 갯수를 저장해놓는 방법을 선택하였다.
 *1의 갯수를 세기 위하여 배열을 하나 만들어 배열의 0번째에 0을 1번째에 1을 할당함으로써 0과 1을 부를 때마다 수를 
 *합하는 방법으로 1의 갯수만 저장하고 0의 배열도 동일하게 한다.
 *계산된 갯수는 배열에 저장하여 마지막에 출력하여 준다. 
*/