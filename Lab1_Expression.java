import java.util.*;
import java.lang.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class lab1f{

	public static void main(String[] args){
		// flag setting
		ArrayList<ArrayList<Node>> A1 = null;
		boolean sign = true;
		boolean flagexit = false;
		String mem = null;

		// main input-output circle
		while (sign) {
			System.out.print('>');
			String str;
			Scanner s = new Scanner(System.in);
			str = s.nextLine();

			if (!str.startsWith("!"))
			{
				//remove space
				str = str.replaceAll(" ","");

                //add by
				String rule6 = "[0-9][a-zA-Z]";
				Pattern p6 = Pattern.compile(rule6);
				Matcher m6 = p6.matcher(str);
				boolean flag6 = m6.find();
				while(flag6)
				{
					int loca = m6.start();
					loca++;
					StringBuilder strh = new StringBuilder (str); 
					strh.insert(loca, "*");  
					str = strh.toString();
					m6 = p6.matcher(str);
					flag6 = m6.find();
				}

				//subtract process
				str = transform_subtract(str);

				String rule7 = "[1-9][\\^]";
				Pattern p7 = Pattern.compile(rule7);
				Matcher m7 = p6.matcher(str);
				boolean flag7 = m6.find();
				String rule8 = "[\\^][a-zA-Z]";
				Pattern p8 = Pattern.compile(rule8);
				Matcher m8 = p8.matcher(str);
				boolean flag8 = m8.find();
				boolean flag9 = flag8 || flag7;
				if(flag9){
					System.out.println("Illegal Expression : Misuse Of Operator ^");
				}
				
				//exponentail process
				str = transform_exponential(str);

				//formula check
				boolean f1 = check(str);
				if(f1){
					mem = str;
					A1 = expression(mem);
					printExpression(A1);
					flagexit = true;
				}
			}
			else if (str.startsWith("!simplify"))
			{
				long start = System.nanoTime();
				if(flagexit){
					//check whether the simplify command is legal
					str = str.replaceFirst("!simplify ","");
					String rule11 = "[\\s0-9A-Za-z\\=]{0,}";
					String rule41 = "[\\=]{2,}";
					String rule51 = "[A-Za-z][0-9]{1,}";
					String rule61 = "[0-9][A-Za-z]{1,}";
					String rule71 = "[A-Za-z][\\=][0-9]{1,}";
					Pattern p11 = Pattern.compile(rule11);
					Pattern p41 = Pattern.compile(rule41);
					Pattern p51 = Pattern.compile(rule51);
					Pattern p61 = Pattern.compile(rule61);
					Pattern p71 = Pattern.compile(rule71);
					Matcher m11 = p11.matcher(str);
					boolean flag11 = m11.matches();
					Matcher m41 = p41.matcher(str);
					boolean flag41 = m41.find();
					Matcher m51 = p51.matcher(str);
					boolean flag51 = m51.find();
					Matcher m61 = p61.matcher(str);
					boolean flag61 = m61.find();
					Matcher m71 = p71.matcher(str);
					boolean flag71 = m71.find();
					boolean flag100 = flag11 & !flag41 & !flag51 &!flag61 & flag71;		
					if(!flag100)
					{
						System.out.println("Illegal Simplify Command");
					}
					else{
						String[] varset = str.split(" ");
						ArrayList<ArrayList<Node>> A2 = expression(mem);
						for(int h=0; h<varset.length; h++){
							
							String rule01 = "[A-Za-z]{1,}";
							Pattern p01 = Pattern.compile(rule01);
							Matcher m01 = p01.matcher(varset[h]);
							boolean flag01 = m01.matches();
							if(!flag01){
								String[] varandval = varset[h].split("=");
								int val = Integer.parseInt(varandval[1]);
								A2 = simplify(A2, varandval[0], val);
							}
						}
					printExpression(A2);
					long end = System.nanoTime();
					long total=end - start;
					System.out.println("Total time cost is :"+total+ " ns");
					}	
				
				}
				else{
					System.out.println("Currently No Valid Expression Yet");
				}
			}
			else if (str.startsWith("!d/d")){
				if(flagexit){
					long start = System.nanoTime();
					derivative(A1,str);
					long end = System.nanoTime();
					long total=end - start;
					System.out.println("Total time cost is: "+total+" ns");
					
				}
				else{
					System.out.println("Currently No Valid Expression Yet");
				}
			}
			else if (str.equalsIgnoreCase("!exit")){
				System.out.println("Exit Successfully!");
				sign = false;
			}
			else{
				System.out.println("Command Error!");
			}
		}
	}

    public static boolean check(String str){
		boolean flag2 = str.startsWith("+") || str.startsWith("*");
		boolean flag3 = str.endsWith("+") || str.endsWith("*");
		String rule1 = "[0-9A-Za-z\\*\\+\\-]{0,}";
		String rule4 = "[\\+\\*]{2,}";
		String rule5 = "[A-Za-z][0-9]{1,}";
		Pattern p1 = Pattern.compile(rule1);
		Pattern p4 = Pattern.compile(rule4);
		Pattern p5 = Pattern.compile(rule5);
		Matcher m1 = p1.matcher(str);
		boolean flag1 = m1.matches();
		Matcher m4 = p4.matcher(str);
		boolean flag4 = m4.find();
		Matcher m5 = p5.matcher(str);
		boolean flag5 = m5.find();
		boolean flag = flag1 & !flag2 & !flag3 & !flag4 & !flag5;
		if(!flag1){
			System.out.println("Illegal Expression : Containing Illegal Character");
		}
		if(flag2){
			System.out.println("Illegal Expression : Expression Starts With An Operator");
		}
		if(flag3){
			System.out.println("Illegal Expression : Expression Ends With An Operator");
		}
		if(flag4){
			System.out.println("Illegal Expression : An Operator Followed By Another Operator");
		}
		if(flag5){
			System.out.println("Illegal Expression : An Varible Followed By An Number");
		}
		return flag;
	}

	public static String transform_subtract(String str){
		StringBuilder nstr = new StringBuilder(str);
		int cur = 0;
		while(cur<nstr.length()){
			char x = nstr.charAt(cur);
			if(x=='-'){
				nstr.replace(cur,cur+1,"+-1*");
				cur += 3;
			}
			cur++;
		}
		return nstr.toString();
	}

	public static String transform_exponential(String str){
		String rule = "[\\^][1-9]";
		Pattern p = Pattern.compile(rule);
		Matcher m = p.matcher(str);
		boolean flag = m.find();
		while(flag){
			int loca = m.start();
			String var;
			int exp;

			int i=loca+1, j=loca-1;

			while(i<str.length() && Character.isDigit(str.charAt(i))){
				i++;
			}
			exp = Integer.parseInt(str.substring(loca+1,i));

			while(j>=0 && Character.isLetter(str.charAt(j))){
				j--;
			}
			var = str.substring(j+1,loca);

			StringBuilder strf = new StringBuilder(str); 
			StringBuilder strh = new StringBuilder(); 
			for(int h=0; h<exp-1; h++){
				strh.append("*");
				strh.append(var);
			}
			var = strh.toString();
			strf.delete(loca, i);
			strf.insert(loca, var);
			str = strf.toString();

			m = p.matcher(str);
			flag = m.find();
		}
		return str;		
	}

	public static ArrayList<ArrayList<Node>> expression(String exp){
		//pre-process
		String cutter1 = "\\+", cutter2 = "\\*";
		String[] multiply = exp.split(cutter1);
		ArrayList<ArrayList<Node>> vol = new ArrayList<ArrayList<Node>>(); 
		
		//storage construction 
		for(int i=0; i<multiply.length; i++){
			String[] elements = multiply[i].split(cutter2);
			ArrayList<Node> row = new ArrayList<Node>();
			for(int j=0; j<elements.length; j++){
				Node temp_node;
				int temp_int;
				if(Character.isDigit(elements[j].charAt(0)) | elements[j].charAt(0)=='-'){
					temp_int = Integer.parseInt(elements[j]);
					temp_node = new Node(temp_int);
				}
				else{
					temp_node = new Node(elements[j]);
 				}
 				row.add(temp_node);
			}
			vol.add(row);
		}

		return vol;
	}

	public static ArrayList<ArrayList<Node>> simplify(ArrayList<ArrayList<Node>> arr, String variable, int value){
		for(int i=0; i<arr.size(); i++){
			ArrayList<Node> mul = arr.get(i);
			int product = 1;
			for(int j=0; j<mul.size(); j++){
				Node factor = mul.get(j);
				if(factor.isNum){
					product = product * factor.getNum();
					mul.remove(j);
					j--;
				}
				else if(variable.compareTo(factor.getC())==0){
					product = product * value;
					mul.remove(j);
					j--;
				}
			}
			Node newFactor = new Node(product);
			mul.add(0, newFactor);
		}

		int sum=0;
		for(int i=0; i<arr.size(); i++){
			ArrayList<Node> mul2 = arr.get(i);
			if(mul2.size()==1 & mul2.get(0).isNum){
				sum = sum + mul2.get(0).getNum();
				arr.remove(i);
				i--;
			}
		}

		if(sum!=0){
			ArrayList<Node> newMul = new ArrayList<Node>();
			Node newFactor = new Node(sum);
			newMul.add(newFactor);
			arr.add(newMul);
		}

		return arr;
	}

	public static void derivative(ArrayList<ArrayList<Node>> arr,String der){
		int fla  = 0;
		der = der.replaceFirst("!d/d ","");
		String rule1 = "[A-Za-z]{0,}";
		Pattern p1 = Pattern.compile(rule1);
		Matcher m1 = p1.matcher(der);
		boolean flag1 = m1.matches();
		if(!flag1)
		{
			System.out.println("Illegal Derivative Command");
		}
		else{
			for(int i=0; i<arr.size(); i++)
			{
				fla = dersingle(arr,i,der,fla);
			}
			if(fla == 0)
				System.out.println("0");
			else
				System.out.println("");	
		}
	}

	public static int dersingle(ArrayList<ArrayList<Node>> arr,int i,String der, int fla){
		ArrayList<Node> t1 = arr.get(i);
		ArrayList<Integer> nums = new ArrayList<Integer>();
		ArrayList<String> set = new ArrayList<String>();
		int factor=1;
		for(int j=0; j<t1.size(); j++)
		{
			boolean f2 = t1.get(j).isNum;
			if(f2)
			{
				factor = factor * t1.get(j).getNum();
			}
			else
			{
				if(set.contains(t1.get(j).getC()))
				{
					 int index = set.indexOf(t1.get(j).getC());
					 int time = nums.get(index);
					 time++;
					 nums.remove(index);
					 nums.add(index,time);
				}
				else
				{
					 set.add(t1.get(j).getC());
					 nums.add(1);
				}	 
			 }
		}
		if(set.contains(der))
		{
			int loc = set.indexOf(der);
			int coe = nums.get(loc);
			factor = factor * coe;
			coe--;
			nums.remove(loc);
			nums.add(loc,coe);
			if(fla == 1){
				if(factor>0){
					System.out.print("+");
				}
			}
			fla = 1;
			printsingle(factor,set,nums);
		}
		return fla;
	}
		
	public static void printsingle(int factor,ArrayList<String> set,ArrayList<Integer> nums)	{
		System.out.print(factor);
		for(int n=0; n<nums.size();n++)
		{
			for(int p=0; p<nums.get(n);p++)
			{
				System.out.print("*"+set.get(n));
			}
		}
	}

	public static void printExpression(ArrayList<ArrayList<Node>> arr){
		for(int i=0; i<arr.size(); i++){
			ArrayList<Node> tmp1 = arr.get(i);
			for (int j=0; j<tmp1.size(); j++) {
				Node tmp2 = tmp1.get(j);
				if (tmp2.isNum) {
					if(tmp2.getNum()==1){
						continue;
					}
					else if(tmp2.getNum()==-1){
						System.out.print("-");
						continue;
					}
					else{
						System.out.print(tmp2.getNum());
					}
				}
				else {
					System.out.print(tmp2.getC());
				}
				if(j<tmp1.size()-1) {
					System.out.print("*");
				}
			}
			if(i<arr.size()-1) {
				if(arr.get(i+1).get(0).isNum==false || arr.get(i+1).get(0).getNum()>0){
					System.out.print("+");
				}
			}
		}
		System.out.println("");
		return;
	}
}

class Node{
	public boolean isNum;
	private int num;
	private String variable;

	Node(String variable){
		this.isNum=false;
		this.variable=variable;
	}

	Node(int num){
		this.isNum=true;
		this.num=num;
	}

	public int getNum(){
		return num;
	}

	public String getC(){
		return variable;
	}
}