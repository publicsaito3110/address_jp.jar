package org;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @author saito
 *
 */
public class Address_jp {


	//-----------
	//フィールド
	//-----------

	//住所1 ex)東京
	String address1;
	//住所2 ex)新宿区
	String address2;
	//住所3 ex)西新宿
	String address3;
	//フリガナ1 ex)トウキョウト
	String kana1;
	//フリガナ2 ex)シンジュクク
	String kana2;
	//フリガナ3 ex)ニシシンジュク
	String kana3;
	//都道府県コード
	String prefCode;
	//郵便番号
	String zipCode;


	//-----
	//定数
	//-----
	private static final String ZIP_CODE_API_URL = "https://zip-cloud.appspot.com/api/search?zipcode=";

	//-----------------
	//コンストラクタ―
	//-----------------
	public Address_jp(int zipCode) {

		BufferedReader bufferReader = null;

		try {
			//1.接続するための設定をする

			// URL に対して openConnection メソッドを呼び出すし、接続オブジェクトを生成
			URL url = new URL(ZIP_CODE_API_URL + zipCode);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

			// HttpURLConnectionの各種設定
			//HTTPのメソッドをGETに設定
			httpConn.setRequestMethod("GET");
			//リクエストボディへの書き込みを許可
			httpConn.setDoInput(true);
			//レスポンスボディの取得を許可
			httpConn.setDoOutput(true);
			//リクエスト形式をJsonに指定
			httpConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

			// 2.接続を確立する
			httpConn.connect();

			// 3.リクエスとボディに書き込みを行う
			//HttpURLConnectionからOutputStreamを取得し、json文字列を書き込む
			PrintStream printStream = new PrintStream(httpConn.getOutputStream());
			printStream.close();


			// 4.レスポンスを受け取る
			//正常終了時はHttpStatusCode 200が返ってくる
			if (httpConn.getResponseCode() != 200) {
				throw new UnsupportedException("HttpStatus is error");
			}

			//HttpURLConnectionからInputStreamを取得し、読み出す
			bufferReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));

			//レスポンスされたJsonから必要な値のみ抽出
			StringBuilder jsonSb = new StringBuilder();
			String line;
			int lineCount = 1;

			while ((line = bufferReader.readLine()) != null) {

				if (lineCount < 4) {
					lineCount++;
					continue;
				}
				if (14 <= lineCount) {
					break;
				}

				jsonSb.append(line);//4-13
				lineCount++;
			}

			//InputStreamを閉じる
			bufferReader.close();

			//sbが正常に終わらなかったとき
			if (lineCount != 14) {
				throw new UnsupportedException("zip-code is not found");
			}


			//Json形式をを判別
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("js");

			// JavaScriptの実行
			Object obj = engine.eval(String.format("(%s)", jsonSb.toString()));
			// リフレクションでScriptObjectMirrorクラスの取得
			Class scriptClass = Class.forName("jdk.nashorn.api.scripting.ScriptObjectMirror");
			// リフレクションでキーセットを取得
			Object[] keys = ((java.util.Set)obj.getClass().getMethod("keySet").invoke(obj)).toArray();
			// リフレクションでgetメソッドを取得
			Method method_get = obj.getClass().getMethod("get", Class.forName("java.lang.Object"));

