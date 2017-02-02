package grade8;

/*
 * 문제 
오늘은 2007년 1월 1일 월요일이다. 그렇다면 2007년 x월 y일은 무슨 요일일까? 이를 알아내는 프로그램을 작성하시오.
입력 
첫째 줄에 빈 칸을 사이에 두고 x(1≤x≤12)와 y(1≤y≤31)이 주어진다. 참고로 2007년에는 1, 3, 5, 7, 8, 10, 12월은 31일까지, 4, 6, 9, 11월은 30일까지, 2월은 28일까지 있다.
출력 
첫째 줄에 x월 y일이 무슨 요일인지에 따라 SUN, MON, TUE, WED, THU, FRI, SAT중 하나를 출력한다.
예제 입력 복사 
1 1
예제 출력 복사 
MON
 */

import java.util.Scanner;

public class Date2007 {
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		String ins = sc.nextLine();
		String[] date = ins.split(" ");
		
		String[] mon = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
		
		int[] day = new int[13];
		int temp=0;
		day[0]=0;
		for(int i=1;i<=12;i++){
			if(i==1||i==3||i==5||i==7||i==8||i==10||i==12){
				temp+=31;
			}else if(i==4||i==6||i==9||i==11){
				temp+=30;
			}else
				temp+=28;
			
			day[i]=temp;
		}
		
			int result = day[Integer.parseInt(date[0])-1]+Integer.parseInt(date[1]);
			
			System.out.println(mon[((result-1)%7)]);
	}
}

/*
 * 해당 문제는 일단 인트 배열을 만들어 월간의 차이를 저장해뒀다가 일간의 차이와 월간의 차이를 일주일로 나눠
 * 나오는 나머지를 요일배열에 대입하여 결과물을 출력할 수 있었다.
 */
*/