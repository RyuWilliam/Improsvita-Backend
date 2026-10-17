package co.improsvita.web.controller;

import co.improsvita.domain.model.Client;
import co.improsvita.domain.service.ClientService;
import co.improsvita.web.dto.ClientRequest;
import co.improsvita.web.dto.ClientResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        List<ClientResponse> clients = clientService.getAllClients()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Integer id) {
        Client client = clientService.getClientById(id);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(client));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ClientResponse> getClientByName(@PathVariable String name) {
        Client client = clientService.getClientByName(name);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(client));
    }

    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody ClientRequest request) {
        try {
            Client client = new Client();
            client.setName(request.getName());
            client.setPhone(request.getPhone());
            client.setEmail(request.getEmail());
            client.setActive(true);

            Client created = clientService.createClient(client);
            return ResponseEntity.ok(toResponse(created));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Integer id, @RequestBody ClientRequest request) {
        Client existing = clientService.getClientById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setName(request.getName());
        existing.setPhone(request.getPhone());
        existing.setEmail(request.getEmail());

        try {
            Client updated = clientService.updateClient(existing);
            return ResponseEntity.ok(toResponse(updated));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientById(@PathVariable Integer id) {
        clientService.deleteClientById(id);
        return ResponseEntity.noContent().build();
    }

    private ClientResponse toResponse(Client client) {
        ClientResponse response = new ClientResponse();
        response.setId(client.getId());
        response.setName(client.getName());
        response.setPhone(client.getPhone());
        response.setEmail(client.getEmail());
        response.setActive(client.getActive());
        return response;
    }
}
