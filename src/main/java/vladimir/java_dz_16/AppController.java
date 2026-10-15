package vladimir.java_dz_16;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AppController {

    private final DataService dataService;

    public AppController(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping("/users/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody UserEntity user) {
        boolean exists = dataService.getUsers().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()));
        if (exists) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username already taken"));
        }
        user.setRole("USER");
        user.setBlocked(false);
        dataService.getUsers().add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered successfully"));
    }

    @GetMapping("/reviews")
    public List<Review> getAllReviews() {
        return dataService.getReviews();
    }

    @GetMapping("/reviews/search")
    public List<Review> searchReviews(@RequestParam String query) {
        return dataService.getReviews().stream()
                .filter(r -> r.getText().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    @PostMapping("/reviews")
    public ResponseEntity<Review> addReview(@RequestBody Review review, Authentication auth) {
        review.setId(dataService.getReviewIdGenerator().getAndIncrement());
        review.setAuthor(auth.getName());
        dataService.getReviews().add(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<Map<String, String>> editReview(@PathVariable Long id, @RequestBody Review updated, Authentication auth) {
        for (Review r : dataService.getReviews()) {
            if (r.getId().equals(id)) {
                if (!r.getAuthor().equalsIgnoreCase(auth.getName())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied"));
                }
                r.setText(updated.getText());
                return ResponseEntity.ok(Map.of("message", "Review updated successfully"));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Review not found"));
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Map<String, String>> deleteOwnReview(@PathVariable Long id, Authentication auth) {
        for (Review r : dataService.getReviews()) {
            if (r.getId().equals(id)) {
                if (!r.getAuthor().equalsIgnoreCase(auth.getName())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Access denied"));
                }
                dataService.getReviews().remove(r);
                return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Review not found"));
    }

    @PostMapping("/admin/users")
    public ResponseEntity<Map<String, String>> adminAddUser(@RequestBody UserEntity user) {
        dataService.getUsers().add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User created by admin"));
    }

    @DeleteMapping("/admin/users/{username}")
    public ResponseEntity<Map<String, String>> adminDeleteUser(@PathVariable String username) {
        boolean removed = dataService.getUsers().removeIf(u -> u.getUsername().equalsIgnoreCase(username));
        if (removed) {
            return ResponseEntity.ok(Map.of("message", "User deleted by admin"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
    }

    @PostMapping("/admin/users/{username}/block")
    public ResponseEntity<Map<String, String>> adminBlockUser(@PathVariable String username) {
        for (UserEntity u : dataService.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                u.setBlocked(true);
                return ResponseEntity.ok(Map.of("message", "User blocked successfully"));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
    }

    @DeleteMapping("/admin/reviews/{id}")
    public ResponseEntity<Map<String, String>> adminDeleteReview(@PathVariable Long id) {
        boolean removed = dataService.getReviews().removeIf(r -> r.getId().equals(id));
        if (removed) {
            return ResponseEntity.ok(Map.of("message", "Review deleted by admin"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Review not found"));
    }
}
