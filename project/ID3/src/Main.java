import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


public class Main {

	//入力：正解付き学習データD，特徴集合A
	private static String Data[][]	=	new String[1000][];		//data.txt用
	private static String DataX[][]	=	new String[1000][];		//no.txt用
	private static int DataLength[]	=	{0,0};					//{data.txtの行数 , no.txtの行数}


	//private static ArrayList<String> LearnData = new ArrayList<String>();
	//private static ArrayList<String> ConditionData = new ArrayList<String>();
	//ArrayList<String> QestionSentence = new ArrayList<String>();


	//テキストファイルの名前
	private static String DataName1 =	"tiridata.txt";
	private static String DataName2 =	"tirino.txt";

	//平均情報量
	private static double Entropy[][]	= {	{0},		//関数内の平均情報量
											{0,0,0}};	//独立変数ごとに分かれた平均情報量

	//データ比率の割合
	private static int Ratio[][] = new int[1000][];
									/*	= {	{5,4,5},	//outlook
											{4,6,4},	//temperature
											{7,7},		//humidiry
											{6,8},		//windy
											{9,5}};		//play*/
	private static int Ratio3[][] = new int[1000][];
									/*	= {	{2,3},
											{4,0},
											{3,2}};*/

	//ルートの探索に使う変数
	private static String Route[];						//木の通った箇所を記録する変数
	private static String word[];						//木の通った箇所を"仮に"記録する変数
	private static String word2[];						//ID3で使う変数
	private static String IrregularWord[];				//この配列に質問が入っていた場合，例外処理として計算に含まない

	//main
	public static void main(String args[]){
		//システムメソッドを実行
		System();
	}



