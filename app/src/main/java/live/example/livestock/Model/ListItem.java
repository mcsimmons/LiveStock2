package live.example.livestock.Model;

public class ListItem {
    private String name;
    private String description;
    private int id;

    public ListItem(int id,String name, String description) {
    this.name = name;
    this.description = description;
    this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId(){return id;}

    public void setID(int id){this.id = id;}




}
