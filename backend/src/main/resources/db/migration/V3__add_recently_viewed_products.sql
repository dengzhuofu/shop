CREATE TABLE IF NOT EXISTS ums_recently_viewed_product (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES sys_user(id),
    product_id BIGINT NOT NULL REFERENCES pms_product(id),
    view_count INT NOT NULL DEFAULT 1,
    last_viewed_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, product_id)
);

CREATE INDEX IF NOT EXISTS idx_ums_recently_viewed_product_user_time
    ON ums_recently_viewed_product (user_id, last_viewed_time DESC, id DESC);
