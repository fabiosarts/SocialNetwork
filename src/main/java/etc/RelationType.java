package etc;

public enum RelationType {

    Blocked(0),
    Request(1),
    Friends(2),
    None (3);
    
    private int value;
    
    private RelationType(int value) {
        this.value = value;
    }
}
