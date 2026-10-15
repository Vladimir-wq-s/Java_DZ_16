package vladimir.java_dz_16;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DataService {
    private final List<UserEntity> users = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();
    private final AtomicLong reviewIdGenerator = new AtomicLong(1);

    public DataService() {
        users.add(new UserEntity("admin", "admin123", "ADMIN", false));
        users.add(new UserEntity("user", "user123", "USER", false));
        reviews.add(new Review(reviewIdGenerator.getAndIncrement(), "Excellent product, highly recommended!", "user"));
        reviews.add(new Review(reviewIdGenerator.getAndIncrement(), "Bad customer service, disappointed.", "user"));
    }

    public List<UserEntity> getUsers() { return users; }
    public List<Review> getReviews() { return reviews; }
    public AtomicLong getReviewIdGenerator() { return reviewIdGenerator; }
}
