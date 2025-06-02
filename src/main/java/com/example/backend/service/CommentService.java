package com.example.backend.service;

import com.example.backend.domain.comment.Comment;
import com.example.backend.domain.user.User;
import com.example.backend.dto.CommentResponseDTO;
import com.example.backend.dto.SuggestionResponseDTO;
import com.example.backend.repositories.CommentRepository;
import com.example.backend.repositories.ResponseRepository;
import com.example.backend.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ResponseRepository responseRepository;
    @Autowired
    private UserRepository userRepository;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CommentService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public void addComment(String text, User user, boolean isAnonymous) {

        Comment comment = new Comment();
        comment.setText(text);
        comment.setUser(user);
        comment.setAnonymous(isAnonymous);

        commentRepository.save(comment);
    }

    public List<CommentResponseDTO> getCommentsByUser(User user) {
        List<Comment> comments = commentRepository.findByUser(user);

        return comments.stream()
                .map(comment -> new CommentResponseDTO(
                        comment.getId(),
                        comment.getText(),
                        comment.getUser().getUserPhotoLink(),
                        comment.getUser().getUsername(),
                        comment.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public CommentResponseDTO getRandomComment(User user) {
        //List<Comment> comments = commentRepository.findByUser_IdNotOrUserIsNull((user == null) ? user.getId() : null);

        List<Comment> comments = commentRepository.findAll();

        List<Comment> filteredComments = comments.stream()
                .filter(c -> {
                    // Se o user atual é null (anônimo), não filtra nada
                    if (user == null) return true;

                    // Se o comentário é anônimo ou de outro usuário, mantém
                    return c.getUser() == null || !c.getUser().getId().equals(user.getId());
                })
                .toList();
        if (filteredComments.isEmpty()) {
            throw new RuntimeException("No comments available.");
        }

        int randomIndex = new Random().nextInt(filteredComments.size());
        Comment randomComment = filteredComments.get(randomIndex);

        return new CommentResponseDTO(
                randomComment.getId(),
                randomComment.getText(),
                (randomComment.isAnonymous()) ? "null" : randomComment.getUser().getUserPhotoLink(),
                (randomComment.isAnonymous()) ? "Anonymous" : randomComment.getUser().getUsername(),
                randomComment.getCreatedAt()
        );
    }

    public List<SuggestionResponseDTO> getCommentSuggestions(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        String prompt = buildPrompt(comment.getText());

        String url = GEMINI_ENDPOINT + "?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to fetch suggestions from Gemini API");
        }

        return extractSuggestions(responseEntity.getBody());
    }

    private String buildPrompt(String commentText) {
        return String.format(
                "Write 3 suggestions with a maximum of 15 words each suggestion, only for the following text that is delimited by 4 at-signs (@) at the beginning and end (Notes: Write in the language of the delimited text (probably Portuguese or English); The suggestions must also be delimited by 4 at-signs (@) at the beginning and end; Do not use line breaks). @@@@ %s @@@@",
                commentText
        );
    }

    private List<SuggestionResponseDTO> extractSuggestions(String jsonResponse) {
        try {
            // Parse the JSON response
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode candidatesNode = rootNode.path("candidates");


            List<SuggestionResponseDTO> suggestions = new ArrayList<>();

            // Loop through all candidates
            for (JsonNode candidate : candidatesNode) {
                JsonNode contentNode = candidate.path("content").path("parts");
                for (JsonNode part : contentNode) {
                    String text = part.path("text").asText();

                    // Split the text by the delimiter "@@@@" to get individual suggestions
                    String[] suggestionArray = text.split("@@@@");

                    // Add each suggestion to the list as a SuggestionResponseDTO
                    for (String suggestion : suggestionArray) {
                        if (!suggestion.trim().isEmpty()) {
                            suggestions.add(new SuggestionResponseDTO(suggestion.trim()));
                        }
                    }
                }
            }

            return suggestions;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini API response", e);
        }
    }


}
