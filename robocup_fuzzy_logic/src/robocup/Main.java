package robocup;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		String jadeArgs[] = { "-agents", "RC:robocup.RoboCup; A0:sma.PlayerAgent; A1:sma.PlayerAgent; A2:sma.PlayerAgent; A3:sma.PlayerAgent; A4:sma.PlayerAgent; A5:sma.PlayerAgent; A6:sma.PlayerAgent; A7:sma.PlayerAgent; A8:sma.PlayerAgent; A9:sma.PlayerAgent" };
		jade.Boot.main(jadeArgs);
	}
}
