import java.util.ArrayList;
import java.util.List;

public class Pet {
    private String nome;
    private String especie;
    private String raca;
    private int idade;
    private Tutor tutor;

    public Pet(String nome, String especie, String raca, int idade, Tutor tutor) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.tutor = tutor;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getEspecie() {
        return especie;
    }

    public String getRaca() {
        return raca;
    }

    public int getIdade() {
        return idade;
    }

    public Tutor getTutor() {
        return tutor;
    }

    // Static list for simulation
    private static List<Pet> pets = new ArrayList<>();

    // CRUD methods
    public static void adicionar(Pet pet) {
        pets.add(pet);
    }

    public static List<Pet> listar() {
        return new ArrayList<>(pets);
    }

    public static Pet buscarPorNome(String nome) {
        for (Pet p : pets) {
            if (p.getNome().equals(nome)) {
                return p;
            }
        }
        return null;
    }

    public static boolean editar(String nome, Pet novoPet) {
        Pet pet = buscarPorNome(nome);
        if (pet != null) {
            pets.remove(pet);
            pets.add(novoPet);
            return true;
        }
        return false;
    }

    public static boolean excluir(String nome) {
        Pet pet = buscarPorNome(nome);
        if (pet != null) {
            pets.remove(pet);
            return true;
        }
        return false;
    }
}
