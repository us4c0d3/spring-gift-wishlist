package gift.repository;

import gift.dto.user.UserRequestDto;
import gift.entity.User;
import gift.exception.user.UserAlreadyExistException;
import gift.mapper.UserMapper;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long insert(UserRequestDto userRequest) {
        User user = UserMapper.toUser(userRequest);
        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        if (findByEmail(userRequest.email()).isPresent()) {
            throw new UserAlreadyExistException("이미 존재하는 Email입니다.");
        }

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.email());
            ps.setString(2, user.password());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        List<User> users = jdbcTemplate.query(sql, userRowMapper(), email);

        return users.stream().findFirst();
    }

    public Optional<User> findByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper(), email, password);
        return users.stream().findFirst();
    }


    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(
            rs.getLong("id"),
            rs.getString("email"),
            rs.getString("password")
        );
    }
}
