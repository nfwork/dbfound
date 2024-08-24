package dbfound.test.entity;

public enum Role{
    ADMIN(1),
    STUDENT(2);

    final Integer value;

    Role(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
}