package com.example.backend.dataseed;

import com.example.backend.domain.container.Container;
import com.example.backend.domain.environmentVariable.EnvironmentVariable;
import com.example.backend.domain.user.User;
import com.example.backend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ContainerRepository containerRepository;
    @Autowired
    EnvironmentVariableRepository environmentVariableRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadData();
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
        container1.setSubDomain("backend");
        container1.setName("Backend app");
        container1.setDockerImage("docker.io/me/backend:latest");
        container1.setExposedPort(8080);
        user1.addContainer(container1);

        containerRepository.save(container1);

        Container container2 = new Container();
        container2.setName("Frontend app");
        container2.setSubDomain("frontend");
        container2.setDockerImage("docker.io/me/frontend:latest");
        container2.setExposedPort(3000);
        user1.addContainer(container2);

        containerRepository.save(container2);

        Container container3 = new Container();
        container3.setName("Database app");
        container3.setSubDomain("postgres");
        container3.setDockerImage("docker.io/me/postgres:latest");
        container3.setExposedPort(3000);
        user1.addContainer(container3);

        containerRepository.save(container3);

        // Environment Variables
        EnvironmentVariable env1 = new EnvironmentVariable();
        env1.setKey("key");
        env1.setValue("value");
        container1.addEnvironmentVariable(env1);

        environmentVariableRepository.save(env1);
    }
}
