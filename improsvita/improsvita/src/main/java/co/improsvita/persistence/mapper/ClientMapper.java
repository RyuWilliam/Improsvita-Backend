package co.improsvita.persistence.mapper;

import co.improsvita.domain.model.Client;
import co.improsvita.persistence.entities.ClientEntity;

public class ClientMapper {

    private ClientMapper() {
    }

    public static Client toDomain(ClientEntity entity) {
        if (entity == null) return null;
        Client client = new Client();
        client.setId(entity.getClientId());
        client.setName(entity.getName());
        client.setPhone(entity.getPhone());
        client.setEmail(entity.getEmail());
        client.setActive(entity.getActive());
        return client;
    }

    public static ClientEntity toEntity(Client domain) {
        if (domain == null) return null;
        ClientEntity entity = new ClientEntity();
        entity.setClientId(domain.getId());
        entity.setName(domain.getName());
        entity.setPhone(domain.getPhone());
        entity.setEmail(domain.getEmail());
        entity.setActive(domain.getActive());
        return entity;
    }
}
