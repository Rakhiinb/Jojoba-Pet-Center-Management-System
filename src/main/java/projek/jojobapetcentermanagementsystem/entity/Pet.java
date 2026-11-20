package projek.jojobapetcentermanagementsystem.entity;

/** Satu baris tabel pets (hewan milik satu customer). */
public class Pet extends BaseEntity implements Searchable {

    private int customerId;
    private String name;
    private String species;
    private String breed;
    private String gender;

    public Pet() {
    }

    /** Constructor tanpa id: untuk data baru yang belum disimpan. */
    public Pet(int customerId, String name, String species, String breed, String gender) {
        this.customerId = customerId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.gender = gender;
    }

    /** Constructor dengan id: untuk data yang dibaca dari database. */
    public Pet(int id, int customerId, String name, String species, String breed, String gender) {
        super(id);
        this.customerId = customerId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.gender = gender;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String key = keyword.trim().toLowerCase();
        return contains(name, key) || contains(species, key) || contains(breed, key);
    }

    private static boolean contains(String text, String lowerKey) {
        return text != null && text.toLowerCase().contains(lowerKey);
    }

    @Override
    public String toString() {
        return name + " (" + species + ")";
    }
}
