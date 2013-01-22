package analysis;

public class AnalyzerFactory {
	public static Analyzer createAnalyzer(){
		return new YahooAnalyzer();
	}
}
