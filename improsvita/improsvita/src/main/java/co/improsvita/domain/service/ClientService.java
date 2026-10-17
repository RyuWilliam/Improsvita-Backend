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
        return clientRepository.save(client);
    }

    public Client updateClient(Client client) {
        return clientRepository.save(client);
    }

    public void deleteClientById(Integer id) {
        clientRepository.deleteById(id);
    }
}