	public static void System(){

		//mainで使う変数
		String	Qestion			= ""	;	//質問の番号
		int		QestionPoint	= 0		;	//配列に格納されている質問の場所
		int		Answer			= 0		;	//答えを代入する変数
		String	Result			= ""	;	//答えの単語
		int		ResultPoint		= 0		;	//これが(質問の数-1)であれば結果が出る

		//ArrayList<String> QestionSentence = new ArrayList<String>();


		//txtファイルを読み込む
		System.out.println("readメソッド実行");
		Data = read(DataName1,0);								//Data1(data.txt)からデータを配列に格納
		DataX = read(DataName2,1);								//Data2(no.txt)からデータを配列に格納


		//LearnData = read2(DataName1);

		//配列の初期化
		Route = new String[DataLength[1]-1];					//最後はプレイするかしないかなので
		Arrays.fill(Route, "NaN");								//Routeの要素をすべてNaNに初期化

		word = new String[DataLength[1]-1];						//最後はプレイするかしないかなので
		Arrays.fill(word, "NaN");								//wordの要素をすべてNaNに初期化

		IrregularWord = new String[DataLength[1]];
		Arrays.fill(IrregularWord, "NaN");

		Plus.MM();

		//ここから処理がメイン
		for(int j=0;j<10;j++){
			ResultPoint = 0;

			int loopcnt=0;
			boolean loopcntbreak = false;
			//ID3アルゴリズムで質問を探索
			while(true){
				int NullFlag = 0;
				try{
					Qestion = ID3(QestionPoint);
				}catch(ArrayIndexOutOfBoundsException e){				//AIOOBEの処理
					  System.out.println("配列の範囲を超えています");
				}catch(NullPointerException a){							//ぬるぽの処理
					System.out.println("ぬるぽ");
					NullFlag = 1;
				}
				if(Qestion == ""){	NullFlag = 1;}					//もしもQestionがカラだったらもう一周
				if(NullFlag == 0 || Qestion != ""){	break;	}		//問題がなければループを抜けて入力へ
				loopcnt++;
				if(loopcnt==10){
					loopcntbreak = true;
					break;
				}
			}


			//質問ループしてしまった場合の結果を出す処理
			//（矛盾する要素が存在した場合）
			if(loopcntbreak==true){
				ArrayList<Integer> Remain = new ArrayList<Integer>();		//要素がいくつあるかを入れるリスト
				ArrayList<Integer> RemainPoint = new ArrayList<Integer>();	//要素の座標を入れるリスト
				ArrayList<String> RemainWord = new ArrayList<String>();		//要素の単語を入れるリスト
				int RanP=0;
				int MaxRemain = 0;						//一番多い要素数

				for(int i = 0;i<Ratio[DataLength[1]-1].length;i++){		//レートの中身を探索する
					if(Ratio[DataLength[1]-1][i] != 0){						//もしも結果レートの値が0でなければ
						Remain.add(Ratio[DataLength[1]-1][i]);					//要素数を追加
						RemainPoint.add(i);										//座標を取得
						RemainWord.add(DataX[DataLength[1]-1][i+2]);			//単語を追加
						if(MaxRemain<=Ratio[DataLength[1]-1][i]){
							MaxRemain = Ratio[DataLength[1]-1][i];
						}
					}
				}
				//System.out.println(Remain+":"+RemainPoint+":"+RemainWord);
				//要素を厳選する処理
				for(int i=0;i<Remain.size();i++){
					if(MaxRemain > Remain.get(i)){				//もしMaxRemainより要素が小さければ
						Remain.remove(i);				//小さい要素は取り除く
						RemainPoint.remove(i);			//要素の座標も取り除く
						RemainWord.remove(i);
					}
				}
				//System.out.println(Remain+":"+RemainPoint+":"+RemainWord);
				//ここまでで厳選された要素の特徴
				//"多数決に従い，ケースが一番多いものだけが残っている"
				//それでも同じ場合は下記の処理で乱数を用いて出力する
				//System.out.println("ran:"+(int)(Math.random()*(Remain.size()+1)));
				RanP = (int)(Math.random()*(Remain.size()));
				Result = RemainWord.get(RanP);
				break;
			}

			//結果を出すための処理（重要）
			//残りの割合を探索し，0でない(答えの可能性がある)場合は
			//Resultに可能性がある答えを代入する.
			for(int i = 0;i<Ratio[DataLength[1]-1].length;i++){
				if(Ratio[DataLength[1]-1][i] == 0){				//割合が0の場合
					ResultPoint += 1;								//ResultPointを+1する
				}else{											//それ以外の場合
					Result = DataX[DataLength[1]-1][i+2];			//Resultに結果を代入する
				}
			}
			//System.out.println(ResultPoint+"=="+(Ratio[DataLength[1]-1].length-1));

			//IrregularWordの中のIrregularを探索する．(結果を除く)全ての要素がIrregularか調べる．
			int IrregularCount=0;
			for(int i = 0;i<IrregularWord.length;i++){
				if(IrregularWord[i].equals("Irregular")){
					IrregularCount++;
				}
			}

			//答えが確定しているか探索
			if(ResultPoint == Ratio[DataLength[1]-1].length-1){		//結果が確定した場合
				System.out.println("結果が出ました．");
				break;													//■探索から抜ける■
			}else if(IrregularCount == IrregularWord.length-1){		//これ以上特定できない場合
				System.out.println("これ以上特定できません．");
				break;													//■探索から抜ける■
			}else if(!Arrays.asList(Route).contains("NaN")){		//全ての質問に答えてしまった場合
				System.out.println("全て探索してしまいました");
				break;													//■探索から抜ける■
			}

			System.out.println(Qestion+" ?"+"	下の選択肢から番号を半角で入力してください．");

			//質問を質問と答えが格納された配列から探索
			for(int i = 0;i<DataLength[1]-1;i++){
				if(DataX[i][1].equals(Qestion)){
					QestionPoint = i;
				}
			}
			//System.out.println(QestionPoint);

			//質問の選択肢を表示
			for(int i = 2;i<DataX[QestionPoint].length;i++){
				System.out.print((i-1)+"."+DataX[QestionPoint][i]+",");
			}
			System.out.println();

			int cnt = 0;
			while(cnt ==0){
				Answer = Integer.parseInt(Sub.Input());
				if(Answer > 0 && Answer<6){
					break;
				}else{
					System.out.println("入力された値は正しくありません");
				}
			}
			System.out.println(DataX[QestionPoint][Answer+1]);

			word[QestionPoint] = DataX[QestionPoint][Answer+1];
			Route[QestionPoint] = word[QestionPoint];
		}




		//ここから最終処理
		System.out.println(Result);

		//正解の判定
		System.out.println("正解ですか？");
		System.out.println("1.Yes,2.No");

		//入力が正しいかの判断）
		int cnt = 0;
		while(cnt == 0){	Answer = Integer.parseInt(Sub.Input());
			if(Answer > 0 && Answer<3){	break;
			}else{	System.out.println("入力された値は正しくありません");	}
		}

		//質問と生物を追加する処理
		//解がNoだった場合の処理
		String	PlusCreature	= "";	//追加する生物
		String	PlusQestion		= "";	//追加する質問
		int		PlusFlag		= 0;	//追加するかのフラグ
		if(Answer == 2){
			System.out.println("それでは何という生物だったのでしょうか");
			PlusCreature = Sub.Input();
			System.out.println("どういう質問を加えたらよいでしょうか(フォーマット：[質問の内容]?)");
			PlusQestion = Sub.Input();
			Result = PlusCreature;
			PlusFlag = 1;
		}else{
			System.out.println("そうですか…");
		}

		//don't knowをNaNに変換
		for(int i=0;i<Route.length;i++){
			if(Route[i]=="NaN"){
				Route[i]="わからない";
			}
			System.out.println(Route[i]);
		}


		//書き込み処理
		if(PlusFlag == 1){//解がNoで質問を追加する場合

			//質問が既にあるか確認
			boolean existingflag = false;
			int select = 0;

			System.out.println("質問に対する答えを入力してください");
			//質問の選択肢を表示
			for(int j = 2;j<DataX[QestionPoint].length;j++){
				System.out.print((j-1)+"."+DataX[QestionPoint][j]+",");
			}
			System.out.println();
			select = Sub.Input_confirmation(0,DataX[QestionPoint].length-1);

			for(int i=0;i<DataLength[1];i++){
				if(DataX[i][1].equals(PlusQestion)){			//質問が既にある場合
					System.out.println();
					existingflag = true;
					Route[i]=DataX[QestionPoint][select+1];
					System.out.println("Route:"+Route[i]);
					break;
				}
			}
			if(existingflag==true){
				Sub.write(DataName1,Data,Route,Result,DataLength[0],"");
				Sub.write2(DataName2,DataX,DataX[0],PlusQestion,Result,DataLength[1],false);
			}else{
				Sub.write(DataName1,Data,Route,Result,DataLength[0],DataX[QestionPoint][select+1]);
				Sub.write2(DataName2,DataX,DataX[0],PlusQestion,Result,DataLength[1],true);
			}
		}else{
			Sub.write(DataName1,Data,Route,Result,DataLength[0],"");
		}
	}





