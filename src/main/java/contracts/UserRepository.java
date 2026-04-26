package contracts;

import model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String id);
}

