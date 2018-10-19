package grade8;

/*
 * 문제 
무한히 큰 배열에 다음과 같이 분수들을 적혀있다.
1/1
1/2
1/3
1/4
1/5
…
2/1
2/2
2/3
2/4
…
…
3/1
3/2
3/3
…
…
…
4/1
4/2
…
…
…
…
5/1
…
…
…
…
…
…
…
…
…
…
…
이와 같이 나열된 분수들을 1/1 -> 1/2 -> 2/1 -> 3/1 -> 2/2 -> … 과 같은 순서로 차례대로 1번, 2번, 3번, 4번, 5번, … 분수라고 하자.
X가 주어졌을 때, X번째 분수를 구하는 프로그램을 작성하시오.
입력 
첫째 줄에 X(1≤X≤10,000,000)가 주어진다.
출력 
첫째 줄에 분수를 출력한다.
 */

import java.util.Scanner;

public class Fraction {
	public static void main(String[] args) {
		
		int n = 1;
		int insert = 0;
		int X = 0;
		int Y = 0;
		
		Scanner sc = new Scanner(System.in);
		
		insert = sc.nextInt();
		
		while(true){
			if(insert > ((n*n)+n)/2){ 
				n++;
			}else{
				break;
			}
		}
		
		X= (n-(((n*n)+n)/2-insert));
		Y= (1+(((n*n)+n)/2-insert));
		
		if(n%2==0){
			System.out.println(X+"/"+Y);
		}else
			System.out.println(Y+"/"+X);
		
	}
}

/*
 * 이 문제는 계차수열을 구하여 일반 수열의 규칙을 파악하여 값을 입력받은 값과 a(n) 비교하여 입력한 값이 클 경우 n값을 증가시킨다.
 * b(n) = (n^2+3n)/2 이고  a(n) = n^2+n 으로 입력한 값이 작을 경우 반복문을 종료하며 n값은 몇 번째 대각선 줄에 위치 하는지를 뜻한다.
 * 그 다음은 몇번째 줄인지를 구하였으면 그 줄에서 몇번째에 위치에 있는지 계산 위하여 (a(n)-입력한 값) 을 한다. 이 값을 temp라고 한다면
 * 이 값은 a(n) 보다 temp 값만큼 뒤에 위치한다는 뜻으로 n값이 홀수와 짝수의 경우 반대이므로 각각 값에 따라 반대로 출력한다.
 * 홀수일 경우는 a(n)은 1/n이고 짝수의 경우는 a(n)=n/1 이므로 이보다 뒤에 떨어진 값은 짝수의 경우 n-temp / 1+temp
 * 홀수의 경우 1+temp / n-temp 이다.
*/