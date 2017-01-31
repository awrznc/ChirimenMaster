import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class Sub {

	//一次元配列（String型）表示
	public static void StringArrayInvisible(String[] Array){
		for(int i = 0;i<Array.length;i++){
			System.out.println(Array[i]);
		}
	}




	//入力です
	public static String Input() {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		String buf = "";
		try {
			buf = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buf;
	}




	//[filename]というファイルにLength個の要素がある[OriginalData]という配列と
	//追加分の配列[PlusData]を書き込む．flag=[質問の答え]で新たな質問の要素を追加する
	public static void write(String filename,String[][] OriginalData,String[] PlusData,String Result,int Length,String flag){
		try{
			File file = new File(filename);
			if (checkBeforeWritefile(file)){
				FileOutputStream fos = new FileOutputStream(file);
				OutputStreamWriter osw = new OutputStreamWriter(fos,"MS932");
				PrintWriter pw = new PrintWriter(osw);

				//元々あるデータ
				for(int i = 0;i<Length;i++){
					for(int j = 0;j<OriginalData[i].length;j++){
						//最後はカンマはいらない処理
						if(flag != ""){
							if(j == OriginalData[i].length-1){
								pw.print(OriginalData[i][j]);
							}else if(j == OriginalData[i].length-2){
								pw.print(OriginalData[i][j]+",わからない,");
							}else{
								pw.print(OriginalData[i][j]+",");
							}
						}else{
							pw.print(OriginalData[i][j]);
							if(j == OriginalData[i].length-1){
							}else{
								pw.print(",");
							}
						}
					}
					pw.println();
				}

				//追加分のデータ
				pw.print((Length+1)+",");
				for(int i = 0;i<PlusData.length;i++){
					pw.print(PlusData[i]+",");
				}
				if(flag != ""){
					pw.print(flag+",");
				}
				pw.print(Result);

				pw.close();
			}else{
				System.out.println("ファイルに書き込めません");
			}
		}catch(IOException e){
			System.out.println(e);
		}
	}




	//書き込みのエラーに使うメソッド
	private static boolean checkBeforeWritefile(File file){
		if (file.exists()){
			if (file.isFile() && file.canWrite()){
				return true;
			}
		}
		return false;
	}



	//([ﾌｧｲﾙの名前],[元々のﾃﾞｰﾀ],[追加したいデータ],[追加したい質問],[追加したい名前],[配列の縦の長さ])
	public static void write2(String filename,String[][] OriginalData,String[] PlusData,String Result,String Cre,int Length,boolean Flag){
		try{
			File file = new File(filename);
			boolean SomeWordFlag = false;

			//追加したい名前があるかどうかの確認
			for(int i = 0;i<OriginalData[Length-1].length;i++){
				if(Cre.equals(OriginalData[Length-1][i])){
					SomeWordFlag = true;
				}
			}

			//PlusData[PlusData.length-1] = Result;
			if (checkBeforeWritefile(file)){
				FileOutputStream	fos = new FileOutputStream(file);
				OutputStreamWriter	osw = new OutputStreamWriter(fos,"MS932");
				PrintWriter			pw	= new PrintWriter(osw);

				//元々あるデータ
				for(int i = 0;i<Length-1;i++){
					pw.print(i+1+",");
					for(int j = 1;j<OriginalData[i].length;j++){
						//最後はカンマはいらない処理
						pw.print(OriginalData[i][j]);
						if(j == OriginalData[i].length-1){
						}else{
							pw.print(",");
						}
					}
					pw.println();
				}

				if(Flag == true){
					//追加のデータの書き込み(最後から二番目の行)
					pw.print(Length+",");
					pw.print(Result+",");
					for(int i = 2;i<OriginalData[0].length;i++){	//最初の質問を流用する
						pw.print(OriginalData[0][i]);
						if(i == OriginalData[0].length-1){
						}else{
							pw.print(",");
						}
					}
					pw.println();

					//最後の『結果』の書き込み(最後の行)
					pw.print(Length+1+",");
					for(int i = 1;i<OriginalData[Length-1].length;i++){
						pw.print(OriginalData[Length-1][i]);
						if(i == OriginalData[Length-1].length-1){
							if(SomeWordFlag != true){
								pw.print(","+Cre);
							}
						}else{
							pw.print(",");
						}
					}
				}else{
					//最後の『結果』の書き込み(最後の行)
					pw.print(Length+",");
					for(int i = 1;i<OriginalData[Length-1].length;i++){
						pw.print(OriginalData[Length-1][i]);
						if(i == OriginalData[Length-1].length-1){
							if(SomeWordFlag != true){
								pw.print(","+Cre);
							}
						}else{
							pw.print(",");
						}
					}
				}
				pw.close();
			}else{
				System.out.println("ファイルに書き込めません");
			}
		}catch(IOException e){
			System.out.println(e);
		}
	}




	//正しく入力されているか確認する
	public static int Input_confirmation(int a,int b){
		int input = 0;
		while(true){
			input = Integer.parseInt(Sub.Input());
			if(input > a && input<b){	break;
			}else{	System.out.println("入力された値は正しくありません");	}
		}
		return input;
	}
}


