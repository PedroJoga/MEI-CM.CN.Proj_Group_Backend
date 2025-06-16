package com.example.backend.dataseed;

import com.example.backend.domain.container.Container;
import com.example.backend.domain.environmentVariable.EnvironmentVariable;
import com.example.backend.domain.user.User;
import com.example.backend.dto.ContainerResponseDTO;
import com.example.backend.repositories.*;
import com.example.backend.service.KubernetesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ContainerRepository containerRepository;
    @Autowired
    EnvironmentVariableRepository environmentVariableRepository;
    @Autowired
    KubernetesService kubernetesService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadData();
        startSavedContainers();
    }

    private void loadData() {
        // if database is empty seed database
        if (
                userRepository.count() != 0 ||
                containerRepository.count() != 0 ||
                environmentVariableRepository.count() != 0
        ) {
            return;
        }

        User user1 = new User();
        user1.setUserPhotoLink("https://github.com/PedroJoga.png");
        user1.setUsername("pedro");
        user1.setEmail("pedro@mail.com");
        user1.setPassword(passwordEncoder.encode("123"));

        User user2 = new User();
        user2.setUserPhotoLink("https://github.com/PedroJoga.png");
        user2.setUsername("joel");
        user2.setEmail("joel@mail.com");
        user2.setPassword(passwordEncoder.encode("123"));

        userRepository.save(user1);
        userRepository.save(user2);

        // Containers
        Container container1 = new Container();
        container1.setSubDomain("pedroapp");
        container1.setName("Backend app");
        container1.setDockerImage("docker.io/nginx");
        container1.setExposedPort(80);
        user1.addContainer(container1);

        containerRepository.save(container1);

        // Environment Variables
        EnvironmentVariable env1 = new EnvironmentVariable();
        env1.setKey("key");
        env1.setValue("value");
        container1.addEnvironmentVariable(env1);

        environmentVariableRepository.save(env1);
    }

    @Transactional(readOnly = true)
    private void startSavedContainers() {
        // Auto start containers on API startup
        List<Container> containers = containerRepository.findAll();
        for (Container container: containers) {
            try {
                kubernetesService.addContainer(new ContainerResponseDTO(
                        container.getId(),
                        container.getSubDomain(),
                        container.getName(),
                        container.getDockerImage(),
                        container.getExposedPort()
                ));

            } catch (Exception e) {
                System.out.println("ERROR: FAILED TO START CONTAINER: " + e.getMessage());
                continue;
            }
        }
    }
}