	//ID3アルゴリズムを実行
	public static String ID3(int QestionPoint){

		//情報獲得量
		double GainNum[] = new double[100];
		ArrayList<Double> GainNumber = new ArrayList<Double>();	//いずれこれに変更

		//一番値が大きい情報獲得量
		double MaxGain = 0;

		word2 = new String[DataLength[1]-1];					//最後はプレイするかしないかなので
		//Arrays.fill(word2, "NaN");								//word2の要素をすべて０に初期化
		//word2=word;

		for(int i =0;i<word.length;i++){
			if(IrregularWord[i].equals("Irregular")){
				word[i] = "NaN";
			}
		}

		System.out.println("解析メソッド実行");
		Ratio = new int[DataLength[1]][];

		for(int i = 0;i<DataLength[1];i++){
			Ratio[i] = new int[DataX[i].length-2];
			for(int j = 0;j<DataX[i].length-2;j++){
				Ratio[i][j] = Analysis(DataLength[0],Data,i,DataX[i][j+2],word);		//割合を解析する
				System.out.print(Ratio[i][j]);
			}
			//System.out.println(" : "+ (DataX[i].length-2)+","+i+","+DataLength[1]);
			System.out.println();
		}

		System.out.println("");
		//System.out.println("平均情報量メソッド実行");
		Entropy[0][0] = Entropy2(Ratio[DataLength[1]-1],DataLength[0]);					//平均情報量の計算をする

		System.out.println();


		for(int i=0;i<Ratio.length-1;i++){									//最後はyes,no(結果)なのでlengthから-1する
		//for(int i=0;i<1;i++){

			//System.out.println("解析メソッド実行２");
			Ratio3 = new int[Ratio[i].length][DataX[DataLength[1]-1].length-2];

			//Ratio3に独立変数ごとの割合を格納します
			for(int j= 0 ;j<Ratio[i].length;j++){
				for(int h=0;h<Ratio3[j].length;h++){
					for(int k =0;k<word.length;k++){
						if(!(IrregularWord[k].equals("Irregular"))){
							word2[k] = word[k];
						}else{
							word2[k] = "NaN";
						}
						//word2[k] = word[k];
					}

					word2[i] = DataX[i][j+2];
					Ratio3[j][h] = Analysis(DataLength[0],Data,DataLength[1]-1,DataX[DataLength[1]-1][h+2],word2);
					//System.out.print(" , "+DataX[i][j+2]+":"+DataX[DataLength[1]-1][h+2]+" , ");
					//System.out.print(" , "+word2[i]+":"+DataX[DataLength[1]-1][h+2]+" ,");
					//StringArrayInvisible(word2);
				}
				//System.out.println("");
			}
			//System.out.println("平均情報量メソッド実行２");
			Entropy[1] = new double [Ratio3.length];
			for(int j=0;j<Ratio[i].length;j++){
				Entropy[1][j] = Entropy2(Ratio3[j],Ratio[i][j]);
			}

			//System.out.println("情報獲得量メソッド実行");
			GainNum[i] = Gain(Entropy,i,DataLength[0]);						//情報獲得量の計算をする
			//System.out.println("");
			//System.out.println(GainNum[i]);

			//IrregularWordの中のIrregularを探索する．(結果を除く)全ての要素がIrregularか調べる．
			int IrregularCount=0;
			for(int j = 0;j<IrregularWord.length;j++){
				if(IrregularWord[j].equals("Irregular")){
					IrregularCount++;
				}
			}

			//一番大きな情報獲得量を取得する処理
			if(MaxGain <= GainNum[i] && !(IrregularWord[i].equals("Irregular"))){	//一番大きい情報獲得量より情報獲得量が大きく，ｲﾚｷﾞｭﾗｰでない場合
				//System.out.println("Not Irregular");
				MaxGain = GainNum[i];
			}else if(IrregularCount==IrregularWord.length-1){							//全ての要素がIrregularな場合
				//System.out.println("exception!");
				MaxGain = GainNum[i];
			}
		}
		System.out.println("MaxGain : "+MaxGain);

		int GainPoint = 0;
		for(int i=0;i<IrregularWord.length-1;i++){
			System.out.print(i+",");
			if(GainNum[i] == MaxGain && !(IrregularWord[i].equals("Irregular"))){
				GainPoint = i;
			}
		}
		System.out.println();


		if(MaxGain == 0){								//一番大きな情報獲得量が0の場合
			IrregularWord[QestionPoint] = "Irregular";	//Irregularと表示
		}else{											//それ以外の処理
			System.out.println(DataX[GainPoint][1]);	//
		}

		//System.out.println("IrregularWordのなかみ");
		Sub.StringArrayInvisible(IrregularWord);

		//System.out.println("wordのなかみ");
		//StringArrayInvisible(word);

		//System.out.println("Routeのなかみ");
		//StringArrayInvisible(Route);

		System.out.println("GainPoint:"+GainPoint);

		//質問が返される
		if(MaxGain == 0){	//カラの場合はもう一周
			return "";//DataX[GainPoint][1];
		}else{
			return DataX[GainPoint][1];
		}
	}




//[]行ある[]という配列の[]列目に[word]という文字がどれだけあるか(尚，[]という文字を含む場合)
	public static int Analysis(int DataPointX,String[][] AnalysisData,int DataPointY,String word,String[] HoldWord){
		int WordCount = 0;
		int kyoka = 0;

		//配列を探索します
		for(int i=0;i<DataPointX;i++){							//一行ずつ探索
			kyoka = 0;
			if(AnalysisData[i][DataPointY+1].equals(word)){		//指定された列で同じ文字を見つけ

				for(int j=0;j<HoldWord.length;j++){				//他に文字が含まれているか繰り返しで探索
					if(	HoldWord[j].equals(AnalysisData[i][j+1]) ||		//HoldWordとデータが同じである場合
						HoldWord[j].equals("NaN")){					//HoldWordにNaNが入っている場合
						kyoka += 1;
					}
					//System.out.println(HoldWord[j]+"	==	"+AnalysisData[i][j+1]);
				}
				if(kyoka == AnalysisData[i].length-2){			//フラグの数がデータ数と同じならば
					WordCount += 1;									//カウントを1プラス
				}
				//System.out.println(AnalysisData[i][DataPointY+1]+"	==	"+word);
				//System.out.print(kyoka+",");
				//System.out.print("ok");
			}
			//System.out.println(" ::"+AnalysisData[i][DataPointY+1]+","+word+":: ");
		}
		//System.out.print(WordCount);
		return WordCount;
	}




