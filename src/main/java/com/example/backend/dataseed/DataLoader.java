package com.example.backend.dataseed;

import com.example.backend.domain.comment.Comment;
import com.example.backend.domain.response.Response;
import com.example.backend.domain.user.User;
import com.example.backend.repositories.CommentRepository;
import com.example.backend.repositories.ResponseRepository;
import com.example.backend.repositories.UserRepository;
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
    CommentRepository commentRepository;
    @Autowired
    ResponseRepository responseRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadData();
    }

    private void loadData() {
        // if database is empty seed database
        if (userRepository.count() != 0 || commentRepository.count() != 0 || responseRepository.count() != 0) {
            return;
        }

        User user1 = new User();
        user1.setUserPhotoLink("https://github.com/PedroJoga.png");
        user1.setUsername("pedro");
        user1.setEmail("pedro@mail.com");
        user1.setPassword(passwordEncoder.encode("123"));

        User user2 = new User();
        user2.setUserPhotoLink("https://github.com/PedroJoga.png");
        user2.setUsername("joao");
        user2.setEmail("joao@mail.com");
        user2.setPassword(passwordEncoder.encode("123"));

        userRepository.save(user1);
        userRepository.save(user2);

        Comment comment1 = new Comment();
        comment1.setText("Have you ever noticed how we only realize a moment was truly happy after it's already gone?");
        comment1.setUser(user1);
        comment1.setAnonymous(true);

        Comment comment2 = new Comment();
        comment2.setText("Sometimes silence is the best answer, but it's hard to know when to use it.");
        comment2.setUser(user1);

        Comment comment3 = new Comment();
        comment3.setText("If everything is a matter of perspective, does 'right' and 'wrong' really exist?");
        comment3.setUser(user1);

        Comment comment4 = new Comment();
        comment4.setText("Some connections happen so naturally, it feels like they were written somewhere long before we met.");
        comment4.setUser(user2);
        comment4.setAnonymous(true);

        Comment comment5 = new Comment();
        comment5.setText("Isn’t it strange how some days feel heavier than others, even when nothing’s really different?");
        comment5.setUser(user2);

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);
        commentRepository.save(comment4);
        commentRepository.save(comment5);

        Response response1 = new Response();
        response1.setText("Yeah, it's like nostalgia adds value that we didn't see when we were actually living it.");
        response1.setUser(user2);
        response1.setAnonymous(true);
        response1.setComment(comment1);

        Response response2 = new Response();
        response2.setText("Maybe it's because we’re too busy chasing the next thing to fully appreciate the now.");
        response2.setUser(user2);
        response2.setComment(comment1);

        Response response3 = new Response();
        response3.setText("True. Silence can speak volumes… but it can also be misunderstood if timed wrong.");
        response3.setUser(user2);
        response3.setComment(comment2);

        Response response4 = new Response();
        response4.setText("Exactly, like meeting an old soul again—some people just make sense instantly.");
        response4.setUser(user1);
        response4.setComment(comment4);

        Response response5 = new Response();
        response5.setText("Yeah, it’s like the weight isn’t in the day itself, but in how we’re carrying it.");
        response5.setUser(user1);
        response5.setAnonymous(true);
        response5.setComment(comment5);

        responseRepository.save(response1);
        responseRepository.save(response2);
        responseRepository.save(response3);
        responseRepository.save(response4);
        responseRepository.save(response5);
    }
}
