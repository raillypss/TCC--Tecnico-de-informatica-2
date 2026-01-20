import java.util.ArrayList;
import java.util.List;

public class Tutor {
    private String nome;
    private String cpf;
    private String telefone;
    private String endereco;

    public Tutor(String nome, String cpf, String telefone, String endereco) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    // Static list for simulation
    private static List<Tutor> tutores = new ArrayList<>();

    // CRUD methods
    public static void adicionar(Tutor tutor) {
        tutores.add(tutor);
    }

    public static List<Tutor> listar() {
        return new ArrayList<>(tutores);
    }

    public static Tutor buscarPorNome(String nome) {
        for (Tutor t : tutores) {
            if (t.getNome().equals(nome)) {
                return t;
            }
        }
        return null;
    }

    public static boolean editar(String nome, Tutor novoTutor) {
        Tutor tutor = buscarPorNome(nome);
        if (tutor != null) {
            tutores.remove(tutor);
            tutores.add(novoTutor);
            return true;
        }
        return false;
    }

    public static boolean excluir(String nome) {
        Tutor tutor = buscarPorNome(nome);
        if (tutor != null) {
            tutores.remove(tutor);
            return true;
        }
        return false;
    }
}