	//平均情報量の計算(平均情報量を出したいデータ,全体の割合)
	public static double Entropy2(int[] abc ,double len){
		double Entropy	= 0;		//平均情報量
		double Length	= len;									//例題の数


		//logの計算を繰り返し行う
		for(int i = 0;i<abc.length;i++){
			//非数（NaNを0にしてあげる）
			if(Double.isNaN(-(abc[i]/Length) * (Math.log(abc[i]/Length)/Math.log(abc.length))) ||
				Double.isInfinite(-(abc[i]/Length) * (Math.log(abc[i]/Length)/Math.log(abc.length)))){
				Entropy += 0;
			}else{
				Entropy += -(abc[i]/Length) * (Math.log(abc[i]/Length)/Math.log(abc.length));
			}
			//System.out.print(" - "+(abc[i]/Length)+" * (Math.log("+ abc[i]/Length+ ")/Math.log("+abc.length+")),");
			//System.out.print(-(abc[i]/Length) * (Math.log(abc[i]/Length)/Math.log(abc.length))+",");
		}
		//中身がNaNの場合は0を返す．それ以外はそのまま返す
		if(Double.isNaN(Entropy)){
			System.out.println("0.0");
			Entropy = 0.0;
		}else{
			System.out.println(Entropy);
		}
		return Entropy;
	}




