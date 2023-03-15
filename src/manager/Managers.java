package manager;

public class Managers {

    private HistoryManager historyManager = Managers.getDefaultHistory();
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
