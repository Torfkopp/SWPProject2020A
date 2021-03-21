package de.uol.swp.common.user;

public class DummyDTO implements Dummy {

    private static int idCounter = 0;
    private final int id;

    public DummyDTO() {
        this(++idCounter);
    }

    public DummyDTO(int id){
        this.id = id;
    }

    @Override
    public int compareTo(UserOrDummy o) {
        Integer id_obj = id; // compareTo is only defined on the wrapper class, so we make one here
        if (o instanceof Dummy) return id_obj.compareTo(o.getID());
        else return 1;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof UserOrDummy)
            return compareTo((UserOrDummy)o) == 0;
        return false;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getUsername() {
        return "Dummy" + id;
    }

    @Override
    public String toString() {
        return "(D) " + getUsername();
    }
}