	//情報獲得量の計算
	public static double Gain(double[][] Ent,int Num,double Length){
		double Gain = 0;

		/*Gain	=	0.94;
		Gain	+=			-	0.357*(-(2.0/5.0)*(Math.log(2.0/5.0)/Math.log(2.0))-(3.0/5.0)*(Math.log(3.0/5.0)/Math.log(2.0)))
							-	0.286*(-(4.0/4.0)*(Math.log(4.0/4.0)/Math.log(2.0))-0)
							-	0.357*(-(3.0/5.0)*(Math.log(3.0/5.0)/Math.log(2.0))-(2.0/5.0)*(Math.log(2.0/5.0)/Math.log(2.0)));
		System.out.println(Gain);*/


		Gain = Ent[0][0];
		for(int i = 0;i<Ratio3.length;i++){
			Gain += -(Ratio[Num][i]/Length)	*	Ent[1][i];
			//System.out.println( Ent+":"+-(Ratio[Num][i]) + "/" + (Length)+" * "+Entropy[1][i]);
		}

		if(Double.isNaN(Gain)){
			//System.out.println("0.0");
			Gain = 0.0;
		}else{
			//System.out.println(Gain);
		}
		//System.out.println(Gain);
		return Gain;
	}




	//[filename]というファイルを読み込み，行数を配列xの[length]番目に格納する
	public static String[][] read(String filename,int length){
		String Data[][]=new String[1000][];
		try {
			FileInputStream fs = new FileInputStream(filename);
			InputStreamReader isr = new InputStreamReader(fs, "MS932");
			BufferedReader br = new BufferedReader(isr);

            String str = br.readLine();

			while(str != null){
				Data[DataLength[length]] = str.split(",");
				str = br.readLine();
				DataLength[length]++;
			}
			br.close();
		} catch( FileNotFoundException e ){
            System.out.println(e);
		} catch (IOException e){
		    e.printStackTrace();
		}
		System.out.println("fileread");

		for(int i = 0;i<DataLength[length];i++){
			for(int j = 0;j<Data[i].length;j++){
				System.out.print(Data[i][j]+",");
			}
			System.out.println("");
		}
		return Data;
	}
}