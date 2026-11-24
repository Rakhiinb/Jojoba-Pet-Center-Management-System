package projek.jojobapetcentermanagementsystem.model;

/**
 * Hewan + nama pemilik, hasil JOIN pets dan customers.
 * Hanya untuk ditampilkan (read-only), bukan baris tabel.
 */
public class PetModel {

    private final int id;
    private final String name;
    private final String species;
    private final String gender;
    private final String ownerName;

    public PetModel(int id, String name, String species, String gender, String ownerName) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.gender = gender;
        this.ownerName = ownerName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public String getGender() {
        return gender;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public String toString() {
        return name + " (" + ownerName + ")";
    }
}
