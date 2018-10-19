package grade8;

/*
 * ���� 
������ 2007�� 1�� 1�� �������̴�. �׷��ٸ� 2007�� x�� y���� ���� �����ϱ�? �̸� �˾Ƴ��� ���α׷��� �ۼ��Ͻÿ�.
�Է� 
ù° �ٿ� �� ĭ�� ���̿� �ΰ� x(1��x��12)�� y(1��y��31)�� �־�����. ����� 2007�⿡�� 1, 3, 5, 7, 8, 10, 12���� 31�ϱ���, 4, 6, 9, 11���� 30�ϱ���, 2���� 28�ϱ��� �ִ�.
��� 
ù° �ٿ� x�� y���� ���� ���������� ���� SUN, MON, TUE, WED, THU, FRI, SAT�� �ϳ��� ����Ѵ�.
���� �Է� ���� 
1 1
���� ��� ���� 
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
 * �ش� ������ �ϴ� ��Ʈ �迭�� ����� ������ ���̸� �����ص״ٰ� �ϰ��� ���̿� ������ ���̸� �����Ϸ� ����
 * ������ �������� ���Ϲ迭�� �����Ͽ� ������� ����� �� �־���.
 */
*/