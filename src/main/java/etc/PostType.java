package etc;

public enum PostType {

    Status(0),
    Picture(1);
    
    private int value;
    
    private PostType(int value) {
        this.value = value;
    }
}
