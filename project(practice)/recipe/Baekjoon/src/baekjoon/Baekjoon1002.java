/*
 * ����
�������� ���ȯ�� �ͷ��� �ٹ��ϴ� �����̴�. ������ ���� ���簨�� ��� �α����� �������� �ʴ´�. ������ �������� ���ȯ�� �����̴�.



�̼����� �������� ���ȯ���� ����� ����(�����)�� ��ġ�� ����϶�� ����� ���ȴ�. �������� ���ȯ�� ���� �ڽ��� �ͷ� ��ġ���� ���� �������� �Ÿ��� ����ߴ�.

�������� ��ǥ (x1, y1)�� ���ȯ�� ��ǥ (x2, y2)�� �־�����, �������� ����� �������� �Ÿ� r1�� ���ȯ�� ����� �������� �Ÿ� r2�� �־����� ��, ������� ���� �� �ִ� ��ǥ�� ���� ����ϴ� ���α׷��� �ۼ��Ͻÿ�.

�Է�
ù° �ٿ� �׽�Ʈ ���̽��� ���� T�� �־�����. �� �׽�Ʈ ���̽��� ������ ���� �����Ǿ��ִ�.

ù° �ٿ� x1, y1, r1, x2, y2, r2�� �־�����. x1, y1, x2, y2�� -10,000���� ũ�ų� ����, 10,000���� �۰ų� ���� �����̰�, r1, r2�� 10,000���� �۰ų� ���� �ڿ����̴�.

���
�� �׽�Ʈ ���̽����� ������� ���� �� �ִ� ��ġ�� ���� ����Ѵ�. ���� ������� ���� �� �ִ� ��ġ�� ������ ���Ѵ��� ��쿡�� -1�� ����Ѵ�.
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
 * �� ���� ���� ���� ���ϱ�
 * �� ���� �߽��� �Ÿ�  = >  d**2 = (x2-x1)**2 + (y2-y1)**2
 * 
 * �� ������ ���� ���� = > (r1-r2)**2 < d**2   AND    (r1+r2)**2 > d
 * 
 * �� ������ ���� ���� = > (r1+r2)**2 == d**2(����) OR     (r1-r2)**2==d**2(����)
 * 
 * ������ �ʴ� ���� = > (r1+r2)**2 < d**2(���� ����������)  OR  (r1-r2)**2 > d (�� ���� �ٸ� �� �ȿ� ��ġ��)
 * 
 * ���Ѵ��� �� = > r1==r2  AND  d==0   (�������� ���� �߽����� ����)
 */