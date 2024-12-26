CREATE TABLE posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    content TEXT,
    author VARCHAR(255),
    created_at BIGINT DEFAULT (UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000),
    updated_at BIGINT DEFAULT (UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000) ON UPDATE (UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000),
    category_id BIGINT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE TABLE post_metadata (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    key_name VARCHAR(255) NOT NULL,
    value TEXT,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

CREATE TABLE recent_posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);


-- Query for fetching data
SELECT c.id AS category_id, c.name AS category_name, c.description AS category_description,
    c.icon_name,
    p.id AS post_id, p.title AS post_title, p.description AS post_description
FROM categories c
LEFT JOIN posts p ON c.id = p.category_id
ORDER BY c.name, p.created_at DESC;
