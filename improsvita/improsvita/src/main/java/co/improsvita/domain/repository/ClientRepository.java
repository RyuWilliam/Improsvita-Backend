package co.improsvita.domain.repository;

import co.improsvita.domain.model.Client;

import java.util.List;

public interface ClientRepository {
    List<Client> getAll();
    Client getById(Integer id);
    Client getByName(String name);
    Client getByEmail(String email);
    Client save(Client client);
    void deleteById(Integer id);
}
