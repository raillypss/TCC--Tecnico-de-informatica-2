import java.util.ArrayList;
import java.util.List;

public class Consulta {
    private Pet pet;
    private String dataConsulta;
    private String motivo;
    private String observacoes;

    public Consulta(Pet pet, String dataConsulta, String motivo, String observacoes) {
        this.pet = pet;
        this.dataConsulta = dataConsulta;
        this.motivo = motivo;
        this.observacoes = observacoes;
    }

    // Getters
    public Pet getPet() {
        return pet;
    }

    public String getDataConsulta() {
        return dataConsulta;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    // Static list for simulation
    private static List<Consulta> consultas = new ArrayList<>();

    // CRUD methods
    public static void adicionar(Consulta consulta) {
        consultas.add(consulta);
    }

    public static List<Consulta> listar() {
        return new ArrayList<>(consultas);
    }

    public static List<Consulta> listarPorPet(Pet pet) {
        List<Consulta> result = new ArrayList<>();
        for (Consulta c : consultas) {
            if (c.getPet().equals(pet)) {
                result.add(c);
            }
        }
        return result;
    }

    public static boolean editar(String petNome, String dataConsulta, Consulta novaConsulta) {
        for (int i = 0; i < consultas.size(); i++) {
            Consulta c = consultas.get(i);
            if (c.getPet().getNome().equals(petNome) && c.getDataConsulta().equals(dataConsulta)) {
                consultas.set(i, novaConsulta);
                return true;
            }
        }
        return false;
    }

    public static boolean excluir(String petNome, String dataConsulta) {
        for (Consulta c : consultas) {
            if (c.getPet().getNome().equals(petNome) && c.getDataConsulta().equals(dataConsulta)) {
                consultas.remove(c);
                return true;
            }
        }
        return false;
    }
}
