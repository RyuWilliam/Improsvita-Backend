package co.improsvita.domain.service;

import co.improsvita.domain.model.Client;
import co.improsvita.domain.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAllClients() {
        return clientRepository.getAll();
    }

    public Client getClientById(Integer id) {
        return clientRepository.getById(id);
    }

    public Client getClientByName(String name) {
        return clientRepository.getByName(name);
    }

    public Client createClient(Client client) {
        assertEmailAvailable(client.getEmail(), null);
        return clientRepository.save(client);
    }

    public Client updateClient(Client client) {
        assertEmailAvailable(client.getEmail(), client.getId());
        return clientRepository.save(client);
    }

    public void deleteClientById(Integer id) {
        clientRepository.deleteById(id);
    }

    /**
     * La unicidad del email es una regla de negocio, así que vive en el servicio:
     * lanzarla desde un bean @Repository haría que Spring la envuelva en una
     * DataAccessException y el mensaje se perdería.
     */
    private void assertEmailAvailable(String email, Integer ownId) {
        if (email == null || email.isBlank()) {
            return;
        }
        Client existing = clientRepository.getByEmail(email);
        if (existing != null && !existing.getId().equals(ownId)) {
            throw new IllegalStateException("Ya existe un cliente con el email " + email);
        }
    }
}
