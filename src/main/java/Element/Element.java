package Element;

public class Element {
int itemId;
int groupId;

    public int getItemId() {
        return itemId;
    }

    public int getGroupId() {
        return groupId;
    }

    public Element(int itemId, int groupId) {
        this.groupId = groupId;
        this.itemId = itemId;
    } 
    
    public void  elementPrint(int itemId, int groupId){
        System.out.println("id " + itemId + " group " + groupId);
        }
}