			//フィールドにセット
			//address1
			Object keyAddress1 = keys[0];
			Object valAddress1 = method_get.invoke(obj, keyAddress1);
			this.address1 = valAddress1.toString();
			//address2
			Object keyAddress2 = keys[1];
			Object valAddress2 = method_get.invoke(obj, keyAddress2);
			this.address2 = valAddress2.toString();
			//address3
			Object keyAddress3 = keys[2];
			Object valAddress3 = method_get.invoke(obj, keyAddress3);
			this.address3 = valAddress3.toString();
			//kana1
			Object keyKana1 = keys[3];
			Object valKana1 = method_get.invoke(obj, keyKana1);
			this.kana1 = valKana1.toString();
			//kana2
			Object keyKana2 = keys[4];
			Object valKana2 = method_get.invoke(obj, keyKana2);
			this.kana2 = valKana2.toString();
			//kana3
			Object keyKana3 = keys[5];
			Object valKana3 = method_get.invoke(obj, keyKana3);
			this.kana3 = valKana3.toString();
			//prefCode
			Object keyPrefCode = keys[6];
			Object valPrefCode = method_get.invoke(obj, keyPrefCode);
			this.prefCode = valPrefCode.toString();
			//zipCode
			Object keyZipCode = keys[7];
			Object valZipCode = method_get.invoke(obj, keyZipCode);
			this.zipCode = valZipCode.toString();

		} catch(Exception e) {
			e.printStackTrace();
			//bufferReaderの接続を解除
			if (bufferReader != null) {
				try {
					bufferReader.close();
					System.exit(1);
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
		} finally {
			//bufferReaderの接続を解除
			if (bufferReader != null) {
				try {
					bufferReader.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}


	//---------
	//メソッド
	//---------

	/**
	 * 住所1を取得
	 *
	 * @param void
	 * @return 住所1<br>
	 * ex) 東京都
	 */
	public String getAddress1() {
		return address1;
	}


	/**
	 * 住所2を取得
	 *
	 * @param void
	 * @return 住所2<br>
	 * ex) 新宿区
	 */
	public String getAddress2() {
		return address2;
	}


	/**
	 * 住所3を取得
	 *
	 * @param void
	 * @return 住所3<br>
	 * ex) 西新宿
	 */
	public String getAddress3() {
		return address3;
	}


	/**
	 * 住所1のフリガナを取得
	 *
	 * @param void
	 * @return 住所1のフリガナ<br>
	 * ex) トウキョウト
	 */
	public String getKana1() {
		return kana1;
	}


	/**
	 * 住所2のフリガナを取得
	 *
	 * @param void
	 * @return 住所2のフリガナ<br>
	 * ex) シンジュクク
	 */
	public String getKana2() {
		return kana2;
	}


	/**
	 * 住所3のフリガナを取得
	 *
	 * @param void
	 * @return 住所3のフリガナ<br>
	 * ex) ニシシンジュク
	 */
	public String getKana3() {
		return kana3;
	}


	/**
	 * 都道府県コードを取得
	 *
	 * @param void
	 * @return 都道府県コード<br>
	 * ex) 東京→13
	 */
	public int getPrefCode() {
		return Integer.parseInt(prefCode);
	}


	/**
	 * 郵便番号を取得
	 *
	 * @param void
	 * @return 郵便番号<br>
	 * ex) 西新宿→1600023
	 */
	public int getZipCode() {
		return Integer.parseInt(zipCode);
	}


	/**
	 * 住所を取得
	 *
	 * @param void
	 * @return 住所<br>
	 * ex) 東京都新宿区西新宿
	 */
	public String getAddressAll() {
		return address1 + address2 + address3;
	}


	/**
	 * 住所(フリガナ)を取得
	 *
	 * @param void
	 * @return 住所(フリガナ)<br>
	 * ex) トウキョウトシンジュククニシシンジュク
	 */
	public String getAddressKana() {
		return kana1 + kana2 + kana3;
	}


	/**
	 * {Private Method}
	 *
	 * <p>
	 * 例外を強制的に実行し、コンソール出力<br>
	 * ex) Validation error, Can't used param etc...
	 * </p>
	 *
	 * @param massage
	 */
	private void executeException(String massage) {
		try {
			throw new UnsupportedException(massage);
		} catch(UnsupportedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}


/**
 * Exception Class
 */
class UnsupportedException extends Exception {

	/**
	 * {Constractor}
	 *
	 * <p>例外を強制的に発生</p>
	 *
	 * @param message
	 */
	public UnsupportedException(String message) {
		super(message);
	}
}