package gift;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {
    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Product> findAll() {
        String sql = "SELECT * FROM products";
        return jdbcTemplate.query(sql, productRowMapper());
    }

    public Product findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, productRowMapper(), id);
    }

    public void save(Product product) {
        String sql = "INSERT INTO products (id, name, price, imageUrl) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, product.id(), product.name(), product.price(), product.imageUrl());
    }

    public void update(Long id, Product product) {
        String checkSql = "SELECT COUNT(*) FROM products WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);

        if(count == 0 || count == null) {
            throw new ProductNotFoundException("해당 ID의 상품을 찾을 수 없습니다.");
        }

        String sql = "UPDATE products SET name = ?, price = ?, imageUrl = ? WHERE id = ?";
        jdbcTemplate.update(sql, product.name(), product.price(), product.imageUrl(), id);
    }

    public void delete(Long id) {
        String checkSql = "SELECT COUNT(*) FROM products WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);

        if(count == 0 || count == null) {
            throw new ProductNotFoundException("해당 ID의 상품을 찾을 수 없습니다.");
        }

        String sql = "DELETE FROM products WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private RowMapper<Product> productRowMapper() {
        return (rs, rowNum) -> new Product(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getInt("price"),
            rs.getString("imageUrl")
        );
    }
}
