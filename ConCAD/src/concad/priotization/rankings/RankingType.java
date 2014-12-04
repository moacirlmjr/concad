package concad.priotization.rankings;

public enum RankingType {
	SMELL_RELEVANCE("Anomaly Relevance"), RELEVANCE_HISTORY_AND_SCENARIOS("Relevance, History, and Scenarios"), SMELL_RELEVANCE_AND_HISTORY("Anomaly Relevance and history");
	
	private String name; 
	
	RankingType(String name) {
        this.name = name;
    }
 
    public String getName() {
        return name;
    }
}
