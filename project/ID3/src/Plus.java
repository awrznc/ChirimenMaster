import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;


public class Plus {
	public static void MM(){
		//System.out.println("ok");

		String filename = "tiriran.txt";

		ArrayList<String[]> DataList = new ArrayList<String[]>();
		String DataName[] = {"","",""};	//["データ名","データ数","データ番号"]


		//ファイルを読み込む
		try {
			FileInputStream fs = new FileInputStream(filename);
			InputStreamReader isr = new InputStreamReader(fs, "MS932");
			BufferedReader br = new BufferedReader(isr);

            String str = br.readLine();

			while(str != null){
				DataList.add(str.split(","));		//","ごとに区切り配列に格納する
				str = br.readLine();				//配列をリストに格納する
			}
			br.close();							//ファイルを閉じる
		} catch( FileNotFoundException e ){	//エラー処理
            System.out.println(e);				//エラーメッセージ
		} catch (IOException e){			//エラー処理
		    e.printStackTrace();				//エラーメッセージ
		}

		//ファイルの表示
		for(int i = 0;i<DataList.size();i++){
			DataName = DataList.get(i);
			for(int j = 0;j<2;j++){
				//System.out.print(DataName[j]+",");
			}
			//System.out.println("");
		}


		//リストのちりめん数を全て足し合わせる
		int DataNum = 0;

		for(int i=0;i<DataList.size();i++){
			DataName = DataList.get(i);
			DataNum += Integer.parseInt(DataName[DataName.length-2]);
		}
		System.out.println(DataNum);


		//乱数で生物を選択する
		//Randomクラスのインスタンス化
		Random rnd = new Random();

		int ran = rnd.nextInt(DataNum);
		System.out.println(ran);

		//生成した乱数からちりめんの名前を求める
		DataNum = 0;							//データNumの初期化
		String RandName1 = "";					//乱数で求めるちりめんの名前
		String RandName2 = "";					//乱数で求めるちりめんの名前
		for(int i=0;i<DataList.size();i++){		//
			DataName = DataList.get(i);
			DataNum += Integer.parseInt(DataName[DataName.length-2]);
			if(ran <= DataNum){						//もしも乱数の値を上回ったら
				RandName1 = DataName[2];					//そこの値のちりめんが選ばれる
				RandName2 = DataName[0];
				break;
			}
		}
		System.out.println(RandName1+":"+RandName2);
	}
}